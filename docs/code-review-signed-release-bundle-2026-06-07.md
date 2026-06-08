# Code Review: 已签名 Release Bundle

日期：2026-06-07

## Findings

未发现阻塞本次变更的问题。

审查期间修复：

- 最初的 Gradle 校验 task 捕获脚本状态，导致 configuration cache 无法保存。
- 校验 task 在依赖图末端才失败，缺少密钥时仍执行大量 release 工作。
- `jarsigner -strict` 会因 upload key 的自签名信任链返回失败；verifier 改为要求标准签名成功输出并显式拒绝 unsigned bundle。
- `jarsigner` 输出受系统语言影响；verifier 固定为英文输出后解析稳定。

## Security Review

- `.gitignore` 排除 `keystore.properties`、`*.jks` 和 `*.keystore`。
- 仓库仅包含无秘密的属性模板。
- 本地 upload key 与属性文件权限为 `0600`。
- Gradle 支持环境变量优先，便于后续接入 CI secret store。
- 文档没有记录密码、私钥或可复用秘密。

## Verification

- 缺失签名配置时，`:composeApp:bundleRelease` 在配置阶段明确失败。
- 无签名配置时，`:composeApp:assembleDebug` 继续通过。
- `node --test tools/verify_release_bundle.test.mjs`：2 tests，0 failures。
- verifier 拒绝非 ZIP 文件和结构正确但未签名的 AAB。
- `:composeApp:bundleRelease`：`BUILD SUCCESSFUL`。
- AAB 包含 `BundleConfig.pb`、base manifest 和 `META-INF/UPLOAD.*`。
- `jarsigner` 输出 `jar verified`。

## Remaining Risks

- upload key 目前只有本机副本；进入 closed testing 前必须完成加密离线备份。
- Play App Signing 和 upload certificate 尚需在 Play Console 中启用/核对。
- `versionCode` 当前为 1；每次上传新版本必须递增。
- 当前没有 CI release job，发布仍依赖本机环境。
