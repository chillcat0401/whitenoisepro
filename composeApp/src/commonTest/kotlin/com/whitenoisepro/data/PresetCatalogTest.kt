package com.whitenoisepro.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PresetCatalogTest {
    @Test
    fun presetsReferenceOnlyPublishedSounds() {
        val published = SoundCatalog.all.map { it.id }.toSet()
        PresetCatalog.all.forEach { preset ->
            val referenced = preset.layers.map { it.soundId }.toSet()
            assertEquals(emptySet(), referenced - published, "preset ${preset.id} 引用了未发布声音")
        }
    }

    @Test
    fun presetsAreWellFormedScenes() {
        assertEquals(6, PresetCatalog.all.size)
        assertEquals(PresetCatalog.all.size, PresetCatalog.all.map { it.id }.toSet().size)
        PresetCatalog.all.forEach { preset ->
            assertTrue(preset.id.startsWith("preset-"), preset.id)
            assertTrue(preset.title.isNotBlank())
            assertTrue(preset.layers.size in 2..4, "preset ${preset.id} 层数 ${preset.layers.size}")
            assertTrue(preset.masterVolume in 0.4f..0.8f)
            preset.layers.forEach { layer ->
                assertTrue(layer.volume > 0f && layer.volume <= 1f)
                assertTrue(!layer.isMuted)
            }
        }
    }

    @Test
    fun presetLookupById() {
        val first = PresetCatalog.all.first()
        assertEquals(first, PresetCatalog.byId(first.id))
        assertEquals(null, PresetCatalog.byId("preset-unknown"))
    }
}
