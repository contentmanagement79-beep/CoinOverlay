package com.coinoverlay.data.remote

import com.coinoverlay.data.model.BinanceStreamPayload
import com.coinoverlay.data.model.CoinTicker
import com.coinoverlay.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var webSocket: WebSocket? = null
    private var currentSymbols: List<String> = emptyList()
    private var reconnectJob: Job? = null
    private var isManuallyStopped = false
    private var reconnectAttempt = 0

    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient.Builder()
        .pingInterval(Constants.PING_INTERVAL_SECONDS, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val _tickerUpdates = MutableSharedFlow<CoinTicker>(extraBufferCapacity = 64)
    val tickerUpdates: SharedFlow<CoinTicker> = _tickerUpdates

    private val _connectionState = MutableSharedFlow<Boolean>(extraBufferCapacity = 4)
    val connectionState: SharedFlow<Boolean> = _connectionState

    fun connect(symbols: List<String>) {
        if (symbols.isEmpty()) return
        currentSymbols = symbols
        isManuallyStopped = false
        reconnectAttempt = 0
        openSocket()
    }

    fun updateSymbols(symbols: List<String>) {
        if (symbols == currentSymbols) return
        currentSymbols = symbols
        if (!isManuallyStopped) {
            closeSocket()
            openSocket()
        }
    }

    fun disconnect() {
        isManuallyStopped = true
        reconnectJob?.cancel()
        closeSocket()
    }

    private fun openSocket() {
        if (currentSymbols.isEmpty()) return
        val streams = currentSymbols.joinToString("/") { "${it.lowercase()}@ticker" }
        val url = "${Constants.BINANCE_WS_BASE_URL}?streams=$streams"
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                reconnectAttempt = 0
                scope.launch { _connectionState.emit(true) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val payload = json.decodeFromString<BinanceStreamPayload>(text)
                    val ticker = CoinTicker(
                        symbol = payload.data.s,
                        price = payload.data.c.toDoubleOrNull() ?: 0.0,
                        priceChangePercent = payload.data.P.toDoubleOrNull() ?: 0.0,
                        lastUpdated = System.currentTimeMillis()
                    )
                    scope.launch { _tickerUpdates.emit(ticker) }
                } catch (_: Exception) {
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                scope.launch { _connectionState.emit(false) }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                scope.launch { _connectionState.emit(false) }
                scheduleReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                scope.launch { _connectionState.emit(false) }
                scheduleReconnect()
            }
        })
    }

    private fun closeSocket() {
        webSocket?.close(1000, "Client closed")
        webSocket = null
    }

    private fun scheduleReconnect() {
        if (isManuallyStopped) return
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            reconnectAttempt++
            val delayMs = (Constants.RECONNECT_DELAY_MS * reconnectAttempt)
                .coerceAtMost(Constants.MAX_RECONNECT_DELAY_MS)
            delay(delayMs)
            if (!isManuallyStopped) {
                openSocket()
            }
        }
    }
}