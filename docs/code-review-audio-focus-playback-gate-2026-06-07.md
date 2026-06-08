# Code Review: 音频焦点播放闸门

日期：2026-06-07

## Findings

审查期间发现并修复：

- P0：`AndroidPlaybackEngine.play()` 在 controller 取得 audio focus 前直接启动 layer；
  Android 15 焦点请求失败时仍可能产生可听输出。现改为 controller `isPlaying` 放行。
- P1：只监听 `onIsPlayingChanged` 无法完整区分 ready、suppression 和明确 pause；
  现统一监听 isPlaying、playWhenReady、suppression reason 和 playback state。
- P1：Buffering 时 UI 的 `isPlaying=false` 会让第二次点击重复 play，无法取消 pending；
  现 AppStore 保留完整 `PlaybackStatus`，Buffering 视为活动播放请求。
- P1：AppStore 在 Toggle、Saved Mix 和 Timer 路径乐观写 `isPlaying=true`；
  现完全服从 `PlaybackEngine.states`。

当前未发现未修复的阻塞问题。

## Tests

- `MediaSessionAudioPolicyTest` 覆盖 Start/Await/Pause 决策。
- `AudioFocusPlaybackGateTest` 覆盖 pending、明确 pause 和旧 callback 防恢复。
- `AppStoreTest` 覆盖非乐观播放和 Buffering 取消。
- Media3 listener API 通过 Android debug 编译。

## Spec Alignment

- controller 实际播放是 layer 唯一授权。
- AwaitAuthorization 保持 Buffering 和全部 layer pause。
- pause/stop 清除 pending play。
- presentation 状态来自 engine StateFlow。
- Android 15 焦点失败不会被 layer player 绕过。

## Remaining Risks

- JVM 单测不能制造真实 AudioManager focus denial、来电或厂商 suppression。
- 必须按 `docs/android-playback-spike.md` 在 Android 12、14、15 真机完成发布矩阵。
- controller 仍是静音 ExoPlayer 代理；后续若改为自定义 MediaSession Player，需要重新审计闸门。
