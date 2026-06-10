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

    @Test
    fun notificationResumeAfterSettledPauseStartsLayers() {
        val gate = AudioFocusPlaybackGate()
        gate.requestPlay()
        assertEquals(AudioFocusGateAction.StartLayers, gate.apply(ControllerPlaybackDecision.StartLayers))

        // 通知栏暂停:controller 稳定暂停落地
        assertEquals(AudioFocusGateAction.PauseLayers, gate.apply(ControllerPlaybackDecision.PauseLayers))
        assertFalse(gate.playRequested)

        // 通知栏播放:稳定暂停后的播放决策 = 用户新意图,重新授权
        assertEquals(AudioFocusGateAction.StartLayers, gate.apply(ControllerPlaybackDecision.StartLayers))
        assertTrue(gate.playRequested)
    }

    @Test
    fun uiPauseAfterSettledPauseStillRejectsStaleStart() {
        val gate = AudioFocusPlaybackGate()
        gate.requestPlay()
        gate.apply(ControllerPlaybackDecision.PauseLayers) // 稳定暂停
        gate.requestPlay() // UI 再次播放
        gate.cancelPlay()  // UI 立即暂停:清除稳定暂停状态

        assertEquals(
            AudioFocusGateAction.PauseLayers,
            gate.apply(ControllerPlaybackDecision.StartLayers),
        )
    }
}
