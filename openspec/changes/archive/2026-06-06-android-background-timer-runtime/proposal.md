# Android 后台定时器运行时

## Why

当前睡眠定时器只在 Compose 协程作用域内运行。用户离开 Activity 或系统重建界面后，后台媒体播放可能继续，但淡出和到点停止不再有可靠执行者。另外，`MainActivity.onDestroy()` 会释放实际播放引擎，与后台播放规格冲突。

## What Changes

- 新增 commonMain `PlatformSleepTimerRuntime` 边界。
- AppStore 在启动、延长、恢复、取消和完成 timer 时同步平台 runtime。
- 新增可用虚拟时间测试的 `SleepTimerDeadlineRunner`。
- Android `MediaSessionService` 持有 runner，并对 AndroidPlaybackEngine 应用 fade/stop。
- AndroidPlaybackEngine 改为 application-context 进程级单例。
- Activity 不再直接拥有或释放后台播放资源。
- Activity 重建后，AppStore 从进程级 PlaybackEngine 恢复当前播放标记。
- 更新后台播放与 timer 验证文档。

## Non-goals

- 进程被系统强杀后的精确唤醒。
- `AlarmManager`、`SCHEDULE_EXACT_ALARM` 或 `USE_EXACT_ALARM`。
- HarmonyOS 后台任务实现。
- 真实音频资产替换。

## Acceptance

- AppStore 启动、延长和恢复活动 timer 时向平台 runtime 提交最新状态。
- 选择新 preset、取消或完成 timer 时取消平台 runtime。
- Deadline runner 根据绝对时间计算 fade factor，并在 StopPlayback 到点时只停止一次。
- Activity 销毁不释放进程级 AndroidPlaybackEngine。
- Service 销毁时取消 timer runner。
- 完整 check、lint、unit tests 和 debug APK 构建成功。
