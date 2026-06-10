package com.whitenoisepro.data

import com.whitenoisepro.domain.model.Sound
import com.whitenoisepro.domain.model.SoundCategory

object SoundCatalog {
    val all: List<Sound> = listOf(
        Sound("white_noise", "白噪声", SoundCategory.Noise, "平衡高频遮蔽", defaultVolume = 0.42f),
        Sound("pink_noise", "粉噪声", SoundCategory.Noise, "自然均衡的睡眠底噪", defaultVolume = 0.52f),
        Sound("brown_noise", "棕噪声", SoundCategory.Noise, "更深、更柔和的低频", defaultVolume = 0.62f),
        Sound("rain_soft", "软雨", SoundCategory.Nature, "真实花园小雨，柔和连续", iconKey = "rain", defaultVolume = 0.42f),
        Sound("rain_light_roof", "轻屋顶雨", SoundCategory.Nature, "轻雨落在屋顶，细密安静", iconKey = "rain", defaultVolume = 0.34f),
        Sound("rain_window", "窗雨", SoundCategory.Nature, "室内闭窗视角的雨声", iconKey = "rain", defaultVolume = 0.36f),
        Sound("rain_roof", "屋顶雨", SoundCategory.Nature, "稳定屋顶雨幕，适合长时播放", iconKey = "rain", defaultVolume = 0.34f),
        Sound("rain_thunder", "雷雨", SoundCategory.Nature, "持续细雨与远处闷雷起伏", iconKey = "rain", defaultVolume = 0.36f),
        Sound("ocean_gentle", "柔和海浪", SoundCategory.Nature, "真实海浪与轻风，低缓起伏", iconKey = "ocean", defaultVolume = 0.38f),
        Sound("ocean_waves", "海浪", SoundCategory.Nature, "近岸水浪，节奏自然", iconKey = "ocean", defaultVolume = 0.36f),
        Sound("ocean_shore", "海岸", SoundCategory.Nature, "平静岸边水声，可作海浪补层", iconKey = "ocean", defaultVolume = 0.34f),
        Sound("fire_crackle", "火焰噼啪", SoundCategory.Home, "真实火焰细碎噼啪声", iconKey = "fireplace", defaultVolume = 0.28f),
        Sound("fire_hearth", "炉火", SoundCategory.Home, "温暖炉火底噪和轻微 crackle", iconKey = "fireplace", defaultVolume = 0.26f),
        Sound("fan_floor", "落地风扇", SoundCategory.Home, "真实落地风扇深层稳定嗡鸣", iconKey = "fan", defaultVolume = 0.42f),
        Sound("wind_forest", "林间风", SoundCategory.Nature, "林间轻风和自然空气感", iconKey = "forest", defaultVolume = 0.3f),
    )

    val availableCategories: List<SoundCategory> =
        all.map { it.category }.distinct()

    private val legacySoundIdAliases: Map<String, String> = mapOf(
        "fan" to "fan_floor",
        "rain" to "rain_soft",
        "ocean" to "ocean_gentle",
        "forest" to "wind_forest",
        "fireplace" to "fire_hearth",
    )

    private val publishedIds: Set<String> = all.map { it.id }.toSet()

    fun canonicalId(soundId: String): String =
        if (soundId in publishedIds) soundId else legacySoundIdAliases[soundId] ?: soundId

    fun filter(category: SoundCategory?, query: String): List<Sound> {
        val normalizedQuery = query.trim().lowercase()
        return all.filter { sound ->
            val categoryMatches = category == null || sound.category == category
            val queryMatches = normalizedQuery.isBlank() ||
                sound.name.lowercase().contains(normalizedQuery) ||
                sound.description.lowercase().contains(normalizedQuery) ||
                sound.id.lowercase().contains(normalizedQuery)
            categoryMatches && queryMatches
        }
    }

    fun nameOf(soundId: String): String =
        all.firstOrNull { it.id == soundId }?.name ?: soundId
}
