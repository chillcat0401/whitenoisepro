package com.whitenoisepro.audio

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoiseSynthesizerTest {
    @Test
    fun generationIsDeterministicPerProfile() {
        NoiseProfile.entries.forEach { profile ->
            val first = NoiseSynthesizer.generatePcm(profile, durationSeconds = 2)
            val second = NoiseSynthesizer.generatePcm(profile, durationSeconds = 2)
            assertContentEquals(first, second)
        }
    }

    @Test
    fun profilesProduceDistinctSignals() {
        val white = NoiseSynthesizer.generatePcm(NoiseProfile.White, durationSeconds = 1)
        val pink = NoiseSynthesizer.generatePcm(NoiseProfile.Pink, durationSeconds = 1)
        val brown = NoiseSynthesizer.generatePcm(NoiseProfile.Brown, durationSeconds = 1)

        assertFalse(white.contentEquals(pink))
        assertFalse(pink.contentEquals(brown))
        assertFalse(white.contentEquals(brown))
    }

    @Test
    fun signalIsAudibleAndPeakLimited() {
        NoiseProfile.entries.forEach { profile ->
            val pcm = NoiseSynthesizer.generatePcm(profile, durationSeconds = 2)
            val peak = pcm.maxOf { abs(it.toInt()) }
            var sumSquares = 0.0
            pcm.forEach { sample ->
                val normalized = sample.toDouble() / Short.MAX_VALUE
                sumSquares += normalized * normalized
            }
            val rms = sqrt(sumSquares / pcm.size)

            assertTrue(peak <= (Short.MAX_VALUE * NoiseSynthesizer.TargetPeak).toInt() + 1, "peak too high for $profile")
            assertTrue(rms > 0.02, "rms too low for $profile: $rms")
        }
    }

    @Test
    fun brownLoopSeamHasNoDiscontinuity() {
        val pcm = NoiseSynthesizer.generatePcm(NoiseProfile.Brown, durationSeconds = 4)
        val seamJump = abs(pcm.first().toInt() - pcm.last().toInt())

        // Brown noise moves slowly sample-to-sample; the crossfaded seam must
        // stay in the same range as ordinary adjacent-sample movement.
        var maxAdjacentJump = 0
        for (i in 1 until pcm.size) {
            val jump = abs(pcm[i].toInt() - pcm[i - 1].toInt())
            if (jump > maxAdjacentJump) maxAdjacentJump = jump
        }
        assertTrue(
            seamJump <= maxAdjacentJump * 2,
            "seam jump $seamJump exceeds adjacent jump bound ${maxAdjacentJump * 2}",
        )
    }

    @Test
    fun wavBytesEncodeValidPcmHeader() {
        val pcm = NoiseSynthesizer.generatePcm(NoiseProfile.White, durationSeconds = 1)
        val wav = NoiseSynthesizer.wavBytes(pcm)

        assertEquals(44 + pcm.size * 2, wav.size)
        assertEquals("RIFF", wav.decodeAscii(0, 4))
        assertEquals("WAVE", wav.decodeAscii(8, 4))
        assertEquals("fmt ", wav.decodeAscii(12, 4))
        assertEquals("data", wav.decodeAscii(36, 4))
        assertEquals(wav.size - 8, wav.readIntLe(4))
        assertEquals(pcm.size * 2, wav.readIntLe(40))
        assertEquals(1, wav.readShortLe(20)) // PCM
        assertEquals(1, wav.readShortLe(22)) // mono
        assertEquals(NoiseSynthesizer.SampleRateHz, wav.readIntLe(24))
        assertEquals(16, wav.readShortLe(34)) // bits per sample
    }

    @Test
    fun noiseProfileMapsCatalogSoundIds() {
        assertEquals(NoiseProfile.White, NoiseProfile.forSoundId("white_noise"))
        assertEquals(NoiseProfile.Pink, NoiseProfile.forSoundId("pink_noise"))
        assertEquals(NoiseProfile.Brown, NoiseProfile.forSoundId("brown_noise"))
        assertEquals(null, NoiseProfile.forSoundId("rain_soft"))
    }

    private fun ByteArray.decodeAscii(offset: Int, length: Int): String =
        (offset until offset + length).map { this[it].toInt().toChar() }.joinToString("")

    private fun ByteArray.readIntLe(offset: Int): Int =
        (this[offset].toInt() and 0xFF) or
            ((this[offset + 1].toInt() and 0xFF) shl 8) or
            ((this[offset + 2].toInt() and 0xFF) shl 16) or
            ((this[offset + 3].toInt() and 0xFF) shl 24)

    private fun ByteArray.readShortLe(offset: Int): Int =
        (this[offset].toInt() and 0xFF) or ((this[offset + 1].toInt() and 0xFF) shl 8)
}
