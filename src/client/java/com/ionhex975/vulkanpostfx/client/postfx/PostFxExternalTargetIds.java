package com.ionhex975.vulkanpostfx.client.postfx;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

import java.util.Set;

public final class PostFxExternalTargetIds {
    public static final Identifier SHADOW_DEPTH =
            Identifier.tryParse(VulkanPostFX.MOD_ID + ":shadow_depth");

    /**
     * 当前对外正式允许的 external targets。
     *
     * 主线收束阶段：
     * - 只保留 minecraft:main
     * - shadow_depth 常量仍保留给内部开发/未来功能，但暂不对外宣称已稳定支持
     */
    private static final Set<Identifier> ALLOWED = Set.of(
            PostChain.MAIN_TARGET_ID
    );

    private PostFxExternalTargetIds() {
    }

    public static Set<Identifier> allowedTargets() {
        return ALLOWED;
    }
}