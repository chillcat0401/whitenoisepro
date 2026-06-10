## Why

WhiteNoisePro 需要持续扩充声音素材，但直接下载第三方素材会带来许可和上架风险。项目已经具备第一方程序化音频生成基础，下一步应把它扩展成内部候选素材工具，让团队可以快速生成、试听、筛选和留证。

## What Changes

- 抽出可复用的音频合成核心，保留现有发布音频生成脚本的行为。
- 新增本地 CLI `tools/audio_asset_studio.mjs`，用于批量生成候选 WAV、manifest 和 HTML 试听页。
- 候选输出默认写入 `work/audio-candidates/<run-id>/`，不自动进入 Android `res/raw`。
- manifest 记录 profile、seed、参数、hash、QA 指标和候选状态，便于人工筛选。
- 新增内部工具文档，说明候选生成、试听、审批和发布边界。
- 新增 Node 测试覆盖核心合成、候选 manifest、HTML 试听页和现有发布脚本兼容性。

## Capabilities

### New Capabilities

- `audio-asset-studio`: 定义内部第一方音频候选生成、试听、QA 留证和发布边界。

### Modified Capabilities

- `release-readiness`: 增加候选音频不得自动作为发布素材、发布前必须经过人工听测和 manifest 留证的要求。

## Impact

- 影响 `tools/` 下的 Node 音频生成脚本和测试。
- 追加 `docs/audio-assets/` 或 `docs/release-readiness/` 下的内部工具说明。
- 不修改 App 运行时行为，不新增 Android 权限，不新增第三方依赖。
- 默认生成的候选文件位于 `work/audio-candidates/`，不作为发布资产提交。

