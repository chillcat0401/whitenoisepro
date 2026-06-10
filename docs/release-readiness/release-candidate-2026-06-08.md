# Release Candidate Verification Record

日期：2026-06-08

## Release Candidate

| 字段 | 值 |
| --- | --- |
| Package name | `com.whitenoisepro` |
| Version code | `1` |
| Version name | `0.1.0` |
| minSdk | `26` |
| targetSdk | `36` |
| AAB | `composeApp/build/outputs/bundle/release/composeApp-release.aab` |
| APK | `composeApp/build/outputs/apk/release/composeApp-release.apk` |
| AAB size | 18 MB |
| AAB SHA-256 | `bcb523ff7e83840d71c920bcbbb25125010cca739ac92d8c914be4133a9dacc7` |

## Build and Verification Commands

| 检查项 | 命令 | 结果 |
| --- | --- | --- |
| Signed release AAB | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:bundleRelease` | pass |
| Bundle signature and structure | `node tools/verify_release_bundle.mjs composeApp/build/outputs/bundle/release/composeApp-release.aab` | pass |
| Unit tests and lint | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest :composeApp:lintDebug` | pass |
| Release APK assemble | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:assembleRelease` | pass |

## Bundle Verification Evidence

```text
Verified signed Android App Bundle:
/Volumes/Volumes2T/VibeCodingProjects/WhiteNoisePro/composeApp/build/outputs/bundle/release/composeApp-release.aab
```

## Emulator Release Smoke

设备：

- Serial：`emulator-5554`
- Model：`sdk_gphone64_arm64`
- Android：`16`
- Size：`1280x2856`

安装说明：

- 模拟器原先安装的是 debug 签名包。
- `adb install -r` 返回 `INSTALL_FAILED_UPDATE_INCOMPATIBLE`。
- 已仅在 `emulator-5554` 上卸载旧包并安装 release APK。
- release app 启动成功，PID：`9976`。

验证路径：

| 页面 / 功能 | 证据 |
| --- | --- |
| Home | 命中 `晚安`、`深夜雨林`、`播放`、`推荐入睡定时`、底部导航 |
| Playback | 点击播放后命中 `暂停`，MediaSession `state=PLAYING`，metadata `深夜雨林` |
| Library | 命中 `声音库`、`推荐入睡声音`、`细雨`、`远海`、`夜林`、`暖炉`、噪声条目 |
| Timer | 命中 `定时器`、`快速选择`、`15 分钟`、`30 分钟`、`45 分钟`、`淡出`、`停止播放` |
| Saved | 命中 `已保存`、`深夜雨林`、`柔和专注`、`安静房间`、`新建混音` |
| Mixer | 命中 `混音`、`深夜雨林`、`4 个声音层`、`棕噪声`、`细雨`、`夜林`、`柔和风扇` |
| Settings | 命中 `隐私政策`、`无账号、无广告、无分析`、`声音来源`、`第一方程序化生成音频`、`后台媒体控制` |
| Crash / ANR | 最近 2000 行 logcat 未匹配 `FATAL EXCEPTION`、`ANR in com.whitenoisepro`、`Process: com.whitenoisepro` 或 app exception |

截图：

```text
/tmp/wnp-release-smoke/home.png
/tmp/wnp-release-smoke/library.png
/tmp/wnp-release-smoke/mixer.png
/tmp/wnp-release-smoke/saved.png
/tmp/wnp-release-smoke/settings.png
/tmp/wnp-release-smoke/timer.png
```

## Remaining Blocks Before Closed Testing Submission

- 隐私政策公开 URL 仍需开发者补齐。
- 开发者主体、支持邮箱、隐私联系邮箱仍需开发者补齐。
- Play Console 是否启用 Play App Signing 和 upload certificate 核对仍需账号侧证据。
- upload key 离线备份证据仍需开发者确认。
- 真实 Android 设备音频 QA 尚未完成；模拟器 smoke 不能替代听感、loop、蓝牙、锁屏和打断测试。

