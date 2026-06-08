# Google Play Data Safety Worksheet

复核日期：2026-06-05

当前判断仅覆盖现有 MVP 代码。任何新增 SDK 或数据行为都必须重新评估。

## 当前依赖 / 行为摘要

当前代码未接入：

- analytics。
- crash reporting。
- billing。
- ads。
- account / login。
- cloud sync。
- remote config。

当前 Manifest 权限：

```text
FOREGROUND_SERVICE
FOREGROUND_SERVICE_MEDIA_PLAYBACK
当前不声明 `POST_NOTIFICATIONS`；MediaSession 通知属于 Android 13+ 权限行为豁免项。
```

## 数据收集判断

| 数据类型 | 当前是否收集 | 当前是否分享 | 说明 |
| --- | --- | --- | --- |
| 姓名 | 否 | 否 | 无账号 |
| 邮箱 | 否 | 否 | 无账号 / 无客服表单 |
| 位置 | 否 | 否 | 不请求位置权限 |
| 通讯录 | 否 | 否 | 不请求权限 |
| 照片 / 视频 | 否 | 否 | 不请求权限 |
| 音频录音 | 否 | 否 | 不请求麦克风 |
| 设备或其他 ID | 否 | 否 | 当前无 analytics / ads SDK |
| 应用活动 | 否 | 否 | 当前无 analytics |
| 崩溃日志 | 否 | 否 | 当前无 crash SDK |
| 购买记录 | 否 | 否 | 当前无 billing SDK |

## 本地数据

以下数据设计为设备本地保存，不上传：

- 已保存混音。
- 当前混音。
- 最近混音。
- timer 默认值。
- 用户设置。

如果未来加入云同步，这些项必须重新进入 Data safety。

## Data Safety 初始答案方向

在当前代码行为下：

- Data collected: No。
- Data shared: No。
- Security practices: 需要根据最终实现填写。
- Data deletion: 当前无云端数据；如提供支持邮箱，可作为查询入口说明。

注意：

- Google Play 要求即使不收集数据，也需要完成 Data safety form，并提供隐私政策链接。
- Data safety 必须覆盖同一 package name 在 Google Play 上分发的所有版本和地区。

## 触发重新评估的变更

以下任何变更都必须更新本工作表、隐私政策和 Play Console Data safety：

- 加入 Firebase / Google Analytics / Crashlytics。
- 加入 billing 或订阅。
- 加入登录、账号、云同步。
- 加入广告 SDK。
- 加入客服表单或反馈上传。
- 加入远程配置或 A/B testing。
- 加入第三方音频 CDN 或联网播放。
