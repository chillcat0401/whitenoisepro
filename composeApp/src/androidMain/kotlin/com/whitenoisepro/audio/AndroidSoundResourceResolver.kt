package com.whitenoisepro.audio

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import com.whitenoisepro.R
import com.whitenoisepro.data.SoundCatalog

sealed interface SoundSource {
    data class Resource(@RawRes val resId: Int) : SoundSource
    data class Synthesized(val profile: NoiseProfile) : SoundSource
}

object AndroidSoundResourceResolver {
    fun source(soundId: String): SoundSource {
        val canonicalId = SoundCatalog.canonicalId(soundId)
        NoiseProfile.forSoundId(canonicalId)?.let { profile ->
            return SoundSource.Synthesized(profile)
        }
        val resId = when (canonicalId) {
            "rain_soft" -> R.raw.rain_soft_loop
            "rain_light_roof" -> R.raw.rain_light_roof_loop
            "rain_window" -> R.raw.rain_window_loop
            "rain_roof" -> R.raw.rain_roof_loop
            "rain_thunder" -> R.raw.rain_thunder_loop
            "ocean_gentle" -> R.raw.ocean_gentle_loop
            "ocean_waves" -> R.raw.ocean_waves_loop
            "ocean_shore" -> R.raw.ocean_shore_loop
            "fire_crackle" -> R.raw.fire_crackle_loop
            "fire_hearth" -> R.raw.fire_hearth_loop
            "fan_floor" -> R.raw.fan_floor_loop
            "wind_forest" -> R.raw.wind_forest_loop
            "wind_trees" -> R.raw.wind_trees_loop
            "stream_gentle" -> R.raw.stream_gentle_loop
            "crickets_night" -> R.raw.crickets_night_loop
            "cafe_chatter" -> R.raw.cafe_chatter_loop
            "train_ride" -> R.raw.train_ride_loop
            "airplane_cabin" -> R.raw.airplane_cabin_loop
            else -> return SoundSource.Synthesized(NoiseProfile.Brown)
        }
        return SoundSource.Resource(resId)
    }

    fun uri(context: Context, soundId: String): Uri =
        when (val source = source(soundId)) {
            is SoundSource.Resource ->
                Uri.parse("android.resource://${context.packageName}/${source.resId}")
            is SoundSource.Synthesized ->
                Uri.fromFile(SynthesizedSoundCache.ensureFile(context, source.profile))
        }
}
