package com.whitenoisepro.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BrandCopyTest {
    @Test
    fun primarySloganIsMemorableAndPolicySafe() {
        assertEquals("把世界调低一点。", BrandCopy.PrimarySlogan)
        assertTrue(BrandCopy.HomeSupportingLine.contains("刚好的声音"))
        assertFalse(BrandCopy.hasPolicySensitiveClaim(BrandCopy.PrimarySlogan))
        assertFalse(BrandCopy.hasPolicySensitiveClaim(BrandCopy.HomeSupportingLine))
    }

    @Test
    fun recommendationCopySoundsGentleButConcrete() {
        assertEquals("适合今晚的声音", BrandCopy.RecommendedSoundsTitle)
        assertEquals("今晚的淡出定时", BrandCopy.BedtimeTimerTitle)
        assertEquals("30 分钟后慢慢安静下来", BrandCopy.bedtimeTimerSubtitle(30))
        assertFalse(BrandCopy.hasPolicySensitiveClaim(BrandCopy.bedtimeTimerSubtitle(30)))
    }

    @Test
    fun settingsAudioSourceCopyMatchesCurrentReleaseAssets() {
        assertTrue(BrandCopy.SettingsAudioSourceSubtitle.contains("本地打包"))
        assertTrue(BrandCopy.SettingsAudioSourceSubtitle.contains("第一方噪声"))
        assertTrue(BrandCopy.SettingsAudioSourceSubtitle.contains("CC0"))
        assertTrue(BrandCopy.SettingsAudioSourceSubtitle.contains("自然录音"))
        assertFalse(BrandCopy.hasPolicySensitiveClaim(BrandCopy.SettingsAudioSourceSubtitle))
    }

    @Test
    fun policySensitiveClaimsAreRejected() {
        assertTrue(BrandCopy.hasPolicySensitiveClaim("治疗失眠"))
        assertTrue(BrandCopy.hasPolicySensitiveClaim("保证入睡"))
        assertTrue(BrandCopy.hasPolicySensitiveClaim("医学级助眠"))
        assertFalse(BrandCopy.hasPolicySensitiveClaim("本地助眠声音与淡出定时"))
    }
}
