## Context

当前项目已具备 Android MVP 的核心基础：8 个本地第一方程序化生成声音、混音层控制、保存/收藏/重命名/删除、DataStore 恢复、Sleep Timer、MediaSession 后台播放、audio focus、launcher icon、release signing 和 AAB verifier。归档后的 active OpenSpec 为空，可以开始下一条围绕上架可用性的变更。

Google Play 官方要求仍需保持当前复核：

- 2025-08-31 起，新应用和更新需满足 Android 15 / API 35 或更高 target API 要求；当前 `targetSdk = 36`。
- 2023-11-13 之后创建的个人开发者账号通常需要 12 名 tester 连续 14 天 closed testing 后才能申请生产发布权限，实际以 Play Console 账号提示为准。
- 即使当前不收集数据，也必须提交 Data safety form，并保持隐私政策、表单、Manifest、SDK 行为一致。
- 当前只使用 MediaSession 媒体通知，不声明 `POST_NOTIFICATIONS`；未来新增普通通知必须重新评估。

## Goals / Non-Goals

**Goals:**

- 把上架巡查从“分散文档”收敛为一份可执行状态报告。
- 纠正 release-readiness 文档中仍写“四个声音”等陈旧内容。
- 编写可直接用于发布前补全的隐私政策与开发者信息模板，所有未知字段使用明确占位。
- 让 Settings 和发布文案更清晰地表达无账号、无广告、本地偏好、第一方本地声音、后台媒体控制。
- 做少量基础体验增强，帮助 tester 第一次打开后更快播放默认混音并设置 timer。
- 为 closed testing 前真实设备 QA 留出明确人工证据槽位。

**Non-Goals:**

- 不上传 Play Console，不代替账号侧配置。
- 不新增第三方 SDK、analytics、crash reporting、billing、广告或账号。
- 不新增联网声音、下载管理或云同步。
- 不承诺医疗、治疗、改善失眠等健康功效。
- 不大改音频引擎或重新设计 UI 风格。

## Decisions

### 1. 先做 release readiness 一致性，而不是继续扩展声音数量

当前 8 个声音已覆盖 Noise / Nature / Home 的基础场景。继续扩展素材会增加 QA 和包体压力，但不会解决上架阻断。更有效的是补齐真实设备 QA、素材来源说明、隐私 URL、Data safety 和 tester 流程。

备选方案是继续增加 12-16 个声音；该方案适合作为后续内容版本，但不应阻塞首次 closed testing。

### 2. 基础功能增强只做测试友好的小闭环

本次只增强三个点：

- Home / Timer 更明确推荐 30 或 45 分钟入睡 timer。
- 保存混音时避免重复条目。
- Library 分类和推荐声音入口更清晰。

这些点能直接降低 tester 困惑，并且可用 common tests 验证。备选方案是增加 onboarding 或复杂 preset builder；当前不做，因为会扩大 UI 和状态管理范围。

### 3. 文档状态使用 blocked / ready / needs-human-evidence

上架准备包含代码、文档和人工操作。用三态可以避免把“本机可验证”和“需要 Play Console / 真实设备 / 公开 URL”的事项混在一起。

### 4. 隐私政策和开发者信息先模板化，不伪造发布主体

隐私政策需要开发者主体、联系邮箱、公开 URL、生效日期等信息。当前会先建立完整模板，把不确定项写成 `[待补充：...]` 占位，并在上架巡查报告中标为 blocked。这样可以先完成结构、口径和 Settings 对齐，避免为了推进代码而填入不可靠信息。

## Risks / Trade-offs

- Google Play 政策更新 -> 每份 release 文档保留复核日期和官方链接，发布前再做一次复核。
- 隐私政策 URL 仍无法由代码自动完成 -> 巡查报告标为 blocked，直到开发者主体、邮箱和公开 URL 提供。
- 开发者信息占位可能遗漏 Play Console 所需字段 -> 模板覆盖主体、联系邮箱、所在地/地址占位、支持入口、隐私 URL、生效日期和更新日期。
- 真实设备 QA 无法由 CI 证明 -> 文档保留人工证据槽位，执行时只记录已完成设备、系统版本和结果。
- 过度增强基础功能导致延期 -> tasks 只允许小范围 UI / reducer 改动，新增功能必须另开 OpenSpec。
