# Code Review: Android 后台定时器运行时

日期：2026-06-06

## Findings

未发现阻塞当前变更的代码问题。

审查期间修复：

- Activity 重建后实际播放与 Mini Player 状态不一致。
- Service 独立完成 timer 后仍保留过期 `pendingTimer`。
- AndroidPlaybackEngine 原先由 Activity 持有并在 `onDestroy` 释放，不符合后台播放生命周期。
- Service 原先通过静态 `activeEngine` 引用回调播放；已改为 Service 实例读取进程级 provider。

## Tests

- `AppStoreTest` 覆盖 start、extend、restore、preset replacement、cancel 与平台 runtime 同步。
- `AppStoreTest` 覆盖有无 DataStore snapshot 时恢复进程级播放状态。
- `SleepTimerDeadlineRunnerTest` 覆盖绝对时间淡出、到点停止一次、completion 一次、reschedule 和 cancel。

## Spec Alignment

- commonMain 不依赖 Android 或 Media3。
- AppStore 继续持有 UI timer state，并通过平台边界同步后台截止时间。
- Service 使用独立 `SupervisorJob`，不受 Compose scope 取消影响。
- Android engine 使用 application context 的进程级单例。
- 未增加 AlarmManager、精确闹钟权限或新的 Manifest 权限。
- `MediaSessionService` 保持 `exported=true` 供系统媒体控制接入，并对该已审查场景做定点 lint 说明。

## Remaining Risks

- 进程被系统强杀或用户 force-stop 后不保证到点执行。
- 尚未在真实 Android 设备完成一整轮锁屏、滑掉任务和一至五层播放 timer QA。
- Android engine 仍使用测试静音 loop；真实音频淡出质量需要资产到位后复测。
- Service 与 AppStore 都可能在同一截止时间调用幂等 stop；当前实现安全，但后续若 stop 引入非幂等副作用必须重新审查。

## Verification

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home \
./gradlew :composeApp:check :composeApp:assembleDebug
```

结果：`BUILD SUCCESSFUL`。

- 120 tests，0 failures，0 errors。
- Android lint 完成；本次相关 `ExportedService` 与 `StaticFieldLeak` 警告已处理。
- 剩余 lint warning 为依赖可更新、缺少正式 launcher icon 和既有 KTX 建议。
- Debug APK：`composeApp/build/outputs/apk/debug/composeApp-debug.apk`。

OpenSpec：

```bash
DO_NOT_TRACK=1 openspec validate android-background-timer-runtime \
  --type change --strict --no-interactive
```

结果：change valid。
