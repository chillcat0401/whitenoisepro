package com.whitenoisepro.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AndroidSoundResourceResolverTest {
    private val bundledSoundIds = listOf(
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
        "wind_trees",
        "stream_gentle",
        "crickets_night",
        "cafe_chatter",
        "train_ride",
        "airplane_cabin",
    )

    @Test
    fun bundledSoundIdsResolveToDedicatedResources() {
        val sources = bundledSoundIds.map(AndroidSoundResourceResolver::source)

        sources.forEach { source -> assertIs<SoundSource.Resource>(source) }
        assertEquals(
            bundledSoundIds.size,
            sources.filterIsInstance<SoundSource.Resource>().map { it.resId }.toSet().size,
        )
    }

    @Test
    fun noiseSoundIdsResolveToRuntimeSynthesis() {
        assertEquals(
            SoundSource.Synthesized(NoiseProfile.White),
            AndroidSoundResourceResolver.source("white_noise"),
        )
        assertEquals(
            SoundSource.Synthesized(NoiseProfile.Pink),
            AndroidSoundResourceResolver.source("pink_noise"),
        )
        assertEquals(
            SoundSource.Synthesized(NoiseProfile.Brown),
            AndroidSoundResourceResolver.source("brown_noise"),
        )
    }

    @Test
    fun legacyFirstPartyIdsResolveToReplacementAssets() {
        assertEquals(
            AndroidSoundResourceResolver.source("rain_soft"),
            AndroidSoundResourceResolver.source("rain"),
        )
        assertEquals(
            AndroidSoundResourceResolver.source("ocean_gentle"),
            AndroidSoundResourceResolver.source("ocean"),
        )
        assertEquals(
            AndroidSoundResourceResolver.source("wind_forest"),
            AndroidSoundResourceResolver.source("forest"),
        )
        assertEquals(
            AndroidSoundResourceResolver.source("fire_hearth"),
            AndroidSoundResourceResolver.source("fireplace"),
        )
        assertEquals(
            AndroidSoundResourceResolver.source("fan_floor"),
            AndroidSoundResourceResolver.source("fan"),
        )
    }

    @Test
    fun unknownIdFallsBackToSynthesizedBrownNoise() {
        val source = AndroidSoundResourceResolver.source("legacy_mystery")

        assertIs<SoundSource.Synthesized>(source)
        assertTrue(source.profile == NoiseProfile.Brown)
    }
}
