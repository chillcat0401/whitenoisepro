# Code Review: 媒体控制状态与音频中断

日期：2026-06-07

## Findings

审查期间发现并修复：

- P1：系统媒体控制恢复时读取旧 `lastMix.layer.isMuted`，可能恢复用户后来静音的 layer；
  现改为读取 engine 当前 `mutedLayers`。
- P1：整体播放暂停时取消某个 layer 静音会直接调用 `player.play()`，可能单层意外出声；
  现仅在 engine 状态为 Playing 时启动该 layer。
- P2：测试最初把永久 `StateFlow` collector 放入 `runTest` 主 scope，导致测试无法结束；
  现将短任务 scope 与可注入 observation scope 分离，生产默认仍共享 Compose scope。
- 文档：执行计划引用了不存在的音频校验脚本；现改为
  `node tools/generate_mvp_audio.mjs --verify`。

当前未发现未修复的阻塞问题。

## Tests

- `PlaybackEngineTest` 验证 play/pause/stop 的 StateFlow 状态。
- `AppStoreTest` 验证平台 Playing/Paused 自动同步前台状态。
- `MediaSessionAudioPolicyTest` 验证 audio attributes、焦点所有权、becoming noisy、
  静音恢复和暂停时取消静音策略。
- debug/release 合计 154 tests，0 failures，0 errors。
- Node 3 tests，全部通过。

## Spec Alignment

- `PlaybackEngine.states` 是 common 可观察平台状态边界。
- `AppStore` 使用协程收集，不把运行态写入 DataStore。
- controller player 是唯一 audio focus/noisy 处理者并保持静音。
- layer players 使用 media/music attributes，但不独立请求 audio focus。
- 系统 pause/resume、焦点策略和 becoming-noisy 行为与 OpenSpec delta 一致。

## Remaining Risks

- 当前验证环境没有连接真实 Android 设备，本轮未执行焦点抢占、来电、有线耳机拔出和
  蓝牙断开实测。
- Media3 瞬时焦点恢复可能自动恢复播放，具体厂商行为必须按
  `docs/android-playback-spike.md` 的发布清单验证。
- Lint 保留 3 条非阻塞既有警告：activity-compose 可更新、v26 目录冗余、`toUri`
  KTX 建议；本 change 无 lint error。
