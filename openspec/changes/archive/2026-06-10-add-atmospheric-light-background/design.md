## Context

参考图使用深蓝黑背景、柔和纵向光晕和低对比卡片内部亮部来制造空间感；当前 Compose UI 背景主要是 `WnpColors.Background` 纯色，页面标题从 `WnpSpacing.ScreenTop` 开始，和 Android 状态栏之间的呼吸感不足。

## Goals / Non-Goals

**Goals:**

- 在所有 AppScaffold 页面统一增加柔和光影背景。
- 将页面内容顶部下移，避免标题紧贴状态栏。
- 保持背景可控、轻量、离线、无额外图片依赖。
- 保持文字和控件对比度，不让光影抢内容。

**Non-Goals:**

- 不按参考图重做页面布局或导航。
- 不加入新的品牌插画、远程图片、动画或 heavy canvas。
- 不修改 release 音频、store 文案或 Play Console 资料。

## Decisions

- 使用 Compose `Brush` / `drawBehind` 在统一背景层绘制光影，而不是引入 PNG 背景图。理由：可响应任意屏幕尺寸、包体不增加、暗色主题下更容易控制透明度。
- 背景光影放在 `AppScaffold` 根容器，内容和底部 chrome 仍按原有结构渲染。理由：所有主 tab 一次覆盖，避免逐页不一致。
- 通过 `ScreenTop` token 增加顶部 breathing room。理由：所有页面都依赖 `scaffoldContentPadding()`，改 token 比逐页加 padding 更稳。
- 光影色使用现有 primary / cool dark family 的低 alpha 变体。理由：贴近参考图冷色空气感，同时不让 palette 重新回到单一蓝色主题。

## Risks / Trade-offs

- 背景过亮会降低文字对比度 -> 使用低 alpha radial / vertical gradient，并用截图验证首页、声音库和设置页。
- 顶部 padding 增加会减少首屏内容 -> 只增加少量 dp，并保留 bottom padding 不变。
- 某些 Compose target 对复杂 brush 性能敏感 -> 使用静态背景层，不做动画，不在列表 item 内重复绘制。
