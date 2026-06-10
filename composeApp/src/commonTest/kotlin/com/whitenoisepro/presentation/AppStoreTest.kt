package com.whitenoisepro.presentation

import com.whitenoisepro.app.AppTab
import com.whitenoisepro.data.AppRepository
import com.whitenoisepro.data.AppSnapshot
import com.whitenoisepro.domain.model.PlaybackState
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.domain.model.UserSettings
import com.whitenoisepro.data.SoundCatalog
import com.whitenoisepro.playback.PlaybackEngine
import com.whitenoisepro.playback.FakePlaybackEngine
import com.whitenoisepro.playback.PlatformSleepTimerRuntime
import com.whitenoisepro.domain.model.PlaybackStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppStoreTest {
    @Test
    fun restoreUsesDefaultStateWhenRepositoryIsEmpty() = runTest {
        val repository = RecordingRepository()

        val store = AppStore(
            repository = repository,
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
        )
        advanceUntilIdle()

        assertEquals(AppState().mixState.currentMix.id, store.state.mixState.currentMix.id)
        assertFalse(store.state.isPlaying)
        assertEquals(AppTab.Home, store.state.selectedTab)
        assertTrue(store.isInitialized)
    }

    @Test
    fun emptyRepositoryStillReflectsProcessPlaybackState() = runTest {
        val playback = FakePlaybackEngine().apply {
            play(testMix(id = "process-mix"))
        }
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
        )

        advanceUntilIdle()

        assertTrue(store.state.isPlaying)
    }

    @Test
    fun restoreUsesSnapshotWhenStartLastMixIsEnabled() = runTest {
        val current = SoundMix(id = "stored-current", title = "存储混音")
        val saved = SoundMix(id = "stored-saved", title = "已保存")
        val repository = RecordingRepository(
            restored = AppSnapshot(
                savedMixes = listOf(saved),
                recentMixes = listOf(saved),
                currentMix = current,
                timerDefaults = SleepTimerState(
                    durationMillis = 45 * 60 * 1000L,
                    remainingMillis = 45 * 60 * 1000L,
                ),
                settings = UserSettings(startLastMix = true),
            ),
        )

        val store = AppStore(
            repository = repository,
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
        )
        advanceUntilIdle()

        assertEquals("stored-current", store.state.mixState.currentMix.id)
        assertEquals(listOf("stored-saved"), store.state.mixState.savedMixes.map { it.id })
        assertEquals(listOf("stored-saved"), store.state.mixState.recentMixes.map { it.id })
        assertEquals(45 * 60 * 1000L, store.state.timerState.durationMillis)
        assertFalse(store.state.isPlaying)
    }

    @Test
    fun restoreKeepsDefaultCurrentMixWhenStartLastMixIsDisabled() = runTest {
        val repository = RecordingRepository(
            restored = AppSnapshot(
                currentMix = SoundMix(id = "stored-current", title = "存储混音"),
                settings = UserSettings(startLastMix = false),
            ),
        )

        val store = AppStore(
            repository = repository,
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
        )
        advanceUntilIdle()

        assertEquals(AppState().mixState.currentMix.id, store.state.mixState.currentMix.id)
        assertFalse(store.state.settings.startLastMix)
    }

    @Test
    fun restoreReflectsPlaybackAlreadyRunningInProcessEngine() = runTest {
        val playback = FakePlaybackEngine().apply {
            play(testMix(id = "stored-current"))
        }
        val store = AppStore(
            repository = RecordingRepository(
                restored = AppSnapshot(
                    currentMix = testMix(id = "stored-current"),
                    settings = UserSettings(startLastMix = true),
                ),
            ),
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
        )

        advanceUntilIdle()

        assertTrue(store.state.isPlaying)
    }

    @Test
    fun platformPlaybackStateChangesUpdateVisiblePlayingState() = runTest {
        val playback = RecordingPlaybackEngine()
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
        )
        advanceUntilIdle()

        playback.emitStatus(PlaybackStatus.Playing)
        runCurrent()
        assertTrue(store.state.isPlaying)

        playback.emitStatus(PlaybackStatus.Paused)
        runCurrent()
        assertFalse(store.state.isPlaying)
    }

    @Test
    fun playCommandsWaitForAuthoritativeEnginePlayingState() = runTest {
        val saved = testMix(id = "saved-focus")
        val playback = RecordingPlaybackEngine(emitPlayingOnPlay = false)
        val store = AppStore(
            repository = RecordingRepository(
                restored = AppSnapshot(savedMixes = listOf(saved)),
            ),
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 1_000L),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.TogglePlayback)
        assertFalse(store.state.isPlaying)

        store.dispatch(AppIntent.PlaySavedMix("saved-focus"))
        assertFalse(store.state.isPlaying)

        store.dispatch(AppIntent.SelectTimerPreset(30))
        store.dispatch(AppIntent.StartTimer)
        assertFalse(store.state.isPlaying)

        playback.emitStatus(PlaybackStatus.Playing)
        runCurrent()
        assertTrue(store.state.isPlaying)

        store.dispatch(AppIntent.CancelTimer)
        runCurrent()
    }

    @Test
    fun togglingWhilePlaybackIsBufferingCancelsPendingPlay() = runTest {
        val playback = RecordingPlaybackEngine(emitPlayingOnPlay = false)
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.TogglePlayback)
        playback.emitStatus(PlaybackStatus.Buffering)
        runCurrent()

        store.dispatch(AppIntent.TogglePlayback)

        assertEquals(1, playback.pausedCalls.size)
    }

    @Test
    fun addSoundAndVolumeChangesUpdateMixAndSaveSnapshot() = runTest {
        val repository = RecordingRepository()
        val playback = RecordingPlaybackEngine()
        val store = AppStore(
            repository = repository,
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            idProvider = FixedIdProvider(layerIds = listOf("layer-ocean-new")),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.AddSound("ocean"))
        store.dispatch(AppIntent.SetMasterVolume(0.42f))
        store.dispatch(AppIntent.SetLayerVolume("layer-ocean-new", 0.31f))
        advanceUntilIdle()

        val layer = store.state.mixState.currentMix.layers.first { it.id == "layer-ocean-new" }
        assertEquals("ocean", layer.soundId)
        assertEquals(0.31f, layer.volume)
        assertEquals(0.42f, store.state.mixState.currentMix.masterVolume)
        assertEquals(0.42f, playback.masterVolumes.last())
        assertEquals(0.31f, playback.layerVolumes.getValue("layer-ocean-new"))
        assertEquals("ocean", repository.savedSnapshots.last().currentMix?.layers?.last()?.soundId)
    }

    @Test
    fun addSoundUsesCatalogDefaultVolume() = runTest {
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
            idProvider = FixedIdProvider(layerIds = listOf("layer-pink-new")),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.AddSound("pink_noise"))
        advanceUntilIdle()

        assertEquals(
            SoundCatalog.all.first { it.id == "pink_noise" }.defaultVolume,
            store.state.mixState.currentMix.layers.last().volume,
        )
    }

    @Test
    fun removeAndMuteLayerUpdatePlaybackAndPersistence() = runTest {
        val repository = RecordingRepository()
        val playback = RecordingPlaybackEngine()
        val store = AppStore(repository, playback, this, backgroundScope)
        advanceUntilIdle()
        val layerId = store.state.mixState.currentMix.layers.first().id

        store.dispatch(AppIntent.TogglePlayback)
        runCurrent()
        store.dispatch(AppIntent.SetLayerMuted(layerId, true))
        store.dispatch(AppIntent.SetLayerMuted(layerId, false))
        store.dispatch(AppIntent.RemoveLayer(layerId))
        advanceUntilIdle()

        assertEquals(listOf(true, false), playback.layerMutedHistory.getValue(layerId))
        assertFalse(store.state.mixState.currentMix.layers.any { it.id == layerId })
        assertFalse(repository.savedSnapshots.last().currentMix!!.layers.any { it.id == layerId })
        assertEquals(store.state.mixState.currentMix.layers, playback.playedMixes.last().layers)
    }

    @Test
    fun addSoundWhilePlayingSendsUpdatedMixToPlaybackEngine() = runTest {
        val playback = RecordingPlaybackEngine()
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            idProvider = FixedIdProvider(layerIds = listOf("layer-ocean-new")),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.TogglePlayback)
        runCurrent()
        store.dispatch(AppIntent.AddSound("ocean"))

        assertEquals(listOf("deep-night-noise", "deep-night-noise"), playback.playedMixIds)
        assertEquals("ocean", playback.playedMixes.last().layers.last().soundId)
    }

    @Test
    fun saveCurrentMixUpdatesEquivalentSavedMixAndPersists() = runTest {
        val repository = RecordingRepository()
        val store = AppStore(
            repository = repository,
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 123L),
            idProvider = FixedIdProvider(savedMixIds = listOf("saved-current")),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.SaveCurrentMix)
        advanceUntilIdle()

        assertEquals("deep-night-noise", store.state.mixState.savedMixes.first().id)
        assertEquals(123L, store.state.mixState.savedMixes.first().updatedAtEpochMillis)
        assertEquals("deep-night-noise", repository.savedSnapshots.last().savedMixes.first().id)
    }

    @Test
    fun playPresetMixReplacesCurrentAndStartsPlayback() = runTest {
        val playback = RecordingPlaybackEngine()
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 789L),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.PlayPresetMix("preset-rain-train"))
        runCurrent()
        advanceUntilIdle()

        assertEquals("preset-rain-train", store.state.mixState.currentMix.id)
        assertEquals(789L, store.state.mixState.currentMix.updatedAtEpochMillis)
        assertEquals("preset-rain-train", store.state.mixState.recentMixes.first().id)
        assertEquals(listOf("preset-rain-train"), playback.playedMixIds)

        store.dispatch(AppIntent.PlayPresetMix("preset-unknown"))
        runCurrent()
        assertEquals("preset-rain-train", store.state.mixState.currentMix.id)
        assertEquals(1, playback.playedMixIds.size)
    }

    @Test
    fun rollDiceMixGeneratesStructuredMixAndPlays() = runTest {
        val playback = RecordingPlaybackEngine()
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            random = kotlin.random.Random(7),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.RollDiceMix)
        runCurrent()

        val current = store.state.mixState.currentMix
        assertTrue(current.id.startsWith("dice-"))
        assertTrue(current.layers.size in 2..4)
        assertEquals(current.id, playback.playedMixes.last().id)
        assertEquals(current.id, store.state.mixState.recentMixes.first().id)
    }

    @Test
    fun playSavedMixUpdatesCurrentRecentAndPlayback() = runTest {
        val saved = testMix(id = "saved-focus", title = "专注")
        val repository = RecordingRepository(
            restored = AppSnapshot(savedMixes = listOf(saved), settings = UserSettings(startLastMix = true)),
        )
        val playback = RecordingPlaybackEngine()
        val store = AppStore(
            repository = repository,
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 456L),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.PlaySavedMix("saved-focus"))
        runCurrent()
        advanceUntilIdle()

        assertEquals("saved-focus", store.state.mixState.currentMix.id)
        assertEquals(listOf("saved-focus"), store.state.mixState.recentMixes.map { it.id })
        assertEquals(listOf("saved-focus"), playback.playedMixIds)
        assertTrue(store.state.isPlaying)
        assertEquals("saved-focus", repository.savedSnapshots.last().currentMix?.id)
    }

    @Test
    fun deleteSavedMixRemovesMixAndPersists() = runTest {
        val saved = testMix(id = "saved-focus", title = "专注")
        val repository = RecordingRepository(restored = AppSnapshot(savedMixes = listOf(saved)))
        val store = AppStore(
            repository = repository,
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.DeleteSavedMix("saved-focus"))
        advanceUntilIdle()

        assertTrue(store.state.mixState.savedMixes.isEmpty())
        assertTrue(repository.savedSnapshots.last().savedMixes.isEmpty())
    }

    @Test
    fun renameAndFavoriteSavedMixPersist() = runTest {
        val saved = testMix(id = "saved-focus", title = "旧标题")
        val repository = RecordingRepository(restored = AppSnapshot(savedMixes = listOf(saved)))
        val store = AppStore(repository, RecordingPlaybackEngine(), this, backgroundScope)
        advanceUntilIdle()

        store.dispatch(AppIntent.RenameSavedMix("saved-focus", "新标题"))
        store.dispatch(AppIntent.ToggleFavoriteSavedMix("saved-focus"))
        advanceUntilIdle()

        val updated = store.state.mixState.savedMixes.single()
        assertEquals("新标题", updated.title)
        assertTrue(updated.isFavorite)
        assertEquals(updated, repository.savedSnapshots.last().savedMixes.single())
    }

    @Test
    fun libraryQueryAndCategoryAreTransientAndFilterVisibleSounds() = runTest {
        val repository = RecordingRepository()
        val store = AppStore(
            repository = repository,
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.SetLibraryQuery("粉"))
        store.dispatch(AppIntent.SelectCategory("噪声"))

        assertEquals("粉", store.state.libraryQuery)
        assertEquals("噪声", store.state.selectedCategory)
        assertEquals(listOf("pink_noise"), store.visibleSounds().map { it.id })
        assertTrue(repository.savedSnapshots.isEmpty())
    }

    @Test
    fun timerIntentsUpdateStateAndPersist() = runTest {
        val repository = RecordingRepository()
        val store = AppStore(
            repository = repository,
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 1_000L),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.SelectTimerPreset(45))
        store.dispatch(AppIntent.StartTimer)
        store.dispatch(AppIntent.ExtendTimer(10))
        store.dispatch(AppIntent.CancelTimer)
        advanceUntilIdle()

        assertEquals(0L, store.state.timerState.remainingMillis)
        assertEquals(null, store.state.timerState.startedAtEpochMillis)
        assertEquals(0L, repository.savedSnapshots.last().timerDefaults.remainingMillis)
    }

    @Test
    fun recommendedBedtimeTimerStartsThirtyMinutePlaybackFromHomePath() = runTest {
        val playback = RecordingPlaybackEngine()
        val runtime = RecordingPlatformSleepTimerRuntime()
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = playback,
            platformSleepTimerRuntime = runtime,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 1_000L),
        )
        advanceUntilIdle()

        assertEquals(30, recommendedBedtimeTimerMinutes)
        store.dispatch(AppIntent.StartRecommendedBedtimeTimer)
        runCurrent()

        assertEquals(30 * 60 * 1000L, store.state.timerState.durationMillis)
        assertTrue(store.state.timerState.isActive)
        assertEquals(listOf("deep-night-noise"), playback.playedMixIds)
        assertEquals(30 * 60 * 1000L, runtime.scheduled.single().remainingMillis)

        store.dispatch(AppIntent.CancelTimer)
        runCurrent()
    }

    @Test
    fun customTimerDurationUpdatesPreparedTimerAndPersists() = runTest {
        val repository = RecordingRepository()
        val store = AppStore(repository, RecordingPlaybackEngine(), this, backgroundScope)
        advanceUntilIdle()

        store.dispatch(AppIntent.SetCustomTimerDuration(95))
        advanceUntilIdle()

        assertEquals(95 * 60 * 1000L, store.state.timerState.durationMillis)
        assertEquals(95 * 60 * 1000L, repository.savedSnapshots.last().timerDefaults.durationMillis)
    }

    @Test
    fun startLastMixSettingUpdatesAndPersists() = runTest {
        val repository = RecordingRepository()
        val store = AppStore(
            repository = repository,
            playbackEngine = RecordingPlaybackEngine(),
            scope = this,
            playbackObservationScope = backgroundScope,
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.SetStartLastMix(false))
        advanceUntilIdle()

        assertFalse(store.state.settings.startLastMix)
        assertFalse(repository.savedSnapshots.last().settings.startLastMix)
    }

    @Test
    fun runningTimerTicksFadesAndStopsPlaybackAtCompletion() = runTest {
        val repository = RecordingRepository()
        val playback = RecordingPlaybackEngine()
        val clock = MutableClock(0L)
        val store = AppStore(
            repository = repository,
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = clock,
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.SelectTimerPreset(1))
        store.dispatch(AppIntent.StartTimer)
        runCurrent()
        assertTrue(store.state.isPlaying)
        assertEquals(listOf("deep-night-noise"), playback.playedMixIds)

        clock.now = 56_000L
        advanceTimeBy(1_000L)
        runCurrent()
        assertEquals(4_000L, store.state.timerState.remainingMillis)
        assertTrue(playback.fadeFactors.last() < 1f)

        clock.now = 60_000L
        advanceTimeBy(1_000L)
        runCurrent()
        assertEquals(0L, store.state.timerState.remainingMillis)
        assertFalse(store.state.isPlaying)
        assertEquals(1, playback.stoppedCalls.size)
    }

    @Test
    fun timerTicksDoNotPersistEverySecondAndCancelRestoresFade() = runTest {
        val repository = RecordingRepository()
        val playback = RecordingPlaybackEngine()
        val clock = MutableClock(0L)
        val store = AppStore(
            repository = repository,
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = clock,
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.SelectTimerPreset(1))
        store.dispatch(AppIntent.StartTimer)
        runCurrent()
        val savesAfterStart = repository.savedSnapshots.size

        clock.now = 10_000L
        advanceTimeBy(3_000L)
        runCurrent()
        assertEquals(savesAfterStart, repository.savedSnapshots.size)

        store.dispatch(AppIntent.CancelTimer)
        runCurrent()
        assertEquals(1f, playback.fadeFactors.last())
        assertFalse(store.state.timerState.isActive)
        assertEquals(savesAfterStart + 1, repository.savedSnapshots.size)
    }

    @Test
    fun restoredActiveTimerRecomputesRemainingTimeWithoutAutoPlaying() = runTest {
        val repository = RecordingRepository(
            restored = AppSnapshot(
                timerDefaults = SleepTimerState(
                    durationMillis = 60_000L,
                    remainingMillis = 60_000L,
                    startedAtEpochMillis = 10_000L,
                ),
            ),
        )
        val playback = RecordingPlaybackEngine()
        val store = AppStore(
            repository = repository,
            playbackEngine = playback,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 40_000L),
        )

        runCurrent()

        assertTrue(store.isInitialized)
        assertTrue(store.state.timerState.isActive)
        assertEquals(30_000L, store.state.timerState.remainingMillis)
        assertFalse(store.state.isPlaying)
        assertTrue(playback.playedMixIds.isEmpty())

        store.dispatch(AppIntent.CancelTimer)
        runCurrent()
    }

    @Test
    fun activeTimerChangesAreSynchronizedToPlatformRuntime() = runTest {
        val runtime = RecordingPlatformSleepTimerRuntime()
        val store = AppStore(
            repository = RecordingRepository(),
            playbackEngine = RecordingPlaybackEngine(),
            platformSleepTimerRuntime = runtime,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 1_000L),
        )
        advanceUntilIdle()

        store.dispatch(AppIntent.SelectTimerPreset(30))
        store.dispatch(AppIntent.StartTimer)
        store.dispatch(AppIntent.ExtendTimer(10))
        runCurrent()

        assertEquals(2, runtime.scheduled.size)
        assertEquals(40 * 60 * 1000L, runtime.scheduled.last().durationMillis)

        store.dispatch(AppIntent.CancelTimer)
        runCurrent()
        assertEquals(2, runtime.cancelCalls)
    }

    @Test
    fun restoredActiveTimerIsScheduledOnPlatformRuntime() = runTest {
        val runtime = RecordingPlatformSleepTimerRuntime()
        val store = AppStore(
            repository = RecordingRepository(
                restored = AppSnapshot(
                    timerDefaults = SleepTimerState(
                        durationMillis = 60_000L,
                        remainingMillis = 60_000L,
                        startedAtEpochMillis = 10_000L,
                    ),
                ),
            ),
            playbackEngine = RecordingPlaybackEngine(),
            platformSleepTimerRuntime = runtime,
            scope = this,
            playbackObservationScope = backgroundScope,
            clock = FixedClock(now = 40_000L),
        )

        runCurrent()

        assertEquals(1, runtime.scheduled.size)
        assertEquals(30_000L, runtime.scheduled.single().remainingMillis)

        store.dispatch(AppIntent.CancelTimer)
        runCurrent()
    }
}

