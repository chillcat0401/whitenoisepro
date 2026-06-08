# Design: 历史归档 Superpowers 追溯验收

## 验收记录

每个归档对应 `docs/superpowers/acceptance/<archive-id>.json`，包含：

- `schemaVersion`
- `changeId`
- `mode`: `strict` 或 `retrospective`
- `status`: `passed` 或 `blocked`
- `evidence`: OpenSpec tasks、plan、review、tests、build 和 spec validation 路径或命令
- `unverifiable`: 无法追溯的历史过程
- `remainingRisks`
- `verifiedAt`

JSON 用于 gate，旁边的审计 Markdown 保留人类可读解释。记录只引用仓库相对路径和不含秘密
的命令，不保存 keystore 或密码。

## Gate 工具

`tools/verify_archive_acceptance.mjs` 提供：

- 可测试的纯函数：解析 checkbox、校验单条记录、校验目标变更。
- CLI：`node tools/verify_archive_acceptance.mjs --change <change-id>`。
- `--all-archives`：审计所有历史归档，允许记录声明的 retrospective 缺口。

strict 规则：

1. `openspec/changes/<change-id>/tasks.md` 无未勾选任务。
2. `docs/superpowers/plans/<dated-change-id>.md` 或记录指定 plan 存在且无未勾选任务。
3. 记录指定 review 文件存在。
4. `status=passed`、验证证据非空、`unverifiable=[]`。

retrospective 规则：

1. 历史 archive 和 `tasks.md` 存在且全部完成。
2. review/plan 的实际状态必须准确写入 evidence。
3. 所有缺失过程证据必须列入 `unverifiable`。
4. `status=passed` 且不存在当前 blocker。

## Gradle 集成

根 `build.gradle.kts` 注册 `verifyArchiveAcceptance` Exec task，运行 `--all-archives`，
校验已经归档的 strict/retrospective 基线。所有项目的 `check` task 依赖它。工具只读文件，
执行时间应低于一秒，不触发网络。

当前 active change 在 tasks、plan、review 和完整验证全部完成后，单独运行 `--change` strict
gate；通过后才允许执行 OpenSpec archive。这样避免 Gradle check 与当前任务完成状态形成循环。

## 错误处理

- JSON 解析错误、路径越界、文件缺失和 checkbox 未完成均返回非零退出码。
- CLI 输出按 change 分组的具体失败原因。
- 不允许 absolute path 或 `..` 路径引用，避免验收记录读取仓库外文件。

## 测试

- Node 单测使用临时目录构造 strict pass/fail 和 retrospective pass/fail fixture。
- 先验证工具不存在或行为缺失时测试失败，再实现最小逻辑。
- 集成测试对真实当前 change 执行 strict gate。
- 完整回归执行 Node tests、Gradle check/lint/build、签名 AAB、资产和 OpenSpec validation。
