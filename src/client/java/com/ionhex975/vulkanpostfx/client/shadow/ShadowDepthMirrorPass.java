package com.ionhex975.vulkanpostfx.client.shadow;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

/**
 * 第四批中间层：
 * 把主相机 main target 的 depth 镜像到 shadow target。
 *
 * 这不是最终“太阳视角真实阴影”，
 * 只是为了先验证：
 * - shadow target 里可以有真实深度内容
 * - RenderTarget.copyDepthFrom(...) 这条官方路径可用
 */
public final class ShadowDepthMirrorPass {
    private static boolean firstMirrorLogged;
    private static boolean sizeMismatchLogged;

    private ShadowDepthMirrorPass() {
    }

    public static void copyMainDepthToShadow(Minecraft minecraft) {
        RenderSystem.assertOnRenderThread();

        ShadowFrameState state = ShadowFrameState.get();
        ShadowRenderTargetsLite targets = ShadowRenderTargetsLite.get();

        if (!state.isValid() || !state.isShadowTargetReady() || !targets.isReady()) {
            return;
        }

        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        RenderTarget shadowTarget = targets.getShadowDepthTarget();

        if (mainTarget == null || shadowTarget == null) {
            return;
        }

        if (mainTarget.getDepthTexture() == null || shadowTarget.getDepthTexture() == null) {
            return;
        }

        // 当前 copyDepthFrom 要求源纹理至少覆盖目标纹理尺寸。
        if (mainTarget.width < shadowTarget.width || mainTarget.height < shadowTarget.height) {
            if (!sizeMismatchLogged) {
                sizeMismatchLogged = true;
                VulkanPostFX.LOGGER.warn(
                        "[{}] Skip shadow depth mirror due to size mismatch: main={}x{}, shadow={}x{}",
                        VulkanPostFX.MOD_ID,
                        mainTarget.width,
                        mainTarget.height,
                        shadowTarget.width,
                        shadowTarget.height
                );
            }
            return;
        }

        try {
            shadowTarget.copyDepthFrom(mainTarget);
            state.markShadowDepthMirrored();

            if (!firstMirrorLogged) {
                firstMirrorLogged = true;
                VulkanPostFX.LOGGER.info(
                        "[{}] Shadow depth mirror completed: main={}x{}, shadow={}x{}",
                        VulkanPostFX.MOD_ID,
                        mainTarget.width,
                        mainTarget.height,
                        shadowTarget.width,
                        shadowTarget.height
                );
            }
        } catch (Throwable t) {
            VulkanPostFX.LOGGER.error(
                    "[{}] Shadow depth mirror failed",
                    VulkanPostFX.MOD_ID,
                    t
            );
        }
    }
}