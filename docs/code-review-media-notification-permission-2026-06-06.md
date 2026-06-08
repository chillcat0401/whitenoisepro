# Code Review: MediaSession 通知权限最小化

日期：2026-06-06

## 结论

未发现 blocker。

## 结果

- Manifest 已删除 `POST_NOTIFICATIONS`。
- 合并后的 debug Manifest 不包含该权限。
- Settings 已将“通知权限”改为“后台媒体控制”。
- 当前状态文档已按 Android 官方 MediaSession exemption 修正。
- 未添加不必要的 runtime permission 弹窗。

## 验证

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug
```

结果：`BUILD SUCCESSFUL`。

## 后续条件

如果应用未来新增下载完成、提醒、运营或其他非 MediaSession 通知，必须重新评估 `POST_NOTIFICATIONS`。

