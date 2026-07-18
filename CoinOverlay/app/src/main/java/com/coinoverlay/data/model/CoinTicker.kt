package com.coinoverlay.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CoinTicker(
    val symbol: String,
    val price: Double = 0.0,
    val priceChangePercent: Double = 0.0,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = 0L
)

@Serializable
data class BinanceStreamPayload(
    val stream: String,
    val data: BinanceTickerData
)

@Serializable
data class BinanceTickerData(
    val s: String,
    val c: String,
    val P: String
)

@Serializable
data class BinanceExchangeSymbol(
    val symbol: String,
    val status: String,
    val quoteAsset: String
)