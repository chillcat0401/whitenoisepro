# 代码审查：Google Play 上架巡查与基础能力增强

**变更：** `google-play-readiness-core-polish`

**审查日期：** 2026-06-08

**审查范围：** release-readiness 文档、隐私政策与开发者信息模板、Settings 信任文案、保存混音去重、推荐入睡 timer intent、Home / Library 轻量入口和相关测试。

## 结论

未发现阻断归档的问题。变更范围与 OpenSpec tasks 对齐，没有引入第三方 SDK、账号、analytics、billing、广告、云同步或联网素材。

## 关注点

- 隐私政策和开发者信息模板正确使用 `[待补充：...]` 占位，没有伪造主体、邮箱、URL、地址或生效日期；这些仍是上架前 blocked owner input。
- `MixReducer` 保存去重按标题、master volume、favorite、层 sound id / volume / muted state 比较内容，避免同一混音重复保存；层顺序变化仍会视为不同组合，这是当前实现的可接受约束。
- 推荐入睡 timer 通过 `StartRecommendedBedtimeTimer` 进入既有 timer 启动流程，复用平台 runtime 和 Mini Player 剩余时间展示，没有新增播放路径。
- Home / Library UI 入口是轻量可撤销改动，不触碰音频引擎、权限或发布签名。

## 验证证据

- 目标单元测试通过。
- 完整 `testDebugUnitTest` 通过。
- `lintDebug` 和 `assembleDebug` 通过。
- OpenSpec strict validation 输出 valid；PostHog 证书错误仅来自 CLI telemetry flush，不影响 validation 结果。
