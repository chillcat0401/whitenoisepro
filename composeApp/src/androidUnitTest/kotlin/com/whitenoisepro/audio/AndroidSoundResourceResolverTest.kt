package com.whitenoisepro.audio

import com.whitenoisepro.R
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AndroidSoundResourceResolverTest {
    @Test
    fun publishedSoundIdsResolveToDedicatedResources() {
        val resources = listOf(
            AndroidSoundResourceResolver.resolve("white_noise"),
            AndroidSoundResourceResolver.resolve("pink_noise"),
            AndroidSoundResourceResolver.resolve("brown_noise"),
            AndroidSoundResourceResolver.resolve("fan"),
            AndroidSoundResourceResolver.resolve("rain"),
            AndroidSoundResourceResolver.resolve("ocean"),
            AndroidSoundResourceResolver.resolve("forest"),
            AndroidSoundResourceResolver.resolve("fireplace"),
        )

        assertEquals(8, resources.toSet().size)
    }

    @Test
    fun unknownLegacyIdFallsBackToBrownNoise() {
        assertEquals(
            R.raw.brown_noise_loop,
            AndroidSoundResourceResolver.resolve("legacy_rain"),
        )
        assertNotEquals(0, AndroidSoundResourceResolver.resolve("legacy_rain"))
    }
}
