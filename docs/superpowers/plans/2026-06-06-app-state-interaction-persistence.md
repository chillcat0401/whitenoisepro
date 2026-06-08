# App State Interaction Persistence Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 WhiteNoisePro 当前静态 MVP 的核心 UI 操作接入统一 AppStore、repository restore/save 和 playback intent 路径。

**Architecture:** 在 commonMain 新增小型 `AppStore`，集中处理 app-level intents、状态更新、repository snapshot 和 playback side effects。继续复用现有 `MixReducer`、`TimerReducer`、`AppRepository`、`PlaybackEngine`，不引入完整 DI 或 Android ViewModel。

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, common unit tests, Android Media3 behind existing `PlaybackEngine`.

---

## File Structure

- Create: `composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/AppStore.kt`
  - Defines `AppIntent`, `AppClock`, `AppIdProvider`, `AppStore`.
- Create: `composeApp/src/commonTest/kotlin/com/whitenoisepro/presentation/AppStoreTest.kt`
  - Covers restore, persistence side effects, playback calls, timer/settings/library behavior.
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/App.kt`
  - Creates/remembers AppStore and passes state plus intent callbacks to screens.
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/app/Screens.kt`
  - Replaces no-op callbacks with parameters and uses filtered library list.
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/data/AppRepository.kt`
  - Add minimal test helper visibility only if needed; do not add platform storage here.
- Modify: `docs/compliance-readiness.md`
  - Sync stale Settings placeholder status.
- Modify: `openspec/changes/app-state-interaction-persistence/tasks.md`
  - Check tasks as completed during execution.

## Task 1: AppStore Restore

**Files:**

- Create: `composeApp/src/commonTest/kotlin/com/whitenoisepro/presentation/AppStoreTest.kt`
- Create: `composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/AppStore.kt`

- [ ] **Step 1: Write failing restore tests**

Add tests for default restore, full snapshot restore, and `startLastMix=false`.

```kotlin
@Test
fun restoreUsesDefaultStateWhenRepositoryIsEmpty() {
    val repository = RecordingRepository()

    val store = AppStore(repository = repository, playbackEngine = RecordingPlaybackEngine())

    assertEquals(AppState().mixState.currentMix.id, store.state.mixState.currentMix.id)
    assertFalse(store.state.isPlaying)
    assertEquals(AppTab.Home, store.state.selectedTab)
}

@Test
fun restoreUsesSnapshotWhenStartLastMixIsEnabled() {
    val current = SoundMix(id = "stored-current", title = "存储混音")
    val saved = SoundMix(id = "stored-saved", title = "已保存")
    val repository = RecordingRepository(
        restored = AppSnapshot(
            savedMixes = listOf(saved),
            recentMixes = listOf(saved),
            currentMix = current,
            timerDefaults = SleepTimerState(durationMillis = 45 * 60 * 1000L, remainingMillis = 45 * 60 * 1000L),
            settings = UserSettings(startLastMix = true),
        ),
    )

    val store = AppStore(repository = repository, playbackEngine = RecordingPlaybackEngine())

    assertEquals("stored-current", store.state.mixState.currentMix.id)
    assertEquals(listOf("stored-saved"), store.state.mixState.savedMixes.map { it.id })
    assertEquals(45 * 60 * 1000L, store.state.timerState.durationMillis)
    assertFalse(store.state.isPlaying)
}

@Test
fun restoreKeepsDefaultCurrentMixWhenStartLastMixIsDisabled() {
    val repository = RecordingRepository(
        restored = AppSnapshot(
            currentMix = SoundMix(id = "stored-current", title = "存储混音"),
            settings = UserSettings(startLastMix = false),
        ),
    )

    val store = AppStore(repository = repository, playbackEngine = RecordingPlaybackEngine())

    assertEquals(AppState().mixState.currentMix.id, store.state.mixState.currentMix.id)
    assertFalse(store.state.settings.startLastMix)
}
```

- [ ] **Step 2: Run restore tests and confirm failure**

Run:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest --tests 'com.whitenoisepro.presentation.AppStoreTest'
```

