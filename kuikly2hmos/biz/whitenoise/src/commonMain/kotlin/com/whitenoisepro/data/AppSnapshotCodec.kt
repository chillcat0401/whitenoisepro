package com.whitenoisepro.data

import com.whitenoisepro.domain.model.EndBehavior
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.domain.model.UserSettings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface AppSnapshotCodec {
    fun encode(snapshot: AppSnapshot): String
    fun decode(value: String): AppSnapshot?
}

class AppSnapshotJsonCodec(
    private val json: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    },
) : AppSnapshotCodec {
    override fun encode(snapshot: AppSnapshot): String =
        json.encodeToString(PersistedAppSnapshotV1.serializer(), snapshot.toPersisted())

    override fun decode(value: String): AppSnapshot? =
        runCatching {
            json.decodeFromString(PersistedAppSnapshotV1.serializer(), value)
        }.getOrNull()
            ?.takeIf { it.schemaVersion == CurrentSchemaVersion }
            ?.toDomain()

    private companion object {
        const val CurrentSchemaVersion = 1
    }
}

@Serializable
private data class PersistedAppSnapshotV1(
    val schemaVersion: Int = 1,
    val savedMixes: List<PersistedSoundMix> = emptyList(),
    val favoriteMixIds: Set<String> = emptySet(),
    val recentMixes: List<PersistedSoundMix> = emptyList(),
    val currentMix: PersistedSoundMix? = null,
    val timerDefaults: PersistedTimer = PersistedTimer(),
    val settings: PersistedSettings = PersistedSettings(),
)

@Serializable
private data class PersistedSoundMix(
    val id: String,
    val title: String,
    val layers: List<PersistedSoundLayer> = emptyList(),
    val masterVolume: Float = 0.75f,
    val isFavorite: Boolean = false,
    val updatedAtEpochMillis: Long = 0L,
)

@Serializable
private data class PersistedSoundLayer(
    val id: String,
    val soundId: String,
    val volume: Float = SoundLayer.DefaultVolume,
    val isMuted: Boolean = false,
)

@Serializable
private data class PersistedTimer(
    val durationMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val fadeOutMillis: Long = 5 * 60 * 1000L,
    val endBehavior: String = EndBehavior.StopPlayback.name,
    val startedAtEpochMillis: Long? = null,
)

@Serializable
private data class PersistedSettings(
    val themeId: String = "system",
    val audioQualityId: String = "standard",
    val startLastMix: Boolean = true,
    val offlineDownloads: Boolean = false,
    val hapticsEnabled: Boolean = true,
)

private fun AppSnapshot.toPersisted(): PersistedAppSnapshotV1 =
    PersistedAppSnapshotV1(
        savedMixes = savedMixes.map(SoundMix::toPersisted),
        favoriteMixIds = favoriteMixIds,
        recentMixes = recentMixes.map(SoundMix::toPersisted),
        currentMix = currentMix?.toPersisted(),
        timerDefaults = timerDefaults.toPersisted(),
        settings = settings.toPersisted(),
    )

private fun PersistedAppSnapshotV1.toDomain(): AppSnapshot =
    AppSnapshot(
        savedMixes = savedMixes.map(PersistedSoundMix::toDomain),
        favoriteMixIds = favoriteMixIds,
        recentMixes = recentMixes.map(PersistedSoundMix::toDomain),
        currentMix = currentMix?.toDomain(),
        timerDefaults = timerDefaults.toDomain(),
        settings = settings.toDomain(),
    )

private fun SoundMix.toPersisted(): PersistedSoundMix =
    PersistedSoundMix(
        id = id,
        title = title,
        layers = layers.map { layer ->
            PersistedSoundLayer(
                id = layer.id,
                soundId = layer.soundId,
                volume = layer.volume,
                isMuted = layer.isMuted,
            )
        },
        masterVolume = masterVolume,
        isFavorite = isFavorite,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

private fun PersistedSoundMix.toDomain(): SoundMix =
    SoundMix(
        id = id,
        title = title,
        layers = layers.map { layer ->
            SoundLayer.create(
                id = layer.id,
                soundId = SoundCatalog.canonicalId(layer.soundId),
                volume = layer.volume,
                isMuted = layer.isMuted,
            )
        },
        masterVolume = masterVolume.coerceIn(0f, 1f),
        isFavorite = isFavorite,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

private fun SleepTimerState.toPersisted(): PersistedTimer =
    PersistedTimer(
        durationMillis = durationMillis,
        remainingMillis = remainingMillis,
        fadeOutMillis = fadeOutMillis,
        endBehavior = endBehavior.name,
        startedAtEpochMillis = startedAtEpochMillis,
    )

private fun PersistedTimer.toDomain(): SleepTimerState =
    SleepTimerState(
        durationMillis = durationMillis.coerceAtLeast(0L),
        remainingMillis = remainingMillis.coerceAtLeast(0L),
        fadeOutMillis = fadeOutMillis.coerceAtLeast(0L),
        endBehavior = EndBehavior.entries.firstOrNull { it.name == endBehavior } ?: EndBehavior.StopPlayback,
        startedAtEpochMillis = startedAtEpochMillis,
    )

private fun UserSettings.toPersisted(): PersistedSettings =
    PersistedSettings(
        themeId = themeId,
        audioQualityId = audioQualityId,
        startLastMix = startLastMix,
        offlineDownloads = offlineDownloads,
        hapticsEnabled = hapticsEnabled,
    )

private fun PersistedSettings.toDomain(): UserSettings =
    UserSettings(
        themeId = themeId,
        audioQualityId = audioQualityId,
        startLastMix = startLastMix,
        offlineDownloads = offlineDownloads,
        hapticsEnabled = hapticsEnabled,
    )
