# 已签名 Release Bundle

## Why

应用已有可运行 APK，但还没有 release signing 配置和可验证的 AAB。Google Play 测试轨道需要上传签名 Android App Bundle。

## What Changes

- 添加安全的 upload key 本地配置约定。
- `.gitignore` 排除 keystore 和真实签名属性。
- Gradle release build 在签名配置完整时使用 upload key。
- 缺少或不完整签名配置时，release bundle 任务明确失败。
- 添加 AAB 格式和 JAR 签名验证脚本。
- 生成并验证本机已签名 release AAB。

## Non-goals

- 自动上传 Play Console。
- 管理 Play App Signing 的最终 app signing key。
- CI secrets 配置。
- 修改 applicationId、版本号或产品名称。

## Acceptance

- `keystore.properties` 与 keystore 不会被版本控制。
- `bundleRelease` 生成已签名 AAB。
- `jarsigner -verify` 确认 AAB 已签名。
- AAB 验证脚本确认必要 bundle 条目存在。
- 完整测试、lint 和 OpenSpec strict validation 通过。
