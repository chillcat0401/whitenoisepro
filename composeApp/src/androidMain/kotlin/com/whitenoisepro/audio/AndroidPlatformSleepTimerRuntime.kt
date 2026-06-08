package com.whitenoisepro.audio

import android.content.Context
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.playback.PlatformSleepTimerRuntime

class AndroidPlatformSleepTimerRuntime(
    context: Context,
) : PlatformSleepTimerRuntime {
    private val applicationContext = context.applicationContext

    override fun schedule(timer: SleepTimerState) {
        WhiteNoiseMediaSessionService.scheduleTimer(applicationContext, timer)
    }

    override fun cancel() {
        WhiteNoiseMediaSessionService.cancelTimer()
    }
}
