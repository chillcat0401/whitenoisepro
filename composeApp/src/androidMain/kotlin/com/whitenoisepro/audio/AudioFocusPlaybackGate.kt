package com.whitenoisepro.audio

internal enum class AudioFocusGateAction {
    StartLayers,
    AwaitAuthorization,
    PauseLayers,
}

internal class AudioFocusPlaybackGate {
    var playRequested: Boolean = false
        private set

    fun requestPlay() {
        playRequested = true
    }

    fun cancelPlay() {
        playRequested = false
    }

    fun apply(decision: ControllerPlaybackDecision): AudioFocusGateAction {
        if (!playRequested) {
            return AudioFocusGateAction.PauseLayers
        }
        return when (decision) {
            ControllerPlaybackDecision.StartLayers -> AudioFocusGateAction.StartLayers
            ControllerPlaybackDecision.AwaitAuthorization -> AudioFocusGateAction.AwaitAuthorization
            ControllerPlaybackDecision.PauseLayers -> {
                playRequested = false
                AudioFocusGateAction.PauseLayers
            }
        }
    }
}
