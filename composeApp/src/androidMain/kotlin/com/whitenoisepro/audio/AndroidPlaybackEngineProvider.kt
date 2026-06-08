package com.whitenoisepro.audio

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AndroidPlaybackEngineProvider {
    @Volatile
    private var instance: AndroidPlaybackEngine? = null

    fun get(context: Context): AndroidPlaybackEngine =
        instance ?: synchronized(this) {
            instance ?: AndroidPlaybackEngine(context.applicationContext).also {
                instance = it
            }
        }
}
