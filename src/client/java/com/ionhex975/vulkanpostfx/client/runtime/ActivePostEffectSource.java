package com.ionhex975.vulkanpostfx.client.runtime;

/**
 * 当前活动入口后处理源。
 *
 * 当前阶段只记录：
 * - sourceKind: builtin / zip / none
 * - displayPath: 便于日志展示
 * - rawJson: ZIP 入口文件原始内容（builtin 暂时留空）
 */
public final class ActivePostEffectSource {
    public static final ActivePostEffectSource NONE =
            new ActivePostEffectSource("none", "", "");

    private final String sourceKind;
    private final String displayPath;
    private final String rawJson;

    public ActivePostEffectSource(String sourceKind, String displayPath, String rawJson) {
        this.sourceKind = sourceKind;
        this.displayPath = displayPath;
        this.rawJson = rawJson;
    }

    public String sourceKind() {
        return sourceKind;
    }

    public String displayPath() {
        return displayPath;
    }

    public String rawJson() {
        return rawJson;
    }

    public boolean isPresent() {
        return !"none".equals(sourceKind);
    }
}