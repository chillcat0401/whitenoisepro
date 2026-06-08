# Code Review: 可发布音频基线

日期：2026-06-07

## Findings

未发现阻塞本次变更的问题。

审查期间修复：

- Library 原先会展示没有可用声音的“自然”分类。
- 默认混音名称宣称雨声，但实际没有雨声资源。
- 所有 layer 原先共享同一个静音 URI。
- 同一 layer ID 更换 sound ID 时，播放器原先不会替换 MediaItem。

## Tests

- `SoundCatalogTest` 验证四个发布 ID、动态分类和所有示例混音引用。
- `AndroidSoundResourceResolverTest` 验证四个独立资源和 legacy fallback。
- 生成器验证四个 WAV 的 header、非静音、RMS、峰值、哈希和 loop seam ratio。
- `ffprobe` 独立确认 mono、44.1 kHz、16-bit PCM 和时长。

## Spec Alignment

- 当前目录只展示有真实 raw resource 的声音。
- 生成资产不含第三方录音或采样。
- 生成脚本、固定 seed、参数、SHA-256 和 QA 指标均保存在仓库。
- `silence_loop.wav` 已删除。
- 未知旧 sound ID 回退到棕噪声。

## Remaining Risks

- 程序化指标不能替代人耳试听；至少一台真实 Android 设备必须完成 10 分钟单音和默认混音 QA。
- PCM 资源合计约 4 MiB；若目录扩展，应评估 Ogg/Opus 与解码 loop 行为。
- 当前没有自然声音，商店文案与截图不得宣称雨声、海浪或森林。
- 白噪声高频能量较强，closed testing 应重点收集疲劳感和默认音量反馈。

## Verification

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home \
./gradlew :composeApp:check :composeApp:assembleDebug
```

结果：`BUILD SUCCESSFUL`。

- 126 tests，0 failures，0 errors。
- `node tools/generate_mvp_audio.mjs --verify`：4 个资产通过。
- `ffprobe`：全部为 mono、44.1 kHz、16-bit PCM，时长约 11.89 秒。
- APK 包含四个新 raw resource，不包含 `silence_loop.wav`。
- Debug APK：`composeApp/build/outputs/apk/debug/composeApp-debug.apk`。
- OpenSpec strict validation：通过。

Lint 剩余 warning：

- 缺少正式 launcher icon，需下一份上架准备 change 处理。
- `activity-compose` 有稳定版更新提示。
- 既有 `Uri.parse` KTX 建议。
