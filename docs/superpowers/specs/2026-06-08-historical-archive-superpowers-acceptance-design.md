# 历史归档 Superpowers 追溯验收设计

## 目标

建立不伪造历史的验收证据层，并通过自动 gate 阻止未来归档遗漏 Superpowers 流程。

## 决策

- 历史 12 个归档使用 `retrospective`，明确列出无法追溯的 RED/GREEN 或计划同步缺口。
- 新变更使用 `strict`，必须具备已完成 OpenSpec tasks、已完成 Superpowers plan、专项 review、
  完整验证和无 blocker 的验收记录。
- 当前重新运行的测试只能证明当前实现，不用于倒推历史过程。
- gate 使用 Node 标准库实现，不增加 npm 依赖。
- Gradle `check` 执行所有已归档记录，当前 active change 在完成后单独执行 strict pre-archive gate。

## 文件边界

- `tools/archive_acceptance.mjs`：纯解析和校验逻辑。
- `tools/verify_archive_acceptance.mjs`：CLI 参数、文件读取和输出。
- `tools/verify_archive_acceptance.test.mjs`：临时 fixture 与行为测试。
- `docs/superpowers/acceptance/*.json`：机器可读证据。
- `docs/superpowers/acceptance/README.md`：schema 和维护规则。
- `build.gradle.kts`：Gradle gate task。

## 安全与真实性

- 仅允许仓库相对路径，不读取 `..` 或 absolute path。
- 不记录密码、keystore 路径内容或其他秘密。
- 追溯记录的缺失证据必须显式写入 `unverifiable`。
- 不回填旧 plan checkbox。

## 验收

- 严格和追溯规则均有失败优先测试。
- 12 个历史记录通过 retrospective gate。
- 当前 change 通过 strict gate。
- Gradle 和完整发布验证通过。
