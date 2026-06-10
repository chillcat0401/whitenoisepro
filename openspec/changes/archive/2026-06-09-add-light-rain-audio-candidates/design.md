## Context

内部音频运营工具已经能生成 `rain` 候选。用户听测后认为较好的 rain 候选仍偏“暴雨”，这说明需要按雨强建立候选层级，而不是只在一个 profile 内继续调参。

## Goals / Non-Goals

**Goals:**

- 新增 `light-rain` 内部 profile。
- 让 `light-rain` 在机器代理指标上比 `rain` 更轻：更低 `p99AdjacentDelta`，更低峰值风险，更少低频厚重感。
- 输出一组本地候选供人工试听。

**Non-Goals:**

- 不把 `light-rain` 加入正式 App catalog。
- 不替换用户已选择的 `rain-natural` 2 号。
- 不保证机器指标能替代人工听测。

## Decisions

### 1. Profile 命名为 `light-rain`

使用明确语义，避免把小雨混在 `rain` seed 或 prefix 里。后续如果晋升到 App，可对应“小雨”或“远雨”。

### 2. 通过频谱模型降低密度

`light-rain` 使用更弱的低频 body、更柔和的高频 rolloff、更少的 scattered drops。仍保持 deterministic IFFT 和现有 WAV/QA 管线。

### 3. 只做 candidate 输出

候选仍写入 `work/audio-candidates/`，manifest 标记 `publishable: false`。晋升发布包必须另开变更。

## Risks / Trade-offs

- [Risk] `light-rain` 可能过轻，像白噪音而不是雨声。  
  [Mitigation] 生成多条候选，保留人工听测筛选。

- [Risk] 同 RMS 下“小雨”仍可能显得响。  
  [Mitigation] 试听时优先在低音量下判断；后续可给候选工具增加 per-profile target RMS 默认值。
