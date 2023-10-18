package com.example.where42android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }, 3000)
    }
}
