package com.whitenoisepro.audio

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

enum class NoiseProfile(val soundId: String, val seed: Long) {
    White("white_noise", 0x1537A11L),
    Pink("pink_noise", 0x2498B22L),
    Brown("brown_noise", 0x3579C33L),
    ;

    companion object {
        fun forSoundId(soundId: String): NoiseProfile? =
            entries.firstOrNull { it.soundId == soundId }
    }
}

/**
 * Deterministic noise loop generator. Replaces the bundled first-party WAV
 * assets: loops are synthesized on device, so they cost zero APK bytes and
 * regenerate identically from the profile seed.
 */
object NoiseSynthesizer {
    const val SampleRateHz = 44_100
    const val LoopDurationSeconds = 30
    const val LoopCrossfadeMillis = 400
    const val TargetPeak = 0.85f

    // 自定义音色:soundId 形如 noise_custom_t15(频谱倾斜 -1.5 dB/oct ×10)。
    const val CustomSoundIdPrefix = "noise_custom_t"
    const val MaxTiltDbPerOct = 6f
    private const val CustomSeed = 0x7E47C0DEL

    fun customSoundId(tiltDbPerOct: Float): String {
        val steps = (tiltDbPerOct.coerceIn(0f, MaxTiltDbPerOct) * 10f).roundToInt()
        return "$CustomSoundIdPrefix$steps"
    }

    fun parseCustomSoundId(soundId: String): Float? {
        if (!soundId.startsWith(CustomSoundIdPrefix)) return null
        val steps = soundId.removePrefix(CustomSoundIdPrefix).toIntOrNull() ?: return null
        if (steps !in 0..(MaxTiltDbPerOct * 10f).roundToInt()) return null
        return steps / 10f
    }

    fun generatePcm(
        profile: NoiseProfile,
        durationSeconds: Int = LoopDurationSeconds,
        sampleRate: Int = SampleRateHz,
    ): ShortArray = assembleLoop(durationSeconds, sampleRate) { count ->
        renderFloats(profile, count)
    }

    fun generateTiltedPcm(
        tiltDbPerOct: Float,
        durationSeconds: Int = LoopDurationSeconds,
        sampleRate: Int = SampleRateHz,
    ): ShortArray = assembleLoop(durationSeconds, sampleRate) { count ->
        renderTilted(tiltDbPerOct.coerceIn(0f, MaxTiltDbPerOct), count)
    }

    fun tiltedLoopWavBytes(tiltDbPerOct: Float): ByteArray =
        wavBytes(generateTiltedPcm(tiltDbPerOct))

    private inline fun assembleLoop(
        durationSeconds: Int,
        sampleRate: Int,
        render: (Int) -> FloatArray,
    ): ShortArray {
        val loopSamples = durationSeconds * sampleRate
        val fadeSamples = LoopCrossfadeMillis * sampleRate / 1000
        val raw = render(loopSamples + fadeSamples)

        // Blend the start with the would-be continuation past the loop end so
        // the wrap-around point has no discontinuity.
        for (i in 0 until fadeSamples) {
            val t = (i + 1).toFloat() / (fadeSamples + 1).toFloat()
            raw[i] = raw[i] * sqrt(t) + raw[loopSamples + i] * sqrt(1f - t)
        }

        var peak = 0f
        for (i in 0 until loopSamples) {
            val magnitude = abs(raw[i])
            if (magnitude > peak) peak = magnitude
        }
        val gain = if (peak > 0f) TargetPeak / peak else 0f

        return ShortArray(loopSamples) { i ->
            val sample = (raw[i] * gain).coerceIn(-1f, 1f)
            (sample * Short.MAX_VALUE).toInt().toShort()
        }
    }

