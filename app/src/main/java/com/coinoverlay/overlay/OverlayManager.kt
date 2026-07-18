package com.coinoverlay.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle
import com.coinoverlay.data.model.CoinTicker
import com.coinoverlay.data.model.OverlaySettings
import com.coinoverlay.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope

class OverlayManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope
) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var composeView: android.view.View? = null
    private var lifecycleOwner: OverlayLifecycleOwner? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var isAdded = false

    private val positionManager = OverlayPositionManager(context, settingsRepository, scope)

    private val tickerMap = androidx.compose.runtime.mutableStateMapOf<String, CoinTicker>()
    private val settingsState = mutableStateOf(OverlaySettings())

    private var dragManager: DragManager? = null

    @SuppressLint("ClickableViewAccessibility")
    fun showOverlay(initialSettings: OverlaySettings) {
        if (isAdded) return
        settingsState.value = initialSettings
        positionManager.setInitialPosition(initialSettings.overlayX, initialSettings.overlayY)

        val owner = OverlayLifecycleOwner()
        owner.performRestore()
        owner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        owner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        owner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        lifecycleOwner = owner

        val view = createOverlayComposeView(context, owner)
        view.setContent {
            val tickers = tickerMap.values.sortedBy { it.symbol }
            OverlayContent(tickers = tickers, settings = settingsState.value)
        }
        composeView = view

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            TouchThroughManager.baseFlags(),
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = initialSettings.overlayX
            y = initialSettings.overlayY
        }
        TouchThroughManager.applyTouchThrough(params, initialSettings.isTouchThroughEnabled)
        layoutParams = params

        dragManager = DragManager(
            onPositionChanged = { newX, newY ->
                windowManager.updateViewLayout(view, params)
            },
            onDragEnd = { finalX, finalY ->
                positionManager.persistPosition(finalX, finalY)
            }
        )

        view.setOnTouchListener { _, event ->
            val locked = settingsState.value.isPositionLocked
            dragManager?.handleTouchEvent(event, params, locked) ?: false
        }

        windowManager.addView(view, params)
        isAdded = true
    }

    fun updateTicker(ticker: CoinTicker) {
        tickerMap[ticker.symbol] = ticker
    }

    fun updateSettings(settings: OverlaySettings) {
        settingsState.value = settings
        layoutParams?.let { params ->
            TouchThroughManager.applyTouchThrough(params, settings.isTouchThroughEnabled)
            composeView?.let { view ->
                if (isAdded) {
                    windowManager.updateViewLayout(view, params)
                }
            }
        }
    }

    fun removeStaleSymbols(activeSymbols: List<String>) {
        val toRemove = tickerMap.keys.filterNot { it in activeSymbols }
        toRemove.forEach { tickerMap.remove(it) }
    }

    fun hideOverlay() {
        if (!isAdded) return
        composeView?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (_: Exception) {
            }
        }
        lifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        composeView = null
        lifecycleOwner = null
        layoutParams = null
        isAdded = false
        tickerMap.clear()
    }

    fun isOverlayShown(): Boolean = isAdded
}