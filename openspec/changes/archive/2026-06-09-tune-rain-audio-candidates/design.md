## Context

`audio_asset_studio` 复用 `audio_synthesis_core`，通过 deterministic radix-2 inverse FFT 生成候选 WAV。当前 rain profile 的高频 hiss 和固定 droplet band 让机器 QA 通过，但听感偏“窄口花洒”，说明现有 QA 只覆盖格式、响度、峰值和循环边界，不足以约束雨声质感。

## Goals / Non-Goals

**Goals:**

- 降低 rain profile 的狭窄高频水流感。
- 增加更宽、更分散的自然雨幕质感。
- 继续保持 deterministic、first-party、candidate-only 输出。
- 新增可重复执行的代理测试，防止 rain profile 重新退化成过强高频跳变。

**Non-Goals:**

- 不把新候选音频加入发布包。
- 不引入第三方录音、采样包、网络下载或外部音频库。
- 不保证机器指标可以替代人工听测。
- 不改变 App UI、MediaSession、播放引擎或 catalog。

## Decisions

### 1. 调整现有 `rain` profile，而不是新增公开 profile

当前问题来自现有 rain 候选的基础听感。候选尚未发布，直接调整基础模型更简单；如果后续需要“暴雨、窗雨、远雨”等口味，再单独扩展 profile。

### 2. 用频谱和时间域代理指标约束“花洒感”

测试不直接判断主观听感，而是约束两个可重复指标：

- rain 输出的相邻采样跳变不应过高，避免尖锐、细密、近距离水流感。
- rain 输出仍需通过现有 RMS、peak、loop boundary QA。

### 3. 保持候选运营边界

新的 WAV 只生成到 `work/audio-candidates/`，manifest 继续标记 `status: candidate` 和 `publishable: false`。发布晋升仍需要独立变更和人工听测记录。

## Risks / Trade-offs

- [Risk] 降低高频跳变后，雨声可能变得过于柔和或接近白噪音。  
  [Mitigation] 生成多条候选，让人工试听选择。

- [Risk] 机器代理指标无法证明“像雨”。  
  [Mitigation] 只将代理指标作为退化保护，最终保留 audition 人耳确认。

- [Risk] 调整 rain profile 会改变发布生成脚本输出。  
  [Mitigation] 本次不提交新发布 WAV；运行 `node tools/generate_mvp_audio.mjs --verify` 确认现有发布资产仍与当前 manifest 一致。
