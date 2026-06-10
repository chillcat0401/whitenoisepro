# Google Play 上架巡查报告

复核日期：2026-06-08

状态说明：

- `ready`：当前仓库证据支持。
- `blocked`：需要开发者、Play Console 或公开 URL 等外部输入。
- `needs-human-evidence`：需要真实设备、人工试听或账号侧操作记录。

## 总结

WhiteNoisePro 当前代码基础可继续准备 internal testing，但不应直接进入 closed testing。主要阻断项是隐私政策公开 URL、开发者主体和邮箱、真实设备音频 / 后台播放 QA、Play Console closed testing 组织证据。代码侧 targetSdk、最小权限、签名 AAB 基础设施和本地声音目录处于较好状态。

## 巡查表

| 项目 | 状态 | 证据 / 缺口 |
| --- | --- | --- |
| target API | ready | `targetSdk = 36`，满足当前 API 35+ 要求 |
| package name | ready | `com.whitenoisepro` |
| release signing | ready | Gradle release signing 和 AAB verifier 已实现；upload key 仍需离线备份证据 |
| Play App Signing | needs-human-evidence | 需要 Play Console 启用和 upload certificate 核对截图/记录 |
| Manifest 权限 | ready | 仅声明 `FOREGROUND_SERVICE` 和 `FOREGROUND_SERVICE_MEDIA_PLAYBACK` |
| notification runtime permission | ready | 当前不声明 `POST_NOTIFICATIONS`，仅 MediaSession 媒体控制 |
| Data safety | ready | 当前无账号、无广告、无 analytics、无 crash SDK、无 billing、无联网上传 |
| 隐私政策 URL | blocked | 需要公开 https URL，非 PDF，非登录页，非地理封锁 |
| 开发者信息 | blocked | 需要主体/展示名、支持邮箱、隐私邮箱、所在地/地址占位确认 |
| 音频资产来源 | ready | 8 个第一方程序化生成声音和 11 个 Freesound CC0 处理素材，有生成/处理脚本和 manifest |
| 音频真实设备 QA | needs-human-evidence | 需要至少一台 Android 真机完成听感、loop、后台、锁屏、蓝牙和打断测试 |
| closed testing tester | blocked | 若账号适用，需要至少 12 名 tester 连续 opt-in 14 天；建议招募 15-20 人 |
| store listing 文案 | blocked | 需要开发者确认品牌描述、截图、短描述、完整描述和国家/地区 |
| 健康声明风险 | ready | 当前文档要求不承诺治疗、治愈、改善失眠或医学效果 |

## 近期优先级

1. 补齐开发者主体、支持邮箱、隐私联系邮箱、隐私政策 URL。
2. 用 signed release AAB 跑一轮 internal testing 安装和基础路径验证。
3. 完成真实设备音频 QA 矩阵，并记录设备型号、Android 版本、耳机/蓝牙条件。
4. 准备 15-20 名 tester 和 closed testing 反馈收集渠道。
5. 发布前再次复核 Google Play target API、User Data、Data safety、Foreground Service 和 Store Listing policy。
