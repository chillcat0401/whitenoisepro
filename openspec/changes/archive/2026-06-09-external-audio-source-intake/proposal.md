## Why

内部程序化生成声音无法稳定达到睡眠音频质量要求，需要引入真实录音或专业音效库作为本应用的种子音频来源。

## What Changes

- 建立外部音频来源渠道清单和许可分级。
- 给出第一批适合 WhiteNoisePro 的种子音频候选。
- 明确下载、证据留存、转码、听测和发布准入流程。
- 不直接把外部音频加入 Android 发布资源。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `audio-asset-studio`: 内部音频运营流程需要支持外部真实录音 intake，不再把程序化生成视为主要最终素材来源。
- `release-readiness`: 发布素材准入需要记录外部音频的来源 URL、许可证据、原始文件 hash、处理链路和人工听测状态。

## Impact

- 新增 `docs/audio-assets/external-audio-source-intake.md`。
- 后续需要人工下载 Freesound / Sonniss / 其他来源文件，并保存 license evidence。
- 当前不修改 App catalog、Android raw resources 或发布 manifest。
