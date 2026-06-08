package com.whitenoisepro.data

import com.whitenoisepro.domain.model.EndBehavior
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.domain.model.UserSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppSnapshotCodecTest {
    private val codec = AppSnapshotJsonCodec()

    @Test
    fun roundTripsVersionedSnapshot() {
        val snapshot = AppSnapshot(
            savedMixes = listOf(
                SoundMix(
                    id = "saved-rain",
                    title = "雨夜",
                    layers = listOf(SoundLayer.create("rain-layer", "rain", 0.55f, isMuted = true)),
                    masterVolume = 0.64f,
                    isFavorite = true,
                    updatedAtEpochMillis = 42L,
                ),
            ),
            favoriteMixIds = setOf("saved-rain"),
            recentMixes = listOf(SoundMix(id = "recent-ocean", title = "海边")),
            currentMix = SoundMix(id = "current", title = "当前"),
            timerDefaults = SleepTimerState(
                durationMillis = 45 * 60 * 1000L,
                remainingMillis = 30 * 60 * 1000L,
                fadeOutMillis = 3 * 60 * 1000L,
                endBehavior = EndBehavior.KeepPlaying,
                startedAtEpochMillis = 100L,
            ),
            settings = UserSettings(
                themeId = "dark",
                audioQualityId = "high",
                startLastMix = false,
                offlineDownloads = false,
                hapticsEnabled = false,
            ),
        )

        val encoded = codec.encode(snapshot)

        assertEquals(snapshot, codec.decode(encoded))
    }

    @Test
    fun ignoresUnknownAdditiveFields() {
        val encoded = codec.encode(AppSnapshot(currentMix = SoundMix(id = "current", title = "当前")))
        val withUnknownField = encoded.dropLast(1) + ""","futureField":{"enabled":true}}"""

        assertEquals("current", codec.decode(withUnknownField)?.currentMix?.id)
    }

    @Test
    fun returnsNullForMalformedJson() {
        assertNull(codec.decode("{not-json"))
    }

    @Test
    fun returnsNullForUnsupportedSchemaVersion() {
        val encoded = codec.encode(AppSnapshot())
        val unsupported = encoded.replace("\"schemaVersion\":1", "\"schemaVersion\":99")

        assertNull(codec.decode(unsupported))
    }
}
