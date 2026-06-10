## Context

当前 `tools/generate_mvp_audio.mjs` 同时包含合成算法、WAV 编码、QA 分析、manifest 写入和 CLI 入口。它生成发布包使用的 8 个固定声音，输出到 `composeApp/src/androidMain/res/raw` 和 `docs/audio-assets/generated-audio-manifest.json`。

为了做内部运营工具，需要保留发布脚本的稳定性，同时允许生成大量候选声音。候选声音不能默认进入发布包，否则会影响 Google Play 上架材料、Data safety 声音来源说明和人工 QA gate。

## Goals / Non-Goals

**Goals:**

- 抽出纯合成核心模块，供发布脚本和候选工具复用。
- 提供 CLI 批量生成候选 WAV。
- 生成候选 manifest，记录 seed、profile、参数、hash、QA 指标和状态。
- 生成本地 HTML 试听页，使用浏览器原生 `<audio>` 控件。
- 保持候选和发布资产边界清晰。

**Non-Goals:**

- 不做服务端后台、账号、权限或远程存储。
- 不下载第三方素材。
- 不自动把候选发布到 App catalog 或 `res/raw`。
- 不替代人工听测。

## Decisions

### Decision 1: 抽出 `tools/audio_synthesis_core.mjs`

将随机数、inverse FFT、profile 频谱、PCM/WAV encode/decode、hash 和 QA 分析抽为可测试模块。`generate_mvp_audio.mjs` 只保留固定发布资产列表和发布 manifest 写入。

### Decision 2: 新增 `tools/audio_asset_studio.mjs`

CLI 接收 `--profile`、`--count`、`--seed`、`--out`、`--prefix` 等参数。每次运行创建候选目录，写入 WAV、`manifest.json` 和 `audition.html`。

### Decision 3: 候选默认不可发布

manifest 中每个资产状态默认为 `candidate`，并写入 `publishable: false`。只有后续独立 change 才能把候选转入 App catalog。

### Decision 4: 不引入第三方依赖

继续使用 Node 标准库，降低工具维护成本和供应链风险。

## Risks / Trade-offs

- 候选生成质量参差不齐 -> 保留 QA 指标和人工听测状态，不自动发布。
- 频谱合成不像真实录音 -> 第一版用于扩充底噪和氛围候选，真实录音另走授权流程。
- 工具参数过多会难用 -> 第一版只开放少量稳定参数，更多 profile 以后再加。
- 重构可能破坏现有发布音频 hash -> 用测试和 `node tools/generate_mvp_audio.mjs --verify` 保护兼容性。

## Migration Plan

1. 给现有生成脚本行为写 Node 测试，先观察 RED。
2. 抽出 `audio_synthesis_core.mjs`，让现有生成脚本复用核心。
3. 新增候选 CLI 和测试。
4. 生成一次 sample candidate 到临时目录，验证 manifest、WAV 和 HTML。
5. 更新内部工具文档和 OpenSpec task 状态。

