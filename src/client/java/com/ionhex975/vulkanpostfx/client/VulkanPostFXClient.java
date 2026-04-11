package com.ionhex975.vulkanpostfx.client;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.input.PostFxDebugKeybinds;
import com.ionhex975.vulkanpostfx.client.pack.ActiveShaderPackManager;
import com.ionhex975.vulkanpostfx.client.reload.PostFxReloadHooks;
import com.ionhex975.vulkanpostfx.client.runtime.ActivePostEffectBridge;
import com.ionhex975.vulkanpostfx.client.state.PostFxRuntimeState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class VulkanPostFXClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		VulkanPostFX.LOGGER.info("[{}] Client bootstrap initialized", VulkanPostFX.MOD_ID);
		PostFxRuntimeState.markClientInit();

		ActiveShaderPackManager.bootstrap();
		if (ActiveShaderPackManager.getActivePack() != null) {
			PostFxRuntimeState.setActiveEffectKey(ActiveShaderPackManager.getActiveEffectKey());
		}

		ActivePostEffectBridge.refreshFromActivePack();

		PostFxDebugKeybinds.init();
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
				.registerReloadListener(PostFxReloadHooks.createReloadListener());

		VulkanPostFX.LOGGER.info("[{}] Debug key registered: F8 toggles debug post effect", VulkanPostFX.MOD_ID);
		VulkanPostFX.LOGGER.info("[{}] Resource reload listener registered", VulkanPostFX.MOD_ID);
	}
}