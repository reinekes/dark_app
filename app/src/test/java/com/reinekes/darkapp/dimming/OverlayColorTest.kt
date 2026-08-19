package com.reinekes.darkapp.dimming

import org.junit.Assert.assertEquals
import org.junit.Test

class OverlayColorTest {
    @Test
    fun blackFilterKeepsRgbChannelsBlack() {
        val color = OverlayColor.argb(percent = 100, filter = DimFilter.Neutral)

        assertEquals(0xF5000000.toInt(), color)
    }

    @Test
    fun warmFilterAddsAmberToneWithoutChangingAlphaCurve() {
        val color = OverlayColor.argb(percent = 100, filter = DimFilter.Warm)

        assertEquals(0xF5120800.toInt(), color)
    }

    @Test
    fun redFilterKeepsSleepModeColorDimAndWarm() {
        val color = OverlayColor.argb(percent = 100, filter = DimFilter.Red)

        assertEquals(0xF51F0000.toInt(), color)
    }

    @Test
    fun alphaOverrideCanMakeBlackoutDarkerThanTheNormalSliderMaximum() {
        val color = OverlayColor.argb(
            percent = 100,
            filter = DimFilter.Neutral,
            alphaOverride = 0.99f,
        )

        assertEquals(0xFC000000.toInt(), color)
    }
}
