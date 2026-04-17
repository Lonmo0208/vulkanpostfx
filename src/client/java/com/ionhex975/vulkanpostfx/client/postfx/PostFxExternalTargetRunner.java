package com.ionhex975.vulkanpostfx.client.postfx;

import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxCapabilityResolver;
import com.ionhex975.vulkanpostfx.client.pack.vpfx.VpfxRuntimeCapabilities;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.renderer.PostChain;

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

        VpfxRuntimeCapabilities caps =
                new VpfxCapabilityResolver().resolve();

        if (caps.isShadowDepth()) {
            // 未来恢复 shadow 时再接回
        }

        chain.addToFrame(frame, mainTarget.width, mainTarget.height, bundle);
        frame.execute(resourceAllocator);
    }
}