## Why

WhiteNoisePro 当前工程基础已经接近 Google Play 测试发布门槛；继续堆功能会推迟不可压缩的 closed testing 时间窗口。下一步应优先把 app 推入 internal / closed testing，尽快积累真实用户对声音、后台播放、timer、UI 舒适度和隐私信任文案的原始反馈。

## What Changes

- 准备 Google Play closed testing launch package：release candidate 构建、签名包验证、Play Console 输入清单、商店 listing 草案、截图清单、隐私政策发布清单、Data safety 复核清单。
- 建立 tester 运营包：tester 招募表、opt-in 连续性记录、测试说明、反馈分类、production access 回答素材。
- 强制一个 pre-closed-testing gate：只有隐私 URL、开发者信息、signed AAB、真实设备 QA、Data safety、store listing 和 tester roster 都满足时，才允许提交 closed testing。
- 保持现有低风险数据边界：不新增 analytics、crash SDK、广告、billing、账号、云同步、远程配置或用户数据上传。
- 将 release-readiness 规格扩展为“快速 closed testing launch”要求。

## Capabilities

### New Capabilities

- `early-user-feedback`: 定义 closed testing 期间 tester 招募、opt-in 记录、反馈采集、反馈分类和 production access 素材沉淀要求。

### Modified Capabilities

- `release-readiness`: 增加快速 Google Play closed testing launch gate、release candidate package 和 Play Console 提交前检查要求。

## Impact

- 主要影响文档、OpenSpec 规格、release readiness checklist、closed testing feedback loop、QA 记录和构建验证流程。
- 可能影响少量工具脚本或 Gradle release 验证命令，但本 change 不计划引入新 SDK 或新运行时权限。
- 需要开发者提供外部输入：Google Play Console 账号状态、隐私政策公开 URL、开发者主体、支持邮箱、隐私联系邮箱、签名密钥准备状态、tester 名单。

