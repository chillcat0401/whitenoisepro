# Superpowers-Bridge 通用开发协议

适用范围:任何同时使用 OpenSpec(规格工件)与 Superpowers(执行技能)的项目。
本文件与 `schema.yaml`、`README.md` 构成可整体移植的流程包,项目特有约束写在各项目自己的 `PROJECT_PROTOCOL.md`,不要写进本文件。

## 0. 设计原则

1. **规格即队列,不是参考资料。** `tasks.md` 的 checkbox 是 apply 阶段的运行时状态机:解析为严格顺序队列,做完一条、验证一条、回填一条。AI"参考着 spec 自由发挥"是被禁止的默认行为。
2. **证据前移。** 验证证据在每个任务完成的当下落盘(evidence 文件 + 微提交),归档时只做聚合校验。事后凭记忆补写的"证据"不是证据。
3. **勾选 = 验证通过,无其他语义。** checkbox 只能在 verify 命令 exit 0(或显式 manual 验证留痕)之后更新,且勾选变更必须与代码、证据在同一个 commit 里,使每个 `[x]` 在 Git 历史中可对账。
4. **失败即停。** 任何任务验证失败,队列立即冻结。把错误带进下一个任务的成本远高于停下来的成本。
5. **作用域靠对账,不靠承诺。** 工具层无法物理限制 Agent 的写权限,因此用「声明式 scope + 提交前 `git diff --name-only` 对账」替代。超界只有两条路:回滚,或先回写 spec 再继续。
6. **流程与变更体量成正比。** 全流程是为不可逆、跨模块、有验收风险的变更准备的;给错别字修复套全套工件链只会逼人绕过协议(见分级)。

## 1. 变更分级(防流程税)

| 级别 | 判定 | 必需工件 | 必需纪律 |
|---|---|---|---|
| **L0 微改** | 纯文档/注释/错别字/配置注释,不改行为 | 无 | 单提交,信息说明动机 |
| **L1 小改** | 单模块、≤3 个文件、行为变化可由现有测试覆盖 | 仅 `tasks.md`(含 verify/scope) | 逐任务验证 + 微提交 |
| **L2 标准** | 新功能、跨模块、改架构、改对外行为 | 全工件链 proposal→design→tasks→plan | 全部 apply 循环 + 归档门禁 |
| **升级规则** | L1 执行中发现要碰第 4 个文件或第 2 个模块 → 停下,升级为 L2 补 proposal/design | | |

判级有疑义时取高一级。用户明确口头驱动的探索/原型工作可以先做后补(标注 `retrospective`),但**不允许以 strict 名义归档**。

## 2. 阶段定义

### Explore(只读)
澄清目标、范围、非目标、成功标准;读代码、读设计稿、对比 ≥2 个方案并给出推荐。禁止写生产代码、禁止把未确认假设当结论。

### Propose
产出 `proposal.md`(+ 必要的 spec delta)。结束条件:**用户确认 scope 与 non-goals**。

### Design
产出 `design.md`,必须包含**模块作用域表**(本变更允许触碰的路径清单)——它是 apply 阶段 AUDIT 步骤的对账依据。

### Tasks
产出 `tasks.md`。每个任务是三元组:

```markdown
- [ ] 2.1 把音量滑杆激活轨道加粗为 6dp
  - accept: 激活轨道 6dp、未激活 2dp,DesignTokenTest 断言通过
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*DesignTokenTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/design/
```

规则:
- 没有 `verify` 的任务不得进入 apply;无法命令化的写 `verify: manual — <步骤>`,人工结论必须落进 evidence 文件。
- 任务粒度以「一次 TDD 循环 + 一次提交」为准,超过约半天工作量的任务必须拆分。
- `verify` 优先用窄命令(单测试类),任务链末尾必须有一条全量门禁任务(如 `./gradlew check`)。

### Plan
用 `superpowers:writing-plans` 把 tasks 展开为带验证脚本的实施计划,落在 `docs/superpowers/plans/<change-id>.md`。plan 与 tasks 冲突时以 tasks 为准并先回改 tasks。

### Apply(执行循环)
按 `schema.yaml` 的 `apply.instruction` 七步循环执行:SPAWN → TDD → VERIFY → AUDIT → RECORD → COMMIT → NEXT,失败即 HALT。

提交信息格式:`<type>(<change-id>): <task-id> <summary>`,type ∈ feat/fix/refactor/test/docs/chore。

### Review
全部任务完成后做整体代码评审(优先级:行为缺陷 > 缺测试 > 与 spec 不符 > 平台风险 > 性能 > 可维护性)。发现问题:修复或显式记录不修原因,二选一,不允许沉默。

### Archive
归档前置条件与门禁命令见 `schema.yaml` 的 `archive` 段。验收 JSON 的核心约束:
- `mode=strict` 时 `unverifiable` 必须为空;
- `verification` 引用 evidence 目录中的实际文件,不是事后罗列的命令清单;
- 当前测试通过 ≠ 历史上发生过 RED→GREEN,不得据此补勾历史计划。

## 3. 证据目录约定

```
docs/superpowers/
  plans/<change-id>.md                # writing-plans 产物
  evidence/<change-id>/<task-id>.md   # 每任务:命令、exit code、输出摘要、日期
  acceptance/<change-id>.json         # 归档验收记录(聚合引用 evidence)
```

## 4. 红线(任何级别都适用)

- 未跑 verify 就勾选 checkbox。
- 一次性实现多个任务后补勾选、补提交。
- 通过删除或弱化测试让验证通过。
- 实现 tasks 之外的功能("顺手优化"请开新变更或记入 follow-up)。
- scope 之外的文件改动未回写 spec 就提交。
- 把 mock/占位实现当成完成而不在 spec 中声明。
