package com.example.where42android

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment


class CustomDialog: DialogFragment()  {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Start game")
                .setPositiveButton("Start") { dialog, id ->
                    // START THE GAME!
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // User cancelled the dialog.
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    class OldXmlActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_edittext_popup)

            CustomDialog().show(supportFragmentManager, "GAME_DIALOG")
        }
    }
}

//class CustomDialog(context: Context) : Dialog(context) {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(R.layout.custom_dialog) // 커스텀 다이얼로그의 XML 파일 지정
//
//        // 여기에서 커스텀 다이얼로그 내부의 뷰들을 findViewById로 찾아와 설정합니다.
//        // 예를 들어, TextView, Button 등을 설정할 수 있습니다.
//        val btnCancel = findViewById<Button>(R.id.cancel)
//        window?.setBackgroundDrawable(ColorDrawable(Color.GRAY)) // 투명 배경 설정
//        btnCancel.setOnClickListener {
//            dismiss()
//        }
//    }
//
//}