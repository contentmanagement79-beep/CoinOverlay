package com.coinoverlay.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale

object Utils {

    fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun formatPrice(price: Double): String {
        val decimalPlaces = when {
            price >= 1000 -> 2
            price >= 1 -> 4
            else -> 6
        }
        val pattern = "#,##0." + "0".repeat(decimalPlaces)
        val formatter = DecimalFormat(pattern)
        return formatter.format(price)
    }

    fun formatPercent(percent: Double): String {
        val sign = if (percent >= 0) "+" else ""
        val rounded = BigDecimal(percent).setScale(2, RoundingMode.HALF_UP)
        return String.format(Locale.US, "%s%.2f%%", sign, rounded.toDouble())
    }

    fun symbolToDisplayName(symbol: String): String {
        return if (symbol.endsWith("USDT")) {
            symbol.removeSuffix("USDT") + "/USDT"
        } else {
            symbol
        }
    }

    fun dpToPx(context: Context, dp: Float): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun pxToDp(context: Context, px: Int): Float {
        return px / context.resources.displayMetrics.density
    }

    fun isAndroid13Plus(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}