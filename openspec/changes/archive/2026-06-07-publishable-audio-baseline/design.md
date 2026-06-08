# Design: 可发布音频基线

## Asset Generation

`tools/generate_mvp_audio.mjs` 使用固定 seed 和 radix-2 inverse FFT 生成严格周期波形。频谱 bin 与 WAV 周期长度对齐，因此无需依赖不稳定的首尾 crossfade。

- 白噪声：频谱幅度近似平坦。
- 粉噪声：幅度按 `1/sqrt(f)` 衰减。
- 棕噪声：幅度按 `1/f` 衰减，并限制极低频能量。
- 柔和风扇：低频叶片谐波叠加低电平粉噪背景。

所有输出归一化到保守 RMS，保留峰值余量。脚本同时写入 `docs/audio-assets/generated-audio-manifest.json`。

## Catalog

首版 `SoundCatalog.all` 只包含：

- `white_noise`
- `pink_noise`
- `brown_noise`
- `fan`

自然声音不会作为不可用项目展示。默认混音改为棕噪、粉噪和风扇组合。

## Android Mapping

`AndroidSoundResourceResolver` 将 sound ID 映射到 `R.raw.*`。每个 ExoPlayer layer 首次创建时记录其 sound ID；如果同一 layer ID 的 sound ID 改变，则替换 MediaItem。

未知 ID 回退到 `brown_noise_loop`，用于兼容开发阶段已保存的旧 snapshot。

## Verification

- common test 验证发布目录和默认混音引用。
- Android unit test 验证资源映射及 fallback。
- 生成脚本验证 WAV header、时长、RMS、峰值、非静音和 loop seam。
- Gradle 完整验证和 APK 包内资源检查。
