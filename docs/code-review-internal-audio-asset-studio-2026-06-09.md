# 代码审查：内部音频素材运营工具

**变更：** `internal-audio-asset-studio`

**审查日期：** 2026-06-09

**审查范围：** `tools/audio_synthesis_core.mjs`、`tools/audio_asset_studio.mjs`、`tools/generate_mvp_audio.mjs`、音频工具测试、候选素材文档、`.gitignore` 和 OpenSpec 规格同步。

## 结论

未发现阻断归档的问题。实现符合本次 OpenSpec 范围：候选素材只写入运营输出目录，manifest 明确标记为 candidate / non-publishable，发布音频生成流程继续通过现有 manifest 校验。

## 关注点

- `audio_synthesis_core.mjs` 将原发布生成脚本中的确定性 IFFT 合成、WAV、hash 与 QA 逻辑集中复用，降低候选工具和发布工具参数漂移风险。
- `audio_asset_studio.mjs` 默认输出到 `work/audio-candidates/`，且该目录已加入 `.gitignore`，降低候选 WAV 误提交和误打包风险。
- manifest 包含 seed、profile、生成参数、hash、bytes、QA、`status: candidate` 与 `publishable: false`，满足后续人工听测和发布准入追踪。
- `audition.html` 仅是本地试听辅助页面，使用浏览器原生 audio controls，没有引入服务端或第三方音频依赖。
- 已修正 CLI 帮助文案：duration 会映射到最近的 FFT-safe sample count，而不是始终向上取整。

## 剩余风险

- 机器 QA 只能覆盖响度、峰值、WAV 格式和循环边界异常，不能替代人工睡眠场景听测。
- 候选素材晋升到 app release package 仍需要独立 OpenSpec 变更，补齐人耳听测、catalog、release-readiness 和包体影响记录。

## 验证证据

- OpenSpec tasks 已全部勾选完成。
- Node 测试覆盖共享合成核心、候选 CLI 输出、非法 profile 和发布工具验证路径。
- `node tools/generate_mvp_audio.mjs --verify` 已确认当前发布音频资产仍与 manifest 一致。
