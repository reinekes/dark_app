package com.reinekes.darkapp.dimming

enum class DimPreset(
    val label: String,
    val percent: Int,
) {
    Reading("Reading", 22),
    Night("Night", 48),
    Ultra("Ultra", 82),
    Deep("Deep", 96),
}
