package com.seoul.where42android.fragment

import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import androidx.fragment.app.Fragment
import com.seoul.where42android.R

//확대 축소 기능 추가 업데이트 필요
class C1C5Fragment : Fragment(R.layout.fragment_c1c5) {

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ScaleGestureDetector 초기화
        scaleGestureDetector = ScaleGestureDetector(requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor

                // 최소 및 최대 확대/축소 비율 설정
                scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f)

                // 전체 뷰에 스케일 적용
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                return true
            }
        })

        // View에 터치 이벤트 설정
        view.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }
}


//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.seoul.where42android.R
//
//class C1Fragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_c1, container, false)
//    }
//}