# 可发布音频基线

## Why

白噪音应用当前实际播放静音测试文件，无法进入有效的 internal 或 closed testing。目录还展示没有对应资产的自然声音，属于用户可见能力与真实行为不一致。

## What Changes

- 添加确定性音频生成脚本。
- 生成白噪、粉噪、棕噪和柔和风扇四个无缝循环 WAV。
- 添加资产清单，记录算法、参数、SHA-256、时长、格式、RMS 和 loop seam。
- SoundCatalog 只展示当前可发布的四个声音。
- 默认与保存混音只引用可发布声音。
- Android 按声音 ID 解析独立 raw resource。
- 未知或旧版 sound ID 回退到棕噪声，避免恢复旧 DataStore 后静音或崩溃。
- MediaSession 占位媒体项使用棕噪声，不再引用 silence loop。
- 删除 `silence_loop.wav`。

## Non-goals

- 雨声、雷声、海浪、森林、风声或壁炉资产。
- 第三方音频采购。
- 音频压缩格式迁移。
- 专业母带或医疗助眠声称。

## Acceptance

- 四个 WAV 均为 mono 44.1 kHz 16-bit PCM。
- 每个 WAV 使用严格周期生成，loop 首尾差值通过脚本阈值。
- SoundCatalog 与 Android resource map ID 完全一致。
- 默认混音至少包含三个真实资源层。
- AndroidPlaybackEngine 为每个 layer 选择其 soundId 对应资源。
- 发布文档不再把静音文件列为当前播放实现。
- 完整 test、lint、assembleDebug 和 OpenSpec strict validation 通过。
