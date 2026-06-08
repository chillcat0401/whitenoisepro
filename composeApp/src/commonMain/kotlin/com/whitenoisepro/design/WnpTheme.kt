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

object WnpColors {
    val Background = Color(0xFF0D1117)
    val SurfaceLow = Color(0xFF151B23)
    val Surface = Color(0xFF1B222B)
    val SurfaceHigh = Color(0xFF25303A)
    val SurfaceVariant = Color(0xFF34414A)
    val OnSurface = Color(0xFFE2E2E8)
    val OnSurfaceVariant = Color(0xFFBEC9C7)
    val Primary = Color(0xFF8AD3CE)
    val Secondary = Color(0xFFA9C7E8)
    val Tertiary = Color(0xFFFFC178)
    val Danger = Color(0xFFFF8A80)
    val IconMuted = Color(0xFF7F9095)
    val Outline = Color(0xFF2E3A42)
}

object WnpSpacing {
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val ScreenHorizontal = 20.dp
    val ScreenTop = 24.dp
    val PageGap = 24.dp
    val SectionGap = 20.dp
    val CardPadding = 18.dp
    val HeroPadding = 30.dp
    val BottomBreathingRoom = 32.dp
    val ScreenBottomWithPlayer = 184.dp
}

object WnpRadius {
    val Chip = 999.dp
    val Button = 8.dp
    val Card = 8.dp
    val Sheet = 24.dp
}

object WnpDimens {
    val MinTouchTarget = 44.dp
    val BottomNavHeight = 68.dp
    val MiniPlayerHeight = 76.dp
    val BottomChromeGap = 8.dp
}

object WnpTypography {
    val Display = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    )
    val Title = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    )
    val Body = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    )
    val Label = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
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
    MaterialTheme(
        colorScheme = WnpColorScheme,
        content = content,
    )
}
