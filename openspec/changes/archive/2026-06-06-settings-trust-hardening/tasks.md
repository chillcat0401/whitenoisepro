# Tasks

## 1. Settings 内容模型

- [x] 1.1 添加 SettingsContent presentation model
  - Acceptance: 模型能表达 section、read-only row、toggle row、disabled row。
  - Acceptance: release-ready content 隐藏 Restore Purchases。
  - Verification: common unit test。

- [x] 1.2 添加 release-ready Settings 内容测试
  - Acceptance: 测试覆盖隐私、通知权限、离线下载禁用、启动时继续上次混音 toggle。
  - Verification: 先看到测试红灯，再实现通过。

## 2. Settings UI

- [x] 2.1 SettingsScreen 使用 SettingsContent 渲染
  - Acceptance: Settings 展示隐私定位和通知权限说明。
  - Acceptance: Restore Purchases 不展示。
  - Acceptance: Offline Downloads 不再是可切换假功能。
  - Verification: Gradle check；必要时 emulator 截图。

## 3. 文档与验证

- [x] 3.1 更新 release-readiness 状态说明
  - Acceptance: 文档说明 Settings placeholder 已完成第一轮 UI 硬化。
  - Verification: `rg` 检查旧的“为后续付费能力预留”仍只存在于历史评审或策略文档，不存在生产 UI。

- [x] 3.2 运行构建验证
  - Acceptance: `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug` 成功。
  - Verification: 命令输出 `BUILD SUCCESSFUL`。
