package com.coinoverlay.overlay

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.coinoverlay.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OverlayPositionManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope
) {

    private var savedX: Int = 0
    private var savedY: Int = 100

    fun setInitialPosition(x: Int, y: Int) {
        savedX = x
        savedY = y
    }

    fun getScreenBounds(): Pair<Int, Int> {
        val metrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(metrics)
        return Pair(metrics.widthPixels, metrics.heightPixels)
    }

    fun clampPosition(x: Int, y: Int, viewWidth: Int, viewHeight: Int): Pair<Int, Int> {
        val (screenWidth, screenHeight) = getScreenBounds()
        val clampedX = x.coerceIn(0, (screenWidth - viewWidth).coerceAtLeast(0))
        val clampedY = y.coerceIn(0, (screenHeight - viewHeight).coerceAtLeast(0))
        return Pair(clampedX, clampedY)
    }

    fun persistPosition(x: Int, y: Int) {
        savedX = x
        savedY = y
        scope.launch(Dispatchers.IO) {
            settingsRepository.updateOverlayPosition(x, y)
        }
    }

    fun getCurrentPosition(): Pair<Int, Int> = Pair(savedX, savedY)
}