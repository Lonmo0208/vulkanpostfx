package com.ionhex975.vulkanpostfx.client.reload;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.runtime.ActivePostEffectBridge;
import com.ionhex975.vulkanpostfx.client.state.PostFxRuntimeState;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 监听客户端资源热重载。
 *
 * 当前阶段职责：
 * - reload 完成后通知运行时重新应用当前 effect 状态
 * - 同时刷新当前活动包的 entry_post_effect 读取结果
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
                            ActivePostEffectBridge.refreshFromActivePack();
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