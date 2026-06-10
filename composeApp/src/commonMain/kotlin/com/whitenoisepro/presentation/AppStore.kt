package com.whitenoisepro.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.whitenoisepro.app.AppTab
import com.whitenoisepro.data.AppRepository
import com.whitenoisepro.data.AppSnapshot
import com.whitenoisepro.data.PresetCatalog
import com.whitenoisepro.data.SoundCatalog
import com.whitenoisepro.domain.MixDice
import com.whitenoisepro.domain.model.Sound
import com.whitenoisepro.domain.model.SoundCategory
import com.whitenoisepro.domain.model.PlaybackStatus
import com.whitenoisepro.domain.reducer.MixState
import com.whitenoisepro.domain.reducer.MixIntent
import com.whitenoisepro.domain.reducer.MixReducer
import com.whitenoisepro.domain.reducer.TimerIntent
import com.whitenoisepro.domain.reducer.TimerReducer
import com.whitenoisepro.playback.PlaybackEngine
import com.whitenoisepro.playback.NoOpPlatformSleepTimerRuntime
import com.whitenoisepro.playback.PlatformSleepTimerRuntime
import com.whitenoisepro.playback.TimerPlaybackCoordinator
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

sealed interface AppIntent {
    data class SelectTab(val tab: AppTab) : AppIntent
    data object TogglePlayback : AppIntent
    data class SetMasterVolume(val volume: Float) : AppIntent
    data class SetLayerVolume(val layerId: String, val volume: Float) : AppIntent
    data class SetLayerMuted(val layerId: String, val muted: Boolean) : AppIntent
    data class RemoveLayer(val layerId: String) : AppIntent
    data object ToggleFavoriteCurrent : AppIntent
    data class ToggleSound(val soundId: String) : AppIntent
    data object SaveCurrentMix : AppIntent
    data class PlaySavedMix(val mixId: String) : AppIntent
    data class PlayPresetMix(val presetId: String) : AppIntent
    data object RollDiceMix : AppIntent
    data class DeleteSavedMix(val mixId: String) : AppIntent
    data class RenameSavedMix(val mixId: String, val title: String) : AppIntent
    data class ToggleFavoriteSavedMix(val mixId: String) : AppIntent
    data class SetLibraryQuery(val query: String) : AppIntent
    data class SelectCategory(val category: String) : AppIntent
    data class SelectTimerPreset(val minutes: Int) : AppIntent
    data class SetCustomTimerDuration(val minutes: Int) : AppIntent
    data object StartRecommendedBedtimeTimer : AppIntent
    data object StartTimer : AppIntent
    data class ExtendTimer(val minutes: Int) : AppIntent
    data object CancelTimer : AppIntent
    data class SetStartLastMix(val enabled: Boolean) : AppIntent
    data object ClearFeedback : AppIntent
}

interface AppClock {
    fun nowEpochMillis(): Long
}

class SystemAppClock : AppClock {
    @OptIn(ExperimentalTime::class)
    override fun nowEpochMillis(): Long =
        Clock.System.now().toEpochMilliseconds()
}

interface AppIdProvider {
    fun nextLayerId(soundId: String): String
    fun nextSavedMixId(baseTitle: String): String
}

class SequentialAppIdProvider(
    private var next: Int = 1,
) : AppIdProvider {
    override fun nextLayerId(soundId: String): String =
        "layer-$soundId-${next++}"

    override fun nextSavedMixId(baseTitle: String): String =
        "mix-${next++}"
}

