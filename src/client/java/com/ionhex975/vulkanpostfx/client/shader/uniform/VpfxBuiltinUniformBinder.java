package com.ionhex975.vulkanpostfx.client.shader.uniform;

import java.util.List;

public final class VpfxBuiltinUniformBinder {
    private VpfxBuiltinUniformBinder() {
    }

    public static void apply(
            VpfxBuiltinFrameSnapshot snapshot,
            VpfxUniformUploadTarget target
    ) {
        List<VpfxResolvedUniformValue> values = VpfxBuiltinUniformResolver.resolve(snapshot);

        for (VpfxResolvedUniformValue value : values) {
            switch (value.getType()) {
                case INT -> target.setInt(value.getName(), value.getIntValue());
                case FLOAT -> target.setFloat(value.getName(), value.getValues()[0]);
                case VEC2 -> target.setVec2(value.getName(), value.getValues()[0], value.getValues()[1]);
                case VEC3 -> target.setVec3(value.getName(), value.getValues()[0], value.getValues()[1], value.getValues()[2]);
            }
        }
    }
}