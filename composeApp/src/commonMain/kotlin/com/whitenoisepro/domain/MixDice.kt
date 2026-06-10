package com.whitenoisepro.domain

import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix
import kotlin.random.Random

/**
 * 混音骰子:按「1 底噪 + 1~2 主纹理 + 0~1 点缀」规则生成结构合理的随机混音,
 * 名称由场景词组合而成(如「雨夜·列车」)。仅引用已发布 soundId。
 */
object MixDice {
    val baseSoundIds = listOf("white_noise", "pink_noise", "brown_noise", "fan_floor", "airplane_cabin")
    val textureSoundIds = listOf(
        "rain_soft", "rain_light_roof", "rain_window", "rain_roof", "rain_thunder",
        "ocean_gentle", "ocean_waves", "ocean_shore",
        "stream_gentle", "fire_hearth", "fire_crackle", "train_ride", "cafe_chatter",
    )
    val accentSoundIds = listOf("crickets_night", "wind_trees", "wind_forest")

    private val sceneWords: Map<String, String> = mapOf(
        "white_noise" to "白噪", "pink_noise" to "粉噪", "brown_noise" to "深噪",
        "fan_floor" to "风扇", "airplane_cabin" to "机舱",
        "rain_soft" to "雨夜", "rain_light_roof" to "雨夜", "rain_window" to "窗雨",
        "rain_roof" to "雨夜", "rain_thunder" to "雷雨",
        "ocean_gentle" to "海边", "ocean_waves" to "海边", "ocean_shore" to "海岸",
        "stream_gentle" to "溪畔", "fire_hearth" to "炉边", "fire_crackle" to "炉边",
        "train_ride" to "列车", "cafe_chatter" to "咖啡馆",
        "crickets_night" to "夏夜", "wind_trees" to "林间", "wind_forest" to "林间",
    )

    private val defaultVolumes: Map<String, Float> =
        com.whitenoisepro.data.SoundCatalog.all.associate { it.id to it.defaultVolume }

    fun roll(random: Random): SoundMix {
        val base = baseSoundIds.random(random)
        val textures = textureSoundIds.shuffled(random).take(if (random.nextBoolean()) 2 else 1)
        val accents = if (random.nextInt(3) == 0) emptyList() else listOf(accentSoundIds.random(random))
        val soundIds = listOf(base) + textures + accents

        val layers = soundIds.mapIndexed { index, soundId ->
            val baseVolume = defaultVolumes[soundId] ?: SoundLayer.DefaultVolume
            val jitter = (random.nextFloat() - 0.5f) * 0.16f
            SoundLayer.create(
                id = "dice-layer-$index-$soundId",
                soundId = soundId,
                volume = (baseVolume + jitter).coerceIn(0.1f, 0.7f),
            )
        }

        val title = (textures + accents + base)
            .mapNotNull { sceneWords[it] }
            .distinct()
            .take(2)
            .joinToString("·")

        return SoundMix(
            id = "dice-${random.nextInt(1_000_000)}",
            title = title.ifBlank { "随机混音" },
            layers = layers,
            masterVolume = 0.66f,
        )
    }
}