Expected: FAIL because `AppStore` does not exist.

- [ ] **Step 3: Implement minimal AppStore restore**

Create `AppStore.kt` with `AppIntent`, default clock/id provider, constructor restore mapping, and a public `var state`.

- [ ] **Step 4: Run tests**

Run the same command.

Expected: PASS.

## Task 2: Mix And Saved Mixes Intents

**Files:**

- Modify: `composeApp/src/commonTest/kotlin/com/whitenoisepro/presentation/AppStoreTest.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/AppStore.kt`

- [ ] **Step 1: Add failing tests for mix changes**

Cover `AddSound`, `SetMasterVolume`, `SetLayerVolume`, `SaveCurrentMix`, `PlaySavedMix`, `DeleteSavedMix`.

- [ ] **Step 2: Run targeted tests**

Run:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest --tests 'com.whitenoisepro.presentation.AppStoreTest'
```

Expected: FAIL for unhandled intents.

- [ ] **Step 3: Implement mix intent handling**

Use `MixReducer.reduce` for mix updates. Save snapshot after successful persistent changes. Call `playbackEngine.play(state.mixState.currentMix)` after `PlaySavedMix`.

- [ ] **Step 4: Run tests**

Expected: PASS.

## Task 3: Library Intents

**Files:**

- Modify: `composeApp/src/commonTest/kotlin/com/whitenoisepro/presentation/AppStoreTest.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/AppStore.kt`

- [ ] **Step 1: Add failing library tests**

Cover query/category update, no repository save, and filtered sounds from `SoundCatalog.filter`.

- [ ] **Step 2: Implement library intent handling**

`SetLibraryQuery` and `SelectCategory` update transient state only. Add `visibleSounds()` or equivalent helper on AppStore for UI consumption.

- [ ] **Step 3: Run tests**

Expected: PASS.

## Task 4: Timer And Settings Intents

**Files:**

- Modify: `composeApp/src/commonTest/kotlin/com/whitenoisepro/presentation/AppStoreTest.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/AppStore.kt`

- [ ] **Step 1: Add failing timer/settings tests**

Cover preset, start, extend, cancel, and start-last-mix toggle.

- [ ] **Step 2: Implement timer/settings intent handling**

Use `TimerReducer.reduce`. Persist timer/settings state after mutation.

- [ ] **Step 3: Run tests**

Expected: PASS.

## Task 5: Compose UI Wiring

**Files:**

- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/App.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/app/Screens.kt`

- [ ] **Step 1: Update screen signatures**

Add callback parameters for existing no-op interactions. Keep default simple lambdas only where previews/tests require them.

- [ ] **Step 2: Wire WhiteNoiseProApp to AppStore**

Remember one `AppStore`, read `store.state`, and dispatch `AppIntent` from screen callbacks.

- [ ] **Step 3: Run build verification**

Run:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug
```

Expected: BUILD SUCCESSFUL.

## Task 6: Documentation And Final Verification

**Files:**

- Modify: `docs/compliance-readiness.md`
- Modify: `openspec/changes/app-state-interaction-persistence/tasks.md`

- [ ] **Step 1: Update compliance readiness**

State that Restore Purchases is hidden in current release-ready Settings UI and that remaining release blockers are privacy URL, notification runtime permission, real audio assets, QA, and store materials.

- [ ] **Step 2: Run stale text scan**

Run:

```bash
rg -n "Restore Purchases is present|placeholder only|current production UI placeholder" docs/compliance-readiness.md
```

Expected: no output.

- [ ] **Step 3: Full verification**

Run:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Request code review**

Use `superpowers:requesting-code-review` or write a local review note documenting findings and residual risks.

## Self-Review

- Spec coverage: tasks cover restore, persistence, interaction wiring, docs, verification.
- Placeholder scan: no `TBD` or unspecified implementation steps remain.
- Scope check: audio assets, privacy URL, notification runtime permission, billing and HarmonyOS are explicitly out of scope.

