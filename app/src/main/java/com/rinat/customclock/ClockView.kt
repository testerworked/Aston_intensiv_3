package com.rinat.customclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.util.*

class ClockView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var secondHandStartX = 0f
    private var secondHandStartY = 0f
    private var secondHandEndX = 0f
    private var secondHandEndY = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    private val easyHandler: Handler   = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {

        override fun run() {
            // Redraw the clock
            invalidate()

            // Schedule the next redraw after 1 second
            easyHandler.postDelayed(this, 1000)
        }

    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Start the clock redraw when the view is attached to the window
        easyHandler.post(runnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Stop the clock redraw when the view is detached from the window
        easyHandler.removeCallbacks(runnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) / 2f) * 0.9f

        // Draw clock face
        paint.color = Color.GREEN
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Draw hour markings
        paint.color = Color.BLACK
        paint.strokeWidth = 8f
        for (i in 0..11) {
            val angle = Math.PI / 6 * i
            val startX = centerX + Math.sin(angle) * (radius - 40f)
            val startY = centerY - Math.cos(angle) * (radius - 40f)
            val endX = centerX + Math.sin(angle) * radius
            val endY = centerY - Math.cos(angle) * radius
            canvas.drawLine(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat(), paint)
        }

        // Draw hour hand
        val hour = 3 // Replace with actual hour value
        val hourAngle = Math.PI / 6 * hour
        val hourHandLength = radius * 0.5f
        val hourEndX = centerX + Math.sin(hourAngle) * hourHandLength
        val hourEndY = centerY - Math.cos(hourAngle) * hourHandLength
        canvas.drawLine(centerX, centerY, hourEndX.toFloat(), hourEndY.toFloat(), paint)

        // Draw minute hand
        val minute = 30 // Replace with actual minute value
        val minuteAngle = Math.PI / 30 * minute
        val minuteHandLength = radius * 0.7f
        val minuteEndX = centerX + Math.sin(minuteAngle) * minuteHandLength
        val minuteEndY = centerY - Math.cos(minuteAngle) * minuteHandLength
        canvas.drawLine(centerX, centerY, minuteEndX.toFloat(), minuteEndY.toFloat(), paint)


        // Draw second hand
        paint.color = Color.RED
        paint.strokeWidth = 4f
        canvas.drawLine(secondHandStartX, secondHandStartY, secondHandEndX, secondHandEndY, paint)

        // Draw second hand
        val second = 45 // Replace with actual second value
        val secondAngle = Math.PI / 30 * second
        val secondHandLength = radius * 0.8f
        val secondEndX = centerX + Math.sin(secondAngle) * secondHandLength
        val secondEndY = centerY - Math.cos(secondAngle) * secondHandLength
        paint.color = Color.RED
        paint.strokeWidth = 4f
        canvas.drawLine(centerX, centerY, secondEndX.toFloat(), secondEndY.toFloat(), paint)

        // Draw center circle
        paint.color = Color.BLACK
        canvas.drawCircle(centerX, centerY, 8f, paint)
    }
}
