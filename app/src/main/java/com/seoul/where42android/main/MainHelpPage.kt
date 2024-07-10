package com.seoul.where42android.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.seoul.where42android.R
class MainHelpPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_page)

        val noiton = findViewById<TextView>(R.id.noiton)
        noiton.setOnClickListener {
            val url = "https://holy-seatbelt-ff0.notion.site/where42-Android-d776288e21a0407dbbf1dc237063e306?pvs=4"
            // 웹 페이지로 이동하는 Intent 생성
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            // Intent 실행
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}