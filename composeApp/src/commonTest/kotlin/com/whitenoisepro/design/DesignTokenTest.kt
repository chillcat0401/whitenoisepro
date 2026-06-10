package com.whitenoisepro.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DesignTokenTest {
    @Test
    fun colorsMatchChineseVisualBaseline() {
        assertEquals(Color(0xFF151411), WnpColors.Background)
        assertEquals(Color(0xFF25231D), WnpColors.Surface)
        assertEquals(Color(0xFF9AD6C5), WnpColors.Primary)
        assertEquals(Color(0xFFF1ECE2), WnpColors.OnSurface)
        assertNotEquals(Color.White, WnpColors.Background)
    }

    @Test
    fun iconAndOutlineTokensExist() {
        assertEquals(Color(0xFF948B7C), WnpColors.IconMuted)
        assertEquals(Color(0xFF4B4538), WnpColors.Outline)
    }

    @Test
    fun touchTargetMeetsMobileMinimum() {
        assertEquals(44.dp, WnpDimens.MinTouchTarget)
    }

    @Test
    fun typographyKeepsCalmDisplayHierarchy() {
        assertTrue(WnpTypography.DisplayLarge.fontSize.value > WnpTypography.Display.fontSize.value)
        assertEquals(34f, WnpTypography.DisplayLarge.fontSize.value)
    }

    @Test
    fun breathingHaloStaysSubtle() {
        assertTrue(WnpMotion.HaloMaxAlpha <= 0.3f)
        assertTrue(WnpMotion.HaloMinAlpha < WnpMotion.HaloMaxAlpha)
        assertTrue(WnpMotion.BreathDurationMillis >= 2000)
    }

    @Test
    fun radiusAndSpacingTokensExist() {
        assertEquals(24.dp, WnpRadius.Card)
        assertEquals(16.dp, WnpRadius.Button)
        assertEquals(16.dp, WnpRadius.Field)
        assertEquals(22.dp, WnpSpacing.ScreenHorizontal)
        assertEquals(36.dp, WnpSpacing.ScreenTop)
        assertEquals(28.dp, WnpSpacing.PageGap)
        assertEquals(22.dp, WnpSpacing.SectionGap)
        assertEquals(20.dp, WnpSpacing.CardPadding)
        assertEquals(32.dp, WnpSpacing.HeroPadding)
    }

    @Test
    fun atmosphericBackgroundUsesSubtleLayeredLight() {
        assertEquals(4, WnpAtmosphericBackground.layers.size)
        assertEquals(WnpColors.Background, WnpAtmosphericBackground.baseColor)
        assertTrue(WnpAtmosphericBackground.layers.all { it.alpha in 0.04f..0.18f })
        assertTrue(WnpAtmosphericBackground.layers.any { it.placement == AtmosphericLightPlacement.Top })
        assertTrue(WnpAtmosphericBackground.layers.any { it.placement == AtmosphericLightPlacement.Center })
    }

    @Test
    fun bottomChromeUsesRelaxedHeights() {
        assertEquals(80.dp, WnpDimens.MiniPlayerHeight)
        assertEquals(72.dp, WnpDimens.BottomNavHeight)
        assertEquals(10.dp, WnpDimens.BottomChromeGap)
        assertEquals(
            WnpDimens.BottomNavHeight +
                WnpDimens.BottomChromeGap +
                WnpDimens.MiniPlayerHeight +
                WnpSpacing.BottomBreathingRoom,
            WnpSpacing.ScreenBottomWithPlayer,
        )
    }

    @Test
    fun promotedExternalSoundIconsUseFamiliarFamilies() {
        assertEquals(AppIconKind.Rain, soundIconKind("rain_soft"))
        assertEquals(AppIconKind.Rain, soundIconKind("rain_window"))
        assertEquals(AppIconKind.Ocean, soundIconKind("ocean_gentle"))
        assertEquals(AppIconKind.Fireplace, soundIconKind("fire_crackle"))
        assertEquals(AppIconKind.Fan, soundIconKind("fan_floor"))
        assertEquals(AppIconKind.Forest, soundIconKind("wind_forest"))
        assertEquals(AppIconKind.Noise, soundIconKind("noise_custom_t30"))
    }
}
