package com.whitenoisepro.playback

import com.whitenoisepro.domain.model.SleepTimerState

interface PlatformSleepTimerRuntime {
    fun schedule(timer: SleepTimerState)
    fun cancel()
}

object NoOpPlatformSleepTimerRuntime : PlatformSleepTimerRuntime {
    override fun schedule(timer: SleepTimerState) = Unit

    override fun cancel() = Unit
}
