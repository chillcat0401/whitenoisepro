# Explore: 媒体控制状态与音频中断

## 背景

现有 Android 实现用多个 ExoPlayer 输出混音，并用一个静音的 MediaSession
controller player 暴露系统播放控制。系统通知、锁屏或音频焦点导致 controller
player 暂停时，`AndroidPlaybackEngine` 能暂停真实图层，但 `AppStore` 只读取同步
属性 `PlaybackEngine.state`，不会持续观察变化，因此前台播放按钮可能继续显示播放中。

当前所有 layer player 也没有显式声明 Media3 audio attributes。项目需要在上架前明确
电话、其他媒体抢占焦点以及耳机断开时的安全行为。

## 目标

- 系统媒体控制改变播放状态后，Compose UI 自动同步。
- Android 播放声明媒体用途并处理 audio focus。
- 耳机或蓝牙输出断开导致 audio becoming noisy 时自动暂停。
- 保持现有多 ExoPlayer 混音架构和 MediaSession 静音 controller 方案。

## 方案比较

### A. Activity 生命周期轮询

前台恢复时读取 `PlaybackEngine.state`。

优点：改动少。  
缺点：前台期间的系统控制仍不同步，存在竞态，且不能自然覆盖音频焦点事件。

### B. StateFlow + 单一焦点所有者

`PlaybackEngine` 暴露 `StateFlow<PlaybackState>`，`AppStore` 用协程持续收集。
MediaSession controller player 设置 media audio attributes、自动 audio focus 和
audio-becoming-noisy；layer players 使用相同 attributes，但不各自请求焦点。

优点：状态单向传播，符合现有协程架构，避免多个 player 竞争焦点。  
缺点：controller player 仍作为控制代理存在，需要维持 engine/session 防递归保护。

### C. 重构为自定义 Player/单一混音管线

让 MediaSession 直接持有真实混音 player。

优点：系统状态来源更统一。  
缺点：需要重写多层播放、音量和 session command 映射，当前上架阶段风险过高。

## 决策

采用方案 B。Media3 1.10.1 文档确认：

- `ExoPlayer.Builder.setAudioAttributes(attributes, handleAudioFocus)` 可配置自动焦点处理。
- `ExoPlayer.setHandleAudioBecomingNoisy(true)` 可在输出设备断开时自动暂停。
- `Player.Listener.onIsPlayingChanged` 可观察实际播放状态变化。

## 非目标

- 不重写当前多播放器混音架构。
- 不实现电话状态监听或厂商私有音频 API。
- 不新增 UI 页面或设计变更。
- 不承诺进程被强杀后恢复正在播放的音频。
- 不在本 change 内实现 HarmonyOS 音频焦点适配。
