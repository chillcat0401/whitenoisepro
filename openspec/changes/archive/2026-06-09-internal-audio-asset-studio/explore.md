# Explore: Internal Audio Asset Studio

日期：2026-06-09

## 背景

当前项目已经有 `tools/generate_mvp_audio.mjs`，可以用固定 seed 和频谱 profile 生成 8 个第一方 WAV loop，并记录 hash、RMS、peak、loop seam 等 QA 指标。它适合生成发布基线，但不适合作为日常运营工具批量探索声音变体。

## 目标

建立一个本地内部素材工具，让运营 / 开发者可以批量生成候选声音、试听、比较 QA 指标，并把候选留存在 `work/audio-candidates/`。第一版不直接修改 App 声音目录，不把候选自动发布到 `res/raw`，避免影响 closed testing release 和上架材料。

## 可选方案

### 方案 A：增强现有生成脚本

优点：改动最少。

缺点：发布资产生成和候选探索混在一起，容易误把候选写入发布包。

### 方案 B：抽出合成核心，新增候选生成 CLI + HTML 试听页

优点：发布脚本继续稳定；候选工具独立；TDD 覆盖核心算法、manifest 和 HTML 输出。

缺点：需要一次小型重构。

### 方案 C：做完整 Web 后台

优点：运营体验最好。

缺点：需要服务端、状态管理和安全边界；对当前快速上架目标过重。

## 推荐

选择方案 B。新增共享合成核心模块，保留现有发布脚本行为；新增 `tools/audio_asset_studio.mjs` 作为内部 CLI，生成候选目录、WAV、manifest 和 HTML 试听页。后续如果候选工具稳定，再考虑做本地 Web UI。

## 边界

- 不新增联网、账号、analytics、云同步或后端服务。
- 不自动把候选素材加入 App 发布包。
- 候选素材不算 publishable，必须经过人工听测和单独发布 change。
- 只生成第一方程序化素材，不下载第三方音频。

