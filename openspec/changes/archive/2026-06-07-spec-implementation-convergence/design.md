# Design: 规格与实现回归

## State And Intent

沿用现有 AppStore：

- `RemoveLayer(layerId)`
- `SetLayerMuted(layerId, muted)`
- `RenameSavedMix(mixId, title)`
- `ToggleFavoriteSavedMix(mixId)`
- `SetCustomTimerDuration(minutes)`

所有持久行为继续由 `saveSnapshot()` 写入 DataStore。临时输入文本保留在 Composable 本地状态，不进入持久化 schema。

## Mixer

每个 LayerRow 提供音量、mute/unmute 和 remove 控件。播放中移除层时重新提交当前 mix，让 Android engine 释放对应 player。静音变化同时调用 `PlaybackEngine.setLayerMuted`。

## Saved Mixes

每张 MixCard 提供播放、favorite、edit 和 delete。确认非空标题后调用 `RenameSavedMix`。重命名同步同 ID 的 current/recent 标题，避免显示不一致。

## Timer And Mini Player

- 预设为 15、30、45、60、120。
- 自定义输入只接受正整数分钟，确认后发送 `SetCustomTimerDuration`。
- Mini Player subtitle 使用纯函数：active timer 优先显示向上取整后的剩余分钟，否则显示层数。

## Sound Metadata

`Sound` 增加 `loopAssetKey` 和 `defaultVolume`。当前四个发布声音的 asset key 与 stable id 相同。AppStore 添加声音时从目录读取 default volume。

## Android MediaSession

MediaSession 的 ExoPlayer 只负责系统状态和控制回调，不负责可听混音输出。创建时设置固定 `volume = 0f`；真实输出仍由进程级 `AndroidPlaybackEngine` 的 layer players 负责。

## Spec Corrections

- Restore Purchases 仅在 billing 已集成时展示。
- notification requirement 改为后台媒体控制说明，明确不请求 runtime permission。
- multi-layer 示例改为 brown noise、pink noise 和 fan。

## Testing

- reducer tests：rename/favorite saved mix。
- AppStore tests：remove/mute、metadata default volume、rename/favorite、custom timer。
- app helper tests：Mini Player timer subtitle。
- catalog tests：asset key/default volume。
- Android unit test：controller player volume policy。
- 完整 Gradle、Node asset、AAB 签名和 OpenSpec 验证。
