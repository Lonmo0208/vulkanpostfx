package com.ionhex975.vulkanpostfx.client.pack.vpfx;

/**
 * v1 先走结构能力（structural capability）解析，
 * 不是按“当前帧是否有 world / shadow target”动态判断。
 *
 * 也就是说，这里回答的是：
 * “当前模组 runtime 是否支持这类能力”
 * 不是：
 * “这一帧是否已经生成 shadow depth”
 */
public final class VpfxCapabilityResolver {

    public VpfxRuntimeCapabilities resolve() {
        return new VpfxRuntimeCapabilities(
                true,   // sceneColor
                true,   // sceneDepth
                true,   // shadowDepth
                true,   // customTargets
                false   // compute
        );
    }
}