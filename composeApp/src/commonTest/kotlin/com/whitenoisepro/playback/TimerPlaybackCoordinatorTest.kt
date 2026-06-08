package com.whitenoisepro.playback

import com.whitenoisepro.domain.model.EndBehavior
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.SoundMix
import kotlin.test.Test
import kotlin.test.assertEquals

class TimerPlaybackCoordinatorTest {
    @Test
    fun activeTimerAppliesFadeFactorWithoutChangingMixVolume() {
        val engine = FakePlaybackEngine()
        val mix = SoundMix(id = "mix", title = "雨夜", masterVolume = 0.8f)
        engine.play(mix)

        TimerPlaybackCoordinator.apply(
            timer = SleepTimerState(
                durationMillis = 30 * 60 * 1000L,
                remainingMillis = 2 * 60 * 1000L,
                fadeOutMillis = 5 * 60 * 1000L,
                startedAtEpochMillis = 0L,
            ),
            engine = engine,
        )

        assertEquals(0.4f, engine.state.timerFadeFactor)
        assertEquals(0.8f, mix.masterVolume)
    }

    @Test
    fun completedStopTimerStopsPlayback() {
        val engine = FakePlaybackEngine()
        engine.play(SoundMix(id = "mix", title = "雨夜"))

        TimerPlaybackCoordinator.apply(
            timer = SleepTimerState(
                durationMillis = 10L,
                remainingMillis = 0L,
                endBehavior = EndBehavior.StopPlayback,
                startedAtEpochMillis = null,
            ),
            engine = engine,
        )

        assertEquals(com.whitenoisepro.domain.model.PlaybackStatus.Idle, engine.state.status)
    }
}
