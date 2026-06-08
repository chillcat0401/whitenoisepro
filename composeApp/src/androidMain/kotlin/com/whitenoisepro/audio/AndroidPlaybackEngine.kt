package com.whitenoisepro.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.whitenoisepro.domain.model.PlaybackState
import com.whitenoisepro.domain.model.PlaybackStatus
import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.playback.PlaybackEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidPlaybackEngine(
    private val context: Context,
) : PlaybackEngine {
    private val players: MutableMap<String, ExoPlayer> = mutableMapOf()
    private val layerSoundIds: MutableMap<String, String> = mutableMapOf()
    private val layerVolumes: MutableMap<String, Float> = mutableMapOf()
    private val mutedLayers: MutableSet<String> = mutableSetOf()
    private val playbackGate = AudioFocusPlaybackGate()
    private var lastMix: SoundMix? = null

    private val mutableStates = MutableStateFlow(PlaybackState())
    override val states: StateFlow<PlaybackState> = mutableStates.asStateFlow()
    override val state: PlaybackState
        get() = states.value

    override fun play(mix: SoundMix) {
        WhiteNoiseMediaSessionService.ensureStarted(context)
        playbackGate.requestPlay()
        lastMix = mix
        mutableStates.value = state.copy(masterVolume = mix.masterVolume)
        releaseRemovedPlayers(activeLayerIds = mix.layers.map { it.id }.toSet())
        players.values.forEach { it.pause() }
        mix.layers.take(MaxActiveLayers).forEach { layer ->
            val player = players.getOrPut(layer.id) { createLoopingPlayer() }
            layerVolumes[layer.id] = layer.volume
            if (layer.isMuted) mutedLayers += layer.id else mutedLayers -= layer.id
            player.volume = effectiveVolume(layer)
            if (player.mediaItemCount == 0 || layerSoundIds[layer.id] != layer.soundId) {
                player.setMediaItem(
                    MediaItem.fromUri(AndroidSoundResourceResolver.uri(context, layer.soundId)),
                    true,
                )
                player.prepare()
                layerSoundIds[layer.id] = layer.soundId
            }
        }
        mutableStates.value = state.copy(
            status = PlaybackStatus.Buffering,
            currentMixId = mix.id,
            errorMessage = null,
        )
        WhiteNoiseMediaSessionService.setNowPlaying(title = mix.title, playing = true)
    }

    override fun pause() {
        playbackGate.cancelPlay()
        players.values.forEach { it.pause() }
        mutableStates.value = state.copy(status = PlaybackStatus.Paused)
        WhiteNoiseMediaSessionService.setNowPlaying(title = lastMix?.title ?: "", playing = false)
    }

    override fun stop() {
        playbackGate.cancelPlay()
        players.values.forEach { it.stop() }
        mutableStates.value = PlaybackState()
        WhiteNoiseMediaSessionService.clearNowPlaying()
    }

    override fun setLayerVolume(layerId: String, volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        layerVolumes[layerId] = clamped
        players[layerId]?.volume = clamped * state.masterVolume * state.timerFadeFactor
    }

    override fun setLayerMuted(layerId: String, muted: Boolean) {
        val player = players[layerId] ?: return
        if (muted) {
            mutedLayers += layerId
            player.pause()
        } else {
            mutedLayers -= layerId
            if (MediaSessionAudioPolicy.shouldStartUnmutedLayer(state.isPlaying)) {
                player.play()
            }
        }
    }

    override fun setMasterVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        mutableStates.value = state.copy(masterVolume = clamped)
        applyEffectiveVolumes()
    }

    override fun setTimerFadeFactor(factor: Float) {
        val clamped = factor.coerceIn(0f, 1f)
        mutableStates.value = state.copy(timerFadeFactor = clamped)
        applyEffectiveVolumes()
    }

    fun release() {
        playbackGate.cancelPlay()
        players.values.forEach { it.release() }
        players.clear()
        layerVolumes.clear()
        layerSoundIds.clear()
        mutedLayers.clear()
    }

    fun syncFromMediaSession(
        isPlaying: Boolean,
        playWhenReady: Boolean,
    ) {
        val decision = MediaSessionAudioPolicy.controllerDecision(
            isPlaying = isPlaying,
            playWhenReady = playWhenReady,
        )
        when (playbackGate.apply(decision)) {
            AudioFocusGateAction.StartLayers -> {
                val mix = lastMix ?: return
                mix.layers.take(MaxActiveLayers).forEach { layer ->
                    if (MediaSessionAudioPolicy.shouldResumeLayer(layer.id, mutedLayers)) {
                        players[layer.id]?.play()
                    }
                }
                mutableStates.value = state.copy(
                    status = PlaybackStatus.Playing,
                    currentMixId = mix.id,
                    errorMessage = null,
                )
            }
            AudioFocusGateAction.AwaitAuthorization -> {
                players.values.forEach { it.pause() }
                mutableStates.value = state.copy(status = PlaybackStatus.Buffering)
            }
            AudioFocusGateAction.PauseLayers -> {
                players.values.forEach { it.pause() }
                if (state.status != PlaybackStatus.Idle) {
                    mutableStates.value = state.copy(status = PlaybackStatus.Paused)
                }
            }
        }
    }

    private fun createLoopingPlayer(): ExoPlayer =
        ExoPlayer.Builder(context)
            .setAudioAttributes(
                MediaSessionAudioPolicy.audioAttributes,
                MediaSessionAudioPolicy.layerPlayersHandleAudioFocus,
            )
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }

    private fun effectiveVolume(layer: SoundLayer): Float =
        if (layer.id in mutedLayers) {
            0f
        } else {
            (layer.volume * state.masterVolume * state.timerFadeFactor).coerceIn(0f, 1f)
        }

    private fun applyEffectiveVolumes() {
        players.forEach { (layerId, player) ->
            val layerVolume = layerVolumes[layerId] ?: SoundLayer.DefaultVolume
            player.volume = if (layerId in mutedLayers) {
                0f
            } else {
                (layerVolume * state.masterVolume * state.timerFadeFactor).coerceIn(0f, 1f)
            }
        }
    }

    private fun releaseRemovedPlayers(activeLayerIds: Set<String>) {
        val removedIds = players.keys - activeLayerIds
        removedIds.forEach { layerId ->
            players.remove(layerId)?.release()
            layerVolumes.remove(layerId)
            layerSoundIds.remove(layerId)
            mutedLayers.remove(layerId)
        }
    }

    private companion object {
        const val MaxActiveLayers = 5
    }
}
