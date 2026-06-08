# 睡眠 UI 呼吸感升级

## Why

当前 UI 已具备现代化图标与暗色主题，但布局仍偏紧凑，不够惬意。睡眠类应用需要更低认知负担、更松弛的视觉节奏和更清晰的焦点层级。

## What Changes

- 增加舒适布局 token：屏幕边距、顶部留白、页面段落间距、卡片内边距、底部安全留白。
- 调整 AppScaffold content padding、Mini Player 高度和底部区域间距。
- 放松 Home、Mixer、Library、Timer、Saved、Settings 的列表间距和卡片 padding。
- 提升 Home hero 的留白与主视觉尺寸，降低首屏拥挤感。
- 调整 Library 声音卡片比例和内部布局，让声音库更像浏览助眠素材，而不是密集工具网格。

## Non-goals

- 不新增页面或导航。
- 不改变播放、计时器、保存混音、音频资源或持久化逻辑。
- 不引入图片背景、营销页或大型视觉插画。
- 不删除现有 Mini Player 和 bottom navigation。
- 不做平台权限或发布流程变更。

## User Stories

- 作为睡前用户，我希望首页看起来更松弛，播放控件不显得拥挤。
- 作为常用用户，我希望声音库仍能快速浏览，但每张声音卡有足够空间辨认图标、名称和分类。
- 作为混音用户，我希望 Mixer 列表更易扫视，滑块和按钮不互相挤压。

## Functional Scope

- 只调整 Compose UI 布局 token 与现有 Composable 排版。
- 保持所有交互回调、状态模型、sound catalog 和 playback engine 不变。
- 为舒适布局增加单元测试覆盖关键 token 和底部 padding 计算。

## UI/UX Acceptance Criteria

- 全局横向屏幕边距至少 20dp。
- 页面顶部留白至少 24dp。
- Mini Player 高度至少 72dp。
- BottomNav + Mini Player 的内容底部预留能反映新增高度，不遮挡列表尾部。
- Home hero 内部 padding 至少 28dp，主 SoundIcon 至少 96dp。
- Library 声音卡片 aspect ratio 不低于 1.18，并使用更大的卡片内边距。
- 仍满足 44dp 触控目标。

## Technical Approach

- 在 `WnpSpacing` 和 `WnpDimens` 中新增语义 token。
- 将 AppScaffold 的 content padding 改为使用 token。
- 调整现有屏幕的 `Arrangement.spacedBy`、卡片 padding、hero spacing、SoundGridCard aspect ratio。
- 更新 `DesignTokenTest` 与 `AppShellTest`，先红灯再实现。

## State Model

无状态模型变化。所有变更只影响布局和视觉节奏。

## Platform Differences

- common Compose UI 生效于共享 UI。
- Android 端无需平台代码变更。
- HarmonyOS spike 未来可复用同一 shared UI token。

## Test Strategy

- common unit test：验证 comfort token、Mini Player 高度、BottomNav 高度和计算后的 scaffold bottom padding。
- 编译测试：`testDebugUnitTest` 捕捉 Compose API/布局代码编译问题。
- lint/build：`lintDebug` 与 `assembleDebug`。

## Acceptance

- OpenSpec strict validation 通过。
- 新增/更新的测试先失败后通过。
- `testDebugUnitTest`、`lintDebug`、`assembleDebug` 通过。
- UI 改动不触碰音频、持久化和发布签名逻辑。

