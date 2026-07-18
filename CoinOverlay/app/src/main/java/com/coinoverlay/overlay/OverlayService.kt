package com.coinoverlay.overlay

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.coinoverlay.CoinOverlayApp
import com.coinoverlay.notification.NotificationHelper
import com.coinoverlay.util.Constants
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OverlayService : LifecycleService() {

    private lateinit var overlayManager: OverlayManager
    private var currentSymbols: List<String> = emptyList()

    override fun onCreate() {
        super.onCreate()
        val app = application as CoinOverlayApp
        overlayManager = OverlayManager(
            context = this,
            settingsRepository = app.settingsRepository,
            scope = lifecycleScope
        )

        NotificationHelper.createNotificationChannel(this)
        val notification = NotificationHelper.buildServiceNotification(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                Constants.NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(Constants.NOTIFICATION_ID, notification)
        }

        observeSettings()
        observeTickers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_STOP -> {
                stopOverlayAndSelf()
                return START_NOT_STICKY
            }
        }

        val app = application as CoinOverlayApp
        lifecycleScope.launch {
            app.settingsRepository.setOverlayActive(true)
        }

        return START_STICKY
    }

    private fun observeSettings() {
        val app = application as CoinOverlayApp
        app.settingsRepository.settingsFlow
            .distinctUntilChanged()
            .onEach { settings ->
                if (!overlayManager.isOverlayShown()) {
                    overlayManager.showOverlay(settings)
                    app.coinRepository.startTracking(settings.selectedCoins)
                    currentSymbols = settings.selectedCoins
                } else {
                    overlayManager.updateSettings(settings)
                    if (settings.selectedCoins != currentSymbols) {
                        app.coinRepository.updateTrackedSymbols(settings.selectedCoins)
                        overlayManager.removeStaleSymbols(settings.selectedCoins)
                        currentSymbols = settings.selectedCoins
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun observeTickers() {
        val app = application as CoinOverlayApp
        app.coinRepository.tickerUpdates
            .onEach { ticker ->
                overlayManager.updateTicker(ticker)
            }
            .launchIn(lifecycleScope)
    }

    private fun stopOverlayAndSelf() {
        val app = application as CoinOverlayApp
        overlayManager.hideOverlay()
        app.coinRepository.stopTracking()
        lifecycleScope.launch {
            app.settingsRepository.setOverlayActive(false)
        }
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        overlayManager.hideOverlay()
        val app = application as CoinOverlayApp
        app.coinRepository.stopTracking()
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP = "com.coinoverlay.action.STOP_OVERLAY"
        const val ACTION_START = "com.coinoverlay.action.START_OVERLAY"
    }
}