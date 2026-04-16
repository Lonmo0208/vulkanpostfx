package com.ionhex975.vulkanpostfx.client.shader.uniform;

public interface VpfxUniformUploadTarget {
    void setInt(String name, int value);
    void setFloat(String name, float value);
    void setVec2(String name, float x, float y);
    void setVec3(String name, float x, float y, float z);
}