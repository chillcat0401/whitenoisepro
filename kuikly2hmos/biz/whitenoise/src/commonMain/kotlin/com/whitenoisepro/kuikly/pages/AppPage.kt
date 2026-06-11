package com.whitenoisepro.kuikly.pages

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.Module
import com.whitenoisepro.kuikly.modules.AudioPlayerModule
import com.whitenoisepro.kuikly.modules.StorageModule

/**
 * WhiteNoisePro 鸿蒙复刻的根页面。
 *
 * M2.1 阶段:骨架画面 + 音频桥实测按钮(双层混音:软雨 + 柔和海浪)。
 * M3 将替换为 AppShell 五 Tab 结构(对照主仓 app/AppShell.kt)。
 */
@Page("WhiteNoiseApp")
class AppPage : ComposeContainer() {

    override fun createExternalModules(): Map<String, Module>? =
        hashMapOf<String, Module>(
            AudioPlayerModule.MODULE_NAME to AudioPlayerModule(),
            StorageModule.MODULE_NAME to StorageModule(),
        )

    override fun willInit() {
        super.willInit()
        setContent {
            AppRoot(
                onPlayTestMix = ::playTestMix,
                onStop = { audioModule().stopAll() },
            )
        }
    }

    private fun audioModule(): AudioPlayerModule =
        acquireModule(AudioPlayerModule.MODULE_NAME)

    private fun playTestMix() {
        audioModule().playLayers(
            """[
              {"layerId":"l-rain","source":"rawfile:rain_soft_loop.ogg","volume":0.6,"muted":false},
              {"layerId":"l-ocean","source":"rawfile:ocean_gentle_loop.ogg","volume":0.4,"muted":false}
            ]""".trimIndent(),
        )
        audioModule().updateNowPlaying(title = "软雨 · 柔和海浪", playing = true)
    }
}

@Composable
private fun AppRoot(
    onPlayTestMix: () -> Unit,
    onStop: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF151411)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("白噪屿", color = Color(0xFF9AD6C5), fontSize = 28.sp)
        Text("Kuikly · HarmonyOS 复刻骨架", color = Color(0xFFC9C0B2), fontSize = 14.sp)

        Text(
            "▶ 播放测试混音(软雨+海浪)",
            color = Color(0xFF151411),
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF9AD6C5))
                .clickable { onPlayTestMix() }
                .padding(horizontal = 24.dp, vertical = 12.dp),
        )
        Text(
            "■ 停止",
            color = Color(0xFFC9C0B2),
            fontSize = 14.sp,
            modifier = Modifier
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2A2722))
                .clickable { onStop() }
                .padding(horizontal = 24.dp, vertical = 10.dp),
        )
    }
}
