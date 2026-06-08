# Android Release Signing

更新日期：2026-06-07

## Key Roles

- Google Play App Signing 应管理最终 app signing key。
- `upload-keystore.jks` 仅作为 upload key，用于签署提交给 Play Console 的 AAB。
- upload key 丢失后可按 Play Console 流程申请重置，但会中断发布，因此仍需独立备份。

## Local Files

以下文件只保存在本机或安全备份中，不进入版本控制：

- `upload-keystore.jks`
- `keystore.properties`

文件权限应保持为 `0600`。至少制作一份加密离线备份，并将密码保存在密码管理器；不要把密钥和密码只放在同一块磁盘。

## Build

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home \
./gradlew :composeApp:bundleRelease
```

产物：

```text
composeApp/build/outputs/bundle/release/composeApp-release.aab
```

验证：

```bash
node tools/verify_release_bundle.mjs \
  composeApp/build/outputs/bundle/release/composeApp-release.aab
```

upload certificate SHA-256 可通过以下命令读取，并在 Play Console 首次配置时核对：

```bash
keytool -list -v -keystore upload-keystore.jks -alias upload
```

## CI Inputs

未来 CI 应通过秘密存储提供：

- `WHITENOISE_UPLOAD_STORE_FILE`
- `WHITENOISE_UPLOAD_STORE_PASSWORD`
- `WHITENOISE_UPLOAD_KEY_ALIAS`
- `WHITENOISE_UPLOAD_KEY_PASSWORD`

不要把 `keystore.properties` 内容写入 CI 日志。

## Release Rule

- 每次上传必须增加 `versionCode`。
- closed testing 首次上传前启用 Play App Signing 并确认 upload certificate。
- 不得使用 debug key、共享样例 key 或重新生成的临时 key 签署后续版本。
