package com.whitenoisepro.data

import com.whitenoisepro.domain.model.Sound
import com.whitenoisepro.domain.model.SoundCategory

object SoundCatalog {
    val all: List<Sound> = listOf(
        Sound("white_noise", "白噪声", SoundCategory.Noise, "平衡高频遮蔽", defaultVolume = 0.42f),
        Sound("pink_noise", "粉噪声", SoundCategory.Noise, "自然均衡的睡眠底噪", defaultVolume = 0.52f),
        Sound("brown_noise", "棕噪声", SoundCategory.Noise, "更深、更柔和的低频", defaultVolume = 0.62f),
        Sound("fan", "柔和风扇", SoundCategory.Home, "稳定机械低噪，柔和遮蔽环境声", defaultVolume = 0.48f),
        Sound("rain", "细雨", SoundCategory.Nature, "细密雨声，柔和覆盖窗外杂音", defaultVolume = 0.5f),
        Sound("ocean", "远海", SoundCategory.Nature, "低缓海浪起伏，适合入睡", defaultVolume = 0.46f),
        Sound("forest", "夜林", SoundCategory.Nature, "轻柔森林空气与夜晚纹理", defaultVolume = 0.4f),
        Sound("fireplace", "暖炉", SoundCategory.Home, "低亮火焰与温暖噼啪声", defaultVolume = 0.36f),
    )

    val availableCategories: List<SoundCategory> =
        all.map { it.category }.distinct()

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
