# Google Play Store Listing Draft

状态：草案。发布前需要开发者确认目标语言、地区、分类、截图和公开联系信息。

## App Name

WhiteNoisePro

## Short Description

Turn the world down a little with local sleep sounds, mixes, and a fade-out timer.

中文备选：

```text
把世界调低一点。本地助眠声音、混音与淡出定时。
```

## Full Description

```text
Turn the world down a little.

WhiteNoisePro is a quiet local sleep sounds app for bedtime, rest, and focused background listening. Use white noise, gentle rain, distant ocean, night forest, fan, and fireplace sounds to leave one calm layer between you and the noise around you.

Features:
- White, pink, and brown noise
- Gentle fan, rain, ocean, forest, fireplace, wind, and room ambience sounds
- Ready-made calming mixes
- Mixer controls for sound layers and volume
- Sleep timer with quick presets and fade-out behavior
- Saved mixes for repeated use
- Background and lock-screen media controls
- No account, no ads, and no analytics in the current release

The current release uses local audio packaged inside the app, including first-party generated noise and Creative Commons 0 processed recordings. Preferences and saved mixes are stored locally on your device.

WhiteNoisePro does not provide medical advice and does not claim to treat, cure, or diagnose sleep conditions.
```

中文备选：

```text
把世界调低一点。

WhiteNoisePro 是一款安静、低打扰的本地助眠声音应用，适合睡前、休息和专注背景播放。用白噪声、细雨、远海、夜林、风扇和炉火，为周围的杂音留出一层刚好的声音。

功能：
- 白噪声、粉噪声、棕噪声
- 柔和风扇、细雨、远海、夜林、暖炉声音
- 预设助眠混音
- 声音层与音量控制
- 支持快速选择和淡出的睡眠定时器
- 可保存常用混音
- 后台和锁屏媒体控制
- 当前版本无账号、无广告、无分析

当前版本使用随应用本地打包的声音，包括第一方生成噪声和经过处理的 Creative Commons 0 录音素材。偏好和已保存混音仅保存在设备本地。

WhiteNoisePro 不提供医疗建议，也不承诺治疗、治愈或诊断睡眠问题。
```

## Release Notes

```text
Initial closed testing release:
- Local white noise and sleep sound playback
- 19 local bundled sound loops
- Mixer, saved mixes, and sleep timer
- Background media playback controls
- Brand-led Home copy: "把世界调低一点。"
- Privacy and sound source explanations in Settings
```

## Screenshot Inventory

已有截图：

```text
work/android-screenshots/360x800/home.png
work/android-screenshots/360x800/library.png
work/android-screenshots/360x800/mixer.png
work/android-screenshots/360x800/timer.png
work/android-screenshots/360x800/saved.png
work/android-screenshots/360x800/settings.png
work/android-screenshots/430x932/home.png
work/android-screenshots/430x932/library.png
work/android-screenshots/430x932/mixer.png
work/android-screenshots/430x932/timer.png
work/android-screenshots/430x932/saved.png
work/android-screenshots/430x932/settings.png
```

release smoke 截图：

```text
/tmp/wnp-release-smoke/home.png
/tmp/wnp-release-smoke/library.png
/tmp/wnp-release-smoke/mixer.png
/tmp/wnp-release-smoke/timer.png
/tmp/wnp-release-smoke/saved.png
/tmp/wnp-release-smoke/settings.png
```

上传前建议：

- 使用 release smoke 或重新截取 release build 截图。
- 避免截图中出现未确认的隐私 URL、开发者主体或 debug placeholder。
- 文案不使用“治疗失眠”“保证入睡”“医学级”等表述。

## Store Asset References

| 资产 | 路径 | 状态 |
| --- | --- | --- |
| Google Play icon | `docs/store-assets/google-play-icon-512.png` | ready |
| Adaptive launcher icon | `composeApp/src/androidMain/res/mipmap-anydpi-v26/ic_launcher.xml` | ready |
| Round launcher icon | `composeApp/src/androidMain/res/mipmap-anydpi-v26/ic_launcher_round.xml` | ready |
| Feature graphic | [待补充：1024x500 PNG/JPG] | blocked |
