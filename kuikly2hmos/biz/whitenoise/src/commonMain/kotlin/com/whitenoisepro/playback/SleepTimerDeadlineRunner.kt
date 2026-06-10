package com.whitenoisepro.playback

import com.whitenoisepro.domain.model.EndBehavior
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.reducer.TimerIntent
import com.whitenoisepro.domain.reducer.TimerReducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SleepTimerDeadlineRunner(
    private val scope: CoroutineScope,
    private val nowEpochMillis: () -> Long,
    private val onFadeFactor: (Float) -> Unit,
    private val onStopPlayback: () -> Unit,
    private val onCompleted: () -> Unit = {},
    private val tickMillis: Long = DefaultTickMillis,
) {
    private var job: Job? = null

    fun schedule(timer: SleepTimerState) {
        job?.cancel()
        job = null
        if (!timer.isActive) {
            onFadeFactor(1f)
            return
        }

        job = scope.launch {
            var current = timer
            while (current.isActive) {
                current = TimerReducer.reduce(
                    current,
                    TimerIntent.Tick(nowEpochMillis()),
                )
                onFadeFactor(
                    if (current.isActive) TimerReducer.fadeFactor(current) else 0f,
                )
                if (!current.isActive) {
                    onCompleted()
                    if (current.endBehavior == EndBehavior.StopPlayback) {
                        onStopPlayback()
                    }
                    return@launch
                }
                delay(tickMillis)
            }
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
        onFadeFactor(1f)
    }

    private companion object {
        const val DefaultTickMillis = 1_000L
    }
}
