# 媒体控制状态与音频中断设计

## 目标

让系统媒体控制、音频焦点和耳机断开产生的播放状态变化可靠传播到 Compose UI，同时
保持现有多层 ExoPlayer 混音架构。

## 决策

采用 `StateFlow + 单一音频焦点所有者`：

- `PlaybackEngine` 暴露最新状态流。
- `AppStore` 使用已有协程 scope 收集状态。
- 静音 MediaSession controller player 负责 audio focus 与 becoming noisy。
- 实际 layer players 只负责声音输出，不重复请求 audio focus。

不采用 Activity 生命周期轮询，因为前台系统事件仍可能不同步；不重写为自定义单一
Player，因为会扩大上架前的回归范围。

## 数据流

系统 pause/noisy/focus loss -> controller `onIsPlayingChanged(false)` ->
`AndroidPlaybackEngine.pauseFromMediaSession()` -> `StateFlow(Paused)` ->
`AppStore` -> Mini Player 和页面播放按钮。

系统显式 play -> controller `onIsPlayingChanged(true)` ->
`AndroidPlaybackEngine.resumeFromMediaSession()` -> `StateFlow(Playing)` ->
`AppStore`。

瞬时 audio focus 恢复遵循 Media3 默认策略；becoming-noisy 产生明确 pause，必须由用户
再次发出 play 才恢复。

## 验收

- common 与 Android 单测覆盖状态流、AppStore 同步和策略。
- Media3 1.10.1 编译通过。
- 完整构建、lint、签名和资产验证通过。
- 真实设备音频焦点/耳机断开保留为发布检查项。
