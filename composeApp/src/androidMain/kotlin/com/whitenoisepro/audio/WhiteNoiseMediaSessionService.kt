package com.whitenoisepro.audio

import android.content.Context
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.whitenoisepro.R
import com.whitenoisepro.domain.model.SleepTimerState
import com.whitenoisepro.playback.SleepTimerDeadlineRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class WhiteNoiseMediaSessionService : MediaSessionService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var playbackEngine: AndroidPlaybackEngine
    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var updatingFromEngine = false
    private lateinit var timerRunner: SleepTimerDeadlineRunner

    override fun onCreate() {
        super.onCreate()
        activeService = this
        playbackEngine = AndroidPlaybackEngineProvider.get(applicationContext)
        timerRunner = SleepTimerDeadlineRunner(
            scope = serviceScope,
            nowEpochMillis = System::currentTimeMillis,
            onFadeFactor = playbackEngine::setTimerFadeFactor,
            onStopPlayback = playbackEngine::stop,
            onCompleted = { pendingTimer = null },
        )
        val exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(
                MediaSessionAudioPolicy.audioAttributes,
                MediaSessionAudioPolicy.controllerHandlesAudioFocus,
            )
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = MediaSessionAudioPolicy.controllerPlayerVolume
                setHandleAudioBecomingNoisy(MediaSessionAudioPolicy.controllerHandlesAudioBecomingNoisy)
                addListener(
                    object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            syncControllerState()
                        }

                        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                            syncControllerState()
                        }

                        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
                            syncControllerState()
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            syncControllerState()
                        }
                    },
                )
            }
        player = exoPlayer
        // 必须显式 addSession:本应用 UI 直接驱动播放引擎,没有 MediaController
        // 连接来触发 onGetSession 的自动注册;不注册则 Media3 不会发媒体通知、
        // 不会把服务升为前台,熄屏后进程会被系统冻结。
        mediaSession = MediaSession.Builder(this, exoPlayer).build().also(::addSession)
        pendingNowPlaying?.let { updateSession(title = it.title, playing = it.playing) }
        pendingTimer?.let(timerRunner::schedule)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        timerRunner.cancel()
        serviceScope.cancel()
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        player = null
        if (activeService === this) {
            activeService = null
        }
        super.onDestroy()
    }

    private fun updateSession(title: String, playing: Boolean) {
        val exoPlayer = player ?: return
        updatingFromEngine = true
        try {
            if (exoPlayer.mediaItemCount == 0) {
                exoPlayer.setMediaItem(
                    MediaItem.Builder()
                        .setUri(AndroidSoundResourceResolver.uri(this, DefaultSessionSoundId))
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(title)
                                .setArtist(getString(R.string.app_name))
                                .build(),
                        )
                        .build(),
                )
                exoPlayer.prepare()
            }
            if (playing) {
                exoPlayer.play()
            } else {
                exoPlayer.pause()
            }
        } finally {
            updatingFromEngine = false
        }
        syncControllerState()
    }

    private fun stopSession() {
        val exoPlayer = player ?: return
        updatingFromEngine = true
        try {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        } finally {
            updatingFromEngine = false
        }
    }

    private fun syncControllerState() {
        if (updatingFromEngine) return
        val exoPlayer = player ?: return
        playbackEngine.syncFromMediaSession(
            isPlaying = exoPlayer.isPlaying,
            playWhenReady = exoPlayer.playWhenReady,
        )
    }

    companion object {
        private var activeService: WhiteNoiseMediaSessionService? = null
        private var pendingNowPlaying: NowPlaying? = null
        private var pendingTimer: SleepTimerState? = null

        fun ensureStarted(context: Context) {
            context.applicationContext.startService(
                Intent(context.applicationContext, WhiteNoiseMediaSessionService::class.java),
            )
        }

        fun scheduleTimer(context: Context, timer: SleepTimerState) {
            if (!timer.isActive) {
                cancelTimer()
                return
            }
            pendingTimer = timer
            activeService?.timerRunner?.schedule(timer) ?: ensureStarted(context)
        }

        fun cancelTimer() {
            pendingTimer = null
            activeService?.timerRunner?.cancel()
        }

        fun setNowPlaying(title: String, playing: Boolean) {
            pendingNowPlaying = NowPlaying(title = title, playing = playing)
            activeService?.updateSession(title = title, playing = playing)
        }

        fun clearNowPlaying() {
            pendingNowPlaying = null
            activeService?.stopSession()
        }

        private const val DefaultSessionSoundId = "brown_noise"
    }
}

private data class NowPlaying(
    val title: String,
    val playing: Boolean,
)
