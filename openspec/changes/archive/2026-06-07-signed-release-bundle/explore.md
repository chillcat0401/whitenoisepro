# Explore: 已签名 Release Bundle

## 当前状态

- `:composeApp:bundleRelease` 任务存在。
- Android `release` build type 没有 signingConfig。
- 仓库没有 keystore、签名属性模板或 AAB 验证脚本。
- `.gitignore` 尚未排除 keystore 与本地签名属性。

## 方案

### A. 独立 upload key + 本地属性/环境变量

- Play App Signing 管理最终 app signing key。
- 本地 upload key 只用于签署上传 AAB。
- Gradle 优先读取环境变量，回退到 gitignored `keystore.properties`。
- 仓库提交无秘密的属性模板。

优点：能立即生成可上传 AAB，密钥职责清晰。  
缺点：需要安全备份 upload key。

### B. 仅生成 unsigned AAB

优点：配置最少。  
缺点：不能直接上传 Play Console，发布 gate 仍未关闭。

### C. 使用 debug key 签署 release

优点：无需额外密钥。  
缺点：密钥公开且不适合发布，不采用。

## 决策

采用方案 A。生成一枚仅用于上传的本地 key，不把 key 或密码写入仓库、文档或命令输出。