    // 单条 RNG 流同步产生白/粉/棕三路,按倾斜分段等功率混合:
    // t∈[0,3] 白↔粉,t∈(3,6] 粉↔棕。
    private fun renderTilted(tilt: Float, count: Int): FloatArray {
        val rng = SplitMix64(CustomSeed)
        val out = FloatArray(count)
        var b0 = 0f; var b1 = 0f; var b2 = 0f
        var b3 = 0f; var b4 = 0f; var b5 = 0f; var b6 = 0f
        var acc = 0f
        val blendLow: Float
        val blendHigh: Float
        val lowIsWhite: Boolean
        if (tilt <= 3f) {
            val a = tilt / 3f
            blendLow = sqrt(1f - a)
            blendHigh = sqrt(a)
            lowIsWhite = true
        } else {
            val a = (tilt - 3f) / 3f
            blendLow = sqrt(1f - a)
            blendHigh = sqrt(a)
            lowIsWhite = false
        }
        for (i in 0 until count) {
            val w = rng.nextSignedFloat()
            b0 = 0.99886f * b0 + w * 0.0555179f
            b1 = 0.99332f * b1 + w * 0.0750759f
            b2 = 0.96900f * b2 + w * 0.1538520f
            b3 = 0.86650f * b3 + w * 0.3104856f
            b4 = 0.55000f * b4 + w * 0.5329522f
            b5 = -0.7616f * b5 - w * 0.0168980f
            val pink = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + w * 0.5362f) * 0.11f
            b6 = w * 0.115926f
            acc = (acc + 0.02f * w) / 1.02f
            val brown = acc * 3.5f
            out[i] = if (lowIsWhite) {
                blendLow * w + blendHigh * pink
            } else {
                blendLow * pink + blendHigh * brown
            }
        }
        return out
    }

    fun wavBytes(pcm: ShortArray, sampleRate: Int = SampleRateHz): ByteArray {
        val dataBytes = pcm.size * 2
        val bytes = ByteArray(44 + dataBytes)
        var offset = 0

        fun writeAscii(value: String) {
            value.forEach { bytes[offset++] = it.code.toByte() }
        }

        fun writeIntLe(value: Int) {
            bytes[offset++] = (value and 0xFF).toByte()
            bytes[offset++] = ((value ushr 8) and 0xFF).toByte()
            bytes[offset++] = ((value ushr 16) and 0xFF).toByte()
            bytes[offset++] = ((value ushr 24) and 0xFF).toByte()
        }

        fun writeShortLe(value: Int) {
            bytes[offset++] = (value and 0xFF).toByte()
            bytes[offset++] = ((value ushr 8) and 0xFF).toByte()
        }

        writeAscii("RIFF")
        writeIntLe(36 + dataBytes)
        writeAscii("WAVE")
        writeAscii("fmt ")
        writeIntLe(16)
        writeShortLe(1) // PCM
        writeShortLe(1) // mono
        writeIntLe(sampleRate)
        writeIntLe(sampleRate * 2) // byte rate: mono 16-bit
        writeShortLe(2) // block align
        writeShortLe(16) // bits per sample
        writeAscii("data")
        writeIntLe(dataBytes)
        pcm.forEach { sample ->
            writeShortLe(sample.toInt() and 0xFFFF)
        }
        return bytes
    }

    fun loopWavBytes(profile: NoiseProfile): ByteArray =
        wavBytes(generatePcm(profile))

    private fun renderFloats(profile: NoiseProfile, count: Int): FloatArray {
        val rng = SplitMix64(profile.seed)
        val out = FloatArray(count)
        when (profile) {
            NoiseProfile.White -> {
                for (i in 0 until count) {
                    out[i] = rng.nextSignedFloat()
                }
            }
            NoiseProfile.Pink -> {
                // Paul Kellet's refined pink noise filter.
                var b0 = 0f; var b1 = 0f; var b2 = 0f
                var b3 = 0f; var b4 = 0f; var b5 = 0f; var b6 = 0f
                for (i in 0 until count) {
                    val w = rng.nextSignedFloat()
                    b0 = 0.99886f * b0 + w * 0.0555179f
                    b1 = 0.99332f * b1 + w * 0.0750759f
                    b2 = 0.96900f * b2 + w * 0.1538520f
                    b3 = 0.86650f * b3 + w * 0.3104856f
                    b4 = 0.55000f * b4 + w * 0.5329522f
                    b5 = -0.7616f * b5 - w * 0.0168980f
                    out[i] = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + w * 0.5362f) * 0.11f
                    b6 = w * 0.115926f
                }
            }
            NoiseProfile.Brown -> {
                var acc = 0f
                for (i in 0 until count) {
                    val w = rng.nextSignedFloat()
                    acc = (acc + 0.02f * w) / 1.02f
                    out[i] = acc * 3.5f
                }
            }
        }
        return out
    }

    private class SplitMix64(seed: Long) {
        private var state = seed

        fun nextSignedFloat(): Float {
            state += 0x9E3779B97F4A7C15uL.toLong()
            var z = state
            z = (z xor (z ushr 30)) * 0xBF58476D1CE4E5B9uL.toLong()
            z = (z xor (z ushr 27)) * 0x94D049BB133111EBuL.toLong()
            z = z xor (z ushr 31)
            // Top 24 bits → uniform float in [-1, 1).
            return ((z ushr 40).toInt().toFloat() / 8_388_608f) - 1f
        }
    }
}
