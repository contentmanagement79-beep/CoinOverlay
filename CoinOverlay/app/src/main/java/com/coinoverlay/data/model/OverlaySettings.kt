package com.coinoverlay.data.model

data class OverlaySettings(
    val fontSizeSp: Float = 14f,
    val fontColor: Long = 0xFFFFFFFF,
    val opacity: Float = 0.85f,
    val isBold: Boolean = false,
    val hasShadow: Boolean = true,
    val isDarkMode: Boolean = true,
    val isPositionLocked: Boolean = false,
    val isTouchThroughEnabled: Boolean = false,
    val overlayX: Int = 0,
    val overlayY: Int = 100,
    val autoStartOnBoot: Boolean = true,
    val selectedCoins: List<String> = listOf("BTCUSDT", "ETHUSDT", "BNBUSDT"),
    val favoriteCoins: List<String> = emptyList()
)