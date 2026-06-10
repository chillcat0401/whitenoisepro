## Why

当前 rain 候选机器 QA 合格，但用户听感反馈偏“窄口花洒”，不符合睡眠应用对自然雨幕、柔和分散感的预期。候选素材尚未进入发布包，现在调整生成模型成本最低。

## What Changes

- 调整内部程序化 rain profile，使其从稳定窄高频水流感转向更宽的雨幕背景、分散细滴和轻微远景空气感。
- 为 rain 候选增加可测试的音频代理指标，降低过强高频相邻跳变。
- 重新生成一组新的 rain 候选素材目录和试听页，用于人工听测。
- 保持候选素材不进入 Android `res/raw`、App catalog 或发布 manifest。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `audio-asset-studio`: rain profile 候选生成需要支持更自然、宽频、低刺耳风险的雨声调音目标，并输出新的候选试听证据。

## Impact

- 影响 `tools/audio_synthesis_core.mjs` 的 rain 合成逻辑。
- 影响 `tools/audio_synthesis_core.test.mjs` 的 rain 质量代理测试。
- 生成新的 `work/audio-candidates/rain-natural-2026-06-09/` 本地候选输出，继续被 `.gitignore` 忽略。
- 不新增外部依赖，不下载第三方音频，不改变发布包内音频资源。
