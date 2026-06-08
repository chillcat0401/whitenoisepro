# 设计说明

## 内容模型

新增 `SettingsContent` presentation model，用纯 Kotlin 表达 Settings 发布前内容：

- section title
- row title
- row subtitle
- row kind
- enabled / disabled

这样可以用 common unit tests 验证 release-readiness 策略，而不依赖 Compose UI 测试。

## UI 渲染

`SettingsScreen` 不再硬编码所有行，而是从 `SettingsContent.releaseReady()` 渲染：

- `ReadOnly` 行使用 `SettingsRow`。
- `Toggle` 行使用 `ToggleRow`。
- `Disabled` 行使用 `SettingsRow`，trailing 显示 `暂不可用`。

## 发布前策略

- `恢复购买`：隐藏。
- `离线下载`：说明态，暂不可用。
- `音质`：只读说明，当前为标准。
- `主题`：只读说明，当前为深色。
- `隐私政策`：展示当前 MVP 隐私定位，后续接 URL。
- `通知权限`：展示后台/锁屏媒体控制说明。

## 测试策略

新增 `SettingsContentTest`：

- release-ready 内容不包含 `恢复购买`。
- release-ready 内容包含隐私与通知说明。
- `离线下载` 是 disabled row，不是 toggle。
- `启动时继续上次混音` 仍是 toggle。

## 风险

- 这不是完整上架隐私政策实现；只是避免 closed testing 前误导。
- 后续如果要打开 URL，需要新增平台边界或导航能力。
