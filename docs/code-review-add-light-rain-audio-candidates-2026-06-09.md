# 代码审查：小雨音频候选

**变更：** `add-light-rain-audio-candidates`

**审查日期：** 2026-06-09

**审查范围：** `tools/audio_synthesis_core.mjs` 的 `light-rain` profile、音频核心测试、OpenSpec 规格同步和候选输出。

## 结论

未发现阻断归档的问题。`light-rain` 是内部候选 profile，生成结果保留在 `work/audio-candidates/`，未进入 Android release resources、App catalog 或发布 manifest。

## 关注点

- `light-rain` 已加入 supported profiles，CLI 可以直接通过 `--profile light-rain` 生成候选。
- 新测试要求 `light-rain` 的 roughness proxy 明显低于 `rain`，避免与中大雨候选混淆。
- 新候选 `p99AdjacentDelta` 为 `0.060-0.061`，低于 `rain-natural` 的 `0.088-0.089`，机器层面更接近小雨/远雨目标。
- RMS 仍统一为约 `-20 dBFS`，便于 audition 比较；最终听感仍需人工判断。

## 验证证据

- `node --test tools/audio_synthesis_core.test.mjs tools/audio_asset_studio.test.mjs`
- `node tools/generate_mvp_audio.mjs --verify`
- `node --test tools/*.test.mjs`
- `openspec validate add-light-rain-audio-candidates --strict`
