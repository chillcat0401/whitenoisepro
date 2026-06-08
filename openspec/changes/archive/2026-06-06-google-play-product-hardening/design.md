# 设计说明

## 复核结论

`third-party-product-review` 的核心判断有效：

- 当前工程已具备 Android-first Compose Multiplatform MVP 基础。
- 当前仍缺少可发布级声音内容、隐私政策、Data safety、closed testing 计划和真实设备音频 QA。
- Google Play 上架准备不能只看成构建问题，它直接影响产品验证节奏。
- HarmonyOS 应保持独立 spike，不阻塞 Google Play MVP。

官方资料复核日期：2026-06-05。

已复核来源：

- Google Play 新个人开发者账号 closed testing 要求。
- Google Play User Data policy。
- Google Play Data safety form 指南。
- Google Play target API level 要求。

## 计划结构

本变更分为四个工作包：

1. Release readiness：把 internal、closed、open、production track 的进入条件和退出条件写清楚。
2. Privacy readiness：把“无账号、无广告、无云同步、偏好本地保存”的产品承诺转成隐私政策、Data safety 和 Settings 文案。
3. Audio readiness：把 `silence_loop.wav` 从播放管线验证资产降级为测试资产，并定义真实 loop 资产验收门槛。
4. Platform boundary：固化 common/shared core 不依赖 Google-only 服务的规则，为 Huawei / HarmonyOS 保留路径。

## 关键决策

### 1. 先做文档化 release gate，再做生产代码

原因：

- 当前缺口首先是发布准备和验收标准不完整。
- 没有 checklist，后续容易把 placeholder UI、测试音频或未发布隐私政策误认为可上架状态。
- 文档 gate 可以立刻降低误判风险。

### 2. 隐私定位采用最小数据策略

当前代码没有 analytics、账号、广告、云同步。应将此作为产品优势，但必须与实际行为保持一致。

目标表述：

```text
无账号、无广告、偏好本地保存、支持离线播放、后台播放只用于睡眠音频控制。
```

### 3. 真实音频资产是产品 MVP gate

`silence_loop.wav` 只能证明 Media3 管线可运行，不能证明用户体验可用。closed testing 前至少需要一组授权明确、loop 质量可接受、响度一致的声音资产。

### 4. Settings placeholder 必须分流

每个 placeholder 发布前只能进入三种状态之一：

- 已实现并可用。
- 显示为明确禁用态并说明原因。
- 暂时隐藏。

不能以可用入口形式展示未实现能力。

## 验证策略

- 文档类任务：检查文件存在、覆盖 checklist 项、无旧结论冲突。
- 代码类任务：后续执行时必须 TDD，且重新跑 Gradle `check`。
- UI 类任务：继续使用 360x800、390x844、430x932 截图验证。
- 音频类任务：至少一台真实 Android 设备完成手动 QA 后才能作为 closed testing gate 通过。
