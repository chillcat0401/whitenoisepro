# 历史归档 Superpowers 追溯验收 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为历史归档建立真实可追溯的验收记录，并用自动 gate 阻止未来不完整归档。

**Architecture:** Node 标准库负责纯校验和 CLI，JSON 保存机器可读证据，Gradle check 校验已归档基线。历史归档使用 retrospective 模式并显式保留不可追溯项；当前 change 完成后单独执行 strict pre-archive gate。

**Tech Stack:** Node.js test runner、ES modules、Gradle Kotlin DSL、OpenSpec、Markdown/JSON。

---

### Task 1: 验收校验核心

**Files:**
- Create: `tools/archive_acceptance.mjs`
- Create: `tools/verify_archive_acceptance.test.mjs`

- [x] 写 checkbox、strict 缺失 plan/review/验证、retrospective 未声明缺口的失败测试。
- [x] 运行 `node --test tools/verify_archive_acceptance.test.mjs`，确认因模块不存在而 RED。
- [x] 实现纯函数 `parseChecklist`、`validateAcceptanceRecord` 和安全相对路径校验。
- [x] 重跑目标测试，确认 GREEN。

### Task 2: CLI 与真实目录

**Files:**
- Create: `tools/verify_archive_acceptance.mjs`
- Modify: `tools/verify_archive_acceptance.test.mjs`

- [x] 添加临时项目 fixture 的 CLI RED 测试。
- [x] 实现 `--change`、`--all-archives` 和非零错误输出。
- [x] 验证 fixture strict pass/fail 与 retrospective pass/fail。
- [x] 运行全部 Node tests。

### Task 3: 历史证据记录

**Files:**
- Create: `docs/superpowers/acceptance/README.md`
- Create: `docs/superpowers/acceptance/<12 archive ids>.json`
- Modify: `docs/superpowers-archive-acceptance-audit-2026-06-07.md`

- [x] 按现有 tasks、plan、review 和验证报告映射 12 个 retrospective 记录。
- [x] 每个缺失 plan、未勾选 plan、缺失 explore 或无法证明的 RED/GREEN 写入 `unverifiable`。
- [x] 运行 `node tools/verify_archive_acceptance.mjs --all-archives`。
- [x] 修复所有路径和证据不一致。

### Task 4: Strict Gate 与 Gradle

**Files:**
- Create: `docs/code-review-historical-archive-superpowers-acceptance-2026-06-08.md`
- Create: `docs/superpowers/acceptance/historical-archive-superpowers-acceptance.json`
- Modify: `build.gradle.kts`
- Modify: `PROJECT_PROTOCOL.md`

- [x] 添加 Gradle gate 未执行时失败的静态 RED 测试。
- [x] 注册 `verifyArchiveAcceptance` 并挂到所有 `check` task。
- [x] 创建当前 strict acceptance record，完成 plan 和 review 后再标记 passed。
- [x] 运行历史 archive gate 和 Gradle check。

### Task 5: 完整验证与归档

**Files:**
- Modify: `openspec/changes/historical-archive-superpowers-acceptance/tasks.md`
- Modify: `docs/superpowers/plans/2026-06-08-historical-archive-superpowers-acceptance.md`

- [x] 运行 Node tests 和历史 gate。
- [x] 运行 `./gradlew clean check lintDebug assembleDebug bundleRelease`。
- [x] 验证 4 个音频资产和签名 AAB。
- [x] 完成专项代码审查并修复发现。
- [x] 运行 OpenSpec strict validation。

全部 checkbox 完成后，执行：

```bash
node tools/verify_archive_acceptance.mjs \
  --change historical-archive-superpowers-acceptance
```

只有 strict gate 通过后才执行 OpenSpec archive。
