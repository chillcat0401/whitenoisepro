# MediaSession Notification Permission Decision

复核日期:2026-06-10(推翻 2026-06-06 结论)

## 结论变更

2026-06-06 结论假设"媒体会话通知豁免 POST_NOTIFICATIONS",**实测不成立**
(模拟器 API 36,appop 显示 ignore):无权限时 Media3 无法发媒体通知,
服务无法升前台,熄屏约 30 秒后进程被系统冻结(`ActivityManager: freezing`),
后台播放中断。另修复了根因之一:服务内建的 MediaSession 未 `addSession()`
注册(本应用无 MediaController 连接,不会触发 onGetSession 自动注册)。

## 当前决定

- Manifest 声明 `POST_NOTIFICATIONS`,仅用于媒体控制通知与前台服务可见性。
- API 33+ 首次启动时请求一次;拒绝后不再纠缠(前台播放可用,
  但后台稳定性下降、锁屏无媒体控制)。
- 不发送营销、提醒或任何非 MediaSession 通知;新增通知类型必须新建
  OpenSpec change 重新评估。

## Settings 文案

```text
通知权限仅用于播放控制(通知栏与锁屏)和后台播放保活,不用于营销通知。
```

## 验证记录(2026-06-10,模拟器 API 36)

- 授权 + addSession 修复后:媒体通知出现(id=1001),isForeground=true;
- 熄屏 40 秒:无 freezing 日志,会话保持 PLAYING;
- 锁屏媒体卡片可暂停/恢复(状态随点击切换)。
