package com.ionhex975.vulkanpostfx.client.postfx;

import com.ionhex975.vulkanpostfx.client.shadow.ShadowFrameState;
import com.ionhex975.vulkanpostfx.client.shadow.ShadowRenderTargetsLite;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

/**
 * 用多 external target 运行 PostChain。
 *
 * 当前先暴露：
 * - minecraft:main
 * - vulkanpostfx:shadow_depth
 */
public final class PostFxExternalTargetRunner {
    private PostFxExternalTargetRunner() {
    }

    public static void process(
            PostChain chain,
            RenderTarget mainTarget,
            GraphicsResourceAllocator resourceAllocator
    ) {
        FrameGraphBuilder frame = new FrameGraphBuilder();
        MutableTargetBundle bundle = new MutableTargetBundle();

        bundle.put(
                PostChain.MAIN_TARGET_ID,
                frame.importExternal("main", mainTarget)
        );

        addShadowDepthTarget(frame, bundle);

        chain.addToFrame(frame, mainTarget.width, mainTarget.height, bundle);
        frame.execute(resourceAllocator);
    }

    private static void addShadowDepthTarget(
            FrameGraphBuilder frame,
            MutableTargetBundle bundle
    ) {
        ShadowFrameState shadowState = ShadowFrameState.get();
        ShadowRenderTargetsLite targets = ShadowRenderTargetsLite.get();
        RenderTarget shadowTarget = targets.getShadowDepthTarget();

        if (!shadowState.isValid()) {
            return;
        }

        if (!shadowState.wasShadowDepthMirrored()) {
            return;
        }

        if (!targets.isReady() || shadowTarget == null) {
            return;
        }

        if (shadowTarget.getDepthTextureView() == null) {
            return;
        }

        bundle.put(
                PostFxExternalTargetIds.SHADOW_DEPTH,
                frame.importExternal("shadow_depth", shadowTarget)
        );
    }
}