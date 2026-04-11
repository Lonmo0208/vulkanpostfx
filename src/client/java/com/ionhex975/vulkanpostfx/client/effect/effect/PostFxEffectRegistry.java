package com.ionhex975.vulkanpostfx.client.effect;

import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 最小效果注册表。
 *
 * 当前先支持：
 * - 按逻辑名注册效果
 * - 查询效果定义
 * - 记录“当前调试效果”的逻辑名
 *
 * 后面可以继续扩展成：
 * - 多效果切换
 * - 菜单展示
 * - 分类与排序
 */
public final class PostFxEffectRegistry {
    public static final String DEBUG_INVERT = "debug_invert";

    private static final Map<String, PostFxEffectDefinition> EFFECTS = new LinkedHashMap<>();

    static {
        register(
                DEBUG_INVERT,
                new PostFxEffectDefinition(
                        Identifier.fromNamespaceAndPath("vulkanpostfx", "debug_invert"),
                        Identifier.withDefaultNamespace("invert"),
                        "Debug Invert"
                )
        );
    }

    private PostFxEffectRegistry() {
    }

    public static void register(String key, PostFxEffectDefinition definition) {
        EFFECTS.put(key, definition);
    }

    public static PostFxEffectDefinition get(String key) {
        return EFFECTS.get(key);
    }

    public static Collection<PostFxEffectDefinition> all() {
        return EFFECTS.values();
    }
}