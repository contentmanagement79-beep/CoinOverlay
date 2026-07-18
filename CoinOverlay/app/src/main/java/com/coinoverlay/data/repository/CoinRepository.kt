package com.coinoverlay.data.repository

import com.coinoverlay.data.model.BinanceExchangeSymbol
import com.coinoverlay.data.model.CoinTicker
import com.coinoverlay.data.remote.WebSocketManager
import com.coinoverlay.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request

class CoinRepository(
    private val webSocketManager: WebSocketManager
) {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    val tickerUpdates: SharedFlow<CoinTicker> = webSocketManager.tickerUpdates
    val connectionState: SharedFlow<Boolean> = webSocketManager.connectionState

    fun startTracking(symbols: List<String>) {
        webSocketManager.connect(symbols)
    }

    fun updateTrackedSymbols(symbols: List<String>) {
        webSocketManager.updateSymbols(symbols)
    }

    fun stopTracking() {
        webSocketManager.disconnect()
    }

    suspend fun searchSymbols(query: String): List<BinanceExchangeSymbol> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val request = Request.Builder().url(Constants.BINANCE_REST_EXCHANGE_INFO).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()
            val root = json.parseToJsonElement(body).jsonObject
            val symbolsArray = root["symbols"]?.jsonArray ?: return@withContext emptyList()

            symbolsArray.mapNotNull { element ->
                val obj = element.jsonObject
                val symbol = obj["symbol"]?.toString()?.trim('"') ?: return@mapNotNull null
                val status = obj["status"]?.toString()?.trim('"') ?: ""
                val quoteAsset = obj["quoteAsset"]?.toString()?.trim('"') ?: ""

                if (status == "TRADING" && quoteAsset == "USDT" &&
                    symbol.contains(query.uppercase())
                ) {
                    BinanceExchangeSymbol(symbol, status, quoteAsset)
                } else {
                    null
                }
            }.take(50)
        } catch (e: Exception) {
            emptyList()
        }
    }
}