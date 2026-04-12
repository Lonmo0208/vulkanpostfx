package com.ionhex975.vulkanpostfx.client.shadow;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 每帧阴影状态。
 *
 * 当前阶段保存：
 * - 相机位置
 * - shadowAngle
 * - 太阳方向
 * - 阴影视图矩阵
 * - 阴影投影矩阵
 * - 阴影 VP 矩阵
 */
public final class ShadowFrameState {
    private static final ShadowFrameState INSTANCE = new ShadowFrameState();

    private Vec3 cameraPos = Vec3.ZERO;
    private float shadowAngle;
    private final Vector3f sunDirection = new Vector3f(0.0F, -1.0F, 0.0F);
    private final Matrix4f shadowViewMatrix = new Matrix4f();
    private final Matrix4f shadowProjectionMatrix = new Matrix4f();
    private final Matrix4f shadowViewProjectionMatrix = new Matrix4f();
    private boolean valid;

    private ShadowFrameState() {
    }

    public static ShadowFrameState get() {
        return INSTANCE;
    }

    public void update(
            Vec3 cameraPos,
            float shadowAngle,
            Vector3f sunDirection,
            Matrix4f shadowViewMatrix,
            Matrix4f shadowProjectionMatrix
    ) {
        this.cameraPos = cameraPos;
        this.shadowAngle = shadowAngle;
        this.sunDirection.set(sunDirection);
        this.shadowViewMatrix.set(shadowViewMatrix);
        this.shadowProjectionMatrix.set(shadowProjectionMatrix);
        this.shadowViewProjectionMatrix.set(shadowProjectionMatrix).mul(shadowViewMatrix);
        this.valid = true;
    }

    public void invalidate() {
        this.valid = false;
    }

    public boolean isValid() {
        return this.valid;
    }

    public Vec3 getCameraPos() {
        return this.cameraPos;
    }

    public float getShadowAngle() {
        return this.shadowAngle;
    }

    public Vector3f getSunDirection() {
        return new Vector3f(this.sunDirection);
    }

    public Matrix4f getShadowViewMatrix() {
        return new Matrix4f(this.shadowViewMatrix);
    }

    public Matrix4f getShadowProjectionMatrix() {
        return new Matrix4f(this.shadowProjectionMatrix);
    }

    public Matrix4f getShadowViewProjectionMatrix() {
        return new Matrix4f(this.shadowViewProjectionMatrix);
    }
}