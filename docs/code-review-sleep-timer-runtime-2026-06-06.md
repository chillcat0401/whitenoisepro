# Code Review: 睡眠定时器运行时闭环

日期：2026-06-06

## Findings

未发现阻塞当前 MVP 的问题。

审查中确认并修复：

- 活动计时器延长后，下一次 Tick 会丢失一部分延长时长。
- AppStore 原先没有运行 Tick 协程，Timer UI 只保存状态，不会真实倒计时、淡出或停止播放。
- Tick 不写入 DataStore，仅在选择、启动、延长、取消和完成等状态边界保存。

## Tests

- `TimerReducerTest` 覆盖活动计时器延长后的下一次 Tick。
- `AppStoreTest` 使用协程虚拟时间覆盖倒计时、淡出、到点停止、取消和写入频率。
- `AppStoreTest` 覆盖恢复活动计时器时按绝对时间重算剩余时长，且不自动启动音频。

## Spec Alignment

- StartTimer 会在需要时启动当前 mix。
- 运行中的 timer 每秒根据 `AppClock` 更新，而不是依赖累计 delay 计算剩余时间。
- 进入 fade window 后通过 `TimerPlaybackCoordinator` 更新 fade factor。
- StopPlayback 到点后停止 PlaybackEngine，并同步 `AppState.isPlaying=false`。
- Cancel 会取消 ticker、恢复 fade factor 为 1，并保存最终状态。

## Remaining Risks

- ticker 属于 App/Compose 协程作用域。Activity 或进程被销毁后，MediaSessionService 仍可能继续播放，但本次 ticker 不再保证准点停止。
- 真正的后台可靠计时需要单独 OpenSpec：Android 将截止时间交给 MediaSessionService 或系统调度；HarmonyOS 需要对应后台任务方案。
- 淡出平滑度和真实音频到点停止仍需 Android 真机长时间测试。

## Verification

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home \
./gradlew :composeApp:check :composeApp:assembleDebug
```

结果：`BUILD SUCCESSFUL`，包含 debug/release unit tests、Android lint 和 debug APK 构建。

产物：

`composeApp/build/outputs/apk/debug/composeApp-debug.apk`
