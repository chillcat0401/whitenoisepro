# Internal Audio Asset Studio

状态：内部运营工具说明。候选素材默认不进入发布包。

## Purpose

`tools/audio_asset_studio.mjs` 用于生成 WhiteNoisePro 第一方程序化音频候选。它复用发布音频的合成核心，但输出到 `work/audio-candidates/`，不会自动修改 Android `res/raw`、App 声音目录或发布 manifest。

## Generate Candidates

```bash
node tools/audio_asset_studio.mjs \
  --profile rain \
  --count 3 \
  --seed 260609 \
  --prefix rain-soft \
  --duration-seconds 12 \
  --out work/audio-candidates/rain-soft-2026-06-09
```

支持 profile：

```text
white, pink, brown, fan, rain, ocean, forest, fireplace
```

输出内容：

```text
work/audio-candidates/<run>/
  manifest.json
  audition.html
  <candidate>.wav
```

## Audition

用浏览器打开 `audition.html`，逐条试听候选声音。试听时至少记录：

- 是否刺耳。
- 是否有明显 loop 断点。
- 低音量是否仍然舒适。
- 手机扬声器和耳机是否都能接受。
- 是否适合作为独立声音或只适合作为混音层。

## Manifest

`manifest.json` 会记录：

- `mode: candidate`
- `ownership: First-party generated candidate assets; no third-party recordings or samples.`
- profile、seed、sample rate、sample count、target RMS、max peak。
- 每条候选的 SHA-256、bytes、RMS、peak、loop seam ratio。
- `status: candidate`
- `publishable: false`

## Promotion Boundary

候选素材不等于发布素材。要进入 App release package，必须另开 OpenSpec change，并完成：

- 人工听测记录。
- 发布 manifest 更新。
- App `SoundCatalog` 更新。
- Android raw resource 映射更新。
- release-readiness / privacy / store listing 声音来源复核。
- `node tools/generate_mvp_audio.mjs --verify` 或新的发布素材 verifier。

## Notes

- 工具不下载第三方素材。
- 工具不接入网络、账号、analytics 或后端服务。
- `work/audio-candidates/` 已被 `.gitignore` 忽略；如需保留候选证据，应只提交脱敏后的 manifest 摘要或专门的发布 intake 文档。

