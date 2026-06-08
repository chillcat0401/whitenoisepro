package com.whitenoisepro.data

import com.whitenoisepro.domain.model.SoundCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SoundCatalogTest {
    @Test
    fun catalogContainsOnlyPublishedAudioAssets() {
        assertEquals(
            setOf("white_noise", "pink_noise", "brown_noise", "fan", "rain", "ocean", "forest", "fireplace"),
            SoundCatalog.all.map { it.id }.toSet(),
        )
        assertEquals(
            listOf(SoundCategory.Noise, SoundCategory.Home, SoundCategory.Nature),
            SoundCatalog.availableCategories,
        )
    }

    @Test
    fun filtersByCategory() {
        val noise = SoundCatalog.filter(category = SoundCategory.Noise, query = "")

        assertEquals(setOf("white_noise", "brown_noise", "pink_noise"), noise.map { it.id }.toSet())
    }

    @Test
    fun searchesChineseNameAndDescription() {
        val fan = SoundCatalog.filter(category = null, query = "机械")
        val masking = SoundCatalog.filter(category = null, query = "遮蔽")
        val rain = SoundCatalog.filter(category = null, query = "雨")
        val ocean = SoundCatalog.filter(category = null, query = "海")
        val forest = SoundCatalog.filter(category = null, query = "森林")
        val fireplace = SoundCatalog.filter(category = null, query = "炉")

        assertEquals(listOf("fan"), fan.map { it.id })
        assertEquals(listOf("white_noise", "fan"), masking.map { it.id })
        assertEquals(listOf("rain"), rain.map { it.id })
        assertEquals(listOf("ocean"), ocean.map { it.id })
        assertEquals(listOf("forest"), forest.map { it.id })
        assertEquals(listOf("fireplace"), fireplace.map { it.id })
    }

    @Test
    fun sampleMixesReferenceOnlyPublishedSounds() {
        val publishedIds = SoundCatalog.all.map { it.id }.toSet()
        val referencedIds = (SampleContent.savedMixes + SampleContent.currentMix)
            .flatMap { mix -> mix.layers.map { it.soundId } }
            .toSet()

        assertEquals(emptySet(), referencedIds - publishedIds)
        assertEquals(4, SampleContent.currentMix.layers.size)
        assertTrue(SampleContent.currentMix.layers.any { it.soundId == "rain" })
        assertTrue(SampleContent.currentMix.layers.any { it.soundId == "forest" })
    }

    @Test
    fun publishedSoundsExposeStableAssetKeysAndDefaultVolumes() {
        SoundCatalog.all.forEach { sound ->
            assertEquals(sound.id, sound.loopAssetKey)
            assertTrue(sound.defaultVolume in 0f..1f)
        }
    }
}
