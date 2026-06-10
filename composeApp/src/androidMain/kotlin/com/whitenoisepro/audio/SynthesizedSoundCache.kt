package com.whitenoisepro.audio

import android.content.Context
import java.io.File
import kotlin.concurrent.thread

/**
 * Generates and caches the synthesized noise loops under filesDir. Generation
 * is deterministic, so a purged cache simply regenerates identical files.
 */
object SynthesizedSoundCache {
    private const val DirName = "synth-audio"

    // Bump to invalidate previously generated loops after synthesis changes.
    private const val Version = 1

    @Synchronized
    fun ensureFile(context: Context, soundId: String): File {
        val dir = File(context.filesDir, DirName)
        dir.mkdirs()
        val file = File(dir, "${soundId}_loop_v$Version.wav")
        if (!file.exists() || file.length() == 0L) {
            val bytes = NoiseProfile.forSoundId(soundId)?.let { profile ->
                NoiseSynthesizer.loopWavBytes(profile)
            } ?: NoiseSynthesizer.parseCustomSoundId(soundId)?.let { tilt ->
                NoiseSynthesizer.tiltedLoopWavBytes(tilt)
            } ?: NoiseSynthesizer.loopWavBytes(NoiseProfile.Brown)
            val tmp = File(dir, "${file.name}.tmp")
            tmp.writeBytes(bytes)
            if (!tmp.renameTo(file)) {
                tmp.copyTo(file, overwrite = true)
                tmp.delete()
            }
        }
        return file
    }

    fun prewarm(context: Context) {
        val appContext = context.applicationContext
        thread(name = "synth-audio-prewarm", isDaemon = true) {
            NoiseProfile.entries.forEach { profile ->
                runCatching { ensureFile(appContext, profile.soundId) }
            }
        }
    }
}
