# Design: MediaSession 通知权限最小化

## 产品行为

用户首次播放时不弹通知权限请求。MediaSessionService 继续自动发布系统媒体控制。

## Settings

将 row：

```text
通知权限
```

改为：

```text
后台媒体控制
Android 系统在播放时提供锁屏和通知栏控制，不发送营销通知
```

## 文档

保留历史评审原文，但当前状态文档必须说明官方豁免结论。原 `notification-permission-copy.md` 改为“无需 runtime permission”的决策记录和测试场景。

