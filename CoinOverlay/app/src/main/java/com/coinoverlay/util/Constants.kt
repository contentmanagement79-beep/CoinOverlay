package com.coinoverlay.util

object Constants {
    const val BINANCE_WS_BASE_URL = "wss://stream.binance.com:9443/stream"
    const val BINANCE_REST_EXCHANGE_INFO = "https://api.binance.com/api/v3/exchangeInfo"
    const val BINANCE_REST_TICKER_24HR = "https://api.binance.com/api/v3/ticker/24hr"

    const val NOTIFICATION_CHANNEL_ID = "coin_overlay_service_channel"
    const val NOTIFICATION_ID = 1001

    const val DATASTORE_NAME = "coin_overlay_settings"

    const val DEFAULT_FONT_SIZE_SP = 14f
    const val MIN_FONT_SIZE_SP = 8f
    const val MAX_FONT_SIZE_SP = 32f

    const val DEFAULT_OPACITY = 0.85f
    const val MIN_OPACITY = 0.1f
    const val MAX_OPACITY = 1.0f

    const val DEFAULT_OVERLAY_X = 0
    const val DEFAULT_OVERLAY_Y = 100

    const val RECONNECT_DELAY_MS = 3000L
    const val MAX_RECONNECT_DELAY_MS = 30000L
    const val PING_INTERVAL_SECONDS = 20L

    const val WORK_TAG_OVERLAY = "coin_overlay_work"

    val DEFAULT_COINS = listOf("BTCUSDT", "ETHUSDT", "BNBUSDT")
}