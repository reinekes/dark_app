package com.reinekes.darkapp.brightness

enum class BrightnessBoost(
    val label: String,
    val screenBrightnessValue: Int?,
) {
    Auto("Auto", null),
    Percent25("25%", 64),
    Percent50("50%", 128),
    Percent75("75%", 191),
    Percent100("100%", 255),
}
