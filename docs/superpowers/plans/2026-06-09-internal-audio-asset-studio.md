# 内部音频素材运营工具执行计划

> 计划用于归档验收证据。该变更已按 OpenSpec `internal-audio-asset-studio` 完成执行，以下步骤与 tasks.md 对齐。

**目标：** 提供一个本地 Node CLI，用第一方程序化合成方式生成音频候选素材，并输出可复现 manifest 与本地试听页，同时保持候选素材不进入发布包。

**范围：** 仅新增内部运营工具、复用发布音频合成核心、生成候选 WAV/manifest/audition HTML 和文档；不自动下载第三方素材，不修改 Android `res/raw`、声音目录或发布 manifest。

## 执行清单

- [x] 读取 `proposal.md`、`design.md`、`tasks.md` 和相关 spec delta
- [x] 添加 `tools/audio_synthesis_core.test.mjs` 红灯测试，覆盖确定性生成、WAV/QA 指标、profile 校验和 duration 映射
- [x] 添加 `tools/audio_asset_studio.test.mjs` 红灯测试，覆盖候选输出、manifest、audition HTML 和非法 profile
- [x] 抽取 `tools/audio_synthesis_core.mjs`，提供合成、WAV 编解码、hash、QA 和 profile 复用能力
- [x] 重构 `tools/generate_mvp_audio.mjs`，保留现有发布资产生成与 `--verify` 行为
- [x] 实现 `tools/audio_asset_studio.mjs` CLI，支持 profile、count、seed、prefix、duration、RMS、peak 和 out 参数
- [x] 生成候选 WAV、`manifest.json` 和 `audition.html`，并标记 `status: candidate` / `publishable: false`
- [x] 将 `work/audio-candidates/` 加入 `.gitignore`，防止候选音频误入版本库
- [x] 编写 `docs/audio-assets/internal-audio-asset-studio.md`，说明生成、试听、准入和发布边界
- [x] 同步主规格 `audio-asset-studio` 与 `release-readiness`
- [x] 运行 Node 音频工具测试
- [x] 运行发布音频 `--verify`
- [x] 运行全部 Node 工具测试
- [x] 运行 OpenSpec strict validation
- [x] 完成代码审查和归档验收记录

## TDD 记录

- RED：`node --test tools/audio_synthesis_core.test.mjs tools/audio_asset_studio.test.mjs` 初次失败，因为目标模块尚不存在。
- RED：新增 duration 映射断言后，`sampleCountForDuration(3, 44100)` 曾返回 `262144`，与最近 FFT-safe sample count 预期不符。
- GREEN：实现共享合成核心、CLI 与 duration 映射后，目标测试通过。

## 验证记录

- `node --test tools/audio_synthesis_core.test.mjs tools/audio_asset_studio.test.mjs`
- `node tools/generate_mvp_audio.mjs --verify`
- `node tools/audio_asset_studio.mjs --profile rain --count 3 --seed 260609 --prefix rain-soft --duration-seconds 3 --out work/audio-candidates/rain-soft-2026-06-09`
- `openspec validate internal-audio-asset-studio --strict`
- `node --test tools/*.test.mjs`
