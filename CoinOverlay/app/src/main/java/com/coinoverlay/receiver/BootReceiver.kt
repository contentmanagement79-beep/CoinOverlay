package com.coinoverlay.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.coinoverlay.CoinOverlayApp
import com.coinoverlay.overlay.OverlayPermission
import com.coinoverlay.overlay.OverlayService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED &&
            action != "android.intent.action.QUICKBOOT_POWERON"
        ) {
            return
        }

        if (!OverlayPermission.hasOverlayPermission(context)) {
            return
        }

        val pendingResult = goAsync()
        val app = context.applicationContext as CoinOverlayApp

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = app.settingsRepository.settingsFlow.first()
                if (settings.autoStartOnBoot) {
                    val serviceIntent = Intent(context, OverlayService::class.java).apply {
                        action = OverlayService.ACTION_START
                    }
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}