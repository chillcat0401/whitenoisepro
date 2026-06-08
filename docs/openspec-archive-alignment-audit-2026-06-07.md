# OpenSpec 归档与实现一致性审计

日期：2026-06-07

## 审计范围

- 11 个 `openspec/changes/archive/` 归档。
- 13 份 `openspec/specs/` 现行规格。
- `PROJECT_PROTOCOL.md`、初始 Figma MVP 规划和第三方产品评审。
- commonMain、androidMain、commonTest、androidUnitTest 当前实现。

## 总体结论

技术路线没有偏离初始规划：

- 仍为 Android 优先的 Compose Multiplatform 单模块架构。
- commonMain 未引入 Google-only SDK。
- reducer、AppStore、DataStore、Media3 和协程边界符合既定方向。
- 六个 MVP 页面、深色设计系统、真实本地音频、后台播放、定时器和签名 AAB 已建立。

但归档勾选不能完全代表规格已落实。本次发现以下需要回归的问题。

## 实现偏差

### P0：MediaSession 控制播放器可能产生额外棕噪声

`WhiteNoiseMediaSessionService` 为系统媒体会话创建独立 ExoPlayer，并加载棕噪声；当前混音同时由 `AndroidPlaybackEngine` 的多播放器输出。控制播放器未静音，可能让输出多出未在混音中的声音。

### P1：Mixer 缺少移除和静音层操作

`MixReducer` 已支持 `RemoveLayer`、`MuteLayer`、`UnmuteLayer`，但 AppStore 与 Mixer UI 没有暴露，未满足 `mix-management`。

### P1：Saved Mixes 缺少编辑和收藏

当前只支持播放、删除和新建，未满足“save, edit, delete, favorite, and play named mixes”。

### P1：Timer 缺少 2 小时和自定义时长 UI

Reducer 已支持 custom duration，但 AppStore/UI 未接入；预设也缺少 120 分钟。

### P1：Mini Player 不显示活动定时器

subtitle 始终显示声音层数，未满足活动 timer 显示剩余时间的场景。

### P2：Sound metadata 不完整

`Sound` 缺少明确的 loop asset reference 和 default volume；新增声音始终使用全局默认音量。

## 规格偏差

### Restore Purchases 冲突

早期 `settings-compliance` 要求始终显示 Restore Purchases；后续 `settings-trust` 要求 billing 未集成时隐藏。以后者和总体发布策略为准，需修改早期规格。

### Notification permission 过时

`release-readiness` 仍描述 Android 13+ 请求通知权限时的文案，但后续决策和 Manifest 已明确 MediaSession 场景不请求 `POST_NOTIFICATIONS`。应改为后台媒体控制说明要求。

### Multi-layer 示例引用未发布自然声音

`playback-engine` 场景仍使用 rain/fireplace，与当前四个已发布音频目录不一致。

## 本轮回归范围

创建 `spec-implementation-convergence` change：

- 修复 MediaSession 额外声音风险。
- 补齐 Mixer、Saved Mixes、Timer 和 Mini Player 行为。
- 补齐 Sound metadata。
- 修订三个过时或冲突的现行规格。

## 后续仍需人工完成

- 真实 Android 设备的长时播放、锁屏、蓝牙、audio focus 和 timer QA。
- 隐私政策公开 URL、支持 URL 和 Play Console Data safety。
- 商店截图、Feature Graphic 和 closed testing 组织。
- upload key 加密离线备份。
- HarmonyOS/Huawei 独立 spike。