class AppStore(
    private val repository: AppRepository,
    private val playbackEngine: PlaybackEngine,
    private val scope: CoroutineScope,
    private val playbackObservationScope: CoroutineScope = scope,
    private val platformSleepTimerRuntime: PlatformSleepTimerRuntime = NoOpPlatformSleepTimerRuntime,
    private val clock: AppClock = SystemAppClock(),
    private val idProvider: AppIdProvider = SequentialAppIdProvider(),
    private val random: Random = Random.Default,
) {
    private var timerJob: Job? = null

    var state: AppState by mutableStateOf(AppState())
        private set

    var isInitialized: Boolean by mutableStateOf(false)
        private set

    init {
        scope.launch {
            state = restoreInitialState(repository.snapshots.first())
            isInitialized = true
            resumeRestoredTimer()
            playbackObservationScope.launch(start = CoroutineStart.UNDISPATCHED) {
                playbackEngine.states.collect { playbackState ->
                    if (
                        state.playbackStatus != playbackState.status ||
                        state.isPlaying != playbackState.isPlaying
                    ) {
                        state = state.copy(
                            playbackStatus = playbackState.status,
                            isPlaying = playbackState.isPlaying,
                        )
                    }
                }
            }
        }
    }

    fun dispatch(intent: AppIntent) {
        when (intent) {
            is AppIntent.SelectTab -> state = state.copy(selectedTab = intent.tab)
            AppIntent.TogglePlayback -> togglePlayback()
            is AppIntent.SetMasterVolume -> updateMix(
                MixIntent.SetMasterVolume(intent.volume),
                syncPlaybackWhenPlaying = false,
                after = { playbackEngine.setMasterVolume(state.mixState.currentMix.masterVolume) },
            )
            is AppIntent.SetLayerVolume -> updateMix(
                MixIntent.SetLayerVolume(intent.layerId, intent.volume),
                syncPlaybackWhenPlaying = false,
                after = { playbackEngine.setLayerVolume(intent.layerId, intent.volume) },
            )
            is AppIntent.SetLayerMuted -> updateMix(
                if (intent.muted) MixIntent.MuteLayer(intent.layerId) else MixIntent.UnmuteLayer(intent.layerId),
                syncPlaybackWhenPlaying = false,
                after = { playbackEngine.setLayerMuted(intent.layerId, intent.muted) },
            )
            is AppIntent.RemoveLayer -> updateMix(
                MixIntent.RemoveLayer(intent.layerId),
                syncPlaybackWhenPlaying = true,
            )
            AppIntent.ToggleFavoriteCurrent -> updateMix(
                MixIntent.ToggleFavoriteCurrent,
                syncPlaybackWhenPlaying = false,
            )
            is AppIntent.ToggleSound -> {
                val existingLayers = state.mixState.currentMix.layers.filter { it.soundId == intent.soundId }
                if (existingLayers.isEmpty()) {
                    updateMix(
                        MixIntent.AddSound(
                            layerId = idProvider.nextLayerId(intent.soundId),
                            soundId = intent.soundId,
                            volume = SoundCatalog.all.firstOrNull { it.id == intent.soundId }?.defaultVolume
                                ?: com.whitenoisepro.domain.model.SoundLayer.DefaultVolume,
                        ),
                        syncPlaybackWhenPlaying = true,
                    )
                    showFeedback("已加入「${SoundCatalog.nameOf(intent.soundId)}」")
                } else {
                    existingLayers.forEach { layer ->
                        updateMix(
                            MixIntent.RemoveLayer(layer.id),
                            syncPlaybackWhenPlaying = true,
                        )
                    }
                    showFeedback("已移除「${SoundCatalog.nameOf(intent.soundId)}」")
                }
            }
            AppIntent.SaveCurrentMix -> {
                updateMix(
                    MixIntent.SaveCurrentMix(
                        savedMixId = idProvider.nextSavedMixId(state.mixState.currentMix.title),
                        nowEpochMillis = clock.nowEpochMillis(),
                    ),
                    syncPlaybackWhenPlaying = false,
                )
                showFeedback("混音已保存到「已保存」")
            }
            is AppIntent.PlaySavedMix -> {
                updateMix(
                    MixIntent.PlaySavedMix(
                        mixId = intent.mixId,
                        nowEpochMillis = clock.nowEpochMillis(),
                    ),
                )
                playbackEngine.play(state.mixState.currentMix)
            }
            AppIntent.RollDiceMix -> {
                updateMix(
                    MixIntent.ReplaceCurrentMix(
                        mix = MixDice.roll(random),
                        nowEpochMillis = clock.nowEpochMillis(),
                    ),
                )
                playbackEngine.play(state.mixState.currentMix)
            }
            is AppIntent.PlayPresetMix -> {
                val preset = PresetCatalog.byId(intent.presetId) ?: return
                updateMix(
                    MixIntent.ReplaceCurrentMix(
                        mix = preset,
                        nowEpochMillis = clock.nowEpochMillis(),
                    ),
                )
                playbackEngine.play(state.mixState.currentMix)
            }
            is AppIntent.DeleteSavedMix -> updateMix(
                MixIntent.DeleteSavedMix(intent.mixId),
                syncPlaybackWhenPlaying = false,
            )
            is AppIntent.RenameSavedMix -> updateMix(
                MixIntent.RenameSavedMix(intent.mixId, intent.title),
                syncPlaybackWhenPlaying = false,
            )
            is AppIntent.ToggleFavoriteSavedMix -> updateMix(
                MixIntent.ToggleFavoriteSavedMix(intent.mixId),
                syncPlaybackWhenPlaying = false,
            )
            is AppIntent.SetLibraryQuery -> state = state.copy(libraryQuery = intent.query)
            is AppIntent.SelectCategory -> state = state.copy(selectedCategory = intent.category)
            is AppIntent.SelectTimerPreset -> prepareTimer(intent.minutes)
            is AppIntent.SetCustomTimerDuration -> prepareCustomTimer(intent.minutes)
            AppIntent.StartRecommendedBedtimeTimer -> startRecommendedBedtimeTimer()
            AppIntent.StartTimer -> startTimer()
            is AppIntent.ExtendTimer -> extendTimer(intent.minutes)
            AppIntent.CancelTimer -> cancelTimer()
            is AppIntent.SetStartLastMix -> {
                state = state.copy(settings = state.settings.copy(startLastMix = intent.enabled))
                saveSnapshot()
            }
            AppIntent.ClearFeedback -> state = state.copy(feedback = null)
        }
    }

    private var feedbackCounter: Long = 0L

    private fun showFeedback(text: String) {
        feedbackCounter += 1
        state = state.copy(feedback = UiFeedback(id = feedbackCounter, text = text))
    }

    fun visibleSounds(): List<Sound> =
        SoundCatalog.filter(
            category = state.selectedCategory.toSoundCategoryOrNull(),
            query = state.libraryQuery,
        )

    private fun restoreInitialState(snapshot: AppSnapshot?): AppState {
        snapshot ?: return AppState(
            playbackStatus = playbackEngine.state.status,
            isPlaying = playbackEngine.state.isPlaying,
        )
        val defaults = AppState()
        val currentMix = if (snapshot.settings.startLastMix) {
            snapshot.currentMix ?: defaults.mixState.currentMix
        } else {
            defaults.mixState.currentMix
        }
        return defaults.copy(
            mixState = MixState(
                currentMix = currentMix,
                savedMixes = snapshot.savedMixes.ifEmpty { defaults.mixState.savedMixes },
                recentMixes = snapshot.recentMixes,
            ),
            timerState = snapshot.timerDefaults,
            settings = snapshot.settings,
            selectedTab = AppTab.Home,
            playbackStatus = playbackEngine.state.status,
            isPlaying = playbackEngine.state.isPlaying,
        )
    }

    private fun togglePlayback() {
        if (state.hasActivePlayRequest) {
            playbackEngine.pause()
        } else {
            playbackEngine.play(state.mixState.currentMix)
        }
    }

    private fun updateMix(
        intent: MixIntent,
        syncPlaybackWhenPlaying: Boolean = false,
        after: () -> Unit = {},
    ) {
        val wasPlaying = state.hasActivePlayRequest
        state = state.copy(mixState = MixReducer.reduce(state.mixState, intent))
        if (syncPlaybackWhenPlaying && wasPlaying) {
            playbackEngine.play(state.mixState.currentMix)
        }
        after()
        saveSnapshot()
    }

    private fun updateTimer(intent: TimerIntent) {
        state = state.copy(timerState = TimerReducer.reduce(state.timerState, intent))
        saveSnapshot()
    }

    private fun prepareTimer(minutes: Int) {
        timerJob?.cancel()
        timerJob = null
        platformSleepTimerRuntime.cancel()
        playbackEngine.setTimerFadeFactor(1f)
        updateTimer(TimerIntent.SelectPreset(minutes))
    }

    private fun prepareCustomTimer(minutes: Int) {
        timerJob?.cancel()
        timerJob = null
        platformSleepTimerRuntime.cancel()
        playbackEngine.setTimerFadeFactor(1f)
        updateTimer(TimerIntent.SetCustomDuration(minutes))
    }

    private fun startTimer() {
        state = state.copy(
            timerState = TimerReducer.reduce(
                state.timerState,
                TimerIntent.Start(clock.nowEpochMillis()),
            ),
        )
        if (!state.hasActivePlayRequest) {
            playbackEngine.play(state.mixState.currentMix)
        }
        TimerPlaybackCoordinator.apply(state.timerState, playbackEngine)
        platformSleepTimerRuntime.schedule(state.timerState)
        saveSnapshot()
        startTimerJob()
    }

    private fun startRecommendedBedtimeTimer() {
        state = state.copy(
            timerState = TimerReducer.reduce(
                state.timerState,
                TimerIntent.SelectPreset(recommendedBedtimeTimerMinutes),
            ),
        )
        startTimer()
    }

    private fun extendTimer(minutes: Int) {
        updateTimer(TimerIntent.Extend(minutes))
        if (state.timerState.isActive) {
            platformSleepTimerRuntime.schedule(state.timerState)
            startTimerJob()
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        platformSleepTimerRuntime.cancel()
        state = state.copy(timerState = TimerReducer.reduce(state.timerState, TimerIntent.Cancel))
        playbackEngine.setTimerFadeFactor(1f)
        saveSnapshot()
    }

    private fun startTimerJob() {
        timerJob?.cancel()
        if (!state.timerState.isActive) return
        timerJob = scope.launch {
            while (state.timerState.isActive) {
                delay(TimerTickMillis)
                tickTimer()
            }
        }
    }

    private fun tickTimer() {
        val wasActive = state.timerState.isActive
        state = state.copy(
            timerState = TimerReducer.reduce(
                state.timerState,
                TimerIntent.Tick(clock.nowEpochMillis()),
            ),
        )
        TimerPlaybackCoordinator.apply(state.timerState, playbackEngine)
        if (wasActive && !state.timerState.isActive) {
            platformSleepTimerRuntime.cancel()
            if (state.timerState.endBehavior == com.whitenoisepro.domain.model.EndBehavior.StopPlayback) {
                state = state.copy(
                    playbackStatus = PlaybackStatus.Idle,
                    isPlaying = false,
                )
            }
            saveSnapshot()
        }
    }

    private fun resumeRestoredTimer() {
        if (!state.timerState.isActive) return
        tickTimer()
        if (state.timerState.isActive) {
            platformSleepTimerRuntime.schedule(state.timerState)
            startTimerJob()
        }
    }

    private fun saveSnapshot() {
        val snapshot = AppSnapshot(
            savedMixes = state.mixState.savedMixes,
            favoriteMixIds = state.mixState.savedMixes.filter { it.isFavorite }.map { it.id }.toSet(),
            recentMixes = state.mixState.recentMixes,
            currentMix = state.mixState.currentMix,
            timerDefaults = state.timerState,
            settings = state.settings,
        )
        scope.launch {
            repository.save(snapshot)
        }
    }

    private fun String.toSoundCategoryOrNull(): SoundCategory? =
        SoundCategory.entries.firstOrNull { it.displayName == this }

    private val AppState.hasActivePlayRequest: Boolean
        get() = playbackStatus == PlaybackStatus.Playing ||
            playbackStatus == PlaybackStatus.Buffering

    private companion object {
        const val TimerTickMillis = 1_000L
    }
}

const val recommendedBedtimeTimerMinutes: Int = 30
