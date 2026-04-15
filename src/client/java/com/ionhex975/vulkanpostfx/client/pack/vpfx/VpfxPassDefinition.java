package com.ionhex975.vulkanpostfx.client.pack.vpfx;

import java.util.List;

public final class VpfxPassDefinition {
    private final String vertexShader;
    private final String fragmentShader;
    private final List<VpfxPassInput> inputs;
    private final String output;

    public VpfxPassDefinition(
            String vertexShader,
            String fragmentShader,
            List<VpfxPassInput> inputs,
            String output
    ) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.inputs = List.copyOf(inputs);
        this.output = output;
    }

    public String getVertexShader() {
        return vertexShader;
    }

    public String getFragmentShader() {
        return fragmentShader;
    }

    public List<VpfxPassInput> getInputs() {
        return inputs;
    }

    public String getOutput() {
        return output;
    }
}