package com.seoul.where42android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seoul.where42android.R

class CX1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cx1, container, false)

        // seat_r5_1 ~ seat_r5_8의 뷰 가져오기
        val R5seats = listOf(
            view.findViewById<View>(R.id.seat_r5_1),
            view.findViewById<View>(R.id.seat_r5_4),
            view.findViewById<View>(R.id.seat_r5_6),
            view.findViewById<View>(R.id.seat_r5_8)
        )

        val R4seats = listOf(
            view.findViewById<View>(R.id.seat_r4_1),
            view.findViewById<View>(R.id.seat_r4_4),
            view.findViewById<View>(R.id.seat_r4_6),
            view.findViewById<View>(R.id.seat_r4_8)
        )
        // CustomLineView 및 ImageView 위치 설정
        view.post {
            // CustomLineView
            val lineView = view.findViewById<CustomLineView>(R.id.custom_line_view)

            // r3 좌표 설정 및 선 추가
            val seatR34 = view.findViewById<View>(R.id.seat_r3_4)
            val seatR31 = view.findViewById<View>(R.id.seat_r3_1)
            addLinesForViews(lineView, seatR34, seatR31)

            // r2 좌표 설정 및 선 추가
            val seatR24 = view.findViewById<View>(R.id.seat_r2_4)
            val seatR21 = view.findViewById<View>(R.id.seat_r2_1)
            addLinesForViews(lineView, seatR24, seatR21)

            // r1 좌표 설정 및 선 추가
            val seatR14 = view.findViewById<View>(R.id.seat_r1_4)
            val seatR11 = view.findViewById<View>(R.id.seat_r1_1)
            addLinesForViews(lineView, seatR14, seatR11)

            // 중심점 좌표 계산
            val centerX = R5seats.map { it.x + it.width / 2 }.average().toFloat() - 20
            val centerY = R5seats.map { it.y + it.height / 2 }.average().toFloat() + 30

            // 오른쪽 위 대각선 추가
            val seatR5_7 = view.findViewById<View>(R.id.seat_r5_7)
            val seatR5_8 = view.findViewById<View>(R.id.seat_r5_8)
            addDiagonalLine(lineView, centerX, centerY, seatR5_7, seatR5_8, offsetX = 50, offsetY = -50)

            // 자리 그룹 중심 좌표 및 선 추가
            val seatR5_1 = view.findViewById<View>(R.id.seat_r5_1)
            val seatR5_2 = view.findViewById<View>(R.id.seat_r5_2)
            addDiagonalLine(lineView, centerX, centerY, seatR5_1, seatR5_2, offsetX = -40, offsetY = 60)

            val seatR5_3 = view.findViewById<View>(R.id.seat_r5_3)
            val seatR5_4 = view.findViewById<View>(R.id.seat_r5_4)
            addDiagonalLine(lineView, centerX, centerY, seatR5_3, seatR5_4, offsetX = 40, offsetY = 60)

            val seatR5_5 = view.findViewById<View>(R.id.seat_r5_5)
            val seatR5_6 = view.findViewById<View>(R.id.seat_r5_6)
            addDiagonalLine(lineView, centerX, centerY, seatR5_5, seatR5_6, offsetX = -50, offsetY = -50)


            // 중심점 좌표 계산
            val centerR4X = R4seats.map { it.x + it.width / 2 }.average().toFloat() - 20
            val centerR4Y = R4seats.map { it.y + it.height / 2 }.average().toFloat() + 30

            // 오른쪽 위 대각선 추가
            val seatR4_7 = view.findViewById<View>(R.id.seat_r4_7)
            val seatR4_8 = view.findViewById<View>(R.id.seat_r4_8)
            addDiagonalLine(lineView, centerR4X, centerR4Y, seatR4_7, seatR4_8, offsetX = 50, offsetY = -50)

            // 자리 그룹 중심 좌표 및 선 추가
            val seatR4_1 = view.findViewById<View>(R.id.seat_r4_1)
            val seatR4_2 = view.findViewById<View>(R.id.seat_r4_2)
            addDiagonalLine(lineView, centerR4X, centerR4Y, seatR4_1, seatR4_2, offsetX = -40, offsetY = 60)

            val seatR4_3 = view.findViewById<View>(R.id.seat_r4_3)
            val seatR4_4 = view.findViewById<View>(R.id.seat_r4_4)
            addDiagonalLine(lineView, centerR4X, centerR4Y, seatR4_3, seatR4_4, offsetX = 40, offsetY = 60)

            val seatR4_5 = view.findViewById<View>(R.id.seat_r4_5)
            val seatR4_6 = view.findViewById<View>(R.id.seat_r4_6)
            addDiagonalLine(lineView, centerR4X, centerR4Y, seatR4_5, seatR4_6, offsetX = -50, offsetY = -50)

        }

        return view
    }

    private fun addDiagonalLine(
        lineView: CustomLineView,
        centerX: Float,
        centerY: Float,
        view1: View,
        view2: View,
        offsetX: Int = 0,
        offsetY: Int = 0
    ) {
        val endX = (view1.x + view1.width / 2 + view2.x + view2.width / 2) / 2 + offsetX
        val endY = (view1.y + view1.height / 2 + view2.y + view2.height / 2) / 2 + offsetY
        lineView.addLine(centerX, centerY, endX, endY)
    }

    /**
     * 두 뷰의 중심을 기반으로 선을 추가하는 함수
     */
    private fun addLinesForViews(lineView: CustomLineView, view1: View, view2: View) {
        // 첫 번째 뷰의 중심 좌표
        val centerX1 = view1.x + view1.width / 2
        val centerY1 = view1.y + view1.height / 2

        // 두 번째 뷰의 중심 좌표
        val centerX2 = view2.x + view2.width / 2
        val centerY2 = view2.y + view2.height / 2

        // 두 중심점의 중간점 계산
        val midX = (centerX1 + centerX2) / 2
        val midY = (centerY1 + centerY2) / 2

        // 첫 번째 뷰의 오른쪽 좌표 계산
        val rightX1 = centerX1 + view1.width + (view1.width / 2)
        val rightY1 = centerY1 - (view1.height / 2)

        // 두 번째 뷰의 오른쪽 좌표 계산
        val rightX2 = centerX2 + view2.width + (view2.width / 2)
        val rightY2 = centerY2 + (view2.height / 2)

        // 선 추가
        lineView.addLine(midX, midY, rightX1, rightY1)
        lineView.addLine(midX, midY, rightX2, rightY2)
    }
}
