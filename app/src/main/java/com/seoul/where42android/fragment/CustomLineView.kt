package com.seoul.where42android.fragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = 0xFF000000.toInt() // 검은색
        strokeWidth = 8f // 선 두께
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    // 선의 시작점과 끝점을 저장할 리스트
    private val lines = mutableListOf<Pair<Pair<Float, Float>, Pair<Float, Float>>>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 저장된 모든 선을 그림
        for (line in lines) {
            val (start, end) = line
            canvas.drawLine(start.first, start.second, end.first, end.second, paint)
        }
    }
    // 선 추가 메서드
    fun addLine(startX: Float, startY: Float, endX: Float, endY: Float) {
        lines.add(Pair(Pair(startX, startY), Pair(endX, endY)))
        invalidate() // 뷰 다시 그리기 요청
    }

    // 선 초기화 메서드
    fun clearLines() {
        lines.clear()
        invalidate() // 뷰 다시 그리기 요청
    }
}

