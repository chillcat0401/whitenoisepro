package com.whitenoisepro.presentation

import com.whitenoisepro.app.AppTab
import com.whitenoisepro.data.SampleContent
import com.whitenoisepro.domain.model.PlaybackStatus
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.UserSettings
import com.whitenoisepro.domain.reducer.MixState

data class AppState(
    val selectedTab: AppTab = AppTab.Home,
    val mixState: MixState = MixState(
        currentMix = SampleContent.currentMix,
        savedMixes = SampleContent.savedMixes,
        recentMixes = SampleContent.savedMixes.take(2),
    ),
    val timerState: SleepTimerState = SleepTimerState(
        durationMillis = 30 * 60 * 1000L,
        remainingMillis = 30 * 60 * 1000L,
    ),
    val settings: UserSettings = UserSettings(),
    val playbackStatus: PlaybackStatus = PlaybackStatus.Idle,
    val isPlaying: Boolean = false,
    val libraryQuery: String = "",
    val selectedCategory: String = "全部",
)
