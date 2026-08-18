package com.reinekes.darkapp.dimming

enum class DimFilter(
    val label: String,
    val red: Int,
    val green: Int,
    val blue: Int,
) {
    Neutral("Neutral", red = 0x00, green = 0x00, blue = 0x00),
    Warm("Warm", red = 0x12, green = 0x08, blue = 0x00),
    Red("Red", red = 0x1F, green = 0x00, blue = 0x00),
}
