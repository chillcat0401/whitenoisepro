package com.whitenoisepro.domain.model

enum class SoundCategory(val displayName: String) {
    Nature("自然"),
    Noise("噪声"),
    Home("居家"),
    Ambience("环境"),
    Music("旋律"),
    Meditation("冥想"),
}

data class Sound(
    val id: String,
    val name: String,
    val category: SoundCategory,
    val description: String = "",
    val iconKey: String = id,
    val loopAssetKey: String = id,
    val defaultVolume: Float = SoundLayer.DefaultVolume,
    val isPremium: Boolean = false,
) {
    init {
        require(id.isNotBlank()) { "Sound id must be stable and non-blank." }
        require(name.isNotBlank()) { "Sound name must be non-blank." }
        require(loopAssetKey.isNotBlank()) { "Sound loop asset key must be non-blank." }
        require(defaultVolume in 0f..1f) { "Sound default volume must be in 0..1." }
    }
}

data class SoundLayer(
    val id: String,
    val soundId: String,
    val volume: Float = DefaultVolume,
    val isMuted: Boolean = false,
) {
    init {
        require(id.isNotBlank()) { "Layer id must be stable and non-blank." }
        require(soundId.isNotBlank()) { "Layer sound id must be non-blank." }
        require(volume in 0f..1f) { "Layer volume must be in 0..1." }
    }

    companion object {
        const val DefaultVolume = 0.65f

        fun create(
            id: String,
            soundId: String,
            volume: Float = DefaultVolume,
            isMuted: Boolean = false,
        ): SoundLayer = SoundLayer(
            id = id,
            soundId = soundId,
            volume = volume.coerceIn(0f, 1f),
            isMuted = isMuted,
        )
    }
}

data class SoundMix(
    val id: String,
    val title: String,
    val layers: List<SoundLayer> = emptyList(),
    val masterVolume: Float = 0.75f,
    val isFavorite: Boolean = false,
    val updatedAtEpochMillis: Long = 0L,
) {
    init {
        require(id.isNotBlank()) { "Mix id must be stable and non-blank." }
        require(title.isNotBlank()) { "Mix title must be non-blank." }
        require(masterVolume in 0f..1f) { "Master volume must be in 0..1." }
        require(layers.map { it.id }.toSet().size == layers.size) {
            "Mix layers must not contain duplicate ids."
        }
    }

    val activeLayers: List<SoundLayer>
        get() = layers.filterNot { it.isMuted }
}

enum class PlaybackStatus {
    Idle,
    Playing,
    Paused,
    Buffering,
    Error,
}

data class PlaybackState(
    val status: PlaybackStatus = PlaybackStatus.Idle,
    val currentMixId: String? = null,
    val masterVolume: Float = 0.75f,
    val timerFadeFactor: Float = 1f,
    val errorMessage: String? = null,
) {
    init {
        require(masterVolume in 0f..1f) { "Master volume must be in 0..1." }
        require(timerFadeFactor in 0f..1f) { "Timer fade factor must be in 0..1." }
    }

    val isPlaying: Boolean
        get() = status == PlaybackStatus.Playing
}

enum class EndBehavior {
    StopPlayback,
    KeepPlaying,
}

data class SleepTimerState(
    val durationMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val fadeOutMillis: Long = 5 * 60 * 1000L,
    val endBehavior: EndBehavior = EndBehavior.StopPlayback,
    val startedAtEpochMillis: Long? = null,
) {
    init {
        require(durationMillis >= 0L) { "Timer duration must be non-negative." }
        require(remainingMillis >= 0L) { "Timer remaining duration must be non-negative." }
        require(fadeOutMillis >= 0L) { "Fade-out duration must be non-negative." }
    }

    val isActive: Boolean
        get() = startedAtEpochMillis != null && remainingMillis > 0L
}

data class UserSettings(
    val themeId: String = "system",
    val audioQualityId: String = "standard",
    val startLastMix: Boolean = true,
    val offlineDownloads: Boolean = false,
    val hapticsEnabled: Boolean = true,
)
