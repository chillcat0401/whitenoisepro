package com.whitenoisepro

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.preferences.preferencesDataStore
import com.whitenoisepro.audio.AndroidPlatformSleepTimerRuntime
import com.whitenoisepro.audio.AndroidPlaybackEngineProvider
import com.whitenoisepro.data.AppSnapshotJsonCodec
import com.whitenoisepro.data.LocalAppRepository
import com.whitenoisepro.storage.AndroidDataStoreAppStorage

private val Context.appStateDataStore by preferencesDataStore(name = "app_state")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playbackEngine = AndroidPlaybackEngineProvider.get(applicationContext)
        val platformSleepTimerRuntime = AndroidPlatformSleepTimerRuntime(applicationContext)
        val repository = LocalAppRepository(
            AndroidDataStoreAppStorage(
                dataStore = applicationContext.appStateDataStore,
                codec = AppSnapshotJsonCodec(),
            ),
        )
        setContent {
            WhiteNoiseProApp(
                playbackEngine = playbackEngine,
                repository = repository,
                platformSleepTimerRuntime = platformSleepTimerRuntime,
            )
        }
    }
}
