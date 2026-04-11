package com.ionhex975.vulkanpostfx.client.reload;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.state.PostFxRuntimeState;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 监听客户端资源热重载。
 *
 * 当前不自己做重建，只负责在 reload 完成后通知运行时：
 * “下一次进入 PostFX 槽位时，把当前目标 effect 状态重新应用一遍。”
 */
public final class PostFxReloadHooks {
    private static final Identifier LISTENER_ID =
            Identifier.fromNamespaceAndPath("vulkanpostfx", "postfx_reload_restore");

    private PostFxReloadHooks() {
    }

    public static IdentifiableResourceReloadListener createReloadListener() {
        return new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return LISTENER_ID;
            }

            @Override
            public CompletableFuture<Void> reload(
                    PreparableReloadListener.SharedState currentReload,
                    Executor taskExecutor,
                    PreparableReloadListener.PreparationBarrier preparationBarrier,
                    Executor reloadExecutor
            ) {
                return CompletableFuture
                        .supplyAsync(() -> Boolean.TRUE, taskExecutor)
                        .thenCompose(preparationBarrier::wait)
                        .thenAcceptAsync(ignored -> {
                            PostFxRuntimeState.requestReapply();
                            VulkanPostFX.LOGGER.info(
                                    "[{}] Resource reload completed, requested PostFX state reapply",
                                    VulkanPostFX.MOD_ID
                            );
                        }, reloadExecutor);
            }
        };
    }
}