package com.ionhex975.vulkanpostfx.client.pack.vpfx;

/**
 * 当前阶段 capability 必须只声明“runtime 真的能稳定兑现”的能力。
 *
 * 主线收束阶段的口径：
 * - sceneColor: true
 * - sceneDepth: false
 * - shadowDepth: false
 * - customTargets: true
 * - compute: false
 *
 * 说明：
 * 1. shadow depth 当前还不是可对外承诺的正式能力；
 * 2. scene depth 当前也不作为稳定 capability 暴露；
 * 3. customTargets 仅表示 VPFX graph 内部 target 组织能力存在。
 */
public final class VpfxCapabilityResolver {

    public VpfxRuntimeCapabilities resolve() {
        return new VpfxRuntimeCapabilities(
                true,   // sceneColor
                false,  // sceneDepth
                false,  // shadowDepth
                true,   // customTargets
                false   // compute
        );
    }
}