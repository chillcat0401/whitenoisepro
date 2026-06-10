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
    fun ensureFile(context: Context, profile: NoiseProfile): File {
        val dir = File(context.filesDir, DirName)
        dir.mkdirs()
        val file = File(dir, "${profile.soundId}_loop_v$Version.wav")
        if (!file.exists() || file.length() == 0L) {
            val tmp = File(dir, "${file.name}.tmp")
            tmp.writeBytes(NoiseSynthesizer.loopWavBytes(profile))
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
                runCatching { ensureFile(appContext, profile) }
            }
        }
    }
}
