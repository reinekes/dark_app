package com.reinekes.darkapp.dimming

import org.junit.Assert.assertEquals
import org.junit.Test

class DimPresetTest {
    @Test
    fun presetsUseComfortableIncreasingDimLevels() {
        assertEquals(22, DimPreset.Reading.percent)
        assertEquals(48, DimPreset.Night.percent)
        assertEquals(82, DimPreset.Ultra.percent)
        assertEquals(96, DimPreset.Deep.percent)
        assertEquals(100, DimPreset.Blackout.percent)
    }

    @Test
    fun presetLabelsAreShortForCompactButtons() {
        assertEquals("Reading", DimPreset.Reading.label)
        assertEquals("Night", DimPreset.Night.label)
        assertEquals("Ultra", DimPreset.Ultra.label)
        assertEquals("Deep", DimPreset.Deep.label)
        assertEquals("Blackout", DimPreset.Blackout.label)
    }

    @Test
    fun blackoutUsesDedicatedNearlyBlackAlphaWithoutChangingTheNormalSliderMaximum() {
        assertEquals(0.99f, DimPreset.Blackout.alphaOverride!!, 0.0001f)
        assertEquals(null, DimPreset.Deep.alphaOverride)
    }
}
