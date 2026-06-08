# Explore: Settings 信任与占位项硬化

日期：2026-06-06

## 输入

用户要求继续围绕 WhiteNoisePro 进行 OpenSpec explore，并自动执行后续任务。

相关依据：

- `docs/third-party-product-review-2026-06-05.md`
- `docs/release-readiness/first-run-and-settings-strategy.md`
- `docs/release-readiness/notification-permission-copy.md`
- `docs/release-readiness/privacy-policy-draft.md`
- 当前 `SettingsScreen`

## 当前状态

Settings 当前展示：

- 主题：跟随系统
- 音质：标准，节省空间
- 启动时继续上次混音
- 离线下载 toggle
- 隐私政策：应用上架前补齐正式链接
- 恢复购买：为后续付费能力预留
- 版本

问题：

- `恢复购买` 看起来像可用功能，但当前没有 billing SDK。
- `离线下载` 看起来可切换，但当前没有真实离线下载行为。
- `隐私政策` 文案仍是“上架前补齐”，不适合 closed testing。
- 缺少通知权限用途说明。
- 缺少“无账号、无广告、偏好本地保存”的信任定位。

## 可选方案

### 方案 A：只改文案

优点：

- 实现最快。
- 视觉风险低。

缺点：

- placeholder 仍可能以可用入口出现。
- 缺少可测试的内容模型，后续容易回退。

### 方案 B：引入 Settings 内容模型，按 release gate 渲染

优点：

- 可用单元测试锁定哪些项显示、禁用、隐藏。
- UI 与 release-readiness 策略绑定更清晰。
- 后续接入真实功能时只需更新模型。

缺点：

- 比纯文案多一个 presentation 层对象。

### 方案 C：新增完整隐私政策二级页面

优点：

- 更接近上架体验。

缺点：

- 当前没有导航栈，仅用 tab 状态。
- 会引入更多 UI 状态，超出本次低风险硬化范围。

## 推荐

采用方案 B。

本次只做 Settings 内容模型和渲染调整：

- 隐藏 `恢复购买`。
- 将 `离线下载` 改为禁用/说明态，不提供可切换假功能。
- 将 `主题` 与 `音质` 改为只读说明态。
- 增加隐私定位：无账号、无广告、偏好本地保存。
- 增加通知权限说明：只用于后台/锁屏媒体控制，不用于营销。
- 保留 `启动时继续上次混音`，因为 release gate 后续会接真实持久化。

不新增 WebView，不新增外部 URL 打开，不接入 Android runtime permission 请求。
