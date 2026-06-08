package com.whitenoisepro.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.whitenoisepro.data.AppSnapshot
import com.whitenoisepro.data.AppSnapshotJsonCodec
import com.whitenoisepro.domain.model.SoundMix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AndroidDataStoreAppStorageTest {
    @Test
    fun writesEncodedSnapshotAndEmitsDecodedSnapshot() = runTest {
        val dataStore = FakePreferencesDataStore()
        val storage = AndroidDataStoreAppStorage(
            dataStore = dataStore,
            codec = AppSnapshotJsonCodec(),
        )
        val snapshot = AppSnapshot(currentMix = SoundMix(id = "persisted", title = "已保存"))

        storage.write(snapshot)

        assertEquals("persisted", storage.snapshots.first()?.currentMix?.id)
        assertEquals(
            "persisted",
            AppSnapshotJsonCodec()
                .decode(dataStore.current[SnapshotKey] ?: error("missing snapshot JSON"))
                ?.currentMix
                ?.id,
        )
    }

    @Test
    fun emptyPreferencesEmitNull() = runTest {
        val storage = AndroidDataStoreAppStorage(
            dataStore = FakePreferencesDataStore(),
            codec = AppSnapshotJsonCodec(),
        )

        assertNull(storage.snapshots.first())
    }

    @Test
    fun malformedJsonEmitsNull() = runTest {
        val dataStore = FakePreferencesDataStore(preferencesOf(SnapshotKey to "{bad-json"))
        val storage = AndroidDataStoreAppStorage(dataStore, AppSnapshotJsonCodec())

        assertNull(storage.snapshots.first())
    }

    private companion object {
        val SnapshotKey = stringPreferencesKey("app_snapshot_json")
    }
}

private class FakePreferencesDataStore(
    initial: Preferences = emptyPreferences(),
) : DataStore<Preferences> {
    private val state = MutableStateFlow(initial)

    val current: Preferences
        get() = state.value

    override val data: Flow<Preferences> = state

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.emit(updated)
        return updated
    }
}
