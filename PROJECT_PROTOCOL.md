# WhiteNoisePro Project Protocol

本项目流程遵循通用流程包 **superpowers-bridge**,会话开始或进入任何 OpenSpec 阶段时按以下顺序加载:

```
openspec/config.yaml
openspec/schemas/superpowers-bridge/schema.yaml
openspec/schemas/superpowers-bridge/PROTOCOL.md
PROJECT_PROTOCOL.md(本文件,仅项目特有约束)
```

核心规则、变更分级(L0/L1/L2)、apply 七步循环(SPAWN→TDD→VERIFY→AUDIT→RECORD→COMMIT→NEXT)、证据目录、归档门禁、红线,均以流程包为准,本文件不重复流程性内容。

## 项目铁律

- L2 级变更在 OpenSpec 提案被创建并确认之前不得实现;L0/L1 按流程包分级执行。
- 新文档默认中文;技术标识符、代码符号、命令、官方产品名可保留英文。存量英文文档不回溯翻译,除非用户明确要求。
- 不引入 Google Play Services 强依赖,除非 spec 显式允许(HarmonyOS 与国内分发前提)。
- 在合规路径确认前,不引入对海外服务的强依赖(广告、支付、统计、崩溃上报)。

## 技能映射

- Explore:优先 `/opsx:explore`,缺省时 `superpowers:brainstorm`。
- Propose / Archive:`/opsx:propose`、`/opsx:archive`、`/opsx:apply`。
- Apply:优先 `superpowers:subagent-driven-development`,单人小变更可降级 `superpowers:executing-plans`;TDD 用 `superpowers:test-driven-development`。

## 项目验证矩阵(任务 verify 命令的取材来源)

全量门禁:`./gradlew :composeApp:check :composeApp:assembleDebug`(check 含 verifyArchiveAcceptance)。

UI 验证:
- 模拟器/真机运行,截图核对 360x800、390x844、430x932 三档。
- 无文本溢出;底部导航与 Mini Player 不重叠;触控目标 ≥44px;与 design.md / 设计稿一致。
- 深色低亮度主题(暖色基调),主播放动作一眼可见,单手可操作。

音频验证:
- 播放/暂停、多层混音、单层音量、主音量、静音层不发声。
- 后台播放、睡眠定时、淡出、锁屏/通知控制。
- 新增素材:loop 接缝听测(手机扬声器 + 耳机 + 低音量三档);响度对齐 RMS ≈ -29~-32 dB、峰值 ≤ -2 dB;登记 `docs/audio-assets/external-release-audio-manifest.json`;许可证链完整(CC0 快照,流程见 `docs/audio-assets/external-audio-source-intake.md`,工具 `tools/fetch_freesound_audio.mjs`)。

状态验证:
- 保存/删除/收藏混音、恢复上次播放、空状态、错误状态。
- 旧 soundId 迁移(`SoundCatalog.canonicalId`)不丢用户数据。

平台与合规验证:
- Android 权限最小化(不声明不必要的 `POST_NOTIFICATIONS`)。
- 隐私政策、Data safety 与代码行为一致(`docs/release-readiness/`)。
- 涉及大陆/鸿蒙分发的任务:备案、商店审核材料、最小权限、AppGallery 兼容、HarmonyOS NEXT 原生实现评估。

## 归档验收(项目实现细节)

- 记录:`docs/superpowers/acceptance/<change-id>.json`;strict / retrospective 语义见流程包 PROTOCOL.md。
- 门禁命令:
  ```bash
  node tools/verify_archive_acceptance.mjs --change <change-id>   # 单变更
  node tools/verify_archive_acceptance.mjs --all-archives         # 全量(Gradle check 自动执行)
  ```
- 当前测试通过不证明历史 RED→GREEN 发生过;禁止在无仓库证据时补勾历史计划或清空 `unverifiable`。

## 产品与技术约束

产品定位:夜间低干扰白噪音助眠工具,不是营销站、不是声音商城。MVP 七屏:Home/Mixer/Library/Timer/Saved/Settings/MiniPlayer。体验优先级:夜间柔和、半睡半醒可操作、播放稳定、变现不打扰。避免:营销首屏、长引导、全屏广告式 UI、刺眼亮色。

技术:
- 遵循既有目录结构、状态管理与 WnpTheme 设计令牌体系;新依赖(UI kit、状态库、音频库)必须有任务与理由。
- 平台能力封装在 androidMain,业务逻辑留在 commonMain(为 HarmonyOS ArkTS 翻译保留同构性)。
- 持久化数据保持可迁移;关注包体(音频策略:噪声运行时合成、自然声 Opus、扩展包后续走下载)。

商业路线参考 `docs/launch-and-monetization-roadmap.md`(免费 + 买断,后续订阅内容包)。

## 偏差与交付格式

Spec 偏差(流程包 AUDIT/HALT 触发)时输出:

```text
Spec deviation detected:
Reason: / Impact: / Recommended update: / Need user confirmation:
```

完整功能交付的收尾输出包含:Summary、change-id、Tasks completed、Tests/verification、Files changed、Risks、Recommended next step。
