package com.ionhex975.vulkanpostfx.client.runtime.posteffect;

import com.ionhex975.vulkanpostfx.client.pack.ShaderPackContainer;
import com.ionhex975.vulkanpostfx.client.pack.ShaderPackResourceIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 校验 ZIP post_effect 中的 shader 引用是否存在。
 *
 * 当前支持两类引用：
 *
 * 1. builtin 引用
 *    - vulkanpostfx:post/fullscreen
 *    - vulkanpostfx:post/invert
 *    - vulkanpostfx:post/blit
 *    - vulkanpostfx:post/grayscale
 *
 * 2. zip-local 引用
 *    - <activePackId>:post/fullscreen
 *    - <activePackId>:post/grayscale
 *    - <activePackId>:post/blit
 *
 * 同时支持 texture input：
 *    - sampler_name + texture=<logicalName>
 */
public final class ZipShaderReferenceValidator {
    private static final String BUILTIN_NAMESPACE = "vulkanpostfx";

    private ZipShaderReferenceValidator() {
    }

    public static ZipShaderReferenceValidationResult validate(
            ShaderPackContainer activePack,
            ZipPostEffectConfig config
    ) {
        List<String> missing = new ArrayList<>();
        int checked = 0;

        if (activePack == null || config == null) {
            return new ZipShaderReferenceValidationResult(0, List.of("active pack or config is null"));
        }

        for (ZipPostEffectPass pass : config.passes()) {
            checked++;
            validateShaderRef(activePack, pass.vertexShader(), true, missing);

            checked++;
            validateShaderRef(activePack, pass.fragmentShader(), false, missing);

            for (ZipPostEffectInput input : pass.inputs()) {
                if (input.isTextureInput()) {
                    validateTextureInput(activePack, input.texture(), missing);
                }
            }
        }

        return new ZipShaderReferenceValidationResult(checked, missing);
    }

    private static void validateShaderRef(
            ShaderPackContainer activePack,
            String shaderRef,
            boolean vertexStage,
            List<String> missing
    ) {
        if (shaderRef == null || shaderRef.isBlank()) {
            missing.add("(blank shader ref)");
            return;
        }

        int colon = shaderRef.indexOf(':');
        if (colon < 0 || colon == shaderRef.length() - 1) {
            missing.add(shaderRef + " [invalid namespaced shader ref]");
            return;
        }

        String namespace = shaderRef.substring(0, colon);
        String path = shaderRef.substring(colon + 1);

        if (BUILTIN_NAMESPACE.equals(namespace)) {
            String builtinPath = toBuiltinShaderAssetPath(path, vertexStage);
            if (builtinPath == null) {
                missing.add(shaderRef + " [unsupported builtin shader path]");
            }
            return;
        }

        String activePackId = activePack.manifest().id();
        if (activePackId.equals(namespace)) {
            String zipInternalPath = toZipShaderInternalPath(path, vertexStage);
            if (zipInternalPath == null) {
                missing.add(shaderRef + " [unsupported zip-local shader path]");
                return;
            }

            ShaderPackResourceIndex index = activePack.resourceIndex();
            if (!index.exists(zipInternalPath)) {
                missing.add(shaderRef + " -> missing zip entry: " + zipInternalPath);
            }
            return;
        }

        missing.add(shaderRef + " [unsupported shader namespace]");
    }

    private static void validateTextureInput(
            ShaderPackContainer activePack,
            String logicalTextureName,
            List<String> missing
    ) {
        if (logicalTextureName == null || logicalTextureName.isBlank()) {
            missing.add("(blank texture logical name)");
            return;
        }

        if (!activePack.isVpfxNativePack()) {
            missing.add(logicalTextureName + " [texture input requires VPFX native pack]");
            return;
        }

        Map<String, ?> textures = activePack.vpfxDefinition().getManifest().getTextures();
        if (!textures.containsKey(logicalTextureName)) {
            missing.add(logicalTextureName + " [undeclared texture logical name]");
        }
    }

    private static String toBuiltinShaderAssetPath(String shaderPath, boolean vertexStage) {
        if (!shaderPath.startsWith("post/")) {
            return null;
        }

        String extension = vertexStage ? ".vsh" : ".fsh";
        return "assets/vulkanpostfx/shaders/" + shaderPath + extension;
    }

    private static String toZipShaderInternalPath(String shaderPath, boolean vertexStage) {
        if (!shaderPath.startsWith("post/")) {
            return null;
        }

        String extension = vertexStage ? ".vsh" : ".fsh";
        return "shaders/" + shaderPath + extension;
    }
}