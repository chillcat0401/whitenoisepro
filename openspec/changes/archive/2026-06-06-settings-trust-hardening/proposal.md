# Settings 信任与占位项硬化

## 背景

release-readiness 文档要求 closed testing 前必须处理 Settings placeholder：

- 已实现。
- 明确禁用。
- 暂时隐藏。

当前 Settings 仍展示 `恢复购买` 和 `离线下载` 等未实现能力，且隐私政策仍是“应用上架前补齐正式链接”。这会误导 tester，也会削弱“隐私友好、无账号、无广告、本地保存”的产品定位。

## 范围

本变更包含：

- 增加可测试的 Settings 内容模型。
- 隐藏未接入 billing 的 `恢复购买`。
- 将 `离线下载` 从可切换 toggle 改为说明态。
- 将 `主题`、`音质` 明确为只读状态。
- 增加隐私与信任说明。
- 增加通知权限用途说明。
- 更新 Settings UI 使用内容模型渲染。

本变更不包含：

- 接入 Google Play Billing。
- 打开外部隐私政策 URL。
- 请求 Android 通知 runtime permission。
- 新增导航栈或 WebView。
- 真实持久化实现。

## 成功标准

- Settings 不再以可用功能形式展示 `恢复购买`。
- Settings 不再允许用户切换未实现的 `离线下载`。
- Settings 展示隐私定位和通知权限说明。
- 相关内容由 common 层 presentation model 提供，并有单元测试覆盖。
- Gradle `check` 和 `assembleDebug` 通过。
