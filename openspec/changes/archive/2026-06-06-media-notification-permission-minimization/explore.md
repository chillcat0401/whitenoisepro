# Explore: MediaSession 通知权限最小化

日期：2026-06-06

## 官方复核

Android 官方当前说明：

- Android 13+ 的 `POST_NOTIFICATIONS` 适用于非豁免通知。
- Media session notifications 属于豁免项。
- App 不需要 `POST_NOTIFICATIONS` 才能启动 foreground service。
- Media3 `MediaSessionService` 后台播放文档只要求 `FOREGROUND_SERVICE` 和 `FOREGROUND_SERVICE_MEDIA_PLAYBACK`。

当前应用只使用 MediaSessionService 自动创建的 MediaStyle/System UI 媒体通知，不发送营销、提醒或其他普通通知。

## 方案

1. 请求通知权限：不必要，会增加首次播放摩擦。
2. 保留 Manifest 权限但不请求：会扩大 Play Console 权限表面并使 Settings 文案误导。
3. 删除权限并将文案改为后台媒体控制说明：最符合最小权限原则。

推荐方案 3。

