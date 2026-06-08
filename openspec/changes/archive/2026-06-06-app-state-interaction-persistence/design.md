# Design: App 状态、交互与持久化闭环

## 架构决策

采用小型 commonMain `AppStore`，不引入完整 ViewModel/DI/use case 层。

理由：

- 当前项目已有 `MixReducer`、`TimerReducer`、`AppRepository` 和 `PlaybackEngine` 边界。
- 当前最大问题不是缺少层级，而是 UI callback、restore/save 和 playback side effect 没有统一入口。
- 小型 AppStore 能把 app-level intent、state、repository 和 playback 组合起来，同时保持 CMP commonMain 可测试。

## 新增边界

### AppIntent

`AppIntent` 表示 UI 或平台回调想做的事：

- `SelectTab(tab)`
- `TogglePlayback`
- `SetMasterVolume(volume)`
- `SetLayerVolume(layerId, volume)`
- `ToggleFavoriteCurrent`
- `AddSound(soundId)`
- `SaveCurrentMix`
- `PlaySavedMix(mixId)`
- `DeleteSavedMix(mixId)`
- `SetLibraryQuery(query)`
- `SelectCategory(category)`
- `SelectTimerPreset(minutes)`
- `StartTimer`
- `ExtendTimer(minutes)`
- `CancelTimer`
- `SetStartLastMix(enabled)`

### AppStore

`AppStore` 负责：

- 持有当前 `AppState`。
- 初始化时读取 `AppRepository.restore()`。
- 将 AppIntent 转换为 reducer 调用或 AppState copy。
- 调用 `PlaybackEngine.play/pause/stop`。
- 在需要持久化的状态变化后写入 `AppRepository.save(AppSnapshot)`。

AppStore 不负责：

- 直接访问 Android API。
- 执行 Media3 细节。
- 生成真实音频资产。
- 判断商店政策。

### Clock 和 IdProvider

为避免测试依赖真实时间或随机值，AppStore 注入：

- `Clock.nowEpochMillis(): Long`
- `IdProvider.nextLayerId(soundId: String): String`
- `IdProvider.nextSavedMixId(baseTitle: String): String`

生产默认实现可用递增或时间戳生成稳定 enough 的 id；测试使用 fake。

## Restore 策略

从 repository restore 的 snapshot 映射为 AppState：

- `currentMix`: snapshot.currentMix 或默认 current mix。
- `savedMixes`: snapshot.savedMixes 非空则使用，否则默认 saved mixes。
- `recentMixes`: snapshot.recentMixes。
- `timerState`: snapshot.timerDefaults。
- `settings`: snapshot.settings。
- `selectedTab`: 仍从 Home 开始。
- `isPlaying`: false，避免 app 启动后自动播放声音。

如果 `settings.startLastMix == false`，仍可恢复 saved/recent/settings，但 current mix 使用默认 mix，避免违背用户设置。

## Save 策略

以下 intent 成功更新状态后保存 snapshot：

- mix 变化：添加声音、保存、删除、播放 saved mix、收藏、主音量、layer 音量。
- timer default 变化：preset、extend、cancel、start 后状态变化。
- settings 变化：start last mix toggle。

以下 intent 不保存：

- tab 切换。
- library query/category，因为它们是临时浏览状态。
- 播放/暂停本身，除非同时改变 current mix。

## UI 接入

screen 函数保留 composable 结构，但增加 callback 参数：

- `MixerScreen`: 添加声音、保存混音、主音量、layer 音量。
- `LibraryScreen`: query、category、sound card click。
- `TimerScreen`: preset、start、extend、cancel。
- `SavedMixesScreen`: play、delete、favorite filter 后续可扩展。
- `SettingsScreen`: startLastMix toggle。
- `HomeScreen`: favorite、master volume 后续接入；本轮至少把现有 no-op 收藏/音量替换为 intent。

## 测试策略

优先 common unit tests：

- restore 空 snapshot 和非空 snapshot。
- `settings.startLastMix=false` 时不恢复 current mix。
- mix intent 会更新 state 并保存 snapshot。
- playback intent 会调用 fake playback engine。
- library query/category 不触发 repository save。
- timer preset/start/extend/cancel 更新 state 并保存。

UI 层测试保持轻量，重点验证 screen 参数结构不再迫使 no-op；不做大型 screenshot test。

## 风险

- AppStore 可能膨胀。缓解：只放 orchestration，不复制 reducer 逻辑；后续超过可维护范围再拆 use case。
- UI callback 改动较多。缓解：按页面分任务，每个任务先测 store 行为，再改 screen 签名。
- PlaybackEngine 当前接口可能不覆盖所有音量更新。缓解：本轮先保证 state 和 snapshot 正确，真实 per-layer 音量同步可在音频资产/播放策略变更中处理。

