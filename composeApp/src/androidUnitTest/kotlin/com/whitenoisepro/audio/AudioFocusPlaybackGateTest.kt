package com.whitenoisepro.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AudioFocusPlaybackGateTest {
    @Test
    fun playRequestWaitsUntilControllerActuallyPlays() {
        val gate = AudioFocusPlaybackGate()

        gate.requestPlay()

        assertTrue(gate.playRequested)
        assertEquals(
            AudioFocusGateAction.AwaitAuthorization,
            gate.apply(ControllerPlaybackDecision.AwaitAuthorization),
        )
        assertEquals(
            AudioFocusGateAction.StartLayers,
            gate.apply(ControllerPlaybackDecision.StartLayers),
        )
    }

    @Test
    fun explicitControllerPauseClearsPendingPlay() {
        val gate = AudioFocusPlaybackGate()
        gate.requestPlay()

        assertEquals(
            AudioFocusGateAction.PauseLayers,
            gate.apply(ControllerPlaybackDecision.PauseLayers),
        )
        assertFalse(gate.playRequested)
    }

    @Test
    fun cancelledPlayRejectsDelayedStartCallback() {
        val gate = AudioFocusPlaybackGate()
        gate.requestPlay()
        gate.cancelPlay()

        assertEquals(
            AudioFocusGateAction.PauseLayers,
            gate.apply(ControllerPlaybackDecision.StartLayers),
        )
    }
}
