package com.reinekes.darkapp.dimming

import org.junit.Assert.assertEquals
import org.junit.Test

class DimMathTest {
    @Test
    fun alphaCurveIsGentleAtLowValuesAndStrongAtHighValues() {
        assertEquals(0f, DimMath.alphaForPercent(0), 0.0001f)
        assertEquals(0.047f, DimMath.alphaForPercent(10), 0.012f)
        assertEquals(0.96f, DimMath.alphaForPercent(100), 0.0001f)
    }

    @Test
    fun alphaInputIsClampedToSafeBounds() {
        assertEquals(0f, DimMath.alphaForPercent(-40), 0.0001f)
        assertEquals(0.96f, DimMath.alphaForPercent(180), 0.0001f)
    }
}
