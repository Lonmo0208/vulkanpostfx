package com.ionhex975.vulkanpostfx.client.shadow;

import org.joml.Matrix4f;

/**
 * 参考 Iris ShadowMatrices 的最小实现。
 *
 * 当前阶段只负责：
 * - 正交阴影投影矩阵
 * - 阴影视图矩阵（太阳方向 + 相机位置）
 * - 阴影 VP 矩阵
 *
 * 还不负责：
 * - 真正 shadow map 渲染
 * - frustum / culling / snapping 精细化
 */
public final class ShadowMatricesLite {
    public static final float NEAR = -100.05F;
    public static final float FAR = 156.0F;

    private ShadowMatricesLite() {
    }

    public static Matrix4f createOrthoMatrix(float halfPlaneLength, float nearPlane, float farPlane) {
        // 当前目标后端就是 Vulkan，先固定使用 zero-to-one depth。
        return new Matrix4f().setOrthoSymmetric(
                halfPlaneLength * 2.0F,
                halfPlaneLength * 2.0F,
                nearPlane,
                farPlane,
                true
        );
    }

    public static Matrix4f createBaselineModelViewMatrix(float shadowAngle, float sunPathRotationDegrees) {
        float skyAngle;
        if (shadowAngle < 0.25F) {
            skyAngle = shadowAngle + 0.75F;
        } else {
            skyAngle = shadowAngle - 0.25F;
        }

        return new Matrix4f()
                .identity()
                .rotateX((float) Math.toRadians(90.0F))
                .rotateZ((float) Math.toRadians(skyAngle * -360.0F))
                .rotateX((float) Math.toRadians(sunPathRotationDegrees));
    }

    public static Matrix4f createModelViewMatrix(
            float shadowAngle,
            float shadowIntervalSize,
            float sunPathRotationDegrees,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {
        Matrix4f modelView = createBaselineModelViewMatrix(shadowAngle, sunPathRotationDegrees);

        if (Math.abs(shadowIntervalSize) > 1.0E-6F) {
            float offsetX = (float) cameraX % shadowIntervalSize;
            float offsetY = (float) cameraY % shadowIntervalSize;
            float offsetZ = (float) cameraZ % shadowIntervalSize;

            float halfInterval = shadowIntervalSize / 2.0F;
            offsetX -= halfInterval;
            offsetY -= halfInterval;
            offsetZ -= halfInterval;

            modelView.translate(offsetX, offsetY, offsetZ);
        }

        return modelView;
    }

    public static Matrix4f createViewProjectionMatrix(
            float halfPlaneLength,
            float shadowAngle,
            float shadowIntervalSize,
            float sunPathRotationDegrees,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {
        Matrix4f projection = createOrthoMatrix(halfPlaneLength, NEAR, FAR);
        Matrix4f modelView = createModelViewMatrix(
                shadowAngle,
                shadowIntervalSize,
                sunPathRotationDegrees,
                cameraX,
                cameraY,
                cameraZ
        );

        return new Matrix4f(projection).mul(modelView);
    }
}