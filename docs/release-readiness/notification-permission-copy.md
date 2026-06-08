# MediaSession Notification Permission Decision

复核日期：2026-06-06

## 产品原则

当前应用只使用 MediaSessionService 自动创建的媒体控制通知，不发送普通、营销或提醒通知。

Android 13+ 将 media session notifications 列为通知权限行为变更的豁免项。因此当前版本：

- 不声明 `POST_NOTIFICATIONS`。
- 不显示 runtime notification permission 弹窗。
- 不在首次播放前增加权限说明流程。
- Settings 只说明后台媒体控制行为。

## Settings 文案

```text
Android 系统在播放时提供锁屏和通知栏控制，不用于营销通知。
```

## 测试场景

### Android 13+ 播放

- Given 用户首次点击播放。
- When 应用切到后台或锁屏。
- Then 不出现通知 runtime permission 弹窗。
- And MediaSession 系统媒体控制可用。

### 非营销保证

- When 应用没有正在播放音频。
- Then 不发送营销或促销通知。

## 重新评估条件

如果未来新增提醒、运营、下载完成或其他非 MediaSession 通知，必须新建 OpenSpec change，重新评估 `POST_NOTIFICATIONS` 和权限请求时机。

官方依据：

- https://developer.android.com/guide/topics/ui/notifiers/notification-permission
- https://developer.android.com/media/media3/session/background-playback
