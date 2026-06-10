package com.whitenoisepro.domain

import com.whitenoisepro.data.SoundCatalog
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MixDiceTest {
    @Test
    fun rollIsDeterministicForSameSeed() {
        val first = MixDice.roll(Random(42))
        val second = MixDice.roll(Random(42))

        assertEquals(first.title, second.title)
        assertEquals(first.layers.map { it.soundId }, second.layers.map { it.soundId })
        assertEquals(first.layers.map { it.volume }, second.layers.map { it.volume })
    }

    @Test
    fun rollFollowsStructureRules() {
        repeat(200) { seed ->
            val mix = MixDice.roll(Random(seed))
            val ids = mix.layers.map { it.soundId }

            assertEquals(1, ids.count { it in MixDice.baseSoundIds }, "seed=$seed 底噪数错误: $ids")
            assertTrue(ids.count { it in MixDice.textureSoundIds } in 1..2, "seed=$seed 纹理数错误: $ids")
            assertTrue(ids.count { it in MixDice.accentSoundIds } <= 1, "seed=$seed 点缀数错误: $ids")
            assertTrue(mix.layers.size in 2..4)
            assertEquals(ids.size, ids.toSet().size, "seed=$seed 出现重复声音")
            mix.layers.forEach { assertTrue(it.volume in 0.1f..0.7f) }
        }
    }

    @Test
    fun rollReferencesOnlyPublishedSounds() {
        val published = SoundCatalog.all.map { it.id }.toSet()
        repeat(100) { seed ->
            val mix = MixDice.roll(Random(seed))
            assertEquals(emptySet(), mix.layers.map { it.soundId }.toSet() - published)
        }
    }

    @Test
    fun rollProducesSceneStyleTitle() {
        repeat(50) { seed ->
            val mix = MixDice.roll(Random(seed))
            assertTrue(mix.title.isNotBlank())
            assertTrue(mix.title.length <= 12, "标题过长: ${mix.title}")
        }
        // 多个种子之间应当出现不同标题(避免退化成常量)
        val titles = (0 until 30).map { MixDice.roll(Random(it)).title }.toSet()
        assertTrue(titles.size > 5, "标题多样性不足: $titles")
    }
}
