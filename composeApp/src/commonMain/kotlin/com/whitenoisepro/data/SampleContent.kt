package com.whitenoisepro.data

import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix

object SampleContent {
    val sounds = SoundCatalog.all

    val currentMix = SoundMix(
        id = "deep-night-noise",
        title = "深夜雨林",
        layers = listOf(
            SoundLayer.create("layer-brown", "brown_noise", 0.62f),
            SoundLayer.create("layer-rain", "rain", 0.34f),
            SoundLayer.create("layer-forest", "forest", 0.24f),
            SoundLayer.create("layer-fan", "fan", 0.24f),
        ),
        masterVolume = 0.68f,
        isFavorite = true,
        updatedAtEpochMillis = 1L,
    )

    val savedMixes = listOf(
        currentMix,
        SoundMix(
            id = "soft-focus",
            title = "柔和专注",
            layers = listOf(
                SoundLayer.create("layer-pink-focus", "pink_noise", 0.68f),
                SoundLayer.create("layer-white-focus", "white_noise", 0.18f),
                SoundLayer.create("layer-ocean-focus", "ocean", 0.16f),
            ),
            masterVolume = 0.58f,
            updatedAtEpochMillis = 2L,
        ),
        SoundMix(
            id = "quiet-room",
            title = "安静房间",
            layers = listOf(
                SoundLayer.create("layer-fan-room", "fan", 0.58f),
                SoundLayer.create("layer-brown-room", "brown_noise", 0.3f),
                SoundLayer.create("layer-fire-room", "fireplace", 0.18f),
            ),
            masterVolume = 0.62f,
            isFavorite = true,
            updatedAtEpochMillis = 3L,
        ),
    )

    fun soundName(soundId: String): String =
        SoundCatalog.nameOf(soundId)
}
