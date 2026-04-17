package com.ionhex975.vulkanpostfx.client.shadow;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxCapabilityResolver;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxRuntimeCapabilities;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public final class ShadowRendererLite {
    private static boolean firstPreparedLogged;

    private ShadowRendererLite() {
    }

    public static void prepareFrame(
            Minecraft minecraft,
            CameraRenderState cameraState
    ) {
        VpfxRuntimeCapabilities caps =
                new VpfxCapabilityResolver().resolve();

        if (!caps.isShadowDepth()) {
            ShadowFrameState.get().setShadowTargetState(false, 0);
            return;
        }

        if (minecraft.level == null) {
            ShadowFrameState.get().setShadowTargetState(false, 0);
            return;
        }

        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        int targetWidth = mainTarget.width;
        int targetHeight = mainTarget.height;

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
        VpfxRuntimeCapabilities caps =
                new VpfxCapabilityResolver().resolve();

        if (!caps.isShadowDepth()) {
            return;
        }

        ShadowDepthPassLite.execute();
    }
}