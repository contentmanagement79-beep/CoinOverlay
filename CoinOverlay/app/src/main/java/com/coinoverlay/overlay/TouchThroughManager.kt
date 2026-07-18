package com.coinoverlay.overlay

import android.view.WindowManager

object TouchThroughManager {

    fun applyTouchThrough(params: WindowManager.LayoutParams, enabled: Boolean) {
        params.flags = if (enabled) {
            params.flags or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else {
            params.flags and
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv() and
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        }
    }

    fun baseFlags(): Int {
        return WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
    }
}