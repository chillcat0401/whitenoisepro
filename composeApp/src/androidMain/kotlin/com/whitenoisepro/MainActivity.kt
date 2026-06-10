package com.whitenoisepro

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
        requestNotificationPermissionIfNeeded()
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

    // 媒体控制通知是后台播放可靠性的前提:API 33+ 无此权限时媒体通知无法发出、
    // 服务无法升前台,熄屏后进程会被系统冻结。仅请求一次,拒绝后不纠缠
    // (前台播放仍可用,但后台稳定性下降、锁屏无媒体控制)。
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < 33) return
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }
}
