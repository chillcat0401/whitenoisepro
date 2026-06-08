# Tasks

## 1. AppStore 基础

- [x] 1.1 添加 AppStore restore 测试
  - Acceptance: 空 repository 使用默认 `AppState()`。
  - Acceptance: 非空 snapshot 恢复 saved mixes、recent mixes、current mix、timer defaults 和 settings。
  - Acceptance: `settings.startLastMix=false` 时不恢复 snapshot.currentMix。
  - Verification: 先运行目标测试看到失败，再实现通过。

- [x] 1.2 实现 AppStore、AppIntent、Clock、IdProvider
  - Acceptance: AppStore 位于 commonMain，不依赖 Android。
  - Acceptance: AppStore 初始化逻辑通过 1.1 测试。
  - Verification: `./gradlew :composeApp:testDebugUnitTest --tests 'com.whitenoisepro.presentation.AppStoreTest'`。

## 2. Mix 与 Saved Mixes 行为

- [x] 2.1 添加 mix intent 持久化测试
  - Acceptance: `AddSound` 添加 layer 并保存 snapshot。
  - Acceptance: `SetMasterVolume` 和 `SetLayerVolume` 更新状态并保存 snapshot。
  - Acceptance: `SaveCurrentMix` upsert saved mixes 并保存 snapshot。
  - Verification: 先红后绿。

- [x] 2.2 添加 saved mixes 播放/删除测试
  - Acceptance: `PlaySavedMix` 更新 current mix、recent mixes，并调用 playback engine play。
  - Acceptance: `DeleteSavedMix` 删除 saved mix 并保存 snapshot。
  - Verification: common unit test。

- [x] 2.3 将 Mixer、Saved Mixes、Home 的核心 no-op 接到 AppIntent
  - Acceptance: 添加声音、保存混音、主音量、layer 音量、播放 saved mix、删除 saved mix、收藏当前混音不再是 no-op。
  - Acceptance: 无真实行为的入口必须保持只读、隐藏或禁用。
  - Verification: Gradle check；必要时截图检查布局未破坏。

## 3. Library 行为

- [x] 3.1 添加 library state 测试
  - Acceptance: `SetLibraryQuery` 更新 query 但不保存 repository。
  - Acceptance: `SelectCategory` 更新 selectedCategory 但不保存 repository。
  - Acceptance: category + query 使用 `SoundCatalog.filter` 计算展示列表。
  - Verification: common unit test。

- [x] 3.2 将 Library 搜索、分类和 sound card click 接入 AppIntent
  - Acceptance: 搜索框输入会改变显示声音列表。
  - Acceptance: 分类 chip 会改变显示声音列表。
  - Acceptance: 点击声音会添加到当前 mix。
  - Verification: Gradle check；人工预览或截图检查。

## 4. Timer 与 Settings 行为

- [x] 4.1 添加 timer/settings store 测试
  - Acceptance: timer preset/start/extend/cancel 更新 timer state 并保存 snapshot。
  - Acceptance: `SetStartLastMix` 更新 settings 并保存 snapshot。
  - Verification: common unit test。

- [x] 4.2 将 Timer 页面按钮和 Settings toggle 接入 AppIntent
  - Acceptance: Timer preset、开始、延长、取消不再是 no-op。
  - Acceptance: Settings `启动时继续上次混音` toggle 更新 state。
  - Verification: Gradle check。

## 5. App 集成与文档

- [x] 5.1 将 WhiteNoiseProApp 改为使用 AppStore
  - Acceptance: `WhiteNoiseProApp` 不再直接用 `mutableStateOf(AppState())` 管理业务状态。
  - Acceptance: `PlaybackEngine` 和 `AppRepository` 可注入，默认路径仍能运行。
  - Verification: Gradle check。

- [x] 5.2 更新 stale compliance 文档
  - Acceptance: `docs/compliance-readiness.md` 不再声称 Restore Purchases 是当前生产 UI placeholder。
  - Acceptance: 文档说明 Settings trust hardening 已完成，剩余项是公开隐私 URL、通知 runtime permission、真实音频和 QA。
  - Verification: `rg -n "Restore Purchases is present|placeholder" docs/compliance-readiness.md` 不再出现旧结论。

- [x] 5.3 全量验证与审查
  - Acceptance: `:composeApp:check` 和 `:composeApp:assembleDebug` 成功。
  - Acceptance: 代码审查无 blocker。
  - Verification: 记录验证命令与结果。
