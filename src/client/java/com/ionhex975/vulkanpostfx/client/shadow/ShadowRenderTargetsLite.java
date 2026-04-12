package com.ionhex975.vulkanpostfx.client.shadow;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;

/**
 * Shadow depth target 轻量管理器。
 *
 * 当前阶段目标：
 * - 真正分配一个 shadow target
 * - 管理尺寸变化与释放
 *
 * 不再使用反射。
 * 26.2 的 TextureTarget / RenderTarget API 已经可以直接调用。
 */
public final class ShadowRenderTargetsLite {
    private static final ShadowRenderTargetsLite INSTANCE = new ShadowRenderTargetsLite();

    private RenderTarget shadowDepthTarget;
    private int shadowMapSize;
    private boolean ready;
    private boolean firstAllocatedLogged;

    private ShadowRenderTargetsLite() {
    }

    public static ShadowRenderTargetsLite get() {
        return INSTANCE;
    }

    public void ensureAllocated(int size) {
        RenderSystem.assertOnRenderThread();

        if (this.ready && this.shadowDepthTarget != null && this.shadowMapSize == size) {
            return;
        }

        release();

        try {
            this.shadowDepthTarget = new TextureTarget("VulkanPostFX Shadow Depth", size, size, true);
            this.shadowMapSize = size;
            this.ready = true;

            if (!firstAllocatedLogged) {
                firstAllocatedLogged = true;
                VulkanPostFX.LOGGER.info(
                        "[{}] Shadow depth target allocated: {}x{}, targetClass={}",
                        VulkanPostFX.MOD_ID,
                        size,
                        size,
                        this.shadowDepthTarget.getClass().getName()
                );
            }
        } catch (Throwable t) {
            this.shadowDepthTarget = null;
            this.shadowMapSize = 0;
            this.ready = false;

            VulkanPostFX.LOGGER.error(
                    "[{}] Failed to allocate shadow depth target",
                    VulkanPostFX.MOD_ID,
                    t
            );
        }
    }

    public void release() {
        RenderSystem.assertOnRenderThread();

        if (this.shadowDepthTarget != null) {
            this.shadowDepthTarget.destroyBuffers();
        }

        this.shadowDepthTarget = null;
        this.shadowMapSize = 0;
        this.ready = false;
    }

    public boolean isReady() {
        return this.ready && this.shadowDepthTarget != null;
    }

    public int getShadowMapSize() {
        return this.shadowMapSize;
    }

    public RenderTarget getShadowDepthTarget() {
        return this.shadowDepthTarget;
    }
}