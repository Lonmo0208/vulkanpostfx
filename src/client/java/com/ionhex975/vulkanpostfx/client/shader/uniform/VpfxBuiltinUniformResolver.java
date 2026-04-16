package com.ionhex975.vulkanpostfx.client.shader.uniform;

import java.util.ArrayList;
import java.util.List;

public final class VpfxBuiltinUniformResolver {
    private VpfxBuiltinUniformResolver() {
    }

    public static List<VpfxResolvedUniformValue> resolve(VpfxBuiltinFrameSnapshot snapshot) {
        List<VpfxResolvedUniformValue> out = new ArrayList<>();

        out.add(VpfxResolvedUniformValue.ofInt(
                "vpfx_FrameIndex",
                snapshot.frameIndex()
        ));

        out.add(VpfxResolvedUniformValue.ofFloat(
                "vpfx_DeltaTime",
                snapshot.deltaTime()
        ));

        out.add(VpfxResolvedUniformValue.ofFloat(
                "vpfx_GameTime",
                snapshot.gameTime()
        ));

        out.add(VpfxResolvedUniformValue.ofVec2(
                "vpfx_ViewSize",
                snapshot.viewWidth(),
                snapshot.viewHeight()
        ));

        out.add(VpfxResolvedUniformValue.ofVec2(
                "vpfx_InvViewSize",
                snapshot.invViewWidth(),
                snapshot.invViewHeight()
        ));

        out.add(VpfxResolvedUniformValue.ofVec3(
                "vpfx_CameraPos",
                snapshot.cameraX(),
                snapshot.cameraY(),
                snapshot.cameraZ()
        ));

        out.add(VpfxResolvedUniformValue.ofVec3(
                "vpfx_PreviousCameraPos",
                snapshot.previousCameraX(),
                snapshot.previousCameraY(),
                snapshot.previousCameraZ()
        ));

        out.add(VpfxResolvedUniformValue.ofFloat(
                "vpfx_RainStrength",
                snapshot.rainStrength()
        ));

        return out;
    }
}