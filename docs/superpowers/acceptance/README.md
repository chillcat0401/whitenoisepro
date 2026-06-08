# Superpowers Archive Acceptance Records

每个受治理的 OpenSpec change 或 archive 对应一个同名 JSON 文件。

## 字段

- `schemaVersion`: 当前固定为 `1`。
- `changeId`: active change 名或完整 archive 目录名。
- `mode`: `strict` 或 `retrospective`。
- `status`: 只有无当前 blocker 时才允许 `passed`。
- `evidence.tasks`: OpenSpec tasks 文件。
- `evidence.plan`: Superpowers plan；历史未保留时为 `null`。
- `evidence.review`: 专项 review；历史未保留时为 `null`。
- `evidence.verification`: 当前重新执行的验证命令或结果。
- `unverifiable`: 无法证明的历史流程，使用 `plan:`、`review:`、`tdd:` 等前缀。
- `remainingRisks`: 不阻塞当前验收、但仍需保留的风险。
- `verifiedAt`: 最后重验日期。

## 规则

- `strict` 不允许未勾选 plan、缺失 review、空 verification 或任何 `unverifiable`。
- `retrospective` 必须明确声明缺失或未同步的历史证据。
- 当前测试通过不能证明历史上观察过 RED。
- 不允许绝对路径或 `..`，也不得记录签名秘密。

## 命令

```bash
node tools/verify_archive_acceptance.mjs --change <change-id>
node tools/verify_archive_acceptance.mjs --all-archives
```

