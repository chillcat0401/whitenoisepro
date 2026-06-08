package com.whitenoisepro.audio

import androidx.media3.common.C
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MediaSessionAudioPolicyTest {
    @Test
    fun controllerPlayerIsSilentBecauseLayerPlayersOwnAudibleOutput() {
        assertEquals(0f, MediaSessionAudioPolicy.controllerPlayerVolume)
    }

    @Test
    fun controllerOwnsAudioFocusAndNoisyHandlingForAllLayerPlayers() {
        assertTrue(MediaSessionAudioPolicy.controllerHandlesAudioFocus)
        assertTrue(MediaSessionAudioPolicy.controllerHandlesAudioBecomingNoisy)
        assertFalse(MediaSessionAudioPolicy.layerPlayersHandleAudioFocus)
        assertEquals(C.USAGE_MEDIA, MediaSessionAudioPolicy.audioAttributes.usage)
        assertEquals(C.AUDIO_CONTENT_TYPE_MUSIC, MediaSessionAudioPolicy.audioAttributes.contentType)
    }

    @Test
    fun mutedLayersStayPausedAcrossSystemResume() {
        assertFalse(MediaSessionAudioPolicy.shouldResumeLayer("muted", setOf("muted")))
        assertTrue(MediaSessionAudioPolicy.shouldResumeLayer("audible", setOf("muted")))
    }

    @Test
    fun unmutingOnlyStartsLayerWhileEngineIsPlaying() {
        assertFalse(MediaSessionAudioPolicy.shouldStartUnmutedLayer(engineIsPlaying = false))
        assertTrue(MediaSessionAudioPolicy.shouldStartUnmutedLayer(engineIsPlaying = true))
    }

    @Test
    fun controllerActualPlaybackAuthorizesAudibleLayers() {
        assertEquals(
            ControllerPlaybackDecision.StartLayers,
            MediaSessionAudioPolicy.controllerDecision(isPlaying = true, playWhenReady = true),
        )
    }

    @Test
    fun controllerPlayIntentWithoutActualPlaybackWaitsForAuthorization() {
        assertEquals(
            ControllerPlaybackDecision.AwaitAuthorization,
            MediaSessionAudioPolicy.controllerDecision(isPlaying = false, playWhenReady = true),
        )
    }

    @Test
    fun controllerWithoutPlayIntentPausesAudibleLayers() {
        assertEquals(
            ControllerPlaybackDecision.PauseLayers,
            MediaSessionAudioPolicy.controllerDecision(isPlaying = false, playWhenReady = false),
        )
    }
}
