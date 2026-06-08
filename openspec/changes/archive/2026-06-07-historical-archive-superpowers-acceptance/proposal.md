# 历史归档 Superpowers 追溯验收

## Why

OpenSpec 归档任务和当前实现均处于完成状态，但 12 个历史归档没有完整、统一、可自动检查的
Superpowers 验收证据。继续依赖人工检查会让“功能通过”和“流程通过”混为一谈，也无法
阻止后续归档再次遗漏执行计划、验证或代码审查。

## User Stories

- 维护者可以查看每个历史归档哪些证据已证实、哪些只能当前重验、哪些无法追溯。
- 维护者在归档新变更前可以运行单一命令，检查 OpenSpec 与 Superpowers 流程证据。
- Gradle `check` 会执行当前严格 gate，避免流程约束只停留在文档。
- 审计记录不会通过事后勾选伪造历史 RED/GREEN。

## What Changes

- 定义机器可读的归档验收记录格式和 `strict` / `retrospective` 两种模式。
- 为 12 个非严格通过归档创建追溯验收记录。
- 新增 Node gate，检查归档任务、执行计划、评审、验收记录和阻塞项。
- 新增当前变更严格验收记录，并将 gate 接入 Gradle `check`。
- 更新项目协议，明确归档前 gate 和证据真实性要求。

## Non-goals

- 重写或重新实现历史产品功能。
- 回填无法证实的历史 RED/GREEN。
- 自动归档 OpenSpec change。
- 建立 CI 服务或 Git hook。

## Acceptance

- 12 个历史归档均有追溯验收记录。
- 记录明确列出证据来源、当前验证、不可追溯项和剩余风险。
- gate 的失败场景先有自动化 RED 测试。
- strict 模式拒绝未完成 tasks、未完成 plan、缺失 review、缺失验证或存在 blocker。
- retrospective 模式允许历史缺口，但要求显式记录不可追溯项且无当前 blocker。
- Gradle `check` 执行 gate。
- Node tests、Gradle check、lint、APK、签名 AAB 和 OpenSpec strict validation 全部通过。

