package com.coinoverlay.data.repository

import com.coinoverlay.data.local.DataStoreManager
import com.coinoverlay.data.model.OverlaySettings
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dataStoreManager: DataStoreManager) {

    val settingsFlow: Flow<OverlaySettings> = dataStoreManager.overlaySettingsFlow
    val isOverlayActiveFlow: Flow<Boolean> = dataStoreManager.isOverlayActiveFlow

    suspend fun setOverlayActive(active: Boolean) = dataStoreManager.setOverlayActive(active)

    suspend fun updateFontSize(value: Float) = dataStoreManager.updateFontSize(value)

    suspend fun updateFontColor(value: Long) = dataStoreManager.updateFontColor(value)

    suspend fun updateOpacity(value: Float) = dataStoreManager.updateOpacity(value)

    suspend fun updateBold(value: Boolean) = dataStoreManager.updateBold(value)

    suspend fun updateShadow(value: Boolean) = dataStoreManager.updateShadow(value)

    suspend fun updateDarkMode(value: Boolean) = dataStoreManager.updateDarkMode(value)

    suspend fun updatePositionLocked(value: Boolean) = dataStoreManager.updatePositionLocked(value)

    suspend fun updateTouchThrough(value: Boolean) = dataStoreManager.updateTouchThrough(value)

    suspend fun updateOverlayPosition(x: Int, y: Int) = dataStoreManager.updateOverlayPosition(x, y)

    suspend fun updateAutoStartOnBoot(value: Boolean) = dataStoreManager.updateAutoStartOnBoot(value)

    suspend fun updateSelectedCoins(coins: List<String>) = dataStoreManager.updateSelectedCoins(coins)

    suspend fun updateFavoriteCoins(coins: List<String>) = dataStoreManager.updateFavoriteCoins(coins)
}