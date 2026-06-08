package com.whitenoisepro.playback

import com.whitenoisepro.domain.model.PlaybackState
import com.whitenoisepro.domain.model.PlaybackStatus
import com.whitenoisepro.domain.model.SoundMix
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PlaybackEngine {
    val state: PlaybackState
    val states: StateFlow<PlaybackState>

    fun play(mix: SoundMix)
    fun pause()
    fun stop()
    fun setLayerVolume(layerId: String, volume: Float)
    fun setLayerMuted(layerId: String, muted: Boolean)
    fun setMasterVolume(volume: Float)
    fun setTimerFadeFactor(factor: Float)
}

class FakePlaybackEngine : PlaybackEngine {
    private val mutableStates = MutableStateFlow(PlaybackState())
    override val states: StateFlow<PlaybackState> = mutableStates.asStateFlow()
    override val state: PlaybackState
        get() = states.value

    val layerVolumes: MutableMap<String, Float> = mutableMapOf()
    val mutedLayers: MutableSet<String> = mutableSetOf()

    override fun play(mix: SoundMix) {
        mutableStates.value = PlaybackState(
            status = PlaybackStatus.Playing,
            currentMixId = mix.id,
            masterVolume = mix.masterVolume,
        )
    }

    override fun pause() {
        mutableStates.value = state.copy(status = PlaybackStatus.Paused)
    }

    override fun stop() {
        mutableStates.value = PlaybackState()
    }

    override fun setLayerVolume(layerId: String, volume: Float) {
        layerVolumes[layerId] = volume.coerceIn(0f, 1f)
    }

    override fun setLayerMuted(layerId: String, muted: Boolean) {
        if (muted) mutedLayers += layerId else mutedLayers -= layerId
    }

    override fun setMasterVolume(volume: Float) {
        mutableStates.value = state.copy(masterVolume = volume.coerceIn(0f, 1f))
    }

    override fun setTimerFadeFactor(factor: Float) {
        mutableStates.value = state.copy(timerFadeFactor = factor.coerceIn(0f, 1f))
    }
}
