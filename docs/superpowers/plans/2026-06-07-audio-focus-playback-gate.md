# 音频焦点播放闸门 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans and superpowers:test-driven-development. Steps use checkbox (`- [ ]`) syntax.

**Goal:** 确保 Android audio focus 未授权时任何 layer 都不会输出。

**Architecture:** MediaSession controller 保持唯一焦点所有者；纯 decision 策略把 controller
状态映射为 Start/Await/Pause，Android engine 只按该授权启动 layer；AppStore 服从状态流。

**Tech Stack:** Kotlin、coroutines StateFlow、AndroidX Media3 1.10.1、Compose Multiplatform、kotlin.test。

---

### Task 1: Controller decision

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/MediaSessionAudioPolicy.kt`
- Test: `composeApp/src/androidUnitTest/kotlin/com/whitenoisepro/audio/MediaSessionAudioPolicyTest.kt`

- [x] 写 Start/Await/Pause 失败测试。
- [x] 运行 policy test，确认缺少 decision API 而失败。
- [x] 实现纯 enum 和 decision 函数。
- [x] 重跑 policy test，确认通过。

### Task 2: Engine gate

**Files:**
- Create: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/AudioFocusPlaybackGate.kt`
- Test: `composeApp/src/androidUnitTest/kotlin/com/whitenoisepro/audio/AudioFocusPlaybackGateTest.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/AndroidPlaybackEngine.kt`

- [x] 写 pending intent、Await、Start、Pause、Stop 失败测试。
- [x] 实现纯 gate state，确认测试通过。
- [x] engine play 只 prepare 并进入 Buffering。
- [x] engine controller callback 应用 gate decision。

### Task 3: Session and presentation

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/WhiteNoiseMediaSessionService.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/AppStore.kt`
- Test: `composeApp/src/commonTest/kotlin/com/whitenoisepro/presentation/AppStoreTest.kt`

- [x] 添加 AppStore 非乐观状态失败测试。
- [x] 移除 play 命令后的手动 true。
- [x] service 统一同步 controller 快照。
- [x] 运行目标测试和 `:composeApp:assembleDebug`。

### Task 4: Verification

**Files:**
- Modify: `docs/android-playback-spike.md`
- Create: `docs/code-review-audio-focus-playback-gate-2026-06-07.md`
- Modify: `openspec/changes/audio-focus-playback-gate/tasks.md`

- [x] 更新 Android 12/15 真机清单。
- [x] 运行 `./gradlew clean check lintDebug assembleDebug bundleRelease`。
- [x] 运行 Node、音频资产和签名 AAB 验证。
- [x] 代码审查、strict validate、归档。
