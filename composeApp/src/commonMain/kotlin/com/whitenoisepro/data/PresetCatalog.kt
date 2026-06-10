package com.whitenoisepro.data

import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix

/** 场景预设:精调的开箱混音,首页一键播放。只引用已发布 soundId。 */
object PresetCatalog {
    val all: List<SoundMix> = listOf(
        preset(
            id = "preset-rain-train",
            title = "雨夜列车",
            layers = listOf(
                "train_ride" to 0.45f,
                "rain_roof" to 0.36f,
                "rain_thunder" to 0.18f,
            ),
        ),
        preset(
            id = "preset-night-cafe",
            title = "深夜咖啡馆",
            layers = listOf(
                "cafe_chatter" to 0.42f,
                "rain_window" to 0.3f,
            ),
        ),
        preset(
            id = "preset-summer-porch",
            title = "夏夜门廊",
            layers = listOf(
                "crickets_night" to 0.38f,
                "wind_trees" to 0.26f,
            ),
        ),
        preset(
            id = "preset-cabin-rest",
            title = "机舱浅眠",
            layers = listOf(
                "airplane_cabin" to 0.46f,
                "pink_noise" to 0.2f,
            ),
        ),
        preset(
            id = "preset-stream-read",
            title = "溪畔读书",
            layers = listOf(
                "stream_gentle" to 0.42f,
                "wind_forest" to 0.24f,
            ),
        ),
        preset(
            id = "preset-winter-hearth",
            title = "炉边冬夜",
            layers = listOf(
                "fire_hearth" to 0.4f,
                "fire_crackle" to 0.24f,
                "wind_trees" to 0.16f,
            ),
        ),
    )

    fun byId(presetId: String): SoundMix? =
        all.firstOrNull { it.id == presetId }

    private fun preset(id: String, title: String, layers: List<Pair<String, Float>>): SoundMix =
        SoundMix(
            id = id,
            title = title,
            layers = layers.mapIndexed { index, (soundId, volume) ->
                SoundLayer.create(
                    id = "$id-layer-$index",
                    soundId = soundId,
                    volume = volume,
                )
            },
            masterVolume = 0.66f,
            updatedAtEpochMillis = 0L,
        )
}
