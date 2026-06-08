package com.whitenoisepro.playback

import com.whitenoisepro.domain.model.EndBehavior
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.reducer.TimerReducer

object TimerPlaybackCoordinator {
    fun apply(timer: SleepTimerState, engine: PlaybackEngine) {
        if (timer.remainingMillis == 0L && timer.durationMillis > 0L) {
            engine.setTimerFadeFactor(0f)
            if (timer.endBehavior == EndBehavior.StopPlayback) {
                engine.stop()
            }
            return
        }

        engine.setTimerFadeFactor(TimerReducer.fadeFactor(timer))
    }
}
