# WhiteNoisePro Privacy Policy Draft

状态：模板草案，发布前需要补齐所有 `[待补充：...]` 占位。不得使用未确认的开发者主体、邮箱、地址、URL 或生效日期。

## 隐私政策

WhiteNoisePro 是一款白噪音和助眠声音应用。当前 MVP 设计目标是提供本地播放、混音、定时器和设置保存能力。

## 开发者信息

- 应用名称：WhiteNoisePro
- 开发者主体：[待补充：个人或公司法定/展示名称]
- 隐私联系邮箱：[待补充：privacy/support email]
- 支持邮箱：[待补充：support email，可与隐私联系邮箱相同]
- 开发者所在地 / 地址：[待补充：按 Play Console 和公开政策要求填写]
- 隐私政策公开 URL：[待补充：GitHub Pages URL，例如 `https://chillcat0401.github.io/whitenoisepro/privacy-policy.html`]
- 生效日期：[待补充：YYYY-MM-DD]
- 最近更新日期：[待补充：YYYY-MM-DD]

## 当前数据处理声明

当前 MVP 不包含：

- 用户账号。
- 广告 SDK。
- 第三方 analytics。
- 云同步。
- 社交分享 SDK。
- 精确位置功能。
- 通讯录、相册、麦克风或摄像头访问。

当前应用偏好和混音状态设计为保存在设备本地，例如：

- 当前混音。
- 已保存混音。
- 最近使用混音。
- timer 默认值。
- 主题、音质和离线下载等设置偏好。

这些本地偏好不会上传到开发者服务器。

## 音频素材来源

当前 MVP 使用随应用打包的本地声音，包括第一方程序化生成噪声音频，以及经过处理的 Freesound Creative Commons 0 外部录音素材。这些声音不通过网络下载，也不需要用户提供音频、麦克风或录音权限。

## 权限用途

应用可能使用以下 Android 权限：

- Foreground service：用于后台持续播放助眠声音。
- Foreground service media playback：用于声明后台媒体播放类型。

当前 Manifest 不声明 `POST_NOTIFICATIONS`。应用仅使用 Android MediaSession 媒体控制，让系统在播放时提供后台 / 锁屏控制；这些控制不用于营销通知。

## 数据共享

当前 MVP 不向第三方共享个人数据。

如果未来接入 crash reporting、analytics、billing、广告、云同步或客服系统，本政策和 Google Play Data safety 答案必须先更新。

## 数据保存和删除

当前本地偏好保存在用户设备上。用户可以通过卸载应用清除本地数据。

如果未来增加账号或云同步功能，需要提供更明确的数据删除路径。

## 安全处理

当前 MVP 不传输用户数据到开发者服务器。设备本地数据由 Android 应用沙箱保护。

## 联系方式

如对隐私政策有疑问，请联系：

```text
[待补充：privacy/support email]
```

## 发布要求

Google Play 要求隐私政策：

- 在 Play Console 指定字段提供链接。
- 在应用内提供链接或文本。
- URL 必须公开可访问、非 PDF、非地理封锁、不可编辑。
- 即使应用不收集个人和敏感数据，也必须提交隐私政策。
