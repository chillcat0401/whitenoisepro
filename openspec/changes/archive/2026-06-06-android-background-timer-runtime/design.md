# Design: Android 后台定时器运行时

## Architecture

`AppStore` 保持 UI 状态的唯一所有者，并继续运行页面可见期间的 ticker。它通过 `PlatformSleepTimerRuntime` 同步平台后台截止时间，但不读取平台内部状态。

`SleepTimerDeadlineRunner` 是纯 Kotlin 协程组件：

- 输入完整 `SleepTimerState`。
- 每秒使用注入的 epoch clock 重新计算剩余时间。
- 通过 callback 输出 fade factor。
- StopPlayback 到点时调用一次 stop callback，然后结束 Job。
- schedule 会取消旧 Job，cancel 会恢复 fade factor 为 1。

Android adapter 将 schedule/cancel 转发给 `WhiteNoiseMediaSessionService`。Service 使用自己的 `SupervisorJob + Dispatchers.Main.immediate`，因此 Activity/Compose 销毁不会取消后台 timer。

## Playback Ownership

`AndroidPlaybackEngineProvider` 使用 application context 创建一个进程级 engine。MainActivity 只获取引用，不在 `onDestroy` 释放。Service 销毁时取消自己的 timer job；engine 的最终释放由进程生命周期负责，显式 Stop 会停止播放器并清理媒体会话。

Activity 重建时，新 AppStore 读取进程级 engine 的 `PlaybackState.isPlaying`，避免实际播放与 Mini Player 图标不一致。

## State Consistency

- AppStore 和 Service 都基于 `startedAtEpochMillis + durationMillis` 计算，不累计 delay。
- 前台到点时两侧都可能调用 stop；`AndroidPlaybackEngine.stop()` 必须保持幂等。
- 平台 runtime 不写 DataStore，避免与 AppStore 持久化竞争。
- App 重建后，AppStore 从 DataStore 恢复 timer 并重新 schedule Service。

## Error Handling

- 非活动或零时长 timer 视为 cancel。
- schedule 新 timer 前取消旧 timer。
- Service 未创建时，Android adapter 通过 startService command 启动并提交 timer。
- 进程被杀属于明确非目标，下一阶段再评估 AlarmManager。

## Testing

- common tests：runner fade、completion、reschedule、cancel。
- AppStore tests：平台 runtime 的 schedule/cancel 调用。
- Android compile/lint：Service adapter、provider 和 Manifest 集成。
- 手动 QA：开始 1 分钟 timer，Home 键/熄屏后验证淡出停止；滑掉 Activity 后验证 Service 存活期间行为。
