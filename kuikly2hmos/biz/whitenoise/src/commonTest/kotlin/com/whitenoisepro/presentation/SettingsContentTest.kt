package com.whitenoisepro.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SettingsContentTest {
    @Test
    fun releaseReadySettingsHideUnimplementedBilling() {
        val content = SettingsContent.releaseReady()
        val rows = content.rows()

        assertFalse(rows.any { it.title == "恢复购买" })
    }

    @Test
    fun releaseReadySettingsExplainPrivacyAndBackgroundMediaControls() {
        val rows = SettingsContent.releaseReady().rows()

        assertNotNull(rows.firstOrNull { it.title == "隐私政策" })
        assertTrue(rows.any { row ->
            row.subtitle.contains("无账号") &&
                row.subtitle.contains("无广告") &&
                row.subtitle.contains("本地保存")
        })
        assertFalse(rows.any { it.title == "通知权限" })
        assertTrue(rows.any { row ->
            row.title == "后台媒体控制" &&
                row.subtitle.contains("锁屏") &&
                row.subtitle.contains("不用于营销")
        })
    }

    @Test
    fun releaseReadySettingsExplainGeneratedAudioAndAvoidMedicalClaims() {
        val rows = SettingsContent.releaseReady().rows()
        val text = rows.joinToString(" ") { "${it.title} ${it.subtitle}" }

        assertTrue(text.contains("第一方噪声"))
        assertTrue(text.contains("CC0"))
        assertTrue(text.contains("自然录音"))
        assertTrue(text.contains("本地"))
        assertTrue(text.contains("开发者信息待补充"))
        assertFalse(text.contains("治疗"))
        assertFalse(text.contains("治愈"))
        assertFalse(text.contains("改善失眠"))
        assertFalse(text.contains("保证入睡"))
        assertFalse(text.contains("医学级"))
    }

    @Test
    fun releaseReadySettingsDoNotExposeOfflineDownloadAsEnabledToggle() {
        val offline = SettingsContent.releaseReady().rows().first { it.title == "离线下载" }

        assertEquals(SettingsRowKind.Disabled, offline.kind)
        assertFalse(offline.enabled)
    }

    @Test
    fun releaseReadySettingsKeepStartLastMixAsToggle() {
        val startLastMix = SettingsContent.releaseReady().rows().first { it.title == "启动时继续上次混音" }

        assertEquals(SettingsRowKind.Toggle, startLastMix.kind)
        assertTrue(startLastMix.enabled)
    }
}

private fun SettingsContent.rows(): List<SettingsRowContent> =
    sections.flatMap { it.rows }
