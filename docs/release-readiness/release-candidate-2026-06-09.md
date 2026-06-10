# Release Candidate Verification Record

> 注记(2026-06-10):本文为当日快照,声音目录与版本号已演进,当前状态以 `release-candidate-2026-06-10.md` 为准。

日期：2026-06-09

## Release Candidate

| 字段 | 值 |
| --- | --- |
| Package name | `com.whitenoisepro` |
| Version code | `1` |
| Version name | `0.1.0` |
| minSdk | `26` |
| targetSdk | `36` |
| 声音目录 | 19 个声音：8 个第一方程序化声音 + 11 个 Freesound CC0 处理素材 |
| AAB | `composeApp/build/outputs/bundle/release/composeApp-release.aab` |
| APK | `composeApp/build/outputs/apk/release/composeApp-release.apk` |
| AAB size | 28,562,839 bytes |
| APK size | 29,697,062 bytes |
| AAB SHA-256 | `9f5ee1afb2bc25fb8387679d21fdd65eea90e98bfaa61106de56a10e7034ca2a` |
| APK SHA-256 | `0daa8d0734281634fdbcd1077edd4da192d8c5d17f9f91ee16ebfac2f47fb91b` |

## Build and Verification Commands

| 检查项 | 命令 | 结果 |
| --- | --- | --- |
| Node audio/tool tests | `node --test tools/*.test.mjs` | pass，24 tests |
| Gradle unit tests | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest` | pass |
| Lint | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:lintDebug` | pass |
| Signed release AAB | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:bundleRelease` | pass |
| Bundle signature and structure | `node tools/verify_release_bundle.mjs composeApp/build/outputs/bundle/release/composeApp-release.aab` | pass |
| Release APK assemble | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:assembleRelease` | pass |

## Bundle Verification Evidence

```text
Verified signed Android App Bundle: /Volumes/Volumes2T/VibeCodingProjects/WhiteNoisePro/composeApp/build/outputs/bundle/release/composeApp-release.aab
```

## Emulator Release Smoke

设备：

- Serial：`emulator-5554`
- Model：`sdk_gphone64_arm64`
- Size：`1280x2856`
- Density：`480`
- 说明：本轮同时检测到真机 `3EP7N18C11004712` 和模拟器；smoke 全程显式指定 `emulator-5554`，未操作真机。

安装：

```text
adb -s emulator-5554 install -r composeApp/build/outputs/apk/release/composeApp-release.apk
Performing Streamed Install
Success
```

验证路径：

| 页面 / 功能 | 证据 |
| --- | --- |
| Home | release app 启动成功，首页展示 `晚安`、`把世界调低一点`、`深夜雨林`、播放按钮、今晚的淡出定时、最近使用和固定底部 mini player |
| Library | 展示声音库、搜索、分类 chip、`适合今晚的声音` 和声音卡片；新增声音可通过目录入口访问 |
| Mixer | 混音页可打开并保持底部 mini player / bottom nav 稳定 |
| Timer | 定时页可打开，快速选择和定时配置入口可见 |
| Saved | 已保存页可打开，保存混音列表和 `新建混音` 入口可见 |
| Settings | 设置页可通过首页右上入口打开，隐私与权限、声音来源等 release readiness 文案可见；页面可滚动 |
| Playback | 点击 mini player 播放后，按钮切换为暂停；MediaSession `state=PLAYING`，package 为 `com.whitenoisepro` |
| Crash / ANR | 最近 2000 行 logcat 未匹配 `FATAL EXCEPTION`、`ANR in com.whitenoisepro`、`Process: com.whitenoisepro` 或 app exception |

截图和日志：

```text
/tmp/wnp-release-smoke-2026-06-09/home.png
/tmp/wnp-release-smoke-2026-06-09/library.png
/tmp/wnp-release-smoke-2026-06-09/mixer.png
/tmp/wnp-release-smoke-2026-06-09/timer.png
/tmp/wnp-release-smoke-2026-06-09/saved.png
/tmp/wnp-release-smoke-2026-06-09/settings.png
/tmp/wnp-release-smoke-2026-06-09/after-play.png
/tmp/wnp-release-smoke-2026-06-09/logcat-tail.txt
/tmp/wnp-release-smoke-2026-06-09/media-session.txt
/tmp/wnp-release-smoke-2026-06-09/activity.txt
```

MediaSession 摘要：

```text
package=com.whitenoisepro
active=true
state=PlaybackState {state=PLAYING(3), ...}
metadata: size=4, description=深夜雨林, 白噪声 Pro, null
```

## Remaining Blocks Before Closed Testing Submission

- 隐私政策页面已在 `site/privacy-policy.html` 准备好；仍需部署 GitHub Pages 并确认公开 HTTPS URL。
- 开发者主体、支持邮箱、隐私联系邮箱仍需开发者补齐。
- Play Console 是否启用 Play App Signing 和 upload certificate 核对仍需账号侧证据。
- upload key 离线备份证据仍需开发者确认。
- 真实 Android 设备音频 QA 尚未完成；模拟器 smoke 不能替代真实听感、loop、蓝牙、锁屏和打断测试。
- tester roster、反馈渠道和 closed testing 14 天运营证据仍需开发者侧执行。
