# Android Background Timer Runtime Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Keep sleep-timer fade and stop behavior running in MediaSessionService when the Activity/Compose scope is destroyed.

**Architecture:** AppStore synchronizes absolute timer state through a common platform boundary. A pure coroutine deadline runner performs time-based fade and completion; the Android MediaSessionService owns that runner and a process-level playback engine.

**Tech Stack:** Compose Multiplatform, Kotlin coroutines, AndroidX Media3, AndroidX DataStore, kotlin.test.

---

### Task 1: Platform Runtime Contract

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/whitenoisepro/playback/PlatformSleepTimerRuntime.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/AppStore.kt`
- Test: `composeApp/src/commonTest/kotlin/com/whitenoisepro/presentation/AppStoreTest.kt`

- [ ] Add a recording runtime to AppStore tests.
- [ ] Assert start, extend, and restore schedule the latest active state.
- [ ] Assert preset replacement, cancel, and completion cancel the runtime.
- [ ] Run targeted tests and confirm the missing contract fails.
- [ ] Implement the contract and AppStore synchronization.
- [ ] Run targeted tests until green.

### Task 2: Coroutine Deadline Runner

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/whitenoisepro/playback/SleepTimerDeadlineRunner.kt`
- Test: `composeApp/src/commonTest/kotlin/com/whitenoisepro/playback/SleepTimerDeadlineRunnerTest.kt`

- [ ] Add virtual-time tests for fade, one-shot stop, reschedule, and cancel.
- [ ] Run tests and confirm the runner is missing.
- [ ] Implement a Job-based runner using an injected epoch clock and delay interval.
- [ ] Run runner tests until green.

### Task 3: Android Service Adapter and Playback Ownership

**Files:**
- Create: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/AndroidPlatformSleepTimerRuntime.kt`
- Create: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/AndroidPlaybackEngineProvider.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/WhiteNoiseMediaSessionService.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/MainActivity.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/App.kt`

- [ ] Implement start/cancel service commands carrying timer state.
- [ ] Create the service coroutine scope and deadline runner.
- [ ] Apply runner fade/stop callbacks to the registered process-level engine.
- [ ] Inject the Android runtime into WhiteNoiseProApp/AppStore.
- [ ] Replace Activity-owned engine construction with the provider.
- [ ] Compile Android debug and release source sets.

### Task 4: Verification and Archive

**Files:**
- Modify: `docs/android-playback-spike.md`
- Create: `docs/code-review-android-background-timer-2026-06-06.md`
- Modify: `openspec/changes/android-background-timer-runtime/tasks.md`

- [ ] Document manual background timer QA and process-kill limitation.
- [ ] Run `:composeApp:check :composeApp:assembleDebug`.
- [ ] Review behavior, concurrency, lifecycle, permission, and persistence risks.
- [ ] Strictly validate and archive the OpenSpec change.
