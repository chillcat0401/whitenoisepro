# superpowers-bridge 移植指南

把本流程包搬到新项目只需三步:

1. **拷贝文件**
   ```
   openspec/config.yaml                          # 需按新项目编辑
   openspec/schemas/superpowers-bridge/          # 整目录原样拷贝,勿改
   ```
2. **编辑 `openspec/config.yaml`**(唯一需要改的文件):
   - `context`:项目名、技术栈、模块、全量门禁命令、文档语言。保持 ≤10 行。
   - `rules`:一般不动;有项目级铁律(如"禁止引入 GMS 依赖")加在末尾。
3. **建项目侧配套**:
   - `PROJECT_PROTOCOL.md`:只写项目特有约束(验证矩阵、平台清单、文档语言细则),
     开头一行指向本流程包,流程性内容一律不要复制进去。
   - `docs/superpowers/{plans,evidence,acceptance}/` 三个目录。
   - 归档门禁脚本:拷贝 `tools/verify_archive_acceptance.mjs` 并接入项目的
     check/CI 生命周期(Gradle/npm/justfile 皆可);没有现成脚本时,最低要求是
     CI 校验 acceptance JSON 存在且 `unverifiable` 为空。

## Agent 加载顺序

会话开始或进入任何 OpenSpec 阶段时,按序加载:

```
openspec/config.yaml
openspec/schemas/superpowers-bridge/schema.yaml
openspec/schemas/superpowers-bridge/PROTOCOL.md
PROJECT_PROTOCOL.md(项目特有约束)
```

在 AGENTS.md / CLAUDE.md 的项目工作流一节写明这个加载顺序。

## 版本与修订

- 流程包内文件的修订走正常变更流程,`schema.yaml` 的 `version` 随语义化递增。
- 跨项目同步:以最新项目的流程包为准整目录覆盖,差异通过 git diff 评审。
