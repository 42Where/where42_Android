package com.seoul.where42android.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.seoul.where42android.R

class AnnouncementBottomSheet(
    private val title: String,
    private val description: String,
    private val createdDate: String,
    private val updatedDate: String
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)

                // 높이를 화면의 90%로 설정
                val displayMetrics = resources.displayMetrics
                val desiredHeight = (displayMetrics.heightPixels * 0.95).toInt()
                it.layoutParams.height = desiredHeight
                it.requestLayout()
                // 상태를 확장 상태로 설정
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_announcement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        view.findViewById<TextView>(R.id.text_title).text = title
        view.findViewById<TextView>(R.id.text_description).text = description
//        view.findViewById<TextView>(R.id.text_date).text = "작성일: $createdDate\n수정일: $updatedDate"
        view.findViewById<TextView>(R.id.text_createAt).text = "작성일: $createdDate"
        view.findViewById<TextView>(R.id.text_updateAt).text = "수정일: $updatedDate"

        // 닫기 버튼 리스너 설정
        view.findViewById<Button>(R.id.button_close).setOnClickListener {
            dismiss()
        }
    }
}
