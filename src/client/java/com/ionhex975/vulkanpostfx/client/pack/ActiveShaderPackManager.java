package com.ionhex975.vulkanpostfx.client.pack;

import com.ionhex975.vulkanpostfx.VulkanPostFX;
import com.ionhex975.vulkanpostfx.client.effect.PostFxEffectRegistry;
import net.minecraft.client.Minecraft;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 当前活动光影包管理器。
 *
 * 当前阶段职责：
 * - 注册 pack source
 * - 发现可用包
 * - 选择活动包
 * - 暴露“当前入口效果 key”
 *
 * 目前策略：
 * - 默认激活 builtin pack
 * - 同时发现 zip 包并记录到日志
 *
 * 下一阶段会继续扩展：
 * - 解析 zip manifest
 * - 从 zip 中设置 entryEffectKey
 * - 切换活动包
 */
public final class ActiveShaderPackManager {
    private static final String SHADER_PACK_DIRECTORY_NAME = "shaderpacks";

    private static final List<ShaderPackSource> SOURCES = new ArrayList<>();

    private static ShaderPackContainer activePack;
    private static List<ShaderPackContainer> discoveredPacks = List.of();

    private ActiveShaderPackManager() {
    }

    public static void bootstrap() {
        SOURCES.clear();

        Path runDirectory = Minecraft.getInstance().gameDirectory.toPath();
        Path shaderPackDirectory = runDirectory.resolve(SHADER_PACK_DIRECTORY_NAME);

        SOURCES.add(new BuiltinShaderPackSource());
        SOURCES.add(new ZipShaderPackSource(shaderPackDirectory));

        List<ShaderPackContainer> discovered = new ArrayList<>();
        for (ShaderPackSource source : SOURCES) {
            discovered.addAll(source.discoverPacks());
        }

        discoveredPacks = List.copyOf(discovered);

        if (discovered.isEmpty()) {
            activePack = null;
            VulkanPostFX.LOGGER.warn("[{}] No shader packs discovered", VulkanPostFX.MOD_ID);
            return;
        }

        // 当前阶段先保持开发优先：默认激活 builtin 包。
        activePack = discovered.stream()
                .filter(pack -> BuiltinShaderPackSource.SOURCE_ID.equals(pack.sourceId()))
                .findFirst()
                .orElse(discovered.getFirst());

        VulkanPostFX.LOGGER.info(
                "[{}] Active shader pack set to '{}' from source '{}', entryEffectKey={}",
                VulkanPostFX.MOD_ID,
                activePack.manifest().name(),
                activePack.sourceId(),
                activePack.manifest().entryEffectKey()
        );

        logDiscoveredPacks();
    }

    public static ShaderPackContainer getActivePack() {
        return activePack;
    }

    public static List<ShaderPackContainer> getDiscoveredPacks() {
        return discoveredPacks;
    }

    public static String getActiveEffectKey() {
        if (activePack == null) {
            return PostFxEffectRegistry.DEBUG_INVERT;
        }

        String entryEffectKey = activePack.manifest().entryEffectKey();
        if (entryEffectKey == null || entryEffectKey.isBlank()) {
            return PostFxEffectRegistry.DEBUG_INVERT;
        }

        return entryEffectKey;
    }

    private static void logDiscoveredPacks() {
        for (ShaderPackContainer pack : discoveredPacks) {
            VulkanPostFX.LOGGER.info(
                    "[{}] Discovered shader pack: name='{}', id='{}', source='{}', path={}",
                    VulkanPostFX.MOD_ID,
                    pack.manifest().name(),
                    pack.manifest().id(),
                    pack.sourceId(),
                    pack.sourcePath()
            );
        }
    }
}