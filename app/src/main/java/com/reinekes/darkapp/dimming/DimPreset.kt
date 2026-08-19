package com.reinekes.darkapp.dimming

enum class DimPreset(
    val label: String,
    val percent: Int,
    val alphaOverride: Float? = null,
) {
    Reading("Reading", 22),
    Night("Night", 48),
    Ultra("Ultra", 82),
    Deep("Deep", 96),
    Blackout("Blackout", 100, 0.99f),
}