private class RecordingRepository(
    restored: AppSnapshot? = null,
) : AppRepository {
    private val mutableSnapshots = MutableStateFlow(restored)
    val savedSnapshots = mutableListOf<AppSnapshot>()

    override val snapshots: Flow<AppSnapshot?> = mutableSnapshots.asStateFlow()

    override suspend fun save(snapshot: AppSnapshot) {
        savedSnapshots += snapshot
        mutableSnapshots.emit(snapshot)
    }
}

private class RecordingPlaybackEngine(
    private val emitPlayingOnPlay: Boolean = true,
) : PlaybackEngine {
    private val mutableStates = MutableStateFlow(PlaybackState())
    override val states = mutableStates.asStateFlow()
    override val state: PlaybackState
        get() = states.value
    val playedMixes = mutableListOf<SoundMix>()
    val playedMixIds = mutableListOf<String>()
    val pausedCalls = mutableListOf<Unit>()
    val stoppedCalls = mutableListOf<Unit>()
    val masterVolumes = mutableListOf<Float>()
    val layerVolumes = mutableMapOf<String, Float>()
    val layerMuted = mutableMapOf<String, Boolean>()
    val layerMutedHistory = mutableMapOf<String, MutableList<Boolean>>()
    val fadeFactors = mutableListOf<Float>()

    override fun play(mix: SoundMix) {
        playedMixes += mix
        playedMixIds += mix.id
        if (emitPlayingOnPlay) {
            mutableStates.value = state.copy(
                status = PlaybackStatus.Playing,
                currentMixId = mix.id,
            )
        }
    }

    override fun pause() {
        pausedCalls += Unit
        mutableStates.value = state.copy(status = PlaybackStatus.Paused)
    }

    override fun stop() {
        stoppedCalls += Unit
        mutableStates.value = PlaybackState()
    }

    override fun setLayerVolume(layerId: String, volume: Float) {
        layerVolumes[layerId] = volume
    }

    override fun setLayerMuted(layerId: String, muted: Boolean) {
        layerMuted[layerId] = muted
        layerMutedHistory.getOrPut(layerId) { mutableListOf() } += muted
    }

    override fun setMasterVolume(volume: Float) {
        masterVolumes += volume
    }

    override fun setTimerFadeFactor(factor: Float) {
        fadeFactors += factor
    }

    fun emitStatus(status: PlaybackStatus) {
        mutableStates.value = state.copy(status = status)
    }
}

private class RecordingPlatformSleepTimerRuntime : PlatformSleepTimerRuntime {
    val scheduled = mutableListOf<SleepTimerState>()
    var cancelCalls = 0

    override fun schedule(timer: SleepTimerState) {
        scheduled += timer
    }

    override fun cancel() {
        cancelCalls += 1
    }
}

private class FixedClock(
    private val now: Long,
) : AppClock {
    override fun nowEpochMillis(): Long = now
}

private class MutableClock(
    var now: Long,
) : AppClock {
    override fun nowEpochMillis(): Long = now
}

private class FixedIdProvider(
    private val layerIds: List<String> = emptyList(),
    private val savedMixIds: List<String> = emptyList(),
) : AppIdProvider {
    private var nextLayer = 0
    private var nextSavedMix = 0

    override fun nextLayerId(soundId: String): String =
        layerIds.getOrElse(nextLayer++) { "layer-$soundId-test" }

    override fun nextSavedMixId(baseTitle: String): String =
        savedMixIds.getOrElse(nextSavedMix++) { "mix-test" }
}

private fun testMix(
    id: String,
    title: String = id,
    layers: List<SoundLayer> = emptyList(),
): SoundMix = SoundMix(id = id, title = title, layers = layers)
