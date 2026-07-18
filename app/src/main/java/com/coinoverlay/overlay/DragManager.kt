package com.coinoverlay.overlay

import android.view.MotionEvent
import android.view.WindowManager

class DragManager(
    private val onPositionChanged: (x: Int, y: Int) -> Unit,
    private val onDragEnd: (x: Int, y: Int) -> Unit
) {

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false

    fun handleTouchEvent(
        event: MotionEvent,
        params: WindowManager.LayoutParams,
        isPositionLocked: Boolean
    ): Boolean {
        if (isPositionLocked) return false

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                isDragging = false
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - initialTouchX).toInt()
                val dy = (event.rawY - initialTouchY).toInt()

                if (kotlin.math.abs(dx) > 4 || kotlin.math.abs(dy) > 4) {
                    isDragging = true
                }

                if (isDragging) {
                    val newX = initialX + dx
                    val newY = initialY + dy
                    params.x = newX
                    params.y = newY
                    onPositionChanged(newX, newY)
                }
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    onDragEnd(params.x, params.y)
                }
                isDragging = false
                true
            }
            else -> false
        }
    }

    fun isCurrentlyDragging(): Boolean = isDragging
}