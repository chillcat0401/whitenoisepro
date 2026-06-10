# Tasks(L1:媒体通知缺失 + 后台进程冻结修复)

背景:API 33+ 未声明 POST_NOTIFICATIONS → appop ignore → Media3 无法发媒体通知
→ 服务未升前台 → 熄屏后进程被冻结,后台播放不可靠。2026-06-06 的豁免结论实测不成立。

- [x] 1.1 声明并请求通知权限,恢复媒体通知与前台服务
  - accept: API 33+ 首启请求权限;授权后播放出现媒体通知;熄屏 ≥35s 服务不被销毁、
    会话保持 PLAYING;锁屏出现媒体控制
  - verify: ./gradlew :composeApp:check && 模拟器实测(通知/冻结/锁屏控制)
  - scope: composeApp/src/androidMain/, composeApp/build.gradle.kts

- [x] 1.2 纠偏发布文档(权限决定、checklist、worksheet)
  - accept: notification-permission-copy.md 记录新结论与实证依据;
    google-play-checklist 与 play-console worksheet 权限声明同步
  - verify: 文档互相一致,无"不声明 POST_NOTIFICATIONS"残留
  - scope: docs/release-readiness/
