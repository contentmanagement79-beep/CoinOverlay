package com.coinoverlay.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.coinoverlay.CoinOverlayApp
import com.coinoverlay.data.model.OverlaySettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CoinOverlayApp

    val settings: StateFlow<OverlaySettings> = app.settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverlaySettings())

    fun updateFontSize(value: Float) = viewModelScope.launch {
        app.settingsRepository.updateFontSize(value)
    }

    fun updateFontColor(value: Long) = viewModelScope.launch {
        app.settingsRepository.updateFontColor(value)
    }

    fun updateOpacity(value: Float) = viewModelScope.launch {
        app.settingsRepository.updateOpacity(value)
    }

    fun updateBold(value: Boolean) = viewModelScope.launch {
        app.settingsRepository.updateBold(value)
    }

    fun updateShadow(value: Boolean) = viewModelScope.launch {
        app.settingsRepository.updateShadow(value)
    }

    fun updateDarkMode(value: Boolean) = viewModelScope.launch {
        app.settingsRepository.updateDarkMode(value)
    }

    fun updatePositionLocked(value: Boolean) = viewModelScope.launch {
        app.settingsRepository.updatePositionLocked(value)
    }

    fun updateTouchThrough(value: Boolean) = viewModelScope.launch {
        app.settingsRepository.updateTouchThrough(value)
    }

    fun updateManualPosition(x: Int, y: Int) = viewModelScope.launch {
        app.settingsRepository.updateOverlayPosition(x, y)
    }

    fun updateAutoStartOnBoot(value: Boolean) = viewModelScope.launch {
        app.settingsRepository.updateAutoStartOnBoot(value)
    }
}