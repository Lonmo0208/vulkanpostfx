package com.ionhex975.vulkanpostfx.client.shadow;

import org.joml.Vector3f;

/**
 * 从 sunAngle 近似推导太阳方向。
 *
 * 这里先走与 Iris ShadowMatrices 同语义的旋转方式：
 * - 围绕 X / Z 旋转得到太阳方向
 * - sunPathRotation 先固定为 0，后续再开放配置
 */
public final class SunDirectionProvider {
    private SunDirectionProvider() {
    }

    public static Vector3f fromSunAngle(float shadowAngle, float sunPathRotationDegrees) {
        float skyAngle;
        if (shadowAngle < 0.25F) {
            skyAngle = shadowAngle + 0.75F;
        } else {
            skyAngle = shadowAngle - 0.25F;
        }

        Vector3f direction = new Vector3f(0.0F, -1.0F, 0.0F)
                .rotateZ((float) Math.toRadians(skyAngle * -360.0F))
                .rotateX((float) Math.toRadians(sunPathRotationDegrees));

        if (direction.lengthSquared() < 1.0E-6F) {
            direction.set(0.0F, -1.0F, 0.0F);
        } else {
            direction.normalize();
        }

        return direction;
    }
}