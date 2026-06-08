# Tasks

## 1. Release Readiness

- [x] 1.1 建立 Google Play release-readiness checklist
  - Acceptance: checklist 覆盖 internal、closed、open、production track。
  - Acceptance: 明确 closed testing 的 tester 数量、连续天数、反馈收集和生产访问申请材料。
  - Verification: checklist 文档存在并引用官方复核日期。

- [x] 1.2 建立 closed testing feedback loop
  - Acceptance: 包含 tester 招募、测试说明、反馈模板、问题分类和迭代节奏。
  - Verification: feedback 模板可直接复制给 tester 使用。

## 2. Privacy and Compliance Readiness

- [x] 2.1 建立最小隐私政策草案
  - Acceptance: 覆盖开发者信息、联系渠道、数据收集、数据共享、数据保存/删除、安全处理、政策标题。
  - Acceptance: 明确无账号、无广告、无云同步、偏好本地保存的当前行为。
  - Verification: 草案可作为发布网页内容的基础。

- [x] 2.2 建立 Data safety 工作表
  - Acceptance: 按当前代码行为列出是否收集、分享、处理用户数据。
  - Acceptance: 标注未来加入 analytics、crash reporting、billing 后需要重新填写。
  - Verification: 工作表与 Manifest、依赖和隐私政策草案一致。

- [x] 2.3 补充通知权限说明和测试场景
  - Acceptance: 解释 Android 13+ 通知权限用于后台/锁屏媒体控制，不用于营销推送。
  - Acceptance: 包含授权、拒绝、稍后授权和后台播放场景。
  - Verification: 文档与 `AndroidManifest.xml` 权限一致。

## 3. Audio Product Readiness

- [x] 3.1 建立 MVP 声音资产验收标准
  - Acceptance: 覆盖 loop click、响度一致性、授权、包体、命名、分类、默认混音。
  - Acceptance: 明确 `silence_loop.wav` 仅为测试资产。
  - Verification: 标准能用于评审每一个候选音频文件。

- [x] 3.2 建立手动音频 QA 矩阵
  - Acceptance: 覆盖长 loop、多层播放、audio focus、蓝牙/耳机、锁屏、timer stop/fade、真实设备。
  - Verification: closed testing 前至少一台真实 Android 设备完成矩阵。

## 4. Product Experience Hardening

- [x] 4.1 定义 first-run 默认体验
  - Acceptance: 包含默认混音、主播放动作、推荐 timer、无需多页 onboarding。
  - Verification: 后续 UI 任务能按此验收。

- [x] 4.2 定义 Settings placeholder 策略
  - Acceptance: 每个 placeholder 被标记为实现、禁用或隐藏。
  - Acceptance: Restore Purchases、Offline Downloads、Audio Quality、Theme 有 closed testing 前状态。
  - Verification: UI 实现前有明确决策。

## 5. Platform Boundary

- [x] 5.1 固化 shared core 不依赖 Google-only 服务规则
  - Acceptance: 文档明确 common source set 不直接依赖 Google Play Services、billing、analytics 或 crash SDK。
  - Acceptance: 未来平台 SDK 必须通过平台边界封装。
  - Verification: 依赖与 source set 检查没有冲突。

## 6. Verification

- [x] 6.1 复核 release-readiness 文档一致性
  - Acceptance: 没有与 `third-party-product-review`、`compliance-readiness`、`android-permissions` 冲突的结论。
  - Verification: `rg` 检查旧 blocker 和冲突文案。

- [x] 6.2 运行当前构建验证
  - Acceptance: `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug` 成功。
  - Verification: 记录命令输出摘要。
