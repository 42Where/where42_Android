package com.example.where42android

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class NewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_act)

        val dialog = AlertDialog.Builder(this)
            .setTitle("제목")
            .setMessage("내용")
            .setPositiveButton("예") { dialog, id ->
                // Yes
            }
            .setNegativeButton("아니오") { dialog, id ->
                // No
            }
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.black)
//
        val dialogButton = findViewById<Button>(R.id.dialogButton)
        dialogButton.setOnClickListener {
            dialog.show()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) setFullscreen()
    }

    private fun setFullscreen() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}