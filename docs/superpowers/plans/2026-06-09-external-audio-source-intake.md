# 外部音频来源 intake 执行计划

> 计划用于归档验收证据。该变更已按 OpenSpec `external-audio-source-intake` 完成执行，以下步骤与 tasks.md 对齐。

**目标：** 在程序化生成音频质量不足时，建立外部真实录音素材来源、授权边界、Freesound CC0 首批种子候选和发布晋升前证据要求。

**范围：** 仅新增外部素材 intake 文档、Freesound 候选取证、听测包和 OpenSpec 规格；不直接修改 Android 发布资源或 App catalog。

## 执行清单

- [x] 调研外部来源渠道和授权边界
- [x] 明确 Freesound CC0 为首选来源，Sonniss 等为补充渠道
- [x] 建立首批 rain、ocean、fireplace、fan、wind 候选清单
- [x] 创建 `docs/audio-assets/external-audio-source-intake.md`
- [x] 通过已登录 Freesound 账号下载 11 个 CC0 原始 WAV 到 `work/audio-intake/originals/`
- [x] 生成页面证据、原始 hash、ffprobe 元数据和 intake manifest
- [x] 生成本地听测页、预览片段、波形、频谱和机器 QA 指标
- [x] 记录用户人工听测确认：素材验证可用，听感可以满足
- [x] 将 `work/audio-intake/` 加入 `.gitignore`
- [x] 同步主规格 `audio-asset-studio` 与 `release-readiness`
- [x] 运行 OpenSpec strict validation

## RED / GREEN 记录

- 本变更不修改生产代码，未执行 TDD RED/GREEN。
- 验证重点为 OpenSpec、取证完整性、manifest 一致性、git ignore 边界和人工听测记录。

## 验证记录

- `openspec validate external-audio-source-intake --strict`
- manifest 检查：11 个已下载文件存在，1 个未下载候选保持 `not-ready`
- 听测产物检查：44 个预览片段、22 张图、11 个单条指标 JSON
- `git status --ignored work/audio-intake` 确认 intake 工作目录被忽略
