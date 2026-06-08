package com.whitenoisepro.audio

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C

internal enum class ControllerPlaybackDecision {
    StartLayers,
    AwaitAuthorization,
    PauseLayers,
}

internal object MediaSessionAudioPolicy {
    const val controllerPlayerVolume: Float = 0f
    const val controllerHandlesAudioFocus: Boolean = true
    const val controllerHandlesAudioBecomingNoisy: Boolean = true
    const val layerPlayersHandleAudioFocus: Boolean = false

    val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    fun shouldResumeLayer(layerId: String, mutedLayerIds: Set<String>): Boolean =
        layerId !in mutedLayerIds

    fun shouldStartUnmutedLayer(engineIsPlaying: Boolean): Boolean =
        engineIsPlaying

    fun controllerDecision(
        isPlaying: Boolean,
        playWhenReady: Boolean,
    ): ControllerPlaybackDecision =
        when {
            isPlaying -> ControllerPlaybackDecision.StartLayers
            playWhenReady -> ControllerPlaybackDecision.AwaitAuthorization
            else -> ControllerPlaybackDecision.PauseLayers
        }
}
