## Why

`rain-natural` 2 号被用户认可但听感偏“暴雨”，说明当前 rain 候选更适合中到大雨，缺少小雨/远雨层级。为了后续声音目录更清晰，需要新增内部 `light-rain` 候选 profile。

## What Changes

- 新增内部程序化合成 profile：`light-rain`。
- `light-rain` 输出更低密度、更低相邻采样 roughness、更少低频雨幕冲击的小雨候选。
- 生成一组 `light-rain` 候选 WAV、manifest 和 audition HTML。
- 保持候选素材不进入 Android `res/raw`、App catalog 或发布 manifest。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `audio-asset-studio`: 支持生成 `light-rain` 小雨候选，并与现有 `rain` profile 区分。

## Impact

- 修改 `tools/audio_synthesis_core.mjs` supported profiles 与频谱模型。
- 修改音频工具测试，增加 `light-rain` 与 `rain` 的差异化断言。
- 生成 `work/audio-candidates/light-rain-2026-06-09/`，继续被 git ignore。
- 不新增第三方素材、网络依赖或发布资源变更。
