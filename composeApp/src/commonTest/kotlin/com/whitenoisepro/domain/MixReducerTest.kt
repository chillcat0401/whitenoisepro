package com.whitenoisepro.domain

import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.domain.reducer.MixIntent
import com.whitenoisepro.domain.reducer.MixReducer
import com.whitenoisepro.domain.reducer.MixState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MixReducerTest {
    private val baseMix = SoundMix(
        id = "current",
        title = "夜雨",
        layers = listOf(SoundLayer.create(id = "rain-layer", soundId = "rain", volume = 0.7f)),
        masterVolume = 0.75f,
    )

    @Test
    fun addAndRemoveSoundLayer() {
        val added = MixReducer.reduce(
            state = MixState(currentMix = baseMix),
            intent = MixIntent.AddSound(layerId = "ocean-layer", soundId = "ocean", volume = 0.4f),
        )
        assertEquals(listOf("rain-layer", "ocean-layer"), added.currentMix.layers.map { it.id })

        val removed = MixReducer.reduce(added, MixIntent.RemoveLayer("rain-layer"))
        assertEquals(listOf("ocean-layer"), removed.currentMix.layers.map { it.id })
    }

    @Test
    fun muteUnmuteAndLayerVolumeAreDeterministic() {
        val muted = MixReducer.reduce(MixState(currentMix = baseMix), MixIntent.MuteLayer("rain-layer"))
        assertTrue(muted.currentMix.layers.single().isMuted)

        val louder = MixReducer.reduce(muted, MixIntent.SetLayerVolume("rain-layer", 2f))
        assertEquals(1f, louder.currentMix.layers.single().volume)

        val unmuted = MixReducer.reduce(louder, MixIntent.UnmuteLayer("rain-layer"))
        assertFalse(unmuted.currentMix.layers.single().isMuted)
    }

    @Test
    fun masterVolumeIsClamped() {
        val quiet = MixReducer.reduce(MixState(currentMix = baseMix), MixIntent.SetMasterVolume(-1f))
        val loud = MixReducer.reduce(quiet, MixIntent.SetMasterVolume(4f))

        assertEquals(0f, quiet.currentMix.masterVolume)
        assertEquals(1f, loud.currentMix.masterVolume)
    }

    @Test
    fun favoriteAndSaveCurrentMix() {
        val favorited = MixReducer.reduce(MixState(currentMix = baseMix), MixIntent.ToggleFavoriteCurrent)
        assertTrue(favorited.currentMix.isFavorite)

        val saved = MixReducer.reduce(
            state = favorited,
            intent = MixIntent.SaveCurrentMix(savedMixId = "saved-night-rain", nowEpochMillis = 10L),
        )

        assertEquals(1, saved.savedMixes.size)
        assertEquals("saved-night-rain", saved.savedMixes.single().id)
        assertTrue(saved.savedMixes.single().isFavorite)
        assertEquals(10L, saved.savedMixes.single().updatedAtEpochMillis)
    }

    @Test
    fun saveCurrentMixUpdatesExistingEquivalentMixInsteadOfDuplicating() {
        val existing = baseMix.copy(
            id = "saved-night-rain",
            updatedAtEpochMillis = 10L,
        )
        val state = MixState(
            currentMix = baseMix.copy(id = "current-draft"),
            savedMixes = listOf(existing),
        )

        val saved = MixReducer.reduce(
            state = state,
            intent = MixIntent.SaveCurrentMix(savedMixId = "new-duplicate-id", nowEpochMillis = 99L),
        )

        assertEquals(1, saved.savedMixes.size)
        assertEquals("saved-night-rain", saved.savedMixes.single().id)
        assertEquals(99L, saved.savedMixes.single().updatedAtEpochMillis)
    }

    @Test
    fun saveCurrentMixCreatesDistinctEntryWhenLayersChange() {
        val existing = baseMix.copy(id = "saved-night-rain")
        val edited = baseMix.copy(
            id = "current-draft",
            layers = baseMix.layers + SoundLayer.create("fan-layer", "fan", 0.2f),
        )
        val state = MixState(currentMix = edited, savedMixes = listOf(existing))

        val saved = MixReducer.reduce(
            state = state,
            intent = MixIntent.SaveCurrentMix(savedMixId = "saved-edited", nowEpochMillis = 99L),
        )

        assertEquals(listOf("saved-edited", "saved-night-rain"), saved.savedMixes.map { it.id })
    }

    @Test
    fun deleteSavedMixAndTrackRecentMixes() {
        val saved = SoundMix(id = "saved-1", title = "海边", updatedAtEpochMillis = 1L)
        val state = MixState(currentMix = baseMix, savedMixes = listOf(saved))

        val recent = MixReducer.reduce(state, MixIntent.PlaySavedMix("saved-1", nowEpochMillis = 20L))
        assertEquals("saved-1", recent.currentMix.id)
        assertEquals(listOf("saved-1"), recent.recentMixes.map { it.id })
        assertEquals(20L, recent.recentMixes.single().updatedAtEpochMillis)

        val deleted = MixReducer.reduce(recent, MixIntent.DeleteSavedMix("saved-1"))
        assertTrue(deleted.savedMixes.isEmpty())
    }

    @Test
    fun renameAndFavoriteSavedMixKeepMatchingCurrentAndRecentConsistent() {
        val saved = SoundMix(id = "saved-1", title = "旧标题")
        val state = MixState(
            currentMix = saved,
            savedMixes = listOf(saved),
            recentMixes = listOf(saved),
        )

        val renamed = MixReducer.reduce(
            state,
            MixIntent.RenameSavedMix(mixId = "saved-1", title = "新标题"),
        )
        val favorited = MixReducer.reduce(
            renamed,
            MixIntent.ToggleFavoriteSavedMix("saved-1"),
        )

        assertEquals("新标题", favorited.currentMix.title)
        assertEquals("新标题", favorited.recentMixes.single().title)
        assertEquals("新标题", favorited.savedMixes.single().title)
        assertTrue(favorited.currentMix.isFavorite)
        assertTrue(favorited.recentMixes.single().isFavorite)
        assertTrue(favorited.savedMixes.single().isFavorite)
    }

    @Test
    fun replaceCurrentMixSwapsCurrentAndTracksRecent() {
        val preset = SoundMix(
            id = "preset-rain-train",
            title = "雨夜列车",
            layers = listOf(SoundLayer.create("p-1", "train_ride", 0.45f)),
            masterVolume = 0.66f,
        )
        val state = MixState(
            currentMix = baseMix,
            recentMixes = listOf(
                SoundMix(id = "preset-rain-train", title = "雨夜列车"),
                SoundMix(id = "other", title = "其他"),
            ),
        )

        val next = MixReducer.reduce(state, MixIntent.ReplaceCurrentMix(preset, nowEpochMillis = 99L))

        assertEquals("preset-rain-train", next.currentMix.id)
        assertEquals(99L, next.currentMix.updatedAtEpochMillis)
        assertEquals(listOf("preset-rain-train", "other"), next.recentMixes.map { it.id })
        assertEquals(99L, next.recentMixes.first().updatedAtEpochMillis)
    }

    @Test
    fun renamingSavedMixDoesNotOverwriteCurrentMixEdits() {
        val saved = SoundMix(id = "saved-1", title = "旧标题", masterVolume = 0.4f)
        val editedCurrent = saved.copy(masterVolume = 0.9f)
        val state = MixState(currentMix = editedCurrent, savedMixes = listOf(saved))

        val renamed = MixReducer.reduce(
            state,
            MixIntent.RenameSavedMix(mixId = "saved-1", title = "新标题"),
        )

        assertEquals("新标题", renamed.currentMix.title)
        assertEquals(0.9f, renamed.currentMix.masterVolume)
        assertEquals(0.4f, renamed.savedMixes.single().masterVolume)
    }
}
