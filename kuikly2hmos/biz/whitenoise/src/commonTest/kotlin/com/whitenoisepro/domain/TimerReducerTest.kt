package com.whitenoisepro.domain

import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.domain.reducer.TimerIntent
import com.whitenoisepro.domain.reducer.TimerReducer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimerReducerTest {
    @Test
    fun presetAndCustomDurationPrepareTimer() {
        val preset = TimerReducer.reduce(SleepTimerState(), TimerIntent.SelectPreset(minutes = 30))
        assertEquals(30 * 60 * 1000L, preset.durationMillis)
        assertEquals(preset.durationMillis, preset.remainingMillis)

        val custom = TimerReducer.reduce(preset, TimerIntent.SetCustomDuration(minutes = 95))
        assertEquals(95 * 60 * 1000L, custom.durationMillis)
        assertEquals(custom.durationMillis, custom.remainingMillis)
    }

    @Test
    fun startTickAndCompletionAreDeterministic() {
        val prepared = TimerReducer.reduce(SleepTimerState(), TimerIntent.SelectPreset(minutes = 10))
        val started = TimerReducer.reduce(prepared, TimerIntent.Start(nowEpochMillis = 1_000L))

        assertTrue(started.isActive)
        assertEquals(10 * 60 * 1000L, started.remainingMillis)

        val ticked = TimerReducer.reduce(started, TimerIntent.Tick(nowEpochMillis = 121_000L))
        assertEquals(8 * 60 * 1000L, ticked.remainingMillis)

        val completed = TimerReducer.reduce(started, TimerIntent.Tick(nowEpochMillis = 1_000L + 10 * 60 * 1000L))
        assertFalse(completed.isActive)
        assertEquals(0L, completed.remainingMillis)
        assertNull(completed.startedAtEpochMillis)
    }

    @Test
    fun cancelAndExtendUpdateActiveTimer() {
        val started = TimerReducer.reduce(
            TimerReducer.reduce(SleepTimerState(), TimerIntent.SelectPreset(minutes = 5)),
            TimerIntent.Start(nowEpochMillis = 0L),
        )

        val extended = TimerReducer.reduce(started, TimerIntent.Extend(minutes = 10))
        assertEquals(15 * 60 * 1000L, extended.remainingMillis)
        assertEquals(15 * 60 * 1000L, extended.durationMillis)

        val cancelled = TimerReducer.reduce(extended, TimerIntent.Cancel)
        assertFalse(cancelled.isActive)
        assertEquals(0L, cancelled.remainingMillis)
        assertNull(cancelled.startedAtEpochMillis)
    }

    @Test
    fun extendingActiveTimerKeepsFullExtensionAfterNextTick() {
        val active = SleepTimerState(
            durationMillis = 30 * 60 * 1000L,
            remainingMillis = 20 * 60 * 1000L,
            startedAtEpochMillis = 0L,
        )

        val extended = TimerReducer.reduce(active, TimerIntent.Extend(10))
        val nextTick = TimerReducer.reduce(extended, TimerIntent.Tick(11 * 60 * 1000L))

        assertEquals(40 * 60 * 1000L, extended.durationMillis)
        assertEquals(29 * 60 * 1000L, nextTick.remainingMillis)
    }

    @Test
    fun fadeFactorUsesRemainingTimeWithoutChangingMixVolume() {
        val state = SleepTimerState(
            durationMillis = 30 * 60 * 1000L,
            remainingMillis = 2 * 60 * 1000L,
            fadeOutMillis = 5 * 60 * 1000L,
            startedAtEpochMillis = 0L,
        )
        val mix = SoundMix(id = "mix", title = "雨夜", masterVolume = 0.8f)

        assertEquals(0.4f, TimerReducer.fadeFactor(state))
        assertEquals(0.8f, mix.masterVolume)
    }
}
