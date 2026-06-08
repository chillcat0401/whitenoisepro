package com.whitenoisepro.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.whitenoisepro.data.AppSnapshot
import com.whitenoisepro.data.AppSnapshotCodec
import com.whitenoisepro.data.AppStorage
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class AndroidDataStoreAppStorage(
    private val dataStore: DataStore<Preferences>,
    private val codec: AppSnapshotCodec,
) : AppStorage {
    override val snapshots: Flow<AppSnapshot?> =
        dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preferences ->
                preferences[SnapshotKey]
                    ?.takeIf(String::isNotBlank)
                    ?.let(codec::decode)
            }

    override suspend fun write(snapshot: AppSnapshot) {
        val encoded = codec.encode(snapshot)
        dataStore.edit { preferences ->
            preferences[SnapshotKey] = encoded
        }
    }

    private companion object {
        val SnapshotKey = stringPreferencesKey("app_snapshot_json")
    }
}
