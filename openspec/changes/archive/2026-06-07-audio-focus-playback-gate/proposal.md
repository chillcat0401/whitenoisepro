# 音频焦点播放闸门

## Why

当前 audible layer 在 MediaSession controller 获得 audio focus 前启动。Android 15
焦点请求可能因应用不在顶部且未处于前台服务而失败，此时 controller 不播放，但 layer
仍可能输出，违反音频焦点要求并带来意外外放风险。

## User Stories

- 用户点击播放后，只有 Android 授权 controller 实际播放时才听到声音。
- 焦点被其他媒体或通话临时占用时，所有 layer 同步暂停并在 Media3 授权恢复后同步恢复。
- 焦点请求失败或 player 尚未 ready 时，应用显示非播放状态且不输出声音。
- 用户显式暂停或停止后，延迟 callback 不会重新启动 layer。

## What Changes

- 增加可测试的 controller playback decision：Start、Await、Pause。
- Android engine 的 `play()` 只准备 layer，状态进入 Buffering，不直接调用 layer `play()`。
- controller listener 根据 `isPlaying` 和 `playWhenReady` 驱动 engine。
- Await 状态暂停全部 layer 并保持 Buffering。
- Start 状态只恢复当前未静音 layer，并发射 Playing。
- common AppStore 去除乐观播放状态，完全服从 `PlaybackEngine.states`。
- 更新 Android 15 和真实设备焦点验证清单。

## Non-goals

- 自定义 AudioManager 或 delayed focus request。
- UI 视觉调整。
- 新增错误弹窗。
- HarmonyOS 实现。

## Acceptance

- controller 未 `isPlaying` 时所有 layer 保持暂停。
- `playWhenReady=true` 且 controller 未实际播放时 engine 为 Buffering。
- controller 实际播放后才启动未静音 layer并发射 Playing。
- 显式 pause/stop 后不会由旧 callback 恢复。
- AppStore 不再在 play 命令后直接写 `isPlaying=true`。
- debug/release tests、lint、APK、签名 AAB、资产和 strict validation 通过。
