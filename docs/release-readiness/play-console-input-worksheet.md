# Play Console Input Worksheet

状态：草案。所有 `[待补充：...]` 必须由开发者或 Play Console 当前页面确认后才能提交。

## App Identity

| 字段 | 值 | 状态 |
| --- | --- | --- |
| App name | WhiteNoisePro | ready |
| Default language | [待补充：建议 English (United States) 或 Chinese Simplified，按目标市场决定] | blocked |
| App or game | App | ready |
| Free or paid | Free | assumed |
| Package name | `com.whitenoisepro` | ready |
| Version | `0.2.0` / versionCode `1` | ready |

## App Content

| 项目 | 建议输入 | 状态 |
| --- | --- | --- |
| Privacy policy | `https://chillcat0401.github.io/whitenoisepro/privacy-policy.html`(2026-06-10 已发布,HTTP 200) | ready |
| App access | No restricted app access / no login required | ready |
| Ads | No ads | ready |
| Content rating | [待补充：完成 Play Console 问卷；预期为低龄风险较低，但以问卷结果为准] | blocked |
| Target audience | [待补充：建议先面向成人 / general users，不面向儿童] | blocked |
| News app | No | ready |
| COVID / health claims | No medical or therapeutic claims | ready |
| Data safety | Data collected: No; Data shared: No; no analytics, ads, account, crash SDK, billing, or cloud sync | ready |
| Foreground service | Media playback only; permissions are `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | ready |
| Notification permission | App does not declare `POST_NOTIFICATIONS`; MediaSession controls are not marketing notifications | ready |

## Store Listing

| 项目 | 文件 / 输入 | 状态 |
| --- | --- | --- |
| Short description | `docs/release-readiness/store-listing-draft.md` | ready |
| Full description | `docs/release-readiness/store-listing-draft.md` | ready |
| Release notes | `docs/release-readiness/store-listing-draft.md` | ready |
| App icon | `docs/store-assets/google-play-icon-512.png` | ready |
| Phone screenshots | `work/android-screenshots/360x800/*.png`、`work/android-screenshots/430x932/*.png`、release smoke screenshots in `/tmp/wnp-release-smoke/` | needs-human-review |
| Feature graphic | [待补充：Google Play 1024x500 feature graphic，如目标轨道要求] | blocked |
| Category | [待补充：建议 Health & Fitness、Lifestyle 或 Music & Audio 之间选择；避免医学承诺] | blocked |
| Tags | [待补充：white noise, sleep sounds, focus 等，按 Play Console 可选项] | blocked |
| Contact details | support email / website / phone as required by Play Console | blocked |

## Release Track

| 项目 | 值 | 状态 |
| --- | --- | --- |
| Track | Internal testing first, then Closed testing | ready |
| Artifact | `composeApp/build/outputs/bundle/release/composeApp-release.aab` | ready |
| Signed bundle verification | `node tools/verify_release_bundle.mjs ...` passed on 2026-06-08 | ready |
| Play App Signing | [待补充：Play Console 启用状态和 upload certificate 核对] | blocked |
| Countries / regions | [待补充：建议先选择开发者能支持反馈和政策语言的地区] | blocked |
| Tester group | [待补充：Google Group 或 email list] | blocked |
| Closed testing requirement | 若账号适用，准备至少 15-20 名邀请，目标至少 12 名 tester 连续 opt-in 14 天 | blocked |

## Production Access Evidence Fields

提交 production access 前需要补齐：

- tester 招募方式。
- tester 数量和 opt-in 连续性。
- tester 覆盖的核心路径。
- 主要反馈主题。
- 基于反馈完成的修改。
- 未修复但接受的风险。
- 为什么当前版本可以面向 production。
