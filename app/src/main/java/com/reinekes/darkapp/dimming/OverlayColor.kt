package com.reinekes.darkapp.dimming

import kotlin.math.roundToInt

object OverlayColor {
    fun argb(percent: Int, filter: DimFilter, alphaOverride: Float? = null): Int {
        val overlayAlpha = alphaOverride ?: DimMath.alphaForPercent(percent)
        val alpha = (overlayAlpha.coerceIn(0f, 0.99f) * 255).roundToInt().coerceIn(0, 255)
        return (alpha shl 24) or
            (filter.red.coerceIn(0, 255) shl 16) or
            (filter.green.coerceIn(0, 255) shl 8) or
            filter.blue.coerceIn(0, 255)
    }
}
