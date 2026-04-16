package com.ionhex975.vulkanpostfx.client.shader.uniform;

public record VpfxBuiltinFrameSnapshot(
        int frameIndex,
        float deltaTime,
        float gameTime,

        float viewWidth,
        float viewHeight,
        float invViewWidth,
        float invViewHeight,

        float cameraX,
        float cameraY,
        float cameraZ,

        float previousCameraX,
        float previousCameraY,
        float previousCameraZ,

        float rainStrength
) {
}