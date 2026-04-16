package com.ionhex975.vulkanpostfx.client.runtime.texture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxTextureManifestEntry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class VpfxRuntimeTextureManifestWriter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private VpfxRuntimeTextureManifestWriter() {
    }

    public static VpfxRuntimeTextureManifest build(
            String runtimeNamespace,
            Map<String, VpfxTextureManifestEntry> declaredTextures
    ) {
        Map<String, VpfxRuntimeTextureDescriptor> result = new LinkedHashMap<>();

        for (Map.Entry<String, VpfxTextureManifestEntry> entry : declaredTextures.entrySet()) {
            String logicalName = entry.getKey();
            VpfxTextureManifestEntry texture = entry.getValue();

            String normalizedPath = normalizeRelativeAssetPath(texture.getPath());
            String resourceId = runtimeNamespace + ":" + normalizedPath;

            result.put(logicalName, new VpfxRuntimeTextureDescriptor(
                    logicalName,
                    normalizedPath,
                    resourceId,
                    texture.getFilter(),
                    texture.getWrap()
            ));
        }

        return new VpfxRuntimeTextureManifest(runtimeNamespace, result);
    }

    public static void write(VpfxRuntimeTextureManifest manifest, Path runtimeRoot) throws IOException {
        Path outPath = runtimeRoot
                .resolve("assets")
                .resolve(manifest.getRuntimeNamespace())
                .resolve("vpfx")
                .resolve("textures.json");

        Files.createDirectories(outPath.getParent());
        Files.writeString(outPath, toJson(manifest), StandardCharsets.UTF_8);
    }

    public static String toJson(VpfxRuntimeTextureManifest manifest) {
        JsonObject root = new JsonObject();
        root.addProperty("runtime_namespace", manifest.getRuntimeNamespace());

        JsonObject textures = new JsonObject();
        for (VpfxRuntimeTextureDescriptor descriptor : manifest.getTextures().values()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("relative_path", descriptor.getRelativePath());
            entry.addProperty("resource_id", descriptor.getResourceId());
            entry.addProperty("filter", descriptor.getFilter().getJsonName());
            entry.addProperty("wrap", descriptor.getWrap().getJsonName());
            textures.add(descriptor.getLogicalName(), entry);
        }

        root.add("textures", textures);
        return GSON.toJson(root);
    }

    private static String normalizeRelativeAssetPath(String zipTexturePath) {
        String normalized = zipTexturePath.replace('\\', '/').trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }
}