package com.coinoverlay

import android.app.Application
import com.coinoverlay.data.local.DataStoreManager
import com.coinoverlay.data.remote.WebSocketManager
import com.coinoverlay.data.repository.CoinRepository
import com.coinoverlay.data.repository.SettingsRepository

class CoinOverlayApp : Application() {

    lateinit var dataStoreManager: DataStoreManager
    lateinit var settingsRepository: SettingsRepository
    lateinit var webSocketManager: WebSocketManager
    lateinit var coinRepository: CoinRepository

    override fun onCreate() {
        super.onCreate()

        dataStoreManager = DataStoreManager(this)
        settingsRepository = SettingsRepository(dataStoreManager)
        webSocketManager = WebSocketManager()
        coinRepository = CoinRepository(webSocketManager)
    }
}