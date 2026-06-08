# Design: 音频焦点播放闸门

## Controller Decision

新增纯策略：

```kotlin
enum class ControllerPlaybackDecision {
    StartLayers,
    AwaitAuthorization,
    PauseLayers,
}
```

决策规则：

- `isPlaying=true` -> `StartLayers`
- `isPlaying=false && playWhenReady=true` -> `AwaitAuthorization`
- `isPlaying=false && playWhenReady=false` -> `PauseLayers`

这覆盖 player 未 ready、buffering、audio focus suppression 和明确 pause。

## Engine 生命周期

`AndroidPlaybackEngine.play(mix)`：

1. 保存 mix，创建/更新 layer player 和音量。
2. 所有 layer 保持 pause。
3. engine 发射 Buffering。
4. 请求 MediaSession controller play。

controller decision：

- Start：若当前仍有 pending play intent，启动未静音 layer，发射 Playing。
- Await：暂停全部 layer；若仍有 pending play intent，发射 Buffering。
- Pause：清除 pending play intent，暂停全部 layer，发射 Paused。

`stop()` 清除 pending play intent并发射 Idle。任何 callback 在没有 pending mix/play intent
时都不能启动 layer。

## MediaSession Listener

service 同时监听：

- `onIsPlayingChanged`
- `onPlayWhenReadyChanged`
- `onPlaybackSuppressionReasonChanged`
- `onPlaybackStateChanged`

每次事件都读取 controller 当前快照并调用同一 decision 函数。同步 engine 发出的
pause/stop 继续使用 callback suppression，避免把 Idle 改回 Paused；play 请求不屏蔽，
确保后续 ready/focus 事件能开启 layer。

## AppStore

删除 `TogglePlayback`、`PlaySavedMix` 和 `StartTimer` 中直接设置 `isPlaying=true` 的代码。
UI 仅由 `PlaybackEngine.states` 更新。Android engine 的 Buffering 映射为
`isPlaying=false`；fake/测试 engine 可同步发射 Playing，因此 common 行为保持确定。

## Android 15

本 change 不绕过 Android 15 限制。首次播放由前台 Activity 发起；后台媒体恢复由
MediaSessionService 处理。若系统仍拒绝 focus，controller 不进入 `isPlaying`，layer
闸门保持关闭。

## 测试

- 策略单测覆盖 Start/Await/Pause。
- engine lifecycle 通过提取的纯 gate state 或策略函数覆盖 pending intent、stop 和 mute。
- AppStore 单测验证不乐观更新。
- Android 编译验证 Media3 listener API。
- 真机验证 Android 12、14、15：其他媒体、来电、有线/蓝牙断开、通知恢复。
