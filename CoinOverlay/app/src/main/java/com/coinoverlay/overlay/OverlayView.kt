package com.coinoverlay.overlay

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.coinoverlay.data.model.CoinTicker
import com.coinoverlay.data.model.OverlaySettings
import com.coinoverlay.util.Utils

class OverlayLifecycleOwner : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    fun performRestore() {
        savedStateRegistryController.performRestore(null)
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }
}

fun createOverlayComposeView(
    context: Context,
    lifecycleOwner: OverlayLifecycleOwner
): ComposeView {
    val composeView = ComposeView(context)
    composeView.setViewTreeLifecycleOwner(lifecycleOwner)
    composeView.setViewTreeViewModelStoreOwner(lifecycleOwner)
    composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
    return composeView
}

@Composable
fun OverlayContent(
    tickers: List<CoinTicker>,
    settings: OverlaySettings
) {
    val backgroundColor = if (settings.isDarkMode) {
        Color(0xFF1C1B1F).copy(alpha = settings.opacity)
    } else {
        Color(0xFFFFFBFE).copy(alpha = settings.opacity)
    }
    val textColor = Color(settings.fontColor)

    Column(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        tickers.forEach { ticker ->
            var lastPrice by mutableStateOf(ticker.price)
            lastPrice = ticker.price

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = Utils.symbolToDisplayName(ticker.symbol),
                    style = textStyleFor(settings, textColor)
                )
                Text(
                    text = Utils.formatPrice(ticker.price),
                    style = textStyleFor(settings, textColor)
                )
                Text(
                    text = Utils.formatPercent(ticker.priceChangePercent),
                    style = textStyleFor(
                        settings,
                        if (ticker.priceChangePercent >= 0) Color(0xFF16C784) else Color(0xFFEA3943)
                    )
                )
            }
        }
    }
}

@Composable
private fun textStyleFor(settings: OverlaySettings, color: Color): TextStyle {
    return TextStyle(
        fontSize = settings.fontSizeSp.sp,
        fontWeight = if (settings.isBold) FontWeight.Bold else FontWeight.Normal,
        color = color,
        shadow = if (settings.hasShadow) {
            Shadow(color = Color.Black, blurRadius = 4f)
        } else null
    )
}