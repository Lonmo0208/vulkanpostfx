package com.ionhex975.vulkanpostfx.client.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.pack.ActiveShaderPackManager;
import com.ionhex975.vulkanpostfx.client.pack.ShaderPackContainer;
import com.ionhex975.vulkanpostfx.client.pack.ZipShaderPackReader;

/**
 * 当前活动入口后处理桥。
 *
 * 当前阶段职责：
 * - 读取当前活动包的 entry_post_effect
 * - 如果是 ZIP 包，则把 JSON 文本读出来
 * - 做基础 JSON 校验
 * - 缓存到运行时，供下一阶段桥接 ShaderManager 使用
 */
public final class ActivePostEffectBridge {
    private static ActivePostEffectSource activeSource = ActivePostEffectSource.NONE;

    private ActivePostEffectBridge() {
    }

    public static void refreshFromActivePack() {
        ShaderPackContainer activePack = ActiveShaderPackManager.getActivePack();
        if (activePack == null) {
            activeSource = ActivePostEffectSource.NONE;
            VulkanPostFX.LOGGER.warn("[{}] No active shader pack; active post effect source cleared", VulkanPostFX.MOD_ID);
            return;
        }

        String entryPostEffect = activePack.manifest().entryPostEffect();
        if (entryPostEffect == null || entryPostEffect.isBlank()) {
            activeSource = ActivePostEffectSource.NONE;
            VulkanPostFX.LOGGER.warn(
                    "[{}] Active shader pack '{}' does not declare entry_post_effect",
                    VulkanPostFX.MOD_ID,
                    activePack.manifest().name()
            );
            return;
        }

        if ("builtin".equals(activePack.sourceId())) {
            activeSource = new ActivePostEffectSource(
                    "builtin",
                    entryPostEffect,
                    ""
            );

            VulkanPostFX.LOGGER.info(
                    "[{}] Active post effect source prepared from builtin pack: {}",
                    VulkanPostFX.MOD_ID,
                    entryPostEffect
            );
            return;
        }

        if ("zip".equals(activePack.sourceId())) {
            try {
                String rawJson = ZipShaderPackReader.readText(activePack.sourcePath(), entryPostEffect);
                validateJson(rawJson);

                activeSource = new ActivePostEffectSource(
                        "zip",
                        activePack.sourcePath() + "!/" + entryPostEffect,
                        rawJson
                );

                VulkanPostFX.LOGGER.info(
                        "[{}] Active post effect source loaded from zip: {} ({} chars)",
                        VulkanPostFX.MOD_ID,
                        activeSource.displayPath(),
                        rawJson.length()
                );
                return;
            } catch (Exception e) {
                activeSource = ActivePostEffectSource.NONE;
                VulkanPostFX.LOGGER.error(
                        "[{}] Failed to load active ZIP post effect source from '{}'",
                        VulkanPostFX.MOD_ID,
                        entryPostEffect,
                        e
                );
                return;
            }
        }

        activeSource = ActivePostEffectSource.NONE;
        VulkanPostFX.LOGGER.warn(
                "[{}] Unsupported shader pack source '{}'; active post effect source cleared",
                VulkanPostFX.MOD_ID,
                activePack.sourceId()
        );
    }

    public static ActivePostEffectSource getActiveSource() {
        return activeSource;
    }

    private static void validateJson(String rawJson) {
        JsonElement element = JsonParser.parseString(rawJson);
        if (!element.isJsonObject()) {
            throw new IllegalStateException("entry_post_effect root must be a JSON object");
        }
    }
}