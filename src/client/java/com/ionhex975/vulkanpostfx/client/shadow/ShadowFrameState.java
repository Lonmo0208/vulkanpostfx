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
 * - shadow depth target 是否已准备
 * - shadow map 尺寸
 * - 是否请求执行 shadow pass
 * - 本帧 shadow pass 是否已执行
 * - 本帧是否已把主深度镜像到 shadow target
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
    private boolean shadowTargetReady;
    private int shadowMapSize;
    private boolean shadowRenderRequested;
    private boolean shadowPassExecuted;
    private boolean shadowDepthMirrored;

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
        this.shadowPassExecuted = false;
        this.shadowDepthMirrored = false;
    }

    public void setShadowTargetState(boolean ready, int size) {
        this.shadowTargetReady = ready;
        this.shadowMapSize = size;
    }

    public void requestShadowRender() {
        this.shadowRenderRequested = true;
    }

    public boolean consumeShadowRenderRequest() {
        boolean requested = this.shadowRenderRequested;
        this.shadowRenderRequested = false;
        return requested;
    }

    public void markShadowPassExecuted() {
        this.shadowPassExecuted = true;
    }

    public boolean wasShadowPassExecuted() {
        return this.shadowPassExecuted;
    }

    public void markShadowDepthMirrored() {
        this.shadowDepthMirrored = true;
    }

    public boolean wasShadowDepthMirrored() {
        return this.shadowDepthMirrored;
    }

    public void invalidate() {
        this.valid = false;
        this.shadowTargetReady = false;
        this.shadowMapSize = 0;
        this.shadowRenderRequested = false;
        this.shadowPassExecuted = false;
        this.shadowDepthMirrored = false;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isShadowTargetReady() {
        return this.shadowTargetReady;
    }

    public int getShadowMapSize() {
        return this.shadowMapSize;
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