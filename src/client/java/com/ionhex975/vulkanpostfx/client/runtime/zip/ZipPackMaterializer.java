package com.ionhex975.vulkanpostfx.client.runtime.zip;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.pack.ShaderPackContainer;
import com.ionhex975.vulkanpostfx.client.pack.ZipShaderPackReader;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxGraphDefinition;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxNativePackDefinition;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxPassDefinition;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxTextureManifestEntry;
import com.ionhex975.vulkanpostfx.client.runtime.texture.VpfxRuntimeTextureManifest;
import com.ionhex975.vulkanpostfx.client.runtime.texture.VpfxRuntimeTextureManifestWriter;
import com.ionhex975.vulkanpostfx.client.shader.include.VpfxShaderIncludeException;
import com.ionhex975.vulkanpostfx.client.shader.include.VpfxShaderIncludeProcessor;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

public final class ZipPackMaterializer {
    private static final int RESOURCE_PACK_FORMAT = 85;

    private ZipPackMaterializer() {
    }

    public static RuntimeZipPackMaterializationResult materialize(
            ShaderPackContainer activePack,
            Path gameDirectory
    ) throws IOException {
        if (activePack == null || activePack.sourcePath() == null) {
            throw new IOException("active pack or source path is null");
        }

        if (!activePack.isVpfxNativePack()) {
            throw new IOException("active pack is not a VPFX native pack");
        }

        String packId = activePack.manifest().id();
        String runtimeNamespace = ActiveZipRuntimeNamespace.fromPackId(packId);

        Path runtimeBaseDir = gameDirectory.resolve("vulkanpostfx_runtime");
        Path runtimeRoot = runtimeBaseDir.resolve(runtimeNamespace);

        recreateDirectory(runtimeRoot);
        writePackMcmeta(runtimeRoot);

        String entryPostEffectRaw = ZipShaderPackReader.readText(
                activePack.sourcePath(),
                activePack.manifest().entryPostEffect()
        );

        String rewrittenMainJson = ZipPostEffectNamespaceRewriter.rewrite(
                entryPostEffectRaw,
                packId,
                runtimeNamespace
        );

        Path mainJsonPath = runtimeRoot
                .resolve("assets")
                .resolve(runtimeNamespace)
                .resolve("post_effect")
                .resolve("main.json");

        Files.createDirectories(mainJsonPath.getParent());
        Files.writeString(mainJsonPath, rewrittenMainJson, StandardCharsets.UTF_8);

        materializeReferencedShaders(activePack, runtimeRoot, runtimeNamespace);
        materializeDeclaredTextures(activePack, runtimeRoot, runtimeNamespace);

        VpfxRuntimeTextureManifest runtimeTextureManifest = VpfxRuntimeTextureManifestWriter.build(
                runtimeNamespace,
                activePack.vpfxDefinition().getManifest().getTextures()
        );
        VpfxRuntimeTextureManifestWriter.write(runtimeTextureManifest, runtimeRoot);

        Path runtimeTextureManifestPath = runtimeRoot
                .resolve("assets")
                .resolve(runtimeNamespace)
                .resolve("vpfx")
                .resolve("textures.json");

        VulkanPostFX.LOGGER.info(
                "[{}] Generated runtime texture manifest: namespace={}, textureCount={}, path={}",
                VulkanPostFX.MOD_ID,
                runtimeNamespace,
                runtimeTextureManifest.getTextures().size(),
                runtimeTextureManifestPath
        );

        return new RuntimeZipPackMaterializationResult(
                packId,
                runtimeNamespace,
                runtimeRoot,
                Identifier.fromNamespaceAndPath(runtimeNamespace, "main"),
                runtimeTextureManifestPath
        );
    }

    private static void recreateDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            try (var walk = Files.walk(dir)) {
                walk.sorted(java.util.Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException io) {
                    throw io;
                }
                throw e;
            }
        }

        Files.createDirectories(dir);
    }

    private static void writePackMcmeta(Path runtimeRoot) throws IOException {
        String mcmeta = """
        {
          "pack": {
            "pack_format": %d,
            "min_format": %d,
            "max_format": %d,
            "description": "VulkanPostFX runtime zip shader pack"
          }
        }
        """.formatted(RESOURCE_PACK_FORMAT, RESOURCE_PACK_FORMAT, RESOURCE_PACK_FORMAT);

        Files.writeString(runtimeRoot.resolve("pack.mcmeta"), mcmeta, StandardCharsets.UTF_8);
    }

    private static void materializeReferencedShaders(
            ShaderPackContainer activePack,
            Path runtimeRoot,
            String runtimeNamespace
    ) throws IOException {
        VpfxNativePackDefinition vpfxDefinition = activePack.vpfxDefinition();
        VpfxGraphDefinition graph = vpfxDefinition.getGraph();

        Set<String> requiredShaderZipPaths = collectRequiredShaderZipPaths(graph);

        try (ZipFile zipFile = new ZipFile(activePack.sourcePath().toFile())) {
            VpfxShaderIncludeProcessor includeProcessor = new VpfxShaderIncludeProcessor(zipFile);

            for (String zipShaderPath : requiredShaderZipPaths) {
                Path outPath = runtimeRoot
                        .resolve("assets")
                        .resolve(runtimeNamespace)
                        .resolve(zipShaderPath);

                Files.createDirectories(outPath.getParent());

                try {
                    String flattened = includeProcessor.process(zipShaderPath);
                    Files.writeString(outPath, flattened, StandardCharsets.UTF_8);

                    VulkanPostFX.LOGGER.info(
                            "[{}] Materialized referenced shader asset: {} -> {}",
                            VulkanPostFX.MOD_ID,
                            zipShaderPath,
                            outPath
                    );
                } catch (VpfxShaderIncludeException e) {
                    throw new IOException(
                            "Failed to preprocess referenced shader [" + e.getCode() + "][" + e.getPath() + "]: " + e.getMessage(),
                            e
                    );
                }
            }
        }
    }

    private static Set<String> collectRequiredShaderZipPaths(VpfxGraphDefinition graph) throws IOException {
        Set<String> paths = new LinkedHashSet<>();

        for (VpfxPassDefinition pass : graph.getPasses()) {
            paths.add(toShaderZipPath(pass.getVertexShader(), true));
            paths.add(toShaderZipPath(pass.getFragmentShader(), false));
        }

        return paths;
    }

    private static String toShaderZipPath(String shaderRef, boolean vertex) throws IOException {
        int colon = shaderRef.indexOf(':');
        if (colon < 0 || colon == shaderRef.length() - 1) {
            throw new IOException("Invalid shader resource id: " + shaderRef);
        }

        String shaderPath = shaderRef.substring(colon + 1);
        String extension = vertex ? ".vsh" : ".fsh";
        return "shaders/" + shaderPath + extension;
    }

    private static void materializeDeclaredTextures(
            ShaderPackContainer activePack,
            Path runtimeRoot,
            String runtimeNamespace
    ) throws IOException {
        VpfxNativePackDefinition vpfxDefinition = activePack.vpfxDefinition();
        Map<String, VpfxTextureManifestEntry> textures = vpfxDefinition.getManifest().getTextures();

        if (textures.isEmpty()) {
            return;
        }

        try (ZipFile zipFile = new ZipFile(activePack.sourcePath().toFile())) {
            for (VpfxTextureManifestEntry texture : textures.values()) {
                Path outPath = runtimeRoot
                        .resolve("assets")
                        .resolve(runtimeNamespace)
                        .resolve(texture.getPath());

                Files.createDirectories(outPath.getParent());

                var entry = zipFile.getEntry(texture.getPath());
                if (entry == null || entry.isDirectory()) {
                    throw new IOException("Declared texture missing from zip: " + texture.getPath());
                }

                try (InputStream in = zipFile.getInputStream(entry)) {
                    Files.copy(in, outPath);
                }

                VulkanPostFX.LOGGER.info(
                        "[{}] Materialized declared texture asset: {} -> {} (filter={}, wrap={})",
                        VulkanPostFX.MOD_ID,
                        texture.getPath(),
                        outPath,
                        texture.getFilter(),
                        texture.getWrap()
                );
            }
        }
    }
}