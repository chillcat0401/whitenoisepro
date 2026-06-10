## Context

当前仓库已完成 UI 现代化、8 个第一方生成声音、Google Play 隐私/开发者信息模板、Data safety worksheet、closed testing feedback loop、商店图标和 release signing 基础设施。自动化和模拟器 smoke test 显示基础播放、timer、Settings 信任文案可运行，但上架仍受外部材料和真实设备证据阻塞。

Google Play 当前要求新 app / update target Android 15/API 35+；项目 `targetSdk = 36` 已满足。若开发者账号是 2023-11-13 之后创建的个人账号，Play Console 通常要求 closed testing 中至少 12 名 tester 连续 opt-in 14 天后才能申请 production access。该 14 天窗口无法通过继续开发压缩，因此应尽快启动。

## Goals / Non-Goals

**Goals:**

- 产出一个可执行的 closed testing launch package。
- 明确进入 internal testing、closed testing 和 production access 申请前的 gate。
- 将 tester 招募、测试路径、反馈分类和 production access 回答素材结构化。
- 保持当前“无账号、无广告、无分析、无联网用户数据上传”的低政策风险边界。
- 支持开发者快速补齐外部占位字段，而不是在代码中虚构信息。

**Non-Goals:**

- 不新增 analytics、Crashlytics、广告、billing、账号、云同步或远程配置。
- 不在本轮继续扩充声音库或大改 UI。
- 不自动替代 Play Console 操作；账号侧截图、提交和审核结果仍需人工执行和记录。
- 不承诺医学、治疗、改善失眠或健康效果。

## Decisions

### Decision 1: 先 closed testing，后产品增强

采用“release package + tester feedback loop”的最短路径。替代方案是继续完善产品功能，但这会延迟 14 天 testing window，而且缺少真实反馈支撑优先级。

### Decision 2: 用人工反馈表代替内置 analytics

closed testing 第一轮使用外部文档/表格记录 tester 反馈，不接入 analytics 或 crash SDK。替代方案是在 App 内集成反馈和遥测，但会扩大隐私政策、Data safety、第三方依赖和审核风险。

### Decision 3: Release gate 只允许外部输入占位，不允许假装 ready

隐私政策 URL、开发者主体、邮箱、签名密钥、Play Console 状态和 tester 名单必须标记为 `blocked` 或 `ready`，不能用推测值填充。这样可以保持上架材料与真实账号状态一致。

### Decision 4: 真实设备 QA 是 closed testing 前 gate

模拟器可以验证 UI 和基础播放状态，但声音听感、loop 断点、后台播放、锁屏媒体控制、蓝牙和音频焦点必须至少用一台真实 Android 设备人工验证。若发现 P0/P1，先修复再提交 closed testing。

## Risks / Trade-offs

- Play Console 要求与账号状态不一致 -> 以 Play Console 当前提示为准，并在 launch package 中记录截图/日期。
- Tester 不活跃导致 14 天窗口失败 -> 招募 15-20 人，记录 opt-in 日期、提醒节奏和覆盖路径。
- 无 analytics 导致反馈颗粒度低 -> 使用结构化 tester 表、问题分类和生产访问回答草稿弥补。
- 隐私 URL 或开发者信息未补齐 -> 明确阻塞，不提交 closed testing。
- 真机 QA 发现音频 P1 问题 -> 暂停上架，创建独立修复 change。

## Migration Plan

1. 整理 launch checklist、Play Console 输入清单、store listing 草案、screenshot manifest、tester roster 和 feedback tracker。
2. 复核隐私政策、开发者信息和 Data safety；外部字段保持占位直到开发者确认。
3. 生成 signed release AAB，并运行 bundle verifier。
4. 在模拟器和至少一台真实 Android 设备执行 release candidate smoke/audio QA。
5. 先走 internal testing，再将同一 release candidate 或修复后的新版本提交 closed testing。
6. closed testing 期间按 Day 0 / Day 2-3 / Day 7 / Day 12-14 节奏收集反馈和 production access 素材。

## Open Questions

- Play Console 账号是否为 2023-11-13 之后创建的个人开发者账号，以及是否明确显示 12 tester / 14 day 要求。
- 隐私政策将发布到哪个公开 HTTPS URL。
- 开发者展示名、主体、支持邮箱和隐私联系邮箱是什么。
- signed release upload key 是否已经离线备份。
- 首批 tester 是否能达到 15-20 人，并覆盖至少一台国产 Android、一台 Pixel/原生系统和一台蓝牙耳机/音箱场景。

