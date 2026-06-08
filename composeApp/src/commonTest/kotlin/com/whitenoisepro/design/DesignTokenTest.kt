package com.whitenoisepro.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DesignTokenTest {
    @Test
    fun colorsMatchChineseVisualBaseline() {
        assertEquals(Color(0xFF0D1117), WnpColors.Background)
        assertEquals(Color(0xFF1B222B), WnpColors.Surface)
        assertEquals(Color(0xFF8AD3CE), WnpColors.Primary)
        assertEquals(Color(0xFFE2E2E8), WnpColors.OnSurface)
        assertNotEquals(Color.White, WnpColors.Background)
    }

    @Test
    fun iconAndOutlineTokensExist() {
        assertEquals(Color(0xFF7F9095), WnpColors.IconMuted)
        assertEquals(Color(0xFF2E3A42), WnpColors.Outline)
    }

    @Test
    fun touchTargetMeetsMobileMinimum() {
        assertEquals(44.dp, WnpDimens.MinTouchTarget)
    }

    @Test
    fun radiusAndSpacingTokensExist() {
        assertEquals(8.dp, WnpRadius.Card)
        assertEquals(20.dp, WnpSpacing.ScreenHorizontal)
        assertEquals(24.dp, WnpSpacing.ScreenTop)
        assertEquals(24.dp, WnpSpacing.PageGap)
        assertEquals(20.dp, WnpSpacing.SectionGap)
        assertEquals(18.dp, WnpSpacing.CardPadding)
        assertEquals(30.dp, WnpSpacing.HeroPadding)
    }

    @Test
    fun bottomChromeUsesRelaxedHeights() {
        assertEquals(76.dp, WnpDimens.MiniPlayerHeight)
        assertEquals(68.dp, WnpDimens.BottomNavHeight)
        assertEquals(8.dp, WnpDimens.BottomChromeGap)
    }
}
