# Explore: 睡眠定时器运行时闭环

日期：2026-06-06

## 问题

- Timer reducer 支持 Tick，但 AppStore 没有 ticker。
- TimerPlaybackCoordinator 未接入 AppStore。
- UI 点击开始后 remainingMillis 不会减少。
- fade factor 不会随时间应用。
- 到点不会停止 PlaybackEngine。
- active timer Extend 将 duration 设置为 remaining + extension，下一 tick 会错误扣除已过去时间。

## 方案

1. Composable `LaunchedEffect` ticker：生命周期易受页面切换影响，不采用。
2. Android service 内计时：后台最强，但把 common timer 逻辑绑到 Android，当前范围过大。
3. AppStore CoroutineScope ticker：与现有 common 状态/协程架构一致，页面切换不影响，推荐。

本轮采用 3。进程被系统杀死后的后台精确定时属于后续 service/alarm 级增强。

