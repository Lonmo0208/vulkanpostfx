package com.ionhex975.vulkanpostfx.client.runtime.zip;

import net.minecraft.resources.Identifier;

import java.nio.file.Path;

public final class RuntimeZipPackMaterializationResult {
    private final String packId;
    private final String runtimeNamespace;
    private final Path runtimeRoot;
    private final Identifier externalPostEffectId;
    private final Path runtimeTextureManifestPath;

    public RuntimeZipPackMaterializationResult(
            String packId,
            String runtimeNamespace,
            Path runtimeRoot,
            Identifier externalPostEffectId,
            Path runtimeTextureManifestPath
    ) {
        this.packId = packId;
        this.runtimeNamespace = runtimeNamespace;
        this.runtimeRoot = runtimeRoot;
        this.externalPostEffectId = externalPostEffectId;
        this.runtimeTextureManifestPath = runtimeTextureManifestPath;
    }

    // ===== 新风格 getter =====

    public String getPackId() {
        return packId;
    }

    public String getRuntimeNamespace() {
        return runtimeNamespace;
    }

    public Path getRuntimeRoot() {
        return runtimeRoot;
    }

    public Identifier getExternalPostEffectId() {
        return externalPostEffectId;
    }

    public Path getRuntimeTextureManifestPath() {
        return runtimeTextureManifestPath;
    }

    // ===== 兼容旧调用风格（保留现有调用点不改） =====

    public String packId() {
        return packId;
    }

    public String runtimeNamespace() {
        return runtimeNamespace;
    }

    public Path runtimeRoot() {
        return runtimeRoot;
    }

    public Identifier externalPostEffectId() {
        return externalPostEffectId;
    }

    public Path runtimeTextureManifestPath() {
        return runtimeTextureManifestPath;
    }
}