package com.reinekes.darkapp.brightness

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BrightnessBoostTest {
    @Test
    fun autoDoesNotRequestManualBrightness() {
        assertNull(BrightnessBoost.Auto.screenBrightnessValue)
    }

    @Test
    fun manualLevelsMapToAndroidBrightnessRange() {
        assertEquals(64, BrightnessBoost.Percent25.screenBrightnessValue)
        assertEquals(128, BrightnessBoost.Percent50.screenBrightnessValue)
        assertEquals(191, BrightnessBoost.Percent75.screenBrightnessValue)
        assertEquals(255, BrightnessBoost.Percent100.screenBrightnessValue)
    }

    @Test
    fun labelsAreCompactForControlChips() {
        assertEquals("Auto", BrightnessBoost.Auto.label)
        assertEquals("100%", BrightnessBoost.Percent100.label)
    }
}
