# 代码审查：外部音频来源 intake

**变更：** `external-audio-source-intake`

**审查日期：** 2026-06-09

**审查范围：** 外部音频来源文档、Freesound CC0 候选取证、听测包、`.gitignore` 边界和 OpenSpec 主规格同步。

## 结论

未发现阻断归档的问题。该变更没有把外部原始音频直接加入 Android release resources，也没有修改 App catalog；所有下载文件和听测产物保留在 `work/audio-intake/` 并被 git ignore。

## 关注点

- 11 个已下载 Freesound 原始素材均记录为 CC0、human-pass、`accepted-as-seed-pending-processing`。
- `736845` 未下载，仍保持 `not-ready`，未被误标为可处理素材。
- 机器预筛风险字段仍保留，后续音频发布晋升必须继续处理 loop、loudness、包体和处理后 hash。
- 主规格已新增外部素材 intake 与外部音频发布证据要求，防止后续绕过准入。

## 验证证据

- `openspec validate external-audio-source-intake --strict`
- `work/audio-intake/intake-manifest.json` 一致性检查
- `work/audio-intake/audition/listening-qa-report.md` 人工听测确认记录
- `git status --ignored work/audio-intake` 确认工作目录不入仓库
