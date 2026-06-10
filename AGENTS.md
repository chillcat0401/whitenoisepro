<!-- context7 -->
Use Context7 MCP to fetch current documentation whenever the user asks about a library, framework, SDK, API, CLI tool, or cloud service -- even well-known ones like React, Next.js, Prisma, Express, Tailwind, Django, or Spring Boot. This includes API syntax, configuration, version migration, library-specific debugging, setup instructions, and CLI tool usage. Use even when you think you know the answer -- your training data may not reflect recent changes. Prefer this over web search for library docs.

Do not use for: refactoring, writing scripts from scratch, debugging business logic, code review, or general programming concepts.

## Steps

1. Always start with `resolve-library-id` using the library name and the user's question, unless the user provides an exact library ID in `/org/project` format
2. Pick the best match (ID format: `/org/project`) by: exact name match, description relevance, code snippet count, source reputation (High/Medium preferred), and benchmark score (higher is better). If results don't look right, try alternate names or queries (e.g., "next.js" not "nextjs", or rephrase the question). Use version-specific IDs when the user mentions a version
3. `query-docs` with the selected library ID and the user's full question (not single words)
4. Answer using the fetched docs
<!-- context7 -->

## Project Workflow

本项目使用 superpowers-bridge 流程包(OpenSpec 工件链 × Superpowers 执行技能的桥接)。会话开始或进入任何 OpenSpec 阶段时,按以下顺序加载并遵循:

```
openspec/config.yaml                                  # 上下文路由 + 执行铁律摘要
openspec/schemas/superpowers-bridge/schema.yaml       # 工件链 + apply 执行循环定义
openspec/schemas/superpowers-bridge/PROTOCOL.md       # 通用协议(分级/证据/红线)
PROJECT_PROTOCOL.md                                   # 本项目特有约束(验证矩阵/合规)
```

要点:tasks.md 是 apply 阶段的运行时队列(每任务 verify→勾选→微提交,失败即停);变更按 L0/L1/L2 分级决定工件要求;归档走 acceptance JSON 严格门禁。

When Context7 MCP is unavailable and the CLI fallback is needed on this machine, run ctx7 commands with the system CA bundle:

```bash
NODE_EXTRA_CA_CERTS=/etc/ssl/cert.pem ctx7 library <name> "<question>"
NODE_EXTRA_CA_CERTS=/etc/ssl/cert.pem ctx7 docs <libraryId> "<question>"
```
