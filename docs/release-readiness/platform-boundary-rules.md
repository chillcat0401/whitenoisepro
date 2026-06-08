# Platform Boundary Rules

## 核心规则

`commonMain` 不得直接依赖 Google-only 服务。

禁止直接进入 shared core 的能力包括：

- Google Play Services。
- Google Play Billing。
- Google Analytics / Firebase Analytics。
- Firebase Crashlytics。
- Google Play In-App Review。
- Google-specific auth。
- Google-only remote config。

## 允许方式

如未来需要这些能力，必须通过平台边界封装：

- common 层定义接口。
- Android source set 提供 Google 实现。
- Huawei / HarmonyOS 路径必须有替代方案、空实现或明确 non-goal。
- OpenSpec 必须记录平台差异和上架影响。

## 当前状态

当前项目：

- common source set 定义 domain、data、presentation、playback boundary。
- Android source set 实现 Media3 / ExoPlayer / MediaSession。
- 未接入 Google Play Services、billing、analytics、crash reporting 或 ads。

该状态应保持到 Google Play MVP 验证完成。

## 检查命令

```bash
rg -n "com.google|firebase|billing|play-services|crashlytics|analytics" composeApp/src/commonMain composeApp/build.gradle.kts
```

如果命中 common source set，需要先更新 OpenSpec 并解释平台边界。
