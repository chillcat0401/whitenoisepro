package com.whitenoisepro.domain.reducer

import com.whitenoisepro.domain.model.SleepTimerState

sealed interface TimerIntent {
    data class SelectPreset(val minutes: Int) : TimerIntent
    data class SetCustomDuration(val minutes: Int) : TimerIntent
    data class Start(val nowEpochMillis: Long) : TimerIntent
    data class Tick(val nowEpochMillis: Long) : TimerIntent
    data class Extend(val minutes: Int) : TimerIntent
    data object Cancel : TimerIntent
}

object TimerReducer {
    fun reduce(state: SleepTimerState, intent: TimerIntent): SleepTimerState = when (intent) {
        is TimerIntent.SelectPreset -> state.prepared(minutesToMillis(intent.minutes))
        is TimerIntent.SetCustomDuration -> state.prepared(minutesToMillis(intent.minutes))
        is TimerIntent.Start -> {
            val duration = state.durationMillis.takeIf { it > 0L } ?: state.remainingMillis
            state.copy(
                durationMillis = duration,
                remainingMillis = duration,
                startedAtEpochMillis = intent.nowEpochMillis,
            )
        }
        is TimerIntent.Tick -> tick(state, intent.nowEpochMillis)
        is TimerIntent.Extend -> {
            val extension = minutesToMillis(intent.minutes)
            state.copy(
                durationMillis = if (state.isActive) {
                    state.durationMillis + extension
                } else {
                    state.remainingMillis + extension
                },
                remainingMillis = state.remainingMillis + extension,
            )
        }
        TimerIntent.Cancel -> state.copy(
            remainingMillis = 0L,
            startedAtEpochMillis = null,
        )
    }

    fun fadeFactor(state: SleepTimerState): Float {
        if (!state.isActive || state.fadeOutMillis == 0L) return 1f
        if (state.remainingMillis >= state.fadeOutMillis) return 1f
        return (state.remainingMillis.toFloat() / state.fadeOutMillis.toFloat()).coerceIn(0f, 1f)
    }

    private fun tick(state: SleepTimerState, nowEpochMillis: Long): SleepTimerState {
        val startedAt = state.startedAtEpochMillis ?: return state
        val elapsed = (nowEpochMillis - startedAt).coerceAtLeast(0L)
        val remaining = (state.durationMillis - elapsed).coerceAtLeast(0L)
        return if (remaining == 0L) {
            state.copy(remainingMillis = 0L, startedAtEpochMillis = null)
        } else {
            state.copy(remainingMillis = remaining)
        }
    }

    private fun SleepTimerState.prepared(durationMillis: Long): SleepTimerState =
        copy(
            durationMillis = durationMillis,
            remainingMillis = durationMillis,
            startedAtEpochMillis = null,
        )

    private fun minutesToMillis(minutes: Int): Long =
        minutes.coerceAtLeast(0) * 60L * 1000L
}
