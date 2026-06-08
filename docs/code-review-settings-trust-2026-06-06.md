# Code Review: Settings Trust Hardening

日期：2026-06-06

## Findings

未发现阻断问题。

## Scope Review

本次实现对齐 `openspec/changes/settings-trust-hardening`：

- 新增 `SettingsContent.releaseReady()` 内容模型。
- `SettingsScreen` 改为从内容模型渲染。
- `恢复购买` 不再出现在生产 Settings UI。
- `离线下载` 改为禁用说明态，不再是可切换假功能。
- Settings 增加隐私定位与通知权限说明。

## Tests

已执行：

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest --tests 'com.whitenoisepro.presentation.SettingsContentTest'
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug
```

结果：

```text
BUILD SUCCESSFUL
```

## Residual Risk

- 这不是完整隐私政策 URL 实现；当前只完成 closed testing 前避免误导的 Settings UI 硬化。
- Android 13+ notification runtime permission 请求时机仍需后续 OpenSpec 变更处理。
- `启动时继续上次混音` 仍依赖后续真实持久化工作。
