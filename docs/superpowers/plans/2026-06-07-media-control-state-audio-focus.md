# 媒体控制状态与音频中断 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让系统媒体控制与 Android 音频中断状态实时同步到 Compose UI。

**Architecture:** `PlaybackEngine` 用 `StateFlow` 提供单一状态源，`AppStore` 用协程收集；静音 MediaSession controller player 独占 audio focus 和 becoming-noisy 处理，layer players 仅输出多层混音。

**Tech Stack:** Kotlin 2.3.20、kotlinx-coroutines 1.11.0、Compose Multiplatform、AndroidX Media3 1.10.1、kotlin.test、OpenSpec。

---

### Task 1: PlaybackEngine 状态流

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/playback/PlaybackEngine.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/AndroidPlaybackEngine.kt`
- Test: `composeApp/src/commonTest/kotlin/com/whitenoisepro/playback/PlaybackEngineTest.kt`

- [x] **Step 1: 写状态流红灯测试**

在 `PlaybackEngineTest` 收集 `engine.states.value`，依次执行 play、pause、stop，并断言
Playing、Paused、Idle。此时接口没有 `states`，编译应失败。

- [x] **Step 2: 验证红灯**

运行：

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest --tests 'com.whitenoisepro.playback.PlaybackEngineTest'
```

预期：因 `states` 未定义而 FAIL。

- [x] **Step 3: 实现最小状态流**

为接口增加 `val states: StateFlow<PlaybackState>`。fake 与 Android engine 使用私有
`MutableStateFlow`，`state` 返回 `states.value`，所有原有状态赋值改为更新 flow。

- [x] **Step 4: 验证绿灯**

重复 Step 2 命令，预期 PASS。

### Task 2: AppStore 状态同步

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/AppStore.kt`
- Test: `composeApp/src/commonTest/kotlin/com/whitenoisepro/presentation/AppStoreTest.kt`

- [x] **Step 1: 写外部状态变化红灯测试**

让 `RecordingPlaybackEngine` 暴露测试状态流和 `emitStatus` 方法。初始化 Store 后发射
Playing、Paused，并分别断言 `store.state.isPlaying` 为 true、false。

- [x] **Step 2: 验证红灯**

运行：

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest --tests 'com.whitenoisepro.presentation.AppStoreTest'
```

预期：Store 不观察 engine flow，断言 FAIL。

- [x] **Step 3: 实现收集**

repository 恢复和 timer 恢复完成后调用 `playbackEngine.states.collect`，仅当
`state.isPlaying` 与 engine 值不同时复制 `AppState`。

- [x] **Step 4: 验证绿灯与回归**

重复 Step 2 命令，预期全部 PASS。

### Task 3: Android 音频策略

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/MediaSessionAudioPolicy.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/WhiteNoiseMediaSessionService.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/AndroidPlaybackEngine.kt`
- Test: `composeApp/src/androidUnitTest/kotlin/com/whitenoisepro/audio/MediaSessionAudioPolicyTest.kt`

- [x] **Step 1: 写策略红灯测试**

断言 controller 管理 audio focus 与 becoming noisy，layer player 不管理 audio focus，
且两者都使用 media/music attributes。

- [x] **Step 2: 验证红灯**

运行：

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest --tests 'com.whitenoisepro.audio.MediaSessionAudioPolicyTest'
```

预期：新策略字段未定义而 FAIL。

- [x] **Step 3: 实现策略与 Media3 配置**

策略对象创建共享 `AudioAttributes`。controller Builder 调用
`setAudioAttributes(..., true)`，player 调用 `setHandleAudioBecomingNoisy(true)`；
layer Builder 调用 `setAudioAttributes(..., false)`。

- [x] **Step 4: 验证 Android 编译**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest :composeApp:assembleDebug
```

预期：BUILD SUCCESSFUL。

### Task 4: 回归、文档与归档

**Files:**
- Modify: `docs/android-playback-spike.md`
- Create: `docs/code-review-media-control-state-audio-focus-2026-06-07.md`
- Modify: `openspec/changes/media-control-state-audio-focus/tasks.md`

- [x] **Step 1: 更新真实设备检查项**

记录通知/锁屏控制、其他媒体抢焦点、有线耳机拔出、蓝牙断开和来电中断。

- [x] **Step 2: 全量验证**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew clean check lintDebug assembleDebug bundleRelease
node --test tools/*.test.mjs
node tools/generate_mvp_audio.mjs --verify
openspec validate --strict
```

预期：所有命令成功，debug/release 测试 0 failures，AAB 签名校验成功。

- [x] **Step 3: 代码审查**

按行为回归、测试缺口、spec 对齐、平台风险顺序审查并保存报告；发现问题立即修复和复验。

- [x] **Step 4: 归档**

所有 task 勾选后运行：

```bash
openspec validate media-control-state-audio-focus --strict
openspec archive media-control-state-audio-focus --yes
openspec validate --specs --strict
```

预期：change 进入 `openspec/changes/archive/2026-06-07-media-control-state-audio-focus`，
主 `playback-engine` spec 合并新增要求。
