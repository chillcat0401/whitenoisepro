package com.whitenoisepro.app

import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.presentation.AppState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppShellTest {
    @Test
    fun bottomNavigationKeepsPrimaryFlowCompact() {
        assertEquals(
            listOf(AppTab.Home, AppTab.Mixer, AppTab.Library, AppTab.Timer, AppTab.Saved),
            AppTab.bottomNavTabs,
        )
        assertFalse(AppTab.Settings in AppTab.bottomNavTabs)
        assertTrue(AppTab.Settings in AppTab.entries)
    }

    @Test
    fun miniPlayerSubtitlePrefersActiveTimerRemainingTime() {
        val active = AppState(
            timerState = SleepTimerState(
                durationMillis = 30 * 60 * 1000L,
                remainingMillis = 29 * 60 * 1000L + 1L,
                startedAtEpochMillis = 1L,
            ),
        )

        assertEquals("剩余 30 分钟", miniPlayerSubtitle(active))
        assertEquals("4 个声音层", miniPlayerSubtitle(AppState()))
    }

    @Test
    fun timerPresetsIncludeTwoHours() {
        assertEquals(listOf(15, 30, 45, 60, 120), timerPresetMinutes)
    }
}
