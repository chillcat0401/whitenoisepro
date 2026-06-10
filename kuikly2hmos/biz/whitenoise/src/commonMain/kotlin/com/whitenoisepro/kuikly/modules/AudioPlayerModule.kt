package com.whitenoisepro.kuikly.modules

import com.tencent.kuikly.core.module.Module

/**
 * 多层环境音播放桥(common 侧定义,M2 实现)。
 *
 * 接口语义 1:1 对照主仓 PlaybackEngine
 * (composeApp/src/commonMain/kotlin/com/whitenoisepro/playback/PlaybackEngine.kt):
 * 鸿蒙侧用 N 个 AVPlayer 实例(每声层一个,loop=true)+ AVSession 媒体控制
 * + backgroundTasks 长时任务(AUDIO_PLAYBACK)实现,见 REPLICATION_PLAN.md M2。
 *
 * 注意:每层音量 = layerVolume × masterVolume × timerFadeFactor 的乘法在
 * Kotlin 共享层完成(沿用主仓 AndroidPlaybackEngine 的 effectiveVolume 逻辑),
 * 桥只接受最终音量,保持原生侧无状态、可替换。
 */
class AudioPlayerModule : Module() {

    override fun moduleName(): String = MODULE_NAME

    /** layerId → soundSpec(资源文件路径或 synth 缓存文件路径),开始/更新整组播放 */
    fun playLayers(layersJson: String) {
        toNative(false, "playLayers", layersJson, null, false)
    }

    fun pauseAll() {
        toNative(false, "pauseAll", null, null, false)
    }

    fun stopAll() {
        toNative(false, "stopAll", null, null, false)
    }

    /** 设置单层最终音量(0~1,已含主音量与淡出系数) */
    fun setLayerVolume(layerId: String, volume: Float) {
        toNative(false, "setLayerVolume", "$layerId|$volume", null, false)
    }

    fun setLayerMuted(layerId: String, muted: Boolean) {
        toNative(false, "setLayerMuted", "$layerId|$muted", null, false)
    }

    /** 更新 AVSession 元数据(标题/播放态),驱动通知与锁屏控制 */
    fun updateNowPlaying(title: String, playing: Boolean) {
        toNative(false, "updateNowPlaying", "$title|$playing", null, false)
    }

    companion object {
        const val MODULE_NAME = "WNPAudioPlayerModule"
    }
}
