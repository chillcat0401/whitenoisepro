package com.whitenoisepro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.whitenoisepro.app.AppScaffold
import com.whitenoisepro.app.AppTab
import com.whitenoisepro.app.HomeScreen
import com.whitenoisepro.app.LibraryScreen
import com.whitenoisepro.app.MixerScreen
import com.whitenoisepro.app.SavedMixesScreen
import com.whitenoisepro.app.SettingsScreen
import com.whitenoisepro.app.TimerScreen
import com.whitenoisepro.app.miniPlayerSubtitle
import com.whitenoisepro.data.AppRepository
import com.whitenoisepro.data.FakeAppStorage
import com.whitenoisepro.data.LocalAppRepository
import com.whitenoisepro.design.WhiteNoiseTheme
import com.whitenoisepro.presentation.AppIntent
import com.whitenoisepro.presentation.AppStore
import com.whitenoisepro.playback.FakePlaybackEngine
import com.whitenoisepro.playback.NoOpPlatformSleepTimerRuntime
import com.whitenoisepro.playback.PlatformSleepTimerRuntime
import com.whitenoisepro.playback.PlaybackEngine

@Composable
fun WhiteNoiseProApp(
    playbackEngine: PlaybackEngine? = null,
    repository: AppRepository? = null,
    platformSleepTimerRuntime: PlatformSleepTimerRuntime = NoOpPlatformSleepTimerRuntime,
) {
    WhiteNoiseTheme {
        val fallbackEngine = remember { FakePlaybackEngine() }
        val engine = playbackEngine ?: fallbackEngine
        val fallbackRepository = remember { LocalAppRepository(FakeAppStorage()) }
        val appRepository = repository ?: fallbackRepository
        val appScope = rememberCoroutineScope()
        val store = remember(appRepository, engine, appScope, platformSleepTimerRuntime) {
            AppStore(
                repository = appRepository,
                playbackEngine = engine,
                scope = appScope,
                platformSleepTimerRuntime = platformSleepTimerRuntime,
            )
        }
        val state = store.state

        if (!store.isInitialized) {
            return@WhiteNoiseTheme
        }

        AppScaffold(
            selectedTab = state.selectedTab,
            onTabSelected = { store.dispatch(AppIntent.SelectTab(it)) },
            miniPlayerTitle = state.mixState.currentMix.title,
            miniPlayerSubtitle = miniPlayerSubtitle(state),
            isPlaying = state.isPlaying,
            onPlayPause = { store.dispatch(AppIntent.TogglePlayback) },
        ) {
            when (state.selectedTab) {
                AppTab.Home -> HomeScreen(
                    state = state,
                    padding = it,
                    onNavigate = { tab -> store.dispatch(AppIntent.SelectTab(tab)) },
                    onTogglePlay = { store.dispatch(AppIntent.TogglePlayback) },
                    onToggleFavorite = { store.dispatch(AppIntent.ToggleFavoriteCurrent) },
                    onMasterVolumeChange = { volume -> store.dispatch(AppIntent.SetMasterVolume(volume)) },
                )
                AppTab.Mixer -> MixerScreen(
                    state = state,
                    padding = it,
                    onAddSound = { store.dispatch(AppIntent.SelectTab(AppTab.Library)) },
                    onSaveMix = { store.dispatch(AppIntent.SaveCurrentMix) },
                    onMasterVolumeChange = { volume -> store.dispatch(AppIntent.SetMasterVolume(volume)) },
                    onLayerVolumeChange = { layerId, volume -> store.dispatch(AppIntent.SetLayerVolume(layerId, volume)) },
                    onLayerMutedChange = { layerId, muted -> store.dispatch(AppIntent.SetLayerMuted(layerId, muted)) },
                    onRemoveLayer = { layerId -> store.dispatch(AppIntent.RemoveLayer(layerId)) },
                )
                AppTab.Library -> LibraryScreen(
                    state = state,
                    sounds = store.visibleSounds(),
                    padding = it,
                    onQueryChange = { query -> store.dispatch(AppIntent.SetLibraryQuery(query)) },
                    onCategorySelected = { category -> store.dispatch(AppIntent.SelectCategory(category)) },
                    onSoundSelected = { soundId -> store.dispatch(AppIntent.AddSound(soundId)) },
                )
                AppTab.Timer -> TimerScreen(
                    state = state,
                    padding = it,
                    onPresetSelected = { minutes -> store.dispatch(AppIntent.SelectTimerPreset(minutes)) },
                    onStart = { store.dispatch(AppIntent.StartTimer) },
                    onCancel = { store.dispatch(AppIntent.CancelTimer) },
                    onExtend = { minutes -> store.dispatch(AppIntent.ExtendTimer(minutes)) },
                    onCustomDuration = { minutes -> store.dispatch(AppIntent.SetCustomTimerDuration(minutes)) },
                )
                AppTab.Saved -> SavedMixesScreen(
                    state = state,
                    padding = it,
                    onPlayMix = { mixId -> store.dispatch(AppIntent.PlaySavedMix(mixId)) },
                    onDeleteMix = { mixId -> store.dispatch(AppIntent.DeleteSavedMix(mixId)) },
                    onRenameMix = { mixId, title -> store.dispatch(AppIntent.RenameSavedMix(mixId, title)) },
                    onToggleFavorite = { mixId -> store.dispatch(AppIntent.ToggleFavoriteSavedMix(mixId)) },
                    onCreateMix = { store.dispatch(AppIntent.SelectTab(AppTab.Mixer)) },
                )
                AppTab.Settings -> SettingsScreen(
                    state = state,
                    padding = it,
                    onStartLastMixChange = { enabled -> store.dispatch(AppIntent.SetStartLastMix(enabled)) },
                )
            }
        }
    }
}
