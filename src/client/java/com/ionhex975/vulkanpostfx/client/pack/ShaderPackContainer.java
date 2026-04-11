package com.ionhex975.vulkanpostfx.client.pack;

import java.nio.file.Path;

/**
 * 一个已识别的光影包容器。
 *
 * 当前阶段存：
 * - manifest（清单）
 * - sourceId（来源标识）
 * - sourcePath（来源路径；内置包时可为 null）
 *
 * 后面扩展时可继续挂：
 * - zip 虚拟文件系统句柄
 * - 资源索引
 * - 包图标/描述等
 */
public final class ShaderPackContainer {
    private final ShaderPackManifest manifest;
    private final String sourceId;
    private final Path sourcePath;

    public ShaderPackContainer(ShaderPackManifest manifest, String sourceId) {
        this(manifest, sourceId, null);
    }

    public ShaderPackContainer(ShaderPackManifest manifest, String sourceId, Path sourcePath) {
        this.manifest = manifest;
        this.sourceId = sourceId;
        this.sourcePath = sourcePath;
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
}