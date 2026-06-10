## Why

当前 release UI 已有现代化暗色基调，但页面背景仍偏纯色，和参考图中的柔和光影层次相比缺少空间感。同时应用内容顶部离系统状态栏偏近，睡前使用时显得紧张。

## What Changes

- 参考 `/Volumes/Volumes2T/ChromeDownloads/cropped-*.png` 的深色冷光背景，为应用统一背景增加低对比、不可交互的光影效果。
- 将主要内容顶部整体下移一点，给状态栏和标题之间留出更舒适的 breathing room。
- 保持现有 tab、布局结构、音频播放和核心工作流不变。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `design-system`: 扩展 release-ready visual rhythm，要求背景光影和顶部安全间距适配状态栏且不影响可读性。

## Impact

- 影响 `AppScaffold` 或 design 组件中的统一背景渲染。
- 影响 design token 中的顶部 padding / spacing。
- 需要更新设计 token / scaffold 相关单元测试。
- 不新增依赖，不引入位图背景资源，不改音频、catalog、release 签名或 Play Console 文档。
