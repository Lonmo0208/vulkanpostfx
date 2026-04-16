package com.ionhex975.vulkanpostfx.client.shader.uniform;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 注意：
 * 这一版按 Mojang mappings 写，不再使用 Yarn 风格类名。
 *
 * 当前目标：
 * 1. 先恢复编译
 * 2. 提供一版可工作的 builtin frame snapshot
 * 3. 后面再根据你真实的 pass hook 点，把 deltaTime / tickDelta 接精确
 */
public final class VpfxBuiltinFrameSnapshotCollector {
    private static int frameCounter = 0;

    private static double prevCameraX = 0.0;
    private static double prevCameraY = 0.0;
    private static double prevCameraZ = 0.0;
    private static boolean prevCameraInitialized = false;

    private VpfxBuiltinFrameSnapshotCollector() {
    }

    public static VpfxBuiltinFrameSnapshot collect(Minecraft minecraft, float tickDelta) {
        Window window = minecraft.getWindow();

        int width = Math.max(window.getWidth(), 1);
        int height = Math.max(window.getHeight(), 1);

        float deltaTime = 0.0f;
        float gameTime = 0.0f;
        float rainStrength = 0.0f;

        double camX = 0.0;
        double camY = 0.0;
        double camZ = 0.0;

        Level level = minecraft.level;
        if (level != null) {
            gameTime = (float) (level.getGameTime() + tickDelta);
            rainStrength = level.getRainLevel(tickDelta);
        }

        Camera camera = minecraft.gameRenderer != null ? minecraft.gameRenderer.getMainCamera() : null;
        if (camera != null) {
            Vec3 pos = camera.position();
            camX = pos.x;
            camY = pos.y;
            camZ = pos.z;
        }

        if (!prevCameraInitialized) {
            prevCameraX = camX;
            prevCameraY = camY;
            prevCameraZ = camZ;
            prevCameraInitialized = true;
        }

        VpfxBuiltinFrameSnapshot snapshot = new VpfxBuiltinFrameSnapshot(
                frameCounter++,
                deltaTime,
                gameTime,

                (float) width,
                (float) height,
                1.0f / (float) width,
                1.0f / (float) height,

                (float) camX,
                (float) camY,
                (float) camZ,

                (float) prevCameraX,
                (float) prevCameraY,
                (float) prevCameraZ,

                rainStrength
        );

        prevCameraX = camX;
        prevCameraY = camY;
        prevCameraZ = camZ;

        return snapshot;
    }
}