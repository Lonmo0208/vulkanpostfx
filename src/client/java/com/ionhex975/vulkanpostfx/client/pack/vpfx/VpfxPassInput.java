package com.ionhex975.vulkanpostfx.client.pack.vpfx;

public final class VpfxPassInput {
    private final String samplerName;
    private final String target;
    private final boolean useDepthBuffer;

    public VpfxPassInput(String samplerName, String target, boolean useDepthBuffer) {
        this.samplerName = samplerName;
        this.target = target;
        this.useDepthBuffer = useDepthBuffer;
    }

    public String getSamplerName() {
        return samplerName;
    }

    public String getTarget() {
        return target;
    }

    public boolean isUseDepthBuffer() {
        return useDepthBuffer;
    }
}