package com.ionhex975.vulkanpostfx.client.pack;

import java.nio.file.Path;

/**
 * 一个已识别的光影包容器。
 *
 * 当前阶段存：
 * - manifest（清单）
 * - sourceId（来源标识）
 * - sourcePath（来源路径；内置包时可为 null）
 * - resourceIndex（包内资源索引）
 */
public final class ShaderPackContainer {
    private final ShaderPackManifest manifest;
    private final String sourceId;
    private final Path sourcePath;
    private final ShaderPackResourceIndex resourceIndex;

    public ShaderPackContainer(ShaderPackManifest manifest, String sourceId) {
        this(manifest, sourceId, null, ShaderPackResourceIndex.empty());
    }

    public ShaderPackContainer(
            ShaderPackManifest manifest,
            String sourceId,
            Path sourcePath
    ) {
        this(manifest, sourceId, sourcePath, ShaderPackResourceIndex.empty());
    }

    public ShaderPackContainer(
            ShaderPackManifest manifest,
            String sourceId,
            Path sourcePath,
            ShaderPackResourceIndex resourceIndex
    ) {
        this.manifest = manifest;
        this.sourceId = sourceId;
        this.sourcePath = sourcePath;
        this.resourceIndex = resourceIndex;
    }

    public ShaderPackManifest manifest() {
        return manifest;
    }

    public String sourceId() {
        return sourceId;
    }

    public Path sourcePath() {
        return sourcePath;
    }

    public ShaderPackResourceIndex resourceIndex() {
        return resourceIndex;
    }
}