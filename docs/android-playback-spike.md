# Android Playback Spike

## Current Result

The MVP now has a common `PlaybackEngine` boundary and an Android `AndroidPlaybackEngine` implementation backed by Media3 ExoPlayer. The app injects the Android engine from `MainActivity`, so the Home and Mini Player play/pause actions now call the playback boundary.

The Android implementation packages and resolves four bundled loopable raw resources:

```text
composeApp/src/androidMain/res/raw/white_noise_loop.wav
composeApp/src/androidMain/res/raw/pink_noise_loop.wav
composeApp/src/androidMain/res/raw/brown_noise_loop.wav
composeApp/src/androidMain/res/raw/fan_loop.wav
```

Each layer resolves its `soundId` independently. The files are deterministic first-party generated PCM loops with hashes and QA metrics recorded in `docs/audio-assets/generated-audio-manifest.json`.

## Verification

Automated verification currently covers the common playback boundary through `FakePlaybackEngine`.

Manual Android playback verification was completed on a local API 36 emulator.

```text
adb shell dumpsys media_session
state=PLAYING(3)
state=PAUSED(2)
state=PLAYING(3)
```

Result:

- Home play starts the Android playback engine.
- `WhiteNoiseMediaSessionService` is created while playback is active.
- Playback remains represented by an active MediaSession after pressing the Android Home key.
- `KEYCODE_MEDIA_PAUSE` changes session state to `PAUSED(2)`.
- `KEYCODE_MEDIA_PLAY` changes session state back to `PLAYING(3)`.

## Multi-layer Decision

Task 6.2 decision:

```text
Use one ExoPlayer instance per active sound layer for the MVP.
Coordinate those players through a single Android playback service / engine owner.
Cap active layers at 5 in the UI until lower-level mixing is proven necessary.
```

Rationale:

- A single ExoPlayer playlist cannot mix concurrent layers.
- One player per layer keeps per-sound loop, mute, and volume behavior straightforward.
- White-noise MVP mixes are small; the current product baseline shows three layers.
- The common UI/domain layer already talks to `PlaybackEngine`, so this Android strategy can be replaced later.

Implementation notes:

- `AndroidPlaybackEngine` keeps a `Map<layerId, ExoPlayer>`.
- `AndroidSoundResourceResolver` maps each published sound ID to its own raw resource and falls back to brown noise for legacy IDs.
- `AndroidPlaybackEngineProvider` owns one application-context engine per app process.
- `MainActivity` no longer releases the engine in `onDestroy`; Activity recreation reads the existing playback state.
- Each player uses `REPEAT_MODE_ONE`.
- `WhiteNoiseMediaSessionService` exposes a session player for system controls and forwards play/pause events back to the engine.
- `WhiteNoiseMediaSessionService` owns a coroutine sleep-timer deadline runner while the service remains alive.
- The service timer and UI timer both derive remaining time from the same absolute start time and duration.
- Effective layer volume should be:

```text
layer.volume * mix.masterVolume * timerFadeFactor
```

- Removed or muted layer players are released or paused promptly.
- If device testing shows unacceptable CPU/battery cost, update OpenSpec before falling back to a constrained single-layer MVP or lower-level audio mixing.

## Background Timer Boundary

The service-backed timer covers Activity and Compose lifecycle loss while the app process and `MediaSessionService` remain alive.

It does not promise exact execution after the operating system kills the app process. That stronger guarantee would require a separate AlarmManager and policy review.

Manual verification:

1. Start playback and a one-minute timer.
2. Press Home and confirm media controls remain available.
3. Lock the screen and confirm fade and stop occur at the deadline.
4. Reopen the Activity before completion and confirm Mini Player still shows playing.
5. Start another timer, remove the task from Recents, and verify behavior while the media service remains alive.
6. Force-stop the package and record this as an expected process boundary, not a passing background-timer case.

## 音频焦点与系统状态同步

MediaSession controller player 是 Android 音频焦点的唯一所有者：

- controller 使用 `USAGE_MEDIA` / `AUDIO_CONTENT_TYPE_MUSIC` 并启用 Media3 自动 audio focus。
- controller 启用 `setHandleAudioBecomingNoisy(true)`，耳机或蓝牙输出断开时暂停。
- layer players 使用相同 audio attributes，但不单独请求 audio focus。
- `PlaybackEngine.states` 将系统 pause/resume 传播到 `AppStore`，前台 UI 跟随真实状态。
- 已静音 layer 在系统恢复后保持静音；播放暂停时取消静音不会单独启动该 layer。

发布前真实设备验证：

1. 从通知和锁屏分别执行 pause/play，确认 Mini Player 与页面按钮同步。
2. 播放期间启动另一个音乐或视频应用，确认焦点切换时所有 layer 状态一致。
3. 模拟来电或语音通话，记录瞬时焦点恢复是否符合 Media3 和产品预期。
4. 拔出有线耳机，确认立即暂停且不会从扬声器继续播放。
5. 断开蓝牙耳机，确认立即暂停且 UI 同步。
6. 静音一个 layer，再从系统控制 pause/play，确认该 layer 不会恢复出声。
7. 暂停全部播放后取消某个 layer 的静音，确认不会单层意外播放。

## 音频焦点播放闸门

Media3 controller 的 `isPlaying` 是 audible layer 的唯一授权信号：

- 应用 play 命令只 prepare layer，并将 engine 置为 Buffering。
- controller `playWhenReady=true` 但 `isPlaying=false` 时，所有 layer 保持 pause。
- controller `isPlaying=true` 后才启动当前未静音 layer。
- controller 明确 pause 或 engine stop 会清除 pending play，旧 callback 不能恢复 layer。
- Buffering 期间再次点击播放按钮等价于取消 pending play。

Android 官方边界：

- Android 12+ 在其他应用获取焦点或来电时可能自动淡出/静音当前媒体。
- Android 15 / API 35+ 中，非顶部应用且未运行前台服务时 audio focus 请求会失败。
- 本应用不绕过系统限制；请求失败时 controller 不会实际播放，layer 闸门保持关闭。

发布前设备矩阵：

1. Android 12、14、15 各执行一次冷启动首次播放，确认 controller Playing 前无 layer 声音。
2. Android 15 将应用置于后台后，从非 MediaSession 路径尝试恢复，确认不会绕过焦点限制。
3. 从系统通知恢复，确认 MediaSessionService 路径能正常取得焦点并同步 layer。
4. 播放期间启动音乐/视频应用，确认 suppression 时 layer 全部暂停。
5. 瞬时焦点恢复后，确认只恢复未静音 layer。
6. controller 处于 Buffering 时再次点击播放，确认 pending 请求被取消。
7. 执行 stop 后模拟延迟 controller callback，确认 layer 不会重启。
