package com.ionhex975.vulkanpostfx.client.runtime.texture;

import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxTextureFilter;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxTextureWrap;

public final class VpfxRuntimeTextureDescriptor {
    private final String logicalName;
    private final String relativePath;
    private final String resourceId;
    private final VpfxTextureFilter filter;
    private final VpfxTextureWrap wrap;

    public VpfxRuntimeTextureDescriptor(
            String logicalName,
            String relativePath,
            String resourceId,
            VpfxTextureFilter filter,
            VpfxTextureWrap wrap
    ) {
        this.logicalName = logicalName;
        this.relativePath = relativePath;
        this.resourceId = resourceId;
        this.filter = filter;
        this.wrap = wrap;
    }

    public String getLogicalName() {
        return logicalName;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getResourceId() {
        return resourceId;
    }

    public VpfxTextureFilter getFilter() {
        return filter;
    }

    public VpfxTextureWrap getWrap() {
        return wrap;
    }
}