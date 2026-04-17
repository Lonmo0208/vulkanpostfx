package com.ionhex975.vulkanpostfx.client.mixin;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.postfx.PostFxExternalTargetIds;
import com.ionhex975.vulkanpostfx.client.postfx.PostFxExternalTargetRunner;
import com.ionhex975.vulkanpostfx.client.runtime.zip.RuntimeZipPackState;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PostChain.class)
public abstract class PostChainProcessMixin {
    @Unique
    private static boolean vulkanpostfx$firstShadowInterceptLogged;

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
        PostChain self = (PostChain) (Object) this;
        Set<Identifier> externalTargets =
                ((PostChainAccessor) self).vulkanpostfx$getExternalTargets();

        // 只对“真的声明引用 shadow_depth 的 chain”做接管
        if (externalTargets == null || !externalTargets.contains(PostFxExternalTargetIds.SHADOW_DEPTH)) {
            return;
        }

        // 运行时 ZIP 包未激活时，不接管
        if (!RuntimeZipPackState.isActive()) {
            VulkanPostFX.LOGGER.warn(
                    "[{}] PostChain references external target {}, but runtime ZIP pack is not active; falling back to vanilla process()",
                    VulkanPostFX.MOD_ID,
                    PostFxExternalTargetIds.SHADOW_DEPTH
            );
            return;
        }

        if (!vulkanpostfx$firstShadowInterceptLogged) {
            vulkanpostfx$firstShadowInterceptLogged = true;
            VulkanPostFX.LOGGER.info(
                    "[{}] Intercepting PostChain.process only for chain(s) referencing external target {}",
                    VulkanPostFX.MOD_ID,
                    PostFxExternalTargetIds.SHADOW_DEPTH
            );
        }

        PostFxExternalTargetRunner.process(
                self,
                mainTarget,
                resourceAllocator
        );
        ci.cancel();
    }
}