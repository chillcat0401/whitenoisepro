# 代码审查：放松睡眠 UI 节奏

**变更：** `relax-sleep-ui-rhythm`

**审查日期：** 2026-06-08

**审查范围：** `WnpSpacing` / `WnpDimens` 舒适布局 token、`AppScaffold` content padding 纯函数、Mini Player / BottomNav 间距，以及 Home、Mixer、Library、Timer、Saved、Settings 的页面节奏调整。

## 结论

未发现阻断归档的问题。变更保持在设计系统和页面布局层，没有改动音频播放、状态持久化、权限或发布签名路径。

## 关注点

- `scaffoldContentPadding()` 将底部 padding 计算集中到纯函数，并由测试锁定 Mini Player、BottomNav 和额外 breathing room 的组合，降低底部遮挡回归风险。
- 主要屏幕从紧凑 spacing 改为 `PageGap` / `SectionGap` / `CardPadding`，与睡前浏览目标一致；后续仍建议在 360x800、390x844、430x932 真机或截图上做视觉 QA。
- Mini Player 高度提升到 76dp、BottomNav 到 68dp，触控目标不低于既有 44dp 要求；页面 bottom padding 已随之更新。

## 验证证据

- OpenSpec tasks 全部完成。
- `DesignTokenTest` 和 `AppShellTest` 覆盖新增 token 与 bottom padding 公式。
- `testDebugUnitTest`、`lintDebug`、`assembleDebug` 和 OpenSpec strict validation 已在执行阶段通过；PostHog 证书错误仅来自 CLI telemetry flush，不影响 validation 结果。
