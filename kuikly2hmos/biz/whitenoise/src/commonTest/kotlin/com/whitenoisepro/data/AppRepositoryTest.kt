package com.whitenoisepro.data

import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.domain.model.UserSettings
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppRepositoryTest {
    @Test
    fun savesAndEmitsFullAppSnapshot() = runTest {
        val storage = FakeAppStorage()
        val repository = LocalAppRepository(storage)
        val snapshot = AppSnapshot(
            savedMixes = listOf(SoundMix(id = "saved", title = "雨夜", isFavorite = true)),
            favoriteMixIds = setOf("saved"),
            recentMixes = listOf(SoundMix(id = "recent", title = "海边")),
            currentMix = SoundMix(id = "current", title = "当前"),
            timerDefaults = SleepTimerState(durationMillis = 45 * 60 * 1000L, remainingMillis = 45 * 60 * 1000L),
            settings = UserSettings(startLastMix = false, offlineDownloads = true),
        )

        repository.save(snapshot)

        assertEquals(snapshot, repository.snapshots.first())
    }

    @Test
    fun snapshotsInitiallyEmitNullWhenStorageIsEmpty() = runTest {
        val repository = LocalAppRepository(FakeAppStorage())

        assertNull(repository.snapshots.first())
    }

    @Test
    fun collectorsReceiveSubsequentSaves() = runTest {
        val repository = LocalAppRepository(FakeAppStorage())
        val emission = async {
            repository.snapshots.first { it?.currentMix?.id == "updated" }
        }

        repository.save(AppSnapshot(currentMix = SoundMix(id = "updated", title = "更新")))

        assertEquals("updated", emission.await()?.currentMix?.id)
    }
}
