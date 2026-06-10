# Explore: 快速进入 Google Play closed testing 并积累原始用户

日期：2026-06-08

## 目标

当前目标不是继续扩大功能，而是用最短路径把 WhiteNoisePro 推进到 Google Play internal / closed testing，尽快获得真实安装、播放、后台、声音舒适度和信任文案反馈。对于新个人开发者账号，如果 Play Console 适用生产访问门槛，需要至少 12 名 tester 连续 opt-in 14 天，因此越早进入 closed testing，越早启动不可压缩的等待窗口。

## 当前项目事实

- `applicationId = "com.whitenoisepro"`。
- `targetSdk = 36`，满足当前 Google Play 新 app / update 需 target Android 15/API 35+ 的要求。
- 当前 Manifest 仅声明 `FOREGROUND_SERVICE` 和 `FOREGROUND_SERVICE_MEDIA_PLAYBACK`，不声明 `POST_NOTIFICATIONS`。
- 已有 8 个第一方程序化生成音频，已打包到 `res/raw`，并有生成 manifest。
- 已有 512x512 Google Play store icon、adaptive launcher icon、release signing 文档和 bundle verifier。
- 已有隐私政策草案、开发者信息模板、Data safety worksheet、Google Play audit、closed testing feedback loop 文档。
- 仍阻塞：隐私政策公开 URL、开发者主体/邮箱确认、Play Console 账号侧配置、signed release AAB 实际生成/上传记录、真实设备音频 QA、tester 招募与 opt-in 记录。

## 上架策略选项

### 方案 A：直接冲 closed testing

内容：补齐外部材料和 release candidate，生成 signed AAB，先做 internal smoke，再提交 closed testing。

优点：

- 最快启动 14 天 closed testing 窗口。
- 当前产品已足以收集核心反馈：声音舒适度、后台播放、timer、信任文案。
- 不引入 analytics / crash SDK，避免重新扩大 Data safety 和隐私范围。

缺点：

- 反馈采集依赖人工表单和 Play Console 指标，自动化用户行为数据少。
- 需要开发者立即补齐隐私 URL、邮箱、账号侧信息。

### 方案 B：先加用户反馈入口再上架

内容：在 App 内增加反馈入口、邮件或表单链接，再进入测试。

优点：

- tester 反馈路径更清晰。
- 方便收集原始用户意见。

缺点：

- 如果使用第三方表单或邮件深链，需要重新确认隐私政策和 Data safety。
- 增加实现、测试和审核风险，会延迟 closed testing 窗口启动。

### 方案 C：先继续完善产品功能

内容：继续增加声音、偏好、引导或更多现代 UI，再准备上架。

优点：

- 产品完成度更高。

缺点：

- 不解决不可压缩的 closed testing 时间成本。
- 容易在没有真实用户反馈前过度打磨。

## 推荐

选择方案 A。把下一步限定为“Google Play closed testing launch package”：只补齐上架必要材料、RC 构建、Play Console 输入清单、真实设备 QA 和 tester 反馈记录，不新增联网 SDK、不新增用户数据采集、不继续大改 UI。快速启动 closed testing 后，再用 tester 反馈决定下一轮产品增强。

## 风险与边界

- Google Play 政策和账号提示以 Play Console 当日显示为准；文档记录必须允许人工核对。
- 如果隐私政策公开 URL 无法在本轮提供，则只能完成模板和阻塞记录，不能提交 closed testing。
- 如果 signed release keystore 未准备或未备份，不能上传 release AAB。
- 如果真实设备音频 QA 发现 P0/P1 问题，本轮应先修复阻断问题再进入 closed testing。
- 本轮不加入 Firebase Analytics、Crashlytics、广告、billing、云同步或远程配置，避免扩大政策面。

