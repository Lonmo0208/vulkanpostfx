package com.ionhex975.vulkanpostfx.client.shadow;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxCapabilityResolver;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxRuntimeCapabilities;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;

/**
 * Shadow depth pass 轻量骨架。
 *
 * 当前阶段只做：
 * - 清除 shadow target 的 color/depth
 * - 标记本帧 shadow pass 已执行
 *
 * 还不做：
 * - 从太阳视角真正绘制世界几何
 *
 * 这版修复点：
 * - 完全放弃旧式 bindWrite 思路
 * - 直接使用 26.2 正确的 CommandEncoder clear API
 */
public final class ShadowDepthPassLite {
    private static boolean firstExecutedLogged;

    private ShadowDepthPassLite() {
    }

    public static void execute() {
        RenderSystem.assertOnRenderThread();

        VpfxRuntimeCapabilities caps =
                new VpfxCapabilityResolver().resolve();

        if (!caps.isShadowDepth()) {
            return;
        }

        ShadowFrameState state = ShadowFrameState.get();
        ShadowRenderTargetsLite targets = ShadowRenderTargetsLite.get();

        if (!state.isValid() || !state.isShadowTargetReady() || !targets.isReady()) {
            return;
        }

        if (!state.consumeShadowRenderRequest()) {
            return;
        }

        RenderTarget target = targets.getShadowDepthTarget();
        if (target == null) {
            return;
        }

        try {
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();

            if (target.getColorTexture() != null && target.getDepthTexture() != null) {
                encoder.clearColorAndDepthTextures(
                        target.getColorTexture(),
                        0,
                        target.getDepthTexture(),
                        0.0
                );
            } else if (target.getDepthTexture() != null) {
                encoder.clearDepthTexture(target.getDepthTexture(), 0.0);
            } else {
                throw new IllegalStateException("Shadow target has no depth texture");
            }

            state.markShadowPassExecuted();

            if (!firstExecutedLogged) {
                firstExecutedLogged = true;
                VulkanPostFX.LOGGER.info(
                        "[{}] Shadow depth pass lite executed: shadowMapSize={}",
                        VulkanPostFX.MOD_ID,
                        state.getShadowMapSize()
                );
            }
        } catch (Throwable t) {
            VulkanPostFX.LOGGER.error(
                    "[{}] Shadow depth pass lite execution failed",
                    VulkanPostFX.MOD_ID,
                    t
            );
        }
    }
}