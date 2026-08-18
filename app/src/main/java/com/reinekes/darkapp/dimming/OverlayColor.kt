package com.reinekes.darkapp.dimming

import kotlin.math.roundToInt

object OverlayColor {
    fun argb(percent: Int, filter: DimFilter): Int {
        val alpha = (DimMath.alphaForPercent(percent) * 255).roundToInt().coerceIn(0, 255)
        return (alpha shl 24) or
            (filter.red.coerceIn(0, 255) shl 16) or
            (filter.green.coerceIn(0, 255) shl 8) or
            filter.blue.coerceIn(0, 255)
    }
}
