# 规格与实现回归

## Why

归档审计发现部分早期 spec 未被后续 change 正确修订，同时若干已归档核心能力只存在 reducer 或文档，没有完整接入 AppStore/UI。Android MediaSession 控制播放器还可能给真实混音叠加额外棕噪声。

## User Stories

- 用户可以在 Mixer 静音、恢复和移除声音层。
- 用户可以收藏和重命名保存的混音。
- 用户可以选择 2 小时或输入自定义睡眠定时。
- 用户在 Mini Player 中能看到活动 timer 的剩余时间。
- 用户添加声音时使用目录定义的默认音量。
- 系统媒体控制不会产生当前混音之外的可听声音。

## What Changes

- 新增 AppStore intent 和 reducer 行为，接入 Mixer 与 Saved Mixes。
- Timer 接入 120 分钟预设和自定义分钟数。
- Mini Player subtitle 显示活动 timer。
- Sound metadata 增加 loop asset key 和 default volume。
- MediaSession controller ExoPlayer 固定静音。
- 修订 Settings、通知说明和多层播放场景的冲突规格。
- 添加规格追踪审计报告和回归测试。

## Non-goals

- 自定义 fade 时长或 KeepPlaying UI。
- Billing、Restore Purchases 或离线下载。
- 新增声音资产。
- 隐私政策网页、商店截图或 Play Console 自动化。
- 进程被系统强杀后的 timer。
- HarmonyOS 实现。

## Acceptance

- Mixer 的 mute/unmute/remove 更新 state、playback 和 persistence。
- Saved Mixes 的 favorite/rename 更新 state 和 persistence。
- Timer 支持 15/30/45/60/120 分钟以及正整数自定义时长。
- 活动 timer 的 Mini Player subtitle 显示剩余分钟。
- SoundCatalog 每个声音都有 stable asset key 和 default volume。
- MediaSession controller player 音量为 0，不叠加额外声音。
- 冲突 specs 被 delta 正确修订。
- 完整测试、lint、debug APK、release AAB 和 strict validation 通过。
