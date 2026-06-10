package com.whitenoisepro.playback

import com.whitenoisepro.domain.model.SleepTimerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SleepTimerDeadlineRunnerTest {
    @Test
    fun activeTimerFadesAndStopsOnceAtDeadline() = runTest {
        var now = 0L
        val fadeFactors = mutableListOf<Float>()
        var stopCalls = 0
        var completionCalls = 0
        val runner = SleepTimerDeadlineRunner(
            scope = this,
            nowEpochMillis = { now },
            onFadeFactor = { fadeFactors += it },
            onStopPlayback = { stopCalls += 1 },
            onCompleted = { completionCalls += 1 },
        )

        runner.schedule(
            SleepTimerState(
                durationMillis = 60_000L,
                remainingMillis = 60_000L,
                fadeOutMillis = 10_000L,
                startedAtEpochMillis = 0L,
            ),
        )
        runCurrent()
        assertEquals(1f, fadeFactors.last())

        now = 55_000L
        advanceTimeBy(1_000L)
        runCurrent()
        assertEquals(0.5f, fadeFactors.last())

        now = 60_000L
        advanceTimeBy(1_000L)
        runCurrent()
        assertEquals(0f, fadeFactors.last())
        assertEquals(1, stopCalls)
        assertEquals(1, completionCalls)

        advanceTimeBy(5_000L)
        runCurrent()
        assertEquals(1, stopCalls)
        assertEquals(1, completionCalls)
    }

    @Test
    fun rescheduleReplacesOldDeadlineAndCancelRestoresFullFade() = runTest {
        var now = 0L
        val fadeFactors = mutableListOf<Float>()
        var stopCalls = 0
        val runner = SleepTimerDeadlineRunner(
            scope = this,
            nowEpochMillis = { now },
            onFadeFactor = { fadeFactors += it },
            onStopPlayback = { stopCalls += 1 },
        )
        runner.schedule(
            SleepTimerState(
                durationMillis = 10_000L,
                remainingMillis = 10_000L,
                fadeOutMillis = 5_000L,
                startedAtEpochMillis = 0L,
            ),
        )
        runner.schedule(
            SleepTimerState(
                durationMillis = 20_000L,
                remainingMillis = 20_000L,
                fadeOutMillis = 5_000L,
                startedAtEpochMillis = 0L,
            ),
        )

        now = 10_000L
        advanceTimeBy(1_000L)
        runCurrent()
        assertEquals(0, stopCalls)
        assertTrue(fadeFactors.last() > 0f)

        runner.cancel()
        runCurrent()
        assertEquals(1f, fadeFactors.last())

        now = 25_000L
        advanceTimeBy(20_000L)
        runCurrent()
        assertEquals(0, stopCalls)
    }
}
