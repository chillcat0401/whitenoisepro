package com.whitenoisepro.audio

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import com.whitenoisepro.R

object AndroidSoundResourceResolver {
    @RawRes
    fun resolve(soundId: String): Int = when (soundId) {
        "white_noise" -> R.raw.white_noise_loop
        "pink_noise" -> R.raw.pink_noise_loop
        "brown_noise" -> R.raw.brown_noise_loop
        "fan" -> R.raw.fan_loop
        "rain" -> R.raw.rain_loop
        "ocean" -> R.raw.ocean_loop
        "forest" -> R.raw.forest_loop
        "fireplace" -> R.raw.fireplace_loop
        else -> R.raw.brown_noise_loop
    }

    fun uri(context: Context, soundId: String): Uri =
        Uri.parse("android.resource://${context.packageName}/${resolve(soundId)}")
}
