package com.seoul.where42android.fragment

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var used: Float = 0f
    var total: Float = 1f
    var label: String = ""
    var subLabel: String = ""

    private val availablePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9BB0C1")
        style = Paint.Style.FILL
    }
    private val usedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#132743")
        style = Paint.Style.FILL
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 36f
        textAlign = Paint.Align.CENTER
    }
    private val subLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }
    private val oval = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val textAreaHeight = 90f
        val pieSize = minOf(w, h - textAreaHeight) * 0.85f
        val left = (w - pieSize) / 2f
        val top = 8f
        oval.set(left, top, left + pieSize, top + pieSize)

        canvas.drawArc(oval, 0f, 360f, true, availablePaint)

        if (total > 0 && used > 0) {
            val sweep = 360f * (used / total)
            canvas.drawArc(oval, -90f, sweep, true, usedPaint)
        }

        val textY = oval.bottom + 42f
        canvas.drawText(label, w / 2f, textY, labelPaint)
        canvas.drawText(subLabel, w / 2f, textY + 38f, subLabelPaint)
    }
}
