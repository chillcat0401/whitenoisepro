package com.whitenoisepro.design

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitenoisepro.generated.resources.Res
import com.whitenoisepro.generated.resources.lora_medium
import com.whitenoisepro.generated.resources.lora_semibold
import com.whitenoisepro.generated.resources.lxgw_wenkai_subset
import org.jetbrains.compose.resources.Font

object WnpColors {
    val Background = Color(0xFF151411)
    val SurfaceLow = Color(0xFF1D1B17)
    val Surface = Color(0xFF25231D)
    val SurfaceHigh = Color(0xFF302D25)
    val SurfaceVariant = Color(0xFF3C382F)
    val OnSurface = Color(0xFFF1ECE2)
    val OnSurfaceVariant = Color(0xFFC9C0B2)
    val Primary = Color(0xFF9AD6C5)
    val Secondary = Color(0xFFD7B56D)
    val Tertiary = Color(0xFFC7D08B)
    val Danger = Color(0xFFFF8A80)
    val IconMuted = Color(0xFF948B7C)
    val Outline = Color(0xFF4B4538)
}

object WnpSpacing {
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val ScreenHorizontal = 22.dp
    val ScreenTop = 36.dp
    val PageGap = 28.dp
    val SectionGap = 22.dp
    val CardPadding = 20.dp
    val HeroPadding = 32.dp
    val BottomBreathingRoom = 32.dp
    val ScreenBottomWithPlayer = 194.dp
}

enum class AtmosphericLightPlacement {
    Top,
    Center,
    Bottom,
}

data class AtmosphericLightLayer(
    val placement: AtmosphericLightPlacement,
    val color: Color,
    val alpha: Float,
)

object WnpAtmosphericBackground {
    val baseColor = WnpColors.Background
    val layers = listOf(
        AtmosphericLightLayer(AtmosphericLightPlacement.Top, Color(0xFF244252), 0.16f),
        AtmosphericLightLayer(AtmosphericLightPlacement.Top, WnpColors.Primary, 0.08f),
        AtmosphericLightLayer(AtmosphericLightPlacement.Center, Color(0xFF1F3442), 0.08f),
        AtmosphericLightLayer(AtmosphericLightPlacement.Bottom, WnpColors.Secondary, 0.05f),
    )
}

object WnpRadius {
    val Chip = 999.dp
    val Button = 16.dp
    val Card = 24.dp
    val Field = 16.dp
    val Sheet = 28.dp
}

object WnpMotion {
    const val BreathDurationMillis = 3200
    const val HaloMinAlpha = 0.10f
    const val HaloMaxAlpha = 0.28f
}

object WnpDimens {
    val MinTouchTarget = 44.dp
    val BottomNavHeight = 72.dp
    val MiniPlayerHeight = 80.dp
    val BottomChromeGap = 10.dp
}

/**
 * 展示字族由主题在启动时注入(Lora 衬线,Claude 风格的开源近似);
 * 拉丁字母与数字走衬线,中文字形自动回退系统字体。正文保持系统无衬线。
 */
object WnpFonts {
    var display: FontFamily = FontFamily.Serif
}

object WnpTypography {
    val DisplayLarge: TextStyle
        get() = TextStyle(
            fontFamily = WnpFonts.display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 34.sp,
            lineHeight = 42.sp,
        )
    val Display: TextStyle
        get() = TextStyle(
            fontFamily = WnpFonts.display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
        )
    val Title: TextStyle
        get() = TextStyle(
            fontFamily = WnpFonts.display,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 26.sp,
        )
    val Body = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    )
    val Label = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp,
    )
}

val WnpColorScheme: ColorScheme = darkColorScheme(
    primary = WnpColors.Primary,
    secondary = WnpColors.Secondary,
    tertiary = WnpColors.Tertiary,
    background = WnpColors.Background,
    surface = WnpColors.Surface,
    surfaceVariant = WnpColors.SurfaceVariant,
    onPrimary = WnpColors.Background,
    onSecondary = WnpColors.Background,
    onTertiary = WnpColors.Background,
    onBackground = WnpColors.OnSurface,
    onSurface = WnpColors.OnSurface,
    onSurfaceVariant = WnpColors.OnSurfaceVariant,
    error = WnpColors.Danger,
)

@Composable
fun WhiteNoiseTheme(content: @Composable () -> Unit) {
    // 回退链:Lora 管拉丁与数字;霞鹜文楷(GB2312 一级字子集,已剔除拉丁字形)
    // 管中文;子集外生僻字回退系统字体。
    WnpFonts.display = FontFamily(
        Font(Res.font.lora_medium, weight = FontWeight.Medium),
        Font(Res.font.lora_semibold, weight = FontWeight.SemiBold),
        Font(Res.font.lxgw_wenkai_subset, weight = FontWeight.Medium),
        Font(Res.font.lxgw_wenkai_subset, weight = FontWeight.SemiBold),
    )
    MaterialTheme(
        colorScheme = WnpColorScheme,
        content = content,
    )
}
