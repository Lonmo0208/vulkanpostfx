package com.ionhex975.vulkanpostfx.client.pack.vpfx;

import java.util.Arrays;
import java.util.Optional;

public final class VpfxTargetDefinition {
    private final String id;
    private final Double scale;
    private final boolean useDepth;
    private final float[] clearColor;

    public VpfxTargetDefinition(String id, Double scale, boolean useDepth, float[] clearColor) {
        this.id = id;
        this.scale = scale;
        this.useDepth = useDepth;
        this.clearColor = clearColor == null ? null : Arrays.copyOf(clearColor, clearColor.length);
    }

    public String getId() {
        return id;
    }

    public Optional<Double> getScale() {
        return Optional.ofNullable(scale);
    }

    public boolean isUseDepth() {
        return useDepth;
    }

    public Optional<float[]> getClearColor() {
        return clearColor == null
                ? Optional.empty()
                : Optional.of(Arrays.copyOf(clearColor, clearColor.length));
    }
}