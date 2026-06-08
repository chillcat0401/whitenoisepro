# Design: 睡眠 UI 呼吸感升级

## Layout Rhythm

新增语义 token：

- `ScreenTop = 24.dp`
- `ScreenHorizontal = 20.dp`
- `ScreenBottomWithPlayer` 使用 Mini Player、BottomNav 和额外呼吸留白计算
- `PageGap = 24.dp`
- `SectionGap = 20.dp`
- `CardPadding = 18.dp`
- `HeroPadding = 30.dp`

这些 token 让布局意图可测试，也避免在页面中散落 magic numbers。

## Scaffold

`AppScaffold` 的 content padding 从硬编码改为：

- start/end: `WnpSpacing.ScreenHorizontal`
- top: `WnpSpacing.ScreenTop`
- bottom: `WnpSpacing.ScreenBottomWithPlayer`

Mini Player 高度从 64dp 调整为 76dp；BottomNav 保持清晰图标和文字，但高度提升到 68dp。Mini Player 与 BottomNav 之间增加 8dp 间距，让两个底部控件不再粘连。

## Screens

Home：

- 页面 item 间距使用 `PageGap`。
- Hero 内 padding 增加，主图标放大到 104dp。
- Hero 内部主要段落间距从 12-16dp 提升到 18-24dp。
- 最近使用卡片和推荐声音保持可扫视，但卡片 padding 增加。

Mixer：

- 列表间距提升到 `SectionGap`。
- LayerRow padding 增加，滑块与操作按钮间距更宽。
- 主要按钮保持全宽，但上下间距更舒缓。

Library：

- 搜索、分类、声音网格之间使用更大间距。
- `SoundGridCard` aspect ratio 提升到 1.22。
- 卡片内部 padding 使用 `CardPadding`，图标和文字有更多分隔。

Timer / Saved / Settings：

- 页面列表间距整体提升。
- Timer 大数字面板 padding 增加到 36dp。
- Saved/Mix card 内部 padding 增加，编辑区域与主行之间更舒缓。
- Settings row 高度从 56dp 提升到 64dp。

## Testing

TDD 覆盖：

- `DesignTokenTest` 验证 comfort spacing 与 dimensions。
- `AppShellTest` 验证 `scaffoldContentPadding()` 的 bottom padding 随 Mini Player / BottomNav 高度更新。

实现时将 padding 计算提取为纯函数，避免只能通过截图验证。

