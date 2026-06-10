# Closed Testing Launch Checklist

> 注记(2026-06-10):本文为当日快照,声音目录与版本号已演进,当前状态以 `release-candidate-2026-06-10.md` 为准。

日期：2026-06-08

## Summary

当前 release candidate 已能构建 signed AAB，并通过 bundle verifier、unit test、lint、release assemble 和 emulator release smoke。本轮已加入 11 个 Freesound CC0 处理素材和 GitHub Pages-ready 隐私政策页面。仍不建议在外部资料缺失时直接提交 closed testing，因为隐私政策公开 URL、开发者信息、Play App Signing 账号侧证据、upload key 离线备份确认、真实设备音频 QA 和 tester 名单仍未完成。

## Gate

| 项目 | 状态 | 证据 / 缺口 |
| --- | --- | --- |
| target API | ready | `targetSdk = 36` |
| package name | ready | `com.whitenoisepro` |
| version | ready | `versionCode = 1`、`versionName = 0.1.0` |
| signed AAB | ready | `composeApp/build/outputs/bundle/release/composeApp-release.aab` |
| AAB verification | ready | `node tools/verify_release_bundle.mjs ...` pass |
| unit tests | ready | `:composeApp:testDebugUnitTest` pass |
| lint | ready | `:composeApp:lintDebug` pass |
| release assemble | ready | `:composeApp:assembleRelease` pass |
| release emulator smoke | ready | `emulator-5554` release APK smoke pass |
| release candidate record | ready | `docs/release-readiness/release-candidate-2026-06-09.md` |
| external release audio | ready | 11 个 Freesound CC0 处理素材已入包，见 `docs/audio-assets/external-release-audio-manifest.json` |
| Play App Signing | blocked | 需要 Play Console 启用状态和 upload certificate 核对 |
| upload key backup | blocked | 需要开发者确认离线备份和密码管理器记录 |
| privacy policy URL | blocked | 需要公开 HTTPS URL，非 PDF，非登录页，非地理封锁 |
| developer identity | blocked | 需要开发者主体 / 展示名称 |
| support email | blocked | 需要可收信邮箱 |
| privacy contact email | blocked | 需要可收信邮箱，可与支持邮箱相同 |
| Data safety | ready | 当前代码行为为 no data collected / no data shared；仍需 Play Console 表单提交 |
| store listing draft | ready | `docs/release-readiness/store-listing-draft.md` |
| Play Console worksheet | ready | `docs/release-readiness/play-console-input-worksheet.md` |
| icon | ready | `docs/store-assets/google-play-icon-512.png` |
| screenshots | needs-human-evidence | 已有 release smoke 截图；上传前需人工选择并按 Play 素材规范裁切 |
| feature graphic | blocked | 如目标轨道要求，需要 1024x500 图 |
| real-device audio QA | blocked | 未完成真实设备听感、loop、后台、锁屏、蓝牙和打断测试 |
| tester roster | blocked | 模板已准备；真实 tester 名单未补齐 |
| feedback channel | blocked | 模板已准备；真实表单/群/邮箱未确认 |

## Go / No-Go

| 阶段 | 判断 | 原因 |
| --- | --- | --- |
| Local release candidate | go | 19 声音 signed AAB、bundle verifier、测试、lint、release assemble、emulator smoke 均通过 |
| Play internal testing upload | conditional go | 技术包 ready；仍需 Play App Signing 和开发者账号侧字段 |
| Closed testing submission | no-go | privacy URL、开发者信息、tester roster、真实设备音频 QA、Play App Signing 证据未完成 |
| Production access | no-go | closed testing 14 天窗口和真实反馈尚未开始 |

## Immediate Owner Inputs

- [待补充：隐私政策 HTTPS URL]
- [待补充：开发者主体 / 展示名]
- [待补充：支持邮箱]
- [待补充：隐私联系邮箱]
- [待补充：Play Console 是否为新个人开发者账号，是否显示 12 tester / 14 day 要求]
- [待补充：Play App Signing 启用和 upload certificate 核对]
- [待补充：upload key 离线备份确认]
- [待补充：15-20 名 tester 或 Google Group / email list]
- [待补充：真实 Android 设备音频 QA 记录]
