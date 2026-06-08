# Code Review: 历史归档 Superpowers 追溯验收

日期：2026-06-08

## Findings

审查期间发现并修复：

- P1：最初让 Gradle `check` 校验当前 active change，会与完成该 change 所需的 Gradle
  验证形成循环。现改为 Gradle 校验全部已归档基线，当前 change 在完成后单独执行 strict
  pre-archive gate。
- P1：CLI 的 `--change` 最初未限制 change id，可构造路径穿越。现只允许小写字母、数字
  和连字符。
- P1：验收记录最初可引用其他 change 的已完成 tasks。现要求 tasks 文件父目录必须与
  `changeId` 一致。
- P2：retrospective 模式最初会拒绝存在但未勾选的旧 plan。现允许保留该 plan 路径，
  但必须用 `plan:` 显式声明历史同步缺口。

当前未发现未修复的阻塞问题。

## Tests

- checkbox 解析。
- strict 完整通过及 plan/review/verification 缺失失败。
- retrospective 缺口未声明失败、显式声明通过。
- 未同步历史 plan 的 retrospective 行为。
- evidence 相对路径和 change id 路径安全。
- tasks 证据归属。
- CLI 单 change 成功和全归档缺失记录失败。
- Gradle check 静态集成。

## Spec Alignment

- 12 个历史缺口归档使用 retrospective，不伪造历史 RED/GREEN。
- 2 个原本证据完整的归档使用 strict。
- JSON 记录覆盖 evidence、unverifiable、remaining risks 和 verified date。
- Gradle `check` 离线校验已归档基线。
- 当前 change 必须在 tasks、plan、review 和验证完成后通过 strict gate。

## Remaining Risks

- 当前目录不是 Git repository，无法执行基于 SHA 的独立 subagent review。
- JSON schema 由代码校验，尚未额外维护 JSON Schema 文件；当前规模下避免双重 schema 漂移。
- Gate 证明仓库证据完整性，不替代 Android 真实设备、Play Console 或人工音频 QA。

