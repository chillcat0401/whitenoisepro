# Google Play Release Readiness Checklist

复核日期：2026-06-08

官方依据：

- Google Play 新个人开发者账号 closed testing 要求。
- Google Play User Data policy。
- Google Play Data safety form 指南。
- Google Play target API level 要求。

## 当前发布判断

WhiteNoisePro 当前可以作为工程 MVP 继续迭代，但不应直接进入 Google Play closed testing，除非以下 gate 完成：

- 21 个声音可用(2026-06-10):18 个 Freesound CC0 处理素材打包,白/粉/棕噪声设备端运行时合成;7 个新素材已过候选听审与机器接缝 QA(<3dB),真机主观听测待完成。
- Mixer 已支持 layer 音量、静音和移除；Saved Mixes 已支持播放、重命名、收藏和删除。
- Timer 已支持 15/30/45/60/120 分钟、自定义时长、延长、取消和淡出停止。
- adaptive launcher、round、monochrome 和 512×512 商店图标已准备。
- 独立 upload key 和已签名 release AAB 已准备；密钥仍需完成离线备份。
- 隐私政策和开发者信息模板已准备；公开 URL、开发者主体、联系邮箱和生效日期仍需发布前补齐。
- Data safety 工作表与代码行为一致。
- MediaSession 通知权限豁免结论已复核，Manifest 不声明不必要的 `POST_NOTIFICATIONS`。
- Settings placeholder 已处理为实现、禁用或隐藏。
- 至少一台真实 Android 设备完成音频 QA。

## Internal Testing

目的：

- 快速验证 APK 安装、启动、播放、页面导航和基础崩溃风险。

进入条件：

- `:composeApp:check :composeApp:assembleDebug` 通过。
- `:composeApp:bundleRelease` 生成通过签名验证的 AAB。
- 安装包包含 adaptive launcher icon，商店目录包含合规 512×512 PNG。
- 390x844、360x800、430x932 基础截图通过。
- 当前 APK 可安装。

退出条件：

- Home 播放 / 暂停可用。
- Mini Player 在活动 timer 期间显示剩余时间。
- MediaSession controller player 不产生当前混音之外的可听声音。
- Mini Player 与 Bottom Nav 不重叠。
- Settings 中未实现能力不误导 tester。
- 没有启动崩溃或主流程阻断。

## Closed Testing

适用性：

- 对 2023-11-13 之后创建的 Google Play 个人开发者账号，生产发布前通常需要 closed testing。
- 官方要求为至少 12 名 tester 连续 opt-in 14 天。实际是否适用以 Play Console 当前账号提示为准。

进入条件：

- closed testing feedback loop 已准备。
- tester 招募名单和联系渠道已准备。
- 隐私政策 URL 已发布，且 Settings / Play Console / Data safety 使用同一 URL。
- 开发者主体、支持邮箱和隐私联系邮箱已确认。
- Data safety 草案完成。
- 八个第一方声音已覆盖默认混音和声音库，生成清单与哈希已记录。
- 商店图标已生成并通过尺寸、RGBA 与文件大小校验。
- 已启用 Play App Signing，并在 Play Console 核对 upload certificate。
- 真实设备音频 QA 至少完成一轮。
- 商店 listing 草案和基础截图准备完毕。

退出条件：

- 至少 12 名 tester 连续 14 天 opt-in。
- 收集并归类 tester 反馈。
- 完成至少一轮基于反馈的修复或明确记录不修复原因。
- 准备生产访问申请回答：
  - tester 招募是否困难。
  - tester 使用是否覆盖核心功能。
  - 收到的主要反馈。
  - 基于 closed test 做了哪些修改。
  - 为什么认为应用已具备 production readiness。

## Open Testing

进入条件：

- 已获得 production access。
- listing 可公开展示。
- 隐私政策、Data safety、截图和文案已经可面向公开用户。

退出条件：

- 崩溃率和关键反馈可接受。
- 核心留存问题有记录。
- production 发布范围、国家/地区和设备支持策略明确。

## Production

进入条件：

- closed/open test 的主要阻断问题已处理。
- targetSdk 满足当前 Google Play 要求。当前项目 targetSdk 为 36。
- 隐私政策 URL 稳定可访问，非 PDF，非地理封锁。
- 开发者信息与 Play Console、隐私政策、商店详情一致。
- Data safety 答案与代码和第三方 SDK 行为一致。
- 商店截图展示真实应用状态，不使用 debug placeholder。

发布后监控：

- crash / ANR。
- playback 相关差评。
- 权限拒绝导致的后台播放问题。
- 声音 loop 质量反馈。
- 睡眠定时器可靠性反馈。
