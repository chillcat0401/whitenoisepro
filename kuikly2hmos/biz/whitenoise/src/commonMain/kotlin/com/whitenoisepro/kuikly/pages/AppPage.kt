package com.whitenoisepro.kuikly.pages

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.annotations.Page

/**
 * WhiteNoisePro 鸿蒙复刻的根页面(M0 骨架)。
 *
 * 移植时此页将承载 AppShell(五 Tab + 迷你播放器),结构对照
 * 主仓 composeApp/src/commonMain/kotlin/com/whitenoisepro/app/AppShell.kt。
 * Kuikly Compose 与官方 Compose API 基本一致,主要差异是 import 包名
 * (androidx.compose.* → com.tencent.kuikly.compose.*)。
 */
@Page("WhiteNoiseApp")
class AppPage : ComposeContainer() {
    override fun willInit() {
        super.willInit()
        setContent {
            AppRoot()
        }
    }
}

@Composable
private fun AppRoot() {
    // M0 验收画面:深色底 + 品牌字,证明 biz 模块 → libshared.so → ohosApp 链路打通
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF151411)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("白噪声 Pro", color = Color(0xFF9AD6C5), fontSize = 28.sp)
        Text("Kuikly · HarmonyOS 复刻骨架", color = Color(0xFFC9C0B2), fontSize = 14.sp)
    }
}
