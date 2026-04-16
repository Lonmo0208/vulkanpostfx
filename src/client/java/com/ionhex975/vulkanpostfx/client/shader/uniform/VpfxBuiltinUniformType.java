package com.ionhex975.vulkanpostfx.client.shader.uniform;

public enum VpfxBuiltinUniformType {
    INT("int"),
    FLOAT("float"),
    VEC2("vec2"),
    VEC3("vec3");

    private final String glslType;

    VpfxBuiltinUniformType(String glslType) {
        this.glslType = glslType;
    }

    public String getGlslType() {
        return glslType;
    }
}