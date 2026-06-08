# Design: 媒体控制状态与音频中断

## 状态边界

`PlaybackEngine` 保留同步 `state` 属性供初始化和即时命令使用，同时新增：

```kotlin
val states: StateFlow<PlaybackState>
```

实现内部以 `MutableStateFlow` 作为唯一可变状态容器，`state` 返回 `states.value`。
这样不破坏现有调用者，又允许 common presentation 持续观察平台事件。

## AppStore 数据流

初始化协程按顺序执行：

1. 从 repository 恢复 snapshot。
2. 设置 `isInitialized` 并恢复 timer。
3. 收集 `playbackEngine.states`。
4. 当 `isPlaying` 改变时仅更新 `AppState.isPlaying`。

收集不触发持久化，因为播放状态是进程运行态，不属于 DataStore snapshot。初始
`StateFlow` 值会在恢复完成后立即校正 UI，避免 repository 恢复覆盖平台真实状态。

## Android 音频策略

所有 ExoPlayer 使用：

- usage: `C.USAGE_MEDIA`
- content type: `C.AUDIO_CONTENT_TYPE_MUSIC`

MediaSession controller player：

- `handleAudioFocus = true`
- `setHandleAudioBecomingNoisy(true)`
- volume 保持 `0f`

Layer player：

- `handleAudioFocus = false`
- 不单独处理 becoming noisy

controller player 因焦点丢失或 noisy 事件暂停时，现有
`Player.Listener.onIsPlayingChanged(false)` 调用
`AndroidPlaybackEngine.pauseFromMediaSession()`。engine 发射 Paused，AppStore 收集后
同步 UI。瞬时 audio focus 恢复时是否恢复由 Media3 的焦点策略决定；如果 controller
再次进入 playing，listener 会恢复 layer players 并同步 UI。becoming-noisy 触发的是
明确 pause，不会因输出设备变化自行恢复。

## 防递归

应用调用 `setNowPlaying()` 更新 controller player 时继续使用 `updatingFromEngine`
屏蔽 listener 回调。外部系统事件不设置该标记，因此会传播到 engine。

## 测试策略

- common unit：fake engine 的 `states` 发射 Playing/Paused/Idle。
- common unit：外部 engine pause/resume 自动更新 AppStore。
- Android unit：纯策略对象声明 controller/layer 的焦点与 noisy 规则。
- Android 编译验证：实际 ExoPlayer Builder/API 调用与 Media3 1.10.1 匹配。
- 完整回归：Gradle check、lint、debug APK、release AAB、签名校验和音频资产校验。

## 剩余风险

JVM 单测不能模拟真实厂商音频焦点栈。本 change 完成后仍需真实 Android 设备验证：

- 播放期间启动其他音乐/视频应用。
- 来电或语音通话中断。
- 有线耳机拔出。
- 蓝牙耳机断开。
- 通知/锁屏 pause 和 resume。
