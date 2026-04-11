package com.ionhex975.vulkanpostfx.client.input;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.state.PostFxRuntimeState;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class PostFxDebugKeybinds {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("vulkanpostfx", "general"));

    private static final String TOGGLE_DEBUG_EFFECT_KEY = "key.vulkanpostfx.toggle_debug_effect";

    private static KeyMapping toggleDebugEffectKey;
    private static boolean toggleKeyWasDownLastTick;

    private PostFxDebugKeybinds() {
    }

    public static void init() {
        toggleDebugEffectKey = new KeyMapping(
                TOGGLE_DEBUG_EFFECT_KEY,
                GLFW.GLFW_KEY_F8,
                CATEGORY
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isDownNow = toggleDebugEffectKey.isDown();

            // 只在“这一帧按下、上一帧没按下”的上升沿触发一次
            if (isDownNow && !toggleKeyWasDownLastTick) {
                boolean enabled = PostFxRuntimeState.toggleDebugEffectEnabled();
                VulkanPostFX.LOGGER.info(
                        "[{}] Debug post effect toggled: {}",
                        VulkanPostFX.MOD_ID,
                        enabled ? "ON" : "OFF"
                );
            }

            toggleKeyWasDownLastTick = isDownNow;
        });
    }

    public static KeyMapping getToggleDebugEffectKey() {
        return toggleDebugEffectKey;
    }
}