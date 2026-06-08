# MediaSession 通知权限最小化

## Why

当前产品只使用 Android MediaSession 媒体通知，不需要 `POST_NOTIFICATIONS`。继续声明该权限会增加不必要的授权流程，并使 Settings 和发布文档对用户产生误导。

## What Changes

- 删除 Manifest 的 `POST_NOTIFICATIONS`。
- Settings 将“通知权限”改为“后台媒体控制”。
- 文案说明媒体控制由 Android MediaSession 提供，不用于营销推送。
- 更新权限、Data safety、release checklist 和 first-run 文档。
- 添加测试和 Manifest 静态验证。

## 非目标

- 自定义普通通知。
- 营销、提醒或定时通知。
- 修改 MediaSessionService 播放逻辑。

## 验收

- Manifest 不包含 `POST_NOTIFICATIONS`。
- Settings 不声称需要通知权限。
- 当前 release 文档不再要求 runtime notification permission。
- 后台播放和系统媒体控制构建能力保持不变。
