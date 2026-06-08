package com.whitenoisepro.domain.reducer

import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix

data class MixState(
    val currentMix: SoundMix,
    val savedMixes: List<SoundMix> = emptyList(),
    val recentMixes: List<SoundMix> = emptyList(),
)

sealed interface MixIntent {
    data class AddSound(
        val layerId: String,
        val soundId: String,
        val volume: Float = SoundLayer.DefaultVolume,
    ) : MixIntent

    data class RemoveLayer(val layerId: String) : MixIntent
    data class MuteLayer(val layerId: String) : MixIntent
    data class UnmuteLayer(val layerId: String) : MixIntent
    data class SetLayerVolume(val layerId: String, val volume: Float) : MixIntent
    data class SetMasterVolume(val volume: Float) : MixIntent
    data object ToggleFavoriteCurrent : MixIntent
    data class SaveCurrentMix(val savedMixId: String, val nowEpochMillis: Long) : MixIntent
    data class DeleteSavedMix(val mixId: String) : MixIntent
    data class PlaySavedMix(val mixId: String, val nowEpochMillis: Long) : MixIntent
    data class RenameSavedMix(val mixId: String, val title: String) : MixIntent
    data class ToggleFavoriteSavedMix(val mixId: String) : MixIntent
}

object MixReducer {
    fun reduce(state: MixState, intent: MixIntent): MixState = when (intent) {
        is MixIntent.AddSound -> state.updateCurrent {
            val existingLayerIds = layers.map { it.id }.toSet()
            if (intent.layerId in existingLayerIds) {
                this
            } else {
                copy(
                    layers = layers + SoundLayer.create(
                        id = intent.layerId,
                        soundId = intent.soundId,
                        volume = intent.volume,
                    ),
                )
            }
        }

        is MixIntent.RemoveLayer -> state.updateCurrent {
            copy(layers = layers.filterNot { it.id == intent.layerId })
        }

        is MixIntent.MuteLayer -> state.updateLayer(intent.layerId) {
            copy(isMuted = true)
        }

        is MixIntent.UnmuteLayer -> state.updateLayer(intent.layerId) {
            copy(isMuted = false)
        }

        is MixIntent.SetLayerVolume -> state.updateLayer(intent.layerId) {
            copy(volume = intent.volume.coerceIn(0f, 1f))
        }

        is MixIntent.SetMasterVolume -> state.updateCurrent {
            copy(masterVolume = intent.volume.coerceIn(0f, 1f))
        }

        MixIntent.ToggleFavoriteCurrent -> state.updateCurrent {
            copy(isFavorite = !isFavorite)
        }

        is MixIntent.SaveCurrentMix -> {
            val saved = state.currentMix.copy(
                id = intent.savedMixId,
                updatedAtEpochMillis = intent.nowEpochMillis,
            )
            state.copy(savedMixes = state.savedMixes.upsert(saved))
        }

        is MixIntent.DeleteSavedMix -> state.copy(
            savedMixes = state.savedMixes.filterNot { it.id == intent.mixId },
        )

        is MixIntent.PlaySavedMix -> {
            val saved = state.savedMixes.firstOrNull { it.id == intent.mixId } ?: state.currentMix
            val recent = saved.copy(updatedAtEpochMillis = intent.nowEpochMillis)
            state.copy(
                currentMix = recent,
                recentMixes = listOf(recent) + state.recentMixes.filterNot { it.id == recent.id },
            )
        }

        is MixIntent.RenameSavedMix -> {
            val title = intent.title.trim()
            if (title.isEmpty()) state else state.updateMatchingMixes(intent.mixId) { copy(title = title) }
        }

        is MixIntent.ToggleFavoriteSavedMix -> {
            val favorite = state.savedMixes.firstOrNull { it.id == intent.mixId }?.isFavorite?.not()
            if (favorite == null) state else state.updateMatchingMixes(intent.mixId) {
                copy(isFavorite = favorite)
            }
        }
    }

    private fun MixState.updateCurrent(transform: SoundMix.() -> SoundMix): MixState =
        copy(currentMix = currentMix.transform())

    private fun MixState.updateLayer(layerId: String, transform: SoundLayer.() -> SoundLayer): MixState =
        updateCurrent {
            copy(layers = layers.map { layer ->
                if (layer.id == layerId) layer.transform() else layer
            })
        }

    private fun List<SoundMix>.upsert(mix: SoundMix): List<SoundMix> =
        listOf(mix) + filterNot { it.id == mix.id }

    private fun MixState.updateMatchingMixes(
        mixId: String,
        transform: SoundMix.() -> SoundMix,
    ): MixState {
        if (savedMixes.none { it.id == mixId }) return this
        return copy(
            currentMix = if (currentMix.id == mixId) currentMix.transform() else currentMix,
            savedMixes = savedMixes.map { if (it.id == mixId) it.transform() else it },
            recentMixes = recentMixes.map { if (it.id == mixId) it.transform() else it },
        )
    }
}
