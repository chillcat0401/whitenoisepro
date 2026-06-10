package com.whitenoisepro.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.whitenoisepro.design.AtmosphericLightPlacement
import com.whitenoisepro.design.AppIcon
import com.whitenoisepro.design.AppIconKind
import com.whitenoisepro.design.IconButton
import com.whitenoisepro.design.SoundIcon
import com.whitenoisepro.design.VolumeSlider
import com.whitenoisepro.design.WnpAtmosphericBackground
import com.whitenoisepro.design.WnpColors
import com.whitenoisepro.design.WnpDimens
import com.whitenoisepro.design.WnpRadius
import com.whitenoisepro.design.WnpSpacing
import com.whitenoisepro.design.WnpTypography
import com.whitenoisepro.presentation.AppState

enum class AppTab(val title: String) {
    Home("首页"),
    Mixer("混音"),
    Library("声音"),
    Timer("定时"),
    Saved("已保存"),
    Settings("设置"),
    ;

    companion object {
        val bottomNavTabs: List<AppTab> = listOf(Home, Mixer, Library, Timer, Saved)
    }
}

fun miniPlayerSubtitle(state: AppState): String =
    if (state.timerState.isActive) {
        val remainingMinutes = (state.timerState.remainingMillis + 59_999L) / 60_000L
        "剩余 $remainingMinutes 分钟"
    } else {
        "${state.mixState.currentMix.layers.size} 个声音层"
    }

data class ScaffoldContentPadding(
    val start: androidx.compose.ui.unit.Dp,
    val top: androidx.compose.ui.unit.Dp,
    val end: androidx.compose.ui.unit.Dp,
    val bottom: androidx.compose.ui.unit.Dp,
    val bottomChromeInset: androidx.compose.ui.unit.Dp,
)

fun scaffoldContentPadding(): ScaffoldContentPadding =
    ScaffoldContentPadding(
        start = WnpSpacing.ScreenHorizontal,
        top = WnpSpacing.ScreenTop,
        end = WnpSpacing.ScreenHorizontal,
        bottom = WnpSpacing.BottomBreathingRoom,
        bottomChromeInset = WnpDimens.BottomNavHeight +
            WnpDimens.BottomChromeGap +
            WnpDimens.MiniPlayerHeight,
    )

@Composable
fun AppScaffold(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    miniPlayerTitle: String,
    miniPlayerSubtitle: String,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .atmosphericAppBackground(),
    ) {
        val scaffoldPadding = scaffoldContentPadding()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = scaffoldPadding.bottomChromeInset),
        ) {
            content(
                PaddingValues(
                    start = scaffoldPadding.start,
                    top = scaffoldPadding.top,
                    end = scaffoldPadding.end,
                    bottom = scaffoldPadding.bottom,
                ),
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            MiniPlayer(
                title = miniPlayerTitle,
                subtitle = miniPlayerSubtitle,
                isPlaying = isPlaying,
                onPlayPause = onPlayPause,
                modifier = Modifier.padding(horizontal = WnpSpacing.ScreenHorizontal),
            )
            Spacer(Modifier.height(WnpDimens.BottomChromeGap))
            BottomNav(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
            )
        }
    }
}

private fun Modifier.atmosphericAppBackground(): Modifier =
    background(WnpAtmosphericBackground.baseColor)
        .drawBehind {
            WnpAtmosphericBackground.layers.forEach { layer ->
                val center = when (layer.placement) {
                    AtmosphericLightPlacement.Top -> Offset(size.width * 0.52f, size.height * 0.06f)
                    AtmosphericLightPlacement.Center -> Offset(size.width * 0.46f, size.height * 0.38f)
                    AtmosphericLightPlacement.Bottom -> Offset(size.width * 0.58f, size.height * 0.92f)
                }
                val radius = when (layer.placement) {
                    AtmosphericLightPlacement.Top -> size.maxDimension * 0.54f
                    AtmosphericLightPlacement.Center -> size.maxDimension * 0.34f
                    AtmosphericLightPlacement.Bottom -> size.maxDimension * 0.36f
                }
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            layer.color.copy(alpha = layer.alpha),
                            layer.color.copy(alpha = 0f),
                        ),
                        center = center,
                        radius = radius,
                    ),
                )
            }
            drawRect(
                brush = Brush.verticalGradient(
                    0f to WnpColors.SurfaceHigh.copy(alpha = 0.07f),
                    0.34f to WnpColors.Background.copy(alpha = 0f),
                    1f to WnpColors.SurfaceLow.copy(alpha = 0.12f),
                ),
            )
        }

@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = WnpColors.OnSurface,
            style = WnpTypography.DisplayLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (action != null) {
            Spacer(Modifier.size(WnpSpacing.Md))
            action()
        }
    }
}

@Composable
fun MiniPlayer(
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(WnpDimens.MiniPlayerHeight)
            .clip(RoundedCornerShape(WnpRadius.Card))
            .background(WnpColors.SurfaceHigh)
            .padding(horizontal = WnpSpacing.Lg, vertical = WnpSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoundIcon(label = title, active = isPlaying, modifier = Modifier.size(40.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = WnpSpacing.Lg),
        ) {
            Text(
                text = title,
                color = WnpColors.OnSurface,
                style = WnpTypography.Body,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                color = WnpColors.OnSurfaceVariant,
                style = WnpTypography.Label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onPlayPause, modifier = Modifier.size(WnpDimens.MinTouchTarget)) {
            AppIcon(
                kind = if (isPlaying) AppIconKind.Pause else AppIconKind.Play,
                tint = WnpColors.OnSurface,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
fun BottomNav(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(WnpDimens.BottomNavHeight)
            .background(WnpColors.SurfaceLow)
            .padding(horizontal = WnpSpacing.Sm),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppTab.bottomNavTabs.forEach { tab ->
            val selected = tab == selectedTab
            Column(
                modifier = Modifier
                    .heightIn(min = WnpDimens.MinTouchTarget)
                    .weight(1f)
                    .clip(RoundedCornerShape(WnpRadius.Button))
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = WnpSpacing.Sm),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AppIcon(
                    kind = tab.iconKind(),
                    tint = if (selected) WnpColors.Primary else WnpColors.IconMuted,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = tab.title,
                    color = if (selected) WnpColors.Primary else WnpColors.OnSurfaceVariant,
                    style = WnpTypography.Label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun AppTab.iconKind(): AppIconKind = when (this) {
    AppTab.Home -> AppIconKind.Home
    AppTab.Mixer -> AppIconKind.Mixer
    AppTab.Library -> AppIconKind.Library
    AppTab.Timer -> AppIconKind.Timer
    AppTab.Saved -> AppIconKind.Saved
    AppTab.Settings -> AppIconKind.Settings
}

@Composable
fun MasterVolumeRow(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "音量", color = WnpColors.OnSurfaceVariant, style = WnpTypography.Label)
        VolumeSlider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
        )
    }
}
