# 雨声音频候选调音探索

## 用户反馈

当前 `work/audio-candidates/rain-soft-2026-06-09/audition.html` 中的 rain 候选听感“不像雨声，像一个窄口的花洒声音”。

## 当前判断

现有 rain profile 主要由稳定高频 hiss 加几个固定高频 droplet band 组成。这个模型机器 QA 合格，但听感容易偏向连续、狭窄、近距离水柱，而不是开阔、分散、柔和的雨幕。

## 可选方案

### 方案 A：只调参数重新生成候选

- 优点：不改代码，最快。
- 缺点：当前模型结构偏静态，单靠 seed / RMS / duration 很难摆脱“花洒”质感。

### 方案 B：调整 rain 合成模型

- 优点：能从根因降低狭窄高频水柱感，加入更宽的中频雨幕和细滴分散感。
- 缺点：会改变 `rain` profile 的 deterministic output，需要重新生成候选并更新测试。

### 方案 C：新增 `rain-soft` profile

- 优点：保留旧 `rain`，新增更适合睡眠的口味。
- 缺点：会扩展 profile 列表、文档和后续 catalog 决策；当前用户反馈指向现有 rain 候选质量，不必过早增加公开 profile。

## 推荐

采用方案 B：调整现有内部 rain 合成模型。当前候选还未进入发布包，调整成本低；保留第一方程序化生成、manifest、candidate/non-publishable 边界不变。

## 验收口径

- 机器指标：RMS、peak、loop boundary 继续通过。
- 新增代理指标：rain profile 的高频相邻跳变下降，避免过强“细窄水柱”质感。
- 运营输出：生成新的 `rain-natural` 候选目录和试听页，供人工听测。
