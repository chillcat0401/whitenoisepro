package com.whitenoisepro.data

import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSnapshot(
    val savedMixes: List<SoundMix> = emptyList(),
    val favoriteMixIds: Set<String> = emptySet(),
    val recentMixes: List<SoundMix> = emptyList(),
    val currentMix: SoundMix? = null,
    val timerDefaults: SleepTimerState = SleepTimerState(),
    val settings: UserSettings = UserSettings(),
)

interface AppRepository {
    val snapshots: Flow<AppSnapshot?>
    suspend fun save(snapshot: AppSnapshot)
}

interface AppStorage {
    val snapshots: Flow<AppSnapshot?>
    suspend fun write(snapshot: AppSnapshot)
}

class LocalAppRepository(
    private val storage: AppStorage,
) : AppRepository {
    override val snapshots: Flow<AppSnapshot?> = storage.snapshots

    override suspend fun save(snapshot: AppSnapshot) {
        storage.write(snapshot)
    }
}

class FakeAppStorage(
    initialSnapshot: AppSnapshot? = null,
) : AppStorage {
    private val mutableSnapshots = MutableStateFlow(initialSnapshot)

    override val snapshots: Flow<AppSnapshot?> = mutableSnapshots.asStateFlow()

    override suspend fun write(snapshot: AppSnapshot) {
        mutableSnapshots.emit(snapshot)
    }
}
