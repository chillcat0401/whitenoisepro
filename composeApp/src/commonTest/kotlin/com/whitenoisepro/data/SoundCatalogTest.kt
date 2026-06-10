package com.whitenoisepro.data

import com.whitenoisepro.domain.model.SoundCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SoundCatalogTest {
    private val publishedIds = setOf(
        "white_noise",
        "pink_noise",
        "brown_noise",
        "rain_soft",
        "rain_light_roof",
        "rain_window",
        "rain_roof",
        "rain_thunder",
        "ocean_gentle",
        "ocean_waves",
        "ocean_shore",
        "fire_crackle",
        "fire_hearth",
        "fan_floor",
        "wind_forest",
    )

    @Test
    fun catalogContainsOnlyPublishedAudioAssets() {
        assertEquals(
            publishedIds,
            SoundCatalog.all.map { it.id }.toSet(),
        )
        assertEquals(
            listOf(SoundCategory.Noise, SoundCategory.Nature, SoundCategory.Home),
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
        val masking = SoundCatalog.filter(category = null, query = "遮蔽")
        val rain = SoundCatalog.filter(category = null, query = "雨")
        val ocean = SoundCatalog.filter(category = null, query = "海")
        val fireplace = SoundCatalog.filter(category = null, query = "炉")

        assertEquals(listOf("white_noise"), masking.map { it.id })
        assertTrue(rain.map { it.id }.containsAll(listOf("rain_soft", "rain_window", "rain_roof")))
        assertTrue(ocean.map { it.id }.containsAll(listOf("ocean_gentle", "ocean_waves", "ocean_shore")))
        assertTrue(fireplace.map { it.id }.contains("fire_hearth"))
    }

    @Test
    fun sampleMixesReferenceOnlyPublishedSounds() {
        val publishedIds = SoundCatalog.all.map { it.id }.toSet()
        val referencedIds = (SampleContent.savedMixes + SampleContent.currentMix)
            .flatMap { mix -> mix.layers.map { it.soundId } }
            .toSet()

        assertEquals(emptySet(), referencedIds - publishedIds)
        assertEquals(4, SampleContent.currentMix.layers.size)
        assertTrue(SampleContent.currentMix.layers.any { it.soundId == "rain_soft" })
        assertTrue(SampleContent.currentMix.layers.any { it.soundId == "wind_forest" })
    }

    @Test
    fun publishedSoundsExposeStableAssetKeysAndDefaultVolumes() {
        assertEquals(15, SoundCatalog.all.size)
        SoundCatalog.all.forEach { sound ->
            assertEquals(sound.id, sound.loopAssetKey)
            assertTrue(sound.defaultVolume in 0f..1f)
        }
    }

    @Test
    fun externalSoundsAreSearchableByNaturalNames() {
        assertEquals(
            setOf("rain_soft", "rain_light_roof", "rain_window", "rain_roof", "rain_thunder"),
            SoundCatalog.filter(category = null, query = "雨").map { it.id }.toSet(),
        )
        assertTrue(SoundCatalog.filter(category = null, query = "风扇").map { it.id }.contains("fan_floor"))
        assertTrue(SoundCatalog.filter(category = null, query = "海").map { it.id }.contains("ocean_gentle"))
        assertTrue(SoundCatalog.filter(category = null, query = "火").map { it.id }.contains("fire_crackle"))
    }

    @Test
    fun legacyFirstPartySoundIdsRemapToPublishedReplacements() {
        assertEquals("rain_soft", SoundCatalog.canonicalId("rain"))
        assertEquals("ocean_gentle", SoundCatalog.canonicalId("ocean"))
        assertEquals("wind_forest", SoundCatalog.canonicalId("forest"))
        assertEquals("fire_hearth", SoundCatalog.canonicalId("fireplace"))
        assertEquals("fan_floor", SoundCatalog.canonicalId("fan"))
    }

    @Test
    fun canonicalIdKeepsPublishedAndUnknownIdsStable() {
        publishedIds.forEach { id ->
            assertEquals(id, SoundCatalog.canonicalId(id))
        }
        assertEquals("mystery_sound", SoundCatalog.canonicalId("mystery_sound"))
    }
}
