package com.ionhex975.vulkanpostfx.client.shadow;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

/**
 * Shadow Pipeline v1 运行时状态。
 *
 * 当前阶段只做：
 * - 太阳方向
 * - 阴影视图 / 投影 / VP 矩阵
 * - 阴影深度目标纹理占位
 *
 * 还不做：
 * - 真正 shadow map 场景渲染
 * - post chain 阴影采样解析
 */
public final class ShadowRuntimeState {
    private static final ShadowRuntimeState INSTANCE = new ShadowRuntimeState();

    private final Vector3f sunDirection = new Vector3f(0.0F, -1.0F, 0.0F);
    private final Matrix4f shadowViewMatrix = new Matrix4f();
    private final Matrix4f shadowProjectionMatrix = new Matrix4f();
    private final Matrix4f shadowViewProjectionMatrix = new Matrix4f();

    private Vec3 cameraPos = Vec3.ZERO;
    @Nullable
    private TextureTarget shadowDepthTarget;
    private int shadowMapSize;
    private boolean valid;

    private ShadowRuntimeState() {
    }

    public static ShadowRuntimeState get() {
        return INSTANCE;
    }

    public void ensureShadowTarget(int size) {
        RenderSystem.assertOnRenderThread();

        if (this.shadowDepthTarget != null && this.shadowMapSize == size) {
            return;
        }

        if (this.shadowDepthTarget != null) {
            this.shadowDepthTarget.destroyBuffers();
        }

        this.shadowDepthTarget = new TextureTarget("VulkanPostFX Shadow Depth", size, size, true);
        this.shadowMapSize = size;
    }

    public void updateFrame(
            Vec3 cameraPos,
            Vector3f sunDirection,
            Matrix4f shadowViewMatrix,
            Matrix4f shadowProjectionMatrix
    ) {
        this.cameraPos = cameraPos;
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
        return this.valid && this.shadowDepthTarget != null;
    }

    public Vec3 getCameraPos() {
        return this.cameraPos;
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

    @Nullable
    public TextureTarget getShadowDepthTarget() {
        return this.shadowDepthTarget;
    }

    public int getShadowMapSize() {
        return this.shadowMapSize;
    }

    public void close() {
        RenderSystem.assertOnRenderThread();

        if (this.shadowDepthTarget != null) {
            this.shadowDepthTarget.destroyBuffers();
            this.shadowDepthTarget = null;
        }

        this.shadowMapSize = 0;
        this.valid = false;
    }
}