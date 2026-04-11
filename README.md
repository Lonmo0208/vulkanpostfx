# Vulkan PostFX

Minecraft 26.2 snapshot 官方 Vulkan 后端上的最小后处理概念验证项目。

## 当前阶段

Phase 0 / 启动与挂点验证：

1. 模组能正常加载。
2. 客户端主循环 Mixin 命中。
3. 能记录当前图形后端信息。
4. 为后续 full-screen pass（全屏后处理通道）准备统一桥接入口。

## 下一步

- 从 Minecraft 主世界渲染入口中找到更细的 pass 边界。
- 把当前的 run() 级别挂点收缩到真正的渲染阶段。
- 确认能否拿到颜色缓冲、深度缓冲或最终输出目标。
- 再做第一版 Vulkan PostFX pass。