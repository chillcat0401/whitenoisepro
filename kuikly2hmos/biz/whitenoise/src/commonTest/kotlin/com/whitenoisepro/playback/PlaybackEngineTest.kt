package com.whitenoisepro.playback

import com.whitenoisepro.domain.model.PlaybackStatus
import com.whitenoisepro.domain.model.SoundMix
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaybackEngineTest {
    @Test
    fun fakeEngineTracksPlaybackStateAndVolumes() {
        val engine = FakePlaybackEngine()
        val mix = SoundMix(id = "mix", title = "雨夜", masterVolume = 0.8f)

        engine.play(mix)
        engine.setMasterVolume(2f)
        engine.setTimerFadeFactor(0.4f)
        engine.setLayerVolume("layer", -1f)
        engine.setLayerMuted("layer", true)

        assertEquals(PlaybackStatus.Playing, engine.state.status)
        assertEquals(PlaybackStatus.Playing, engine.states.value.status)
        assertEquals("mix", engine.state.currentMixId)
        assertEquals(1f, engine.state.masterVolume)
        assertEquals(0.4f, engine.state.timerFadeFactor)
        assertEquals(0f, engine.layerVolumes["layer"])

        engine.pause()
        assertEquals(PlaybackStatus.Paused, engine.states.value.status)

        engine.stop()
        assertEquals(PlaybackStatus.Idle, engine.states.value.status)
    }
}
