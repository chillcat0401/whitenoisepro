package com.whitenoisepro.domain

import com.whitenoisepro.domain.model.EndBehavior
import com.whitenoisepro.domain.model.PlaybackStatus
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.Sound
import com.whitenoisepro.domain.model.SoundCategory
import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.domain.model.UserSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DomainModelTest {
    @Test
    fun soundRequiresStableIdAndName() {
        assertFailsWith<IllegalArgumentException> {
            Sound(id = "", name = "雨声", category = SoundCategory.Nature)
        }

        val sound = Sound(id = "rain", name = "雨声", category = SoundCategory.Nature)

        assertEquals("rain", sound.id)
        assertEquals("雨声", sound.name)
    }

    @Test
    fun layerVolumeIsClampedToValidRangeByFactory() {
        val quietLayer = SoundLayer.create(id = "layer-1", soundId = "rain", volume = -2f)
        val loudLayer = SoundLayer.create(id = "layer-2", soundId = "ocean", volume = 3f)

        assertEquals(0f, quietLayer.volume)
        assertEquals(1f, loudLayer.volume)
    }

    @Test
    fun soundMixRejectsDuplicateActiveLayerIds() {
        val first = SoundLayer.create(id = "same", soundId = "rain", volume = 0.6f)
        val second = SoundLayer.create(id = "same", soundId = "ocean", volume = 0.4f)

        assertFailsWith<IllegalArgumentException> {
            SoundMix(id = "mix-1", title = "夜雨", layers = listOf(first, second))
        }
    }

    @Test
    fun soundMixRequiresValidMasterVolume() {
        assertFailsWith<IllegalArgumentException> {
            SoundMix(id = "mix-1", title = "夜雨", masterVolume = 1.2f)
        }
    }

    @Test
    fun playbackAndTimerDefaultsAreIdle() {
        val playback = com.whitenoisepro.domain.model.PlaybackState()
        val timer = SleepTimerState()

        assertEquals(PlaybackStatus.Idle, playback.status)
        assertFalse(playback.isPlaying)
        assertFalse(timer.isActive)
        assertEquals(EndBehavior.StopPlayback, timer.endBehavior)
    }

    @Test
    fun userSettingsExposeMvpDefaults() {
        val settings = UserSettings()

        assertTrue(settings.startLastMix)
        assertFalse(settings.offlineDownloads)
        assertEquals("standard", settings.audioQualityId)
    }
}
