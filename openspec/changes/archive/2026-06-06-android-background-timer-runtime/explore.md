# Explore: Android 后台定时器运行时

## 当前状态

- commonMain 的 `AppStore` 已使用协程每秒更新计时器、淡出并在到点时停止播放。
- ticker 绑定 Compose 协程作用域，Activity 销毁后不再执行。
- Android 播放由 `AndroidPlaybackEngine` 持有多个 ExoPlayer，并通过 `WhiteNoiseMediaSessionService` 暴露系统媒体控制。
- `MainActivity.onDestroy()` 当前会释放播放引擎，不符合后台播放和后台 timer 的生命周期要求。

## 方案比较

### 方案 A：MediaSessionService 持有截止时间协调器

- AppStore 向平台 runtime 同步 `SleepTimerState`。
- Android runtime 把绝对开始时间、总时长和淡出时长交给 Service。
- Service 协程按系统时间计算 fade factor，并调用进程级 AndroidPlaybackEngine。
- Activity ticker 继续负责 UI，Service timer 负责后台播放安全，两者使用同一个绝对时间模型。

优点：

- 不增加权限。
- 与当前 Media3 架构一致。
- 可通过纯 Kotlin runner 做虚拟时间测试。

限制：

- 进程被系统强杀后不保证唤醒执行。

### 方案 B：WorkManager

不采用。WorkManager 不保证准点执行，且不适合每秒淡出。

### 方案 C：AlarmManager

本轮不采用。虽然可在进程退出后触发，但精确闹钟权限和 Google Play 政策成本不适合当前 MVP。

## 结论

采用方案 A。AndroidPlaybackEngine 改为 application-context 进程级单例，由 Service/应用进程共同持有；Activity 不再在 `onDestroy` 释放后台播放资源。
