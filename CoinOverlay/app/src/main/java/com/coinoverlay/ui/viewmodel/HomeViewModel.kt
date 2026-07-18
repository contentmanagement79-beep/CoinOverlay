package com.coinoverlay.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.coinoverlay.CoinOverlayApp
import com.coinoverlay.data.model.OverlaySettings
import com.coinoverlay.overlay.OverlayService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CoinOverlayApp

    val isOverlayActive: StateFlow<Boolean> = app.settingsRepository.isOverlayActiveFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val settings: StateFlow<OverlaySettings> = app.settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverlaySettings())

    fun startOverlay() {
        viewModelScope.launch {
            val context = getApplication<CoinOverlayApp>()
            val intent = Intent(context, OverlayService::class.java).apply {
                action = OverlayService.ACTION_START
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    fun stopOverlay() {
        val context = getApplication<CoinOverlayApp>()
        val intent = Intent(context, OverlayService::class.java).apply {
            action = OverlayService.ACTION_STOP
        }
        context.startService(intent)
    }
}