# 媒体控制状态与音频中断

## Why

当前系统媒体控制和音频中断可以改变 Android 真实播放状态，但 common `AppStore`
不会持续观察 `PlaybackEngine`，前台 UI 可能显示错误的播放图标。多个 layer player
也尚未明确 audio focus 所有权和耳机断开行为。这些问题会直接影响后台播放体验和
Google Play 上架后的稳定性。

## User Stories

- 用户从通知或锁屏暂停后，应用内 Mini Player 和播放按钮同步显示暂停。
- 用户从系统媒体控制恢复后，应用内 UI 同步显示播放。
- 其他应用取得音频焦点或耳机断开时，白噪音安全暂停而不是从扬声器意外外放。
- 多层混音继续保持现有音量和循环行为，不因多个 ExoPlayer 重复竞争焦点。

## What Changes

- `PlaybackEngine` 增加只读 `StateFlow<PlaybackState>`。
- fake、测试替身和 Android engine 在状态变化时同步发射。
- `AppStore` 初始化后用现有协程 scope 收集 engine 状态并更新 `isPlaying`。
- MediaSession controller player 成为唯一自动管理 audio focus 和 becoming noisy 的 player。
- layer players 设置媒体 audio attributes，但不各自管理 audio focus。
- 更新 Android 播放验证文档和代码审查记录。

## Non-goals

- 不新增或调整 UI。
- 不替换 MediaSession controller player。
- 不改变保存混音、DataStore schema 或 timer 数据模型。
- 不实现音频 ducking 自定义曲线。
- 不实现 HarmonyOS 版本。

## Acceptance

- 系统侧 pause/resume 导致 engine 状态变化后，`AppStore.state.isPlaying` 自动同步。
- `PlaybackEngine.states` 始终包含最新 `PlaybackState`。
- controller player 使用 media/music attributes、自动 audio focus 和 becoming noisy。
- layer players 使用 media/music attributes且不独立请求 audio focus。
- 原有播放、混音、timer、持久化测试保持通过。
- debug/release 单测、lint、APK、签名 AAB、资产验证和 OpenSpec strict validation 通过。
