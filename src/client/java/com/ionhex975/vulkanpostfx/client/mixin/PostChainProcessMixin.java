package com.ionhex975.vulkanpostfx.client.mixin;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.postfx.MutableTargetBundle;
import com.ionhex975.vulkanpostfx.client.postfx.PostFxExternalTargetIds;
import com.ionhex975.vulkanpostfx.client.shadow.ShadowFrameState;
import com.ionhex975.vulkanpostfx.client.shadow.ShadowRenderTargetsLite;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PostChain.class)
public abstract class PostChainProcessMixin {

    private static boolean firstShadowBundleLogged;

    @Inject(
            method = "process",
            at = @At("HEAD"),
            cancellable = true
    )
    private void vulkanpostfx$processWithExternalTargets(
            RenderTarget mainTarget,
            GraphicsResourceAllocator resourceAllocator,
            CallbackInfo ci
    ) {
        FrameGraphBuilder frame = new FrameGraphBuilder();
        MutableTargetBundle bundle = new MutableTargetBundle();

        bundle.put(
                PostChain.MAIN_TARGET_ID,
                frame.importExternal("main", mainTarget)
        );

        ShadowFrameState shadowState = ShadowFrameState.get();
        ShadowRenderTargetsLite targets = ShadowRenderTargetsLite.get();
        RenderTarget shadowTarget = targets.getShadowDepthTarget();

        if (!firstShadowBundleLogged) {
            firstShadowBundleLogged = true;
            VulkanPostFX.LOGGER.info(
                    "[{}] Added runtime shadow target to PostChain.process bundle: id={}, size={}x{}",
                    VulkanPostFX.MOD_ID,
                    PostFxExternalTargetIds.SHADOW_DEPTH,
                    shadowTarget.width,
                    shadowTarget.height
            );
        }

        ((PostChainAccessor) (Object) this).vulkanpostfx$invokeAddToFrame(
                frame,
                mainTarget.width,
                mainTarget.height,
                bundle
        );

        frame.execute(resourceAllocator);
        ci.cancel();
    }
}