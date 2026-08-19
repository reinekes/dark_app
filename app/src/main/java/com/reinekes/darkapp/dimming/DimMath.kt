package com.reinekes.darkapp.dimming

import kotlin.math.pow

object DimMath {
    private const val MaxOverlayAlpha = 0.96f
    private const val Curve = 1.32

    fun alphaForPercent(percent: Int): Float {
        val normalized = percent.coerceIn(0, 100) / 100f
        return MaxOverlayAlpha * normalized.toDouble().pow(Curve).toFloat()
    }
}
