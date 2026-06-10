# 代码审查：雨声音频候选调音

**变更：** `tune-rain-audio-candidates`

**审查日期：** 2026-06-09

**审查范围：** `tools/audio_synthesis_core.mjs` rain profile、`tools/audio_synthesis_core.test.mjs` rain texture regression、OpenSpec delta、候选输出验证。

## 结论

未发现阻断归档的问题。变更保持在内部候选生成链路内，没有把候选音频加入 Android `res/raw`、App catalog 或发布 manifest。

## 关注点

- 新增测试将 rain 的 `p99AdjacentDelta` 限制在 `< 0.16`，旧模型失败值为 `0.1987`，能覆盖用户反馈中的窄高频水流感退化。
- 调整后的 rain profile 降低高频喷流权重，增加 `rainBed`、`roofBody` 和更宽的 scattered drops，目标是更宽、更柔和的雨幕底噪。
- 新候选 `rain-natural-2026-06-09` 的 `p99AdjacentDelta` 约 `0.088-0.089`，RMS 仍稳定在 `-20 dBFS`，peak 和 loop boundary 均通过。
- 机器指标不能判断“像不像雨”本身，只能降低已知退化风险；最终仍需人工试听 `audition.html`。

## 验证证据

- `node --test tools/audio_synthesis_core.test.mjs tools/audio_asset_studio.test.mjs`
- `node tools/generate_mvp_audio.mjs --verify`
- `node --test tools/*.test.mjs`
- `openspec validate tune-rain-audio-candidates --strict`
