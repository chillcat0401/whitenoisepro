package com.whitenoisepro.audio

internal enum class AudioFocusGateAction {
    StartLayers,
    AwaitAuthorization,
    PauseLayers,
}

/**
 * 声层启动门控:
 * - 应用内 play 通过 [requestPlay] 授权,UI 暂停通过 [cancelPlay] 撤销;
 * - 撤销后迟到的 stale StartLayers 回调被拒绝(防止 UI 暂停被竞态覆盖);
 * - 当 controller 已被观察到「稳定暂停」(PauseLayers 落地)之后,再出现的
 *   播放决策只可能来自用户的外部控制(通知栏/锁屏/蓝牙按键),视为新的
 *   播放意图并重新授权——否则通知栏暂停后将永远无法恢复播放。
 */
internal class AudioFocusPlaybackGate {
    var playRequested: Boolean = false
        private set

    private var controllerSettledPaused: Boolean = false

    fun requestPlay() {
        playRequested = true
    }

    fun cancelPlay() {
        playRequested = false
        controllerSettledPaused = false
    }

    fun apply(decision: ControllerPlaybackDecision): AudioFocusGateAction =
        when (decision) {
            ControllerPlaybackDecision.StartLayers -> {
                if (playRequested || controllerSettledPaused) {
                    playRequested = true
                    controllerSettledPaused = false
                    AudioFocusGateAction.StartLayers
                } else {
                    AudioFocusGateAction.PauseLayers
                }
            }
            ControllerPlaybackDecision.AwaitAuthorization -> {
                if (playRequested || controllerSettledPaused) {
                    playRequested = true
                    controllerSettledPaused = false
                    AudioFocusGateAction.AwaitAuthorization
                } else {
                    AudioFocusGateAction.PauseLayers
                }
            }
            ControllerPlaybackDecision.PauseLayers -> {
                playRequested = false
                controllerSettledPaused = true
                AudioFocusGateAction.PauseLayers
            }
        }
}
