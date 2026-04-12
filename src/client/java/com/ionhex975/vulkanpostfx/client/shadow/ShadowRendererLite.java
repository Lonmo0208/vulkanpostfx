package com.ionhex975.vulkanpostfx.client.shadow;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * Shadow Pipeline v2 / v3 过渡阶段：
 * - 准备 shadow target
 * - 执行 shadow depth pass 骨架
 *
 * 当前阶段“深度镜像”要求 shadow target 与 main target 同尺寸，
 * 否则 RenderTarget.copyDepthFrom(...) 会因区域越界直接失败。
 *
 * 注意：
 * 真正太阳视角 shadow map 阶段会重新切回固定分辨率（例如 2048）。
 */
public final class ShadowRendererLite {
    private static boolean firstPreparedLogged;

    private ShadowRendererLite() {
    }

    public static void prepareFrame(
            Minecraft minecraft,
            CameraRenderState cameraState
    ) {
        if (minecraft.level == null) {
            ShadowFrameState.get().setShadowTargetState(false, 0);
            return;
        }

        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        int targetWidth = mainTarget.width;
        int targetHeight = mainTarget.height;

        // 当前 ShadowRenderTargetsLite 只有一个 size 参数，
        // 所以这里先取较小边，保证 copyDepthFrom 不会越界。
        // 下一阶段如果你要支持非方形 shadow target，再升级成 width/height 双参数。
        int mirrorSize = Math.min(targetWidth, targetHeight);

        ShadowRenderTargetsLite targets = ShadowRenderTargetsLite.get();
        targets.ensureAllocated(mirrorSize);

        ShadowFrameState state = ShadowFrameState.get();
        state.setShadowTargetState(targets.isReady(), targets.getShadowMapSize());

        if (targets.isReady()) {
            state.requestShadowRender();
        }

        if (!firstPreparedLogged) {
            firstPreparedLogged = true;
            VulkanPostFX.LOGGER.info(
                    "[{}] Shadow renderer lite prepared: cameraPos={}, mainTarget={}x{}, targetReady={}, shadowMapSize={}",
                    VulkanPostFX.MOD_ID,
                    cameraState.pos,
                    targetWidth,
                    targetHeight,
                    targets.isReady(),
                    targets.getShadowMapSize()
            );
        }
    }

    public static void executeShadowPassLite() {
        ShadowDepthPassLite.execute();
    }
}