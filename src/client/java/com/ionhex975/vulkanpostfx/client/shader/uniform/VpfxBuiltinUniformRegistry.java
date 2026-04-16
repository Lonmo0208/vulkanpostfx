package com.ionhex975.vulkanpostfx.client.shader.uniform;

import java.util.List;

public final class VpfxBuiltinUniformRegistry {
    public static final List<VpfxBuiltinUniformSpec> DEFAULTS = List.of(
            new VpfxBuiltinUniformSpec("vpfx_FrameIndex", VpfxBuiltinUniformType.INT),
            new VpfxBuiltinUniformSpec("vpfx_DeltaTime", VpfxBuiltinUniformType.FLOAT),
            new VpfxBuiltinUniformSpec("vpfx_GameTime", VpfxBuiltinUniformType.FLOAT),

            new VpfxBuiltinUniformSpec("vpfx_ViewSize", VpfxBuiltinUniformType.VEC2),
            new VpfxBuiltinUniformSpec("vpfx_InvViewSize", VpfxBuiltinUniformType.VEC2),

            new VpfxBuiltinUniformSpec("vpfx_CameraPos", VpfxBuiltinUniformType.VEC3),
            new VpfxBuiltinUniformSpec("vpfx_PreviousCameraPos", VpfxBuiltinUniformType.VEC3),

            new VpfxBuiltinUniformSpec("vpfx_RainStrength", VpfxBuiltinUniformType.FLOAT)
    );

    private VpfxBuiltinUniformRegistry() {
    }

    public static String buildShaderDeclarationHeader() {
        StringBuilder sb = new StringBuilder();

        sb.append("// === VPFX builtin uniforms begin ===\n");
        for (VpfxBuiltinUniformSpec spec : DEFAULTS) {
            sb.append("uniform ")
                    .append(spec.type().getGlslType())
                    .append(' ')
                    .append(spec.name())
                    .append(";\n");
        }
        sb.append("// === VPFX builtin uniforms end ===\n");

        return sb.toString();
    }
}