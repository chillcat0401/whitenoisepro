# Design: 启动图标与商店图标基线

## Visual Direction

- 背景：`#111317`，与应用主背景一致。
- 主标记：`#8AD3CE` 月牙，表达夜间与睡眠。
- 辅助标记：`#ACCAE3` 三条声波，表达声音与混音。
- 不使用文字，保证小尺寸和多语言环境下可识别。

## Android

使用 `mipmap-anydpi-v26` adaptive icon：

- background：纯色资源。
- foreground：VectorDrawable。
- round icon 复用同一 adaptive 构成。

项目 minSdk 为 26，因此不需要旧版 bitmap launcher fallback。

## Store Asset

`tools/generate_store_icon.mjs` 使用 supersampling 和纯 Node PNG 编码生成 `docs/store-assets/google-play-icon-512.png`，并在生成后验证尺寸、RGBA 格式与文件大小。
