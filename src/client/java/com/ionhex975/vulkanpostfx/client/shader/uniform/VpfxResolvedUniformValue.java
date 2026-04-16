package com.ionhex975.vulkanpostfx.client.shader.uniform;

public final class VpfxResolvedUniformValue {
    private final String name;
    private final VpfxBuiltinUniformType type;
    private final float[] values;
    private final int intValue;

    private VpfxResolvedUniformValue(String name, VpfxBuiltinUniformType type, float[] values, int intValue) {
        this.name = name;
        this.type = type;
        this.values = values;
        this.intValue = intValue;
    }

    public static VpfxResolvedUniformValue ofInt(String name, int value) {
        return new VpfxResolvedUniformValue(name, VpfxBuiltinUniformType.INT, null, value);
    }

    public static VpfxResolvedUniformValue ofFloat(String name, float value) {
        return new VpfxResolvedUniformValue(name, VpfxBuiltinUniformType.FLOAT, new float[]{value}, 0);
    }

    public static VpfxResolvedUniformValue ofVec2(String name, float x, float y) {
        return new VpfxResolvedUniformValue(name, VpfxBuiltinUniformType.VEC2, new float[]{x, y}, 0);
    }

    public static VpfxResolvedUniformValue ofVec3(String name, float x, float y, float z) {
        return new VpfxResolvedUniformValue(name, VpfxBuiltinUniformType.VEC3, new float[]{x, y, z}, 0);
    }

    public String getName() {
        return name;
    }

    public VpfxBuiltinUniformType getType() {
        return type;
    }

    public float[] getValues() {
        return values;
    }

    public int getIntValue() {
        return intValue;
    }
}