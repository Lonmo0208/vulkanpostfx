package com.ionhex975.vulkanpostfx.client.shadow;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * 兼容包装器。
 *
 * 旧版代码里曾直接在这里同步太阳方向与阴影状态，
 * 现在统一委托给 ShadowFrameCoordinator。
 */
public final class ShadowMapManager {
    private ShadowMapManager() {
    }

    public static void syncForFrame(
            Minecraft minecraft,
            DeltaTracker deltaTracker,
            CameraRenderState cameraState
    ) {
        ShadowFrameCoordinator.syncFrame(minecraft, deltaTracker, cameraState);
    }
}