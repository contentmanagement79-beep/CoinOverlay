package com.coinoverlay.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.coinoverlay.data.model.OverlaySettings
import com.coinoverlay.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

class DataStoreManager(private val context: Context) {

    private object Keys {
        val FONT_SIZE = floatPreferencesKey("font_size_sp")
        val FONT_COLOR = longPreferencesKey("font_color")
        val OPACITY = floatPreferencesKey("opacity")
        val IS_BOLD = booleanPreferencesKey("is_bold")
        val HAS_SHADOW = booleanPreferencesKey("has_shadow")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val IS_POSITION_LOCKED = booleanPreferencesKey("is_position_locked")
        val IS_TOUCH_THROUGH = booleanPreferencesKey("is_touch_through")
        val OVERLAY_X = intPreferencesKey("overlay_x")
        val OVERLAY_Y = intPreferencesKey("overlay_y")
        val AUTO_START_BOOT = booleanPreferencesKey("auto_start_boot")
        val SELECTED_COINS = stringPreferencesKey("selected_coins")
        val FAVORITE_COINS = stringPreferencesKey("favorite_coins")
        val OVERLAY_ACTIVE = booleanPreferencesKey("overlay_active")
    }

    val overlaySettingsFlow: Flow<OverlaySettings> = context.dataStore.data.map { prefs ->
        OverlaySettings(
            fontSizeSp = prefs[Keys.FONT_SIZE] ?: Constants.DEFAULT_FONT_SIZE_SP,
            fontColor = prefs[Keys.FONT_COLOR] ?: 0xFFFFFFFF,
            opacity = prefs[Keys.OPACITY] ?: Constants.DEFAULT_OPACITY,
            isBold = prefs[Keys.IS_BOLD] ?: false,
            hasShadow = prefs[Keys.HAS_SHADOW] ?: true,
            isDarkMode = prefs[Keys.IS_DARK_MODE] ?: true,
            isPositionLocked = prefs[Keys.IS_POSITION_LOCKED] ?: false,
            isTouchThroughEnabled = prefs[Keys.IS_TOUCH_THROUGH] ?: false,
            overlayX = prefs[Keys.OVERLAY_X] ?: Constants.DEFAULT_OVERLAY_X,
            overlayY = prefs[Keys.OVERLAY_Y] ?: Constants.DEFAULT_OVERLAY_Y,
            autoStartOnBoot = prefs[Keys.AUTO_START_BOOT] ?: true,
            selectedCoins = prefs[Keys.SELECTED_COINS]?.split(",")?.filter { it.isNotBlank() }
                ?: Constants.DEFAULT_COINS,
            favoriteCoins = prefs[Keys.FAVORITE_COINS]?.split(",")?.filter { it.isNotBlank() }
                ?: emptyList()
        )
    }

    val isOverlayActiveFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.OVERLAY_ACTIVE] ?: false
    }

    suspend fun setOverlayActive(active: Boolean) {
        context.dataStore.edit { it[Keys.OVERLAY_ACTIVE] = active }
    }

    suspend fun updateFontSize(value: Float) {
        context.dataStore.edit { it[Keys.FONT_SIZE] = value }
    }

    suspend fun updateFontColor(value: Long) {
        context.dataStore.edit { it[Keys.FONT_COLOR] = value }
    }

    suspend fun updateOpacity(value: Float) {
        context.dataStore.edit { it[Keys.OPACITY] = value }
    }

    suspend fun updateBold(value: Boolean) {
        context.dataStore.edit { it[Keys.IS_BOLD] = value }
    }

    suspend fun updateShadow(value: Boolean) {
        context.dataStore.edit { it[Keys.HAS_SHADOW] = value }
    }

    suspend fun updateDarkMode(value: Boolean) {
        context.dataStore.edit { it[Keys.IS_DARK_MODE] = value }
    }

    suspend fun updatePositionLocked(value: Boolean) {
        context.dataStore.edit { it[Keys.IS_POSITION_LOCKED] = value }
    }

    suspend fun updateTouchThrough(value: Boolean) {
        context.dataStore.edit { it[Keys.IS_TOUCH_THROUGH] = value }
    }

    suspend fun updateOverlayPosition(x: Int, y: Int) {
        context.dataStore.edit {
            it[Keys.OVERLAY_X] = x
            it[Keys.OVERLAY_Y] = y
        }
    }

    suspend fun updateAutoStartOnBoot(value: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_START_BOOT] = value }
    }

    suspend fun updateSelectedCoins(coins: List<String>) {
        context.dataStore.edit { it[Keys.SELECTED_COINS] = coins.joinToString(",") }
    }

    suspend fun updateFavoriteCoins(coins: List<String>) {
        context.dataStore.edit { it[Keys.FAVORITE_COINS] = coins.joinToString(",") }
    }
}