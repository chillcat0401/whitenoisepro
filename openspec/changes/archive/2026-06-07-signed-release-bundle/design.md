# Design: 已签名 Release Bundle

## Signing Inputs

Gradle 使用以下输入，环境变量优先：

- `WHITENOISE_UPLOAD_STORE_FILE`
- `WHITENOISE_UPLOAD_STORE_PASSWORD`
- `WHITENOISE_UPLOAD_KEY_ALIAS`
- `WHITENOISE_UPLOAD_KEY_PASSWORD`

本地开发可在根目录使用 gitignored `keystore.properties`，键名为：

- `storeFile`
- `storePassword`
- `keyAlias`
- `keyPassword`

## Failure Policy

普通 debug/check 任务不要求 release key。执行 release bundle、assemble 或 signing 验证任务时，如果任一签名字段缺失，构建在配置阶段给出明确错误，避免误产出 unsigned release。

## Bundle Verification

`tools/verify_release_bundle.mjs`：

- 检查文件存在且为 ZIP/AAB。
- 检查 `base/manifest/AndroidManifest.xml` 和 `BundleConfig.pb`。
- 调用 JDK `jarsigner -verify` 验证签名，并显式拒绝 unsigned 输出。upload key 使用自签名证书，因此不把公共 CA 信任链警告当作签名无效。

密钥创建与备份步骤记录在 release 文档，但不记录秘密。
