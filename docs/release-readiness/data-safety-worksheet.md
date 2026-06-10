# Google Play Data Safety Worksheet

复核日期：2026-06-08

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
```

当前不声明 `POST_NOTIFICATIONS`；仅使用 MediaSession 媒体控制，不用于营销通知。

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

## 音频与媒体输入

当前应用不收集用户音频，不请求麦克风，不上传播放记录。发布声音均为本地打包音频，包括八个第一方程序化生成音频和 11 个 Freesound Creative Commons 0 处理录音：

- 白噪声。
- 粉噪声。
- 棕噪声。
- 柔和风扇。
- 细雨。
- 远海。
- 夜林。
- 暖炉。
- 软雨。
- 轻屋顶雨。
- 窗雨。
- 屋顶雨。
- 柔和海浪。
- 海浪。
- 海岸。
- 火焰噼啪。
- 炉火。
- 落地风扇。
- 林间风。

音频 manifest 位于 `docs/audio-assets/generated-audio-manifest.json` 和 `docs/audio-assets/external-release-audio-manifest.json`。

## Data Safety 初始答案方向

在当前代码行为下：

- Data collected: No。
- Data shared: No。
- Security practices: 当前无用户数据传输；本地偏好由 Android 应用沙箱保护。
- Data deletion: 当前无云端数据；用户可卸载应用清除本地数据。支持邮箱需要在开发者信息模板补齐后同步。

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
- 改变 Manifest 权限或新增普通通知。

## 2026-06-10 复核

- 代码行为未变化:无网络权限、无 SDK、无数据收集/共享,全部偏好仅 DataStore 本地存储。
- 新增的运行时噪声合成与新素材均为本地文件/本地计算,不影响 Data safety 答案。
- 隐私政策 URL 已就绪(见 developer-info-template.md),与 Settings 文案一致性待开发者主体确认后终检。
