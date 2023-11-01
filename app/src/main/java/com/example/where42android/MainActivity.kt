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
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.TextView
import android.util.TypedValue

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        fun resizeButtonToScreenRatio(button: Button, widthDP: Float, heightDP: Float) {
//            val displayMetrics = button.context.resources.displayMetrics
//            val screenWidth = displayMetrics.widthPixels.toFloat()
//            val screenHeight = displayMetrics.heightPixels.toFloat()
//
//            val widthRatio = widthDP / screenWidth
//            val heightRatio = heightDP / screenHeight
//
//            val buttonWidth = screenWidth * widthRatio
//            val buttonHeight = screenHeight * heightRatio
//
//            val params = button.layoutParams
//            params.width = buttonWidth.toInt()
//            params.height = buttonHeight.toInt()
//            button.layoutParams = params
//        }
//
//        fun resizeTextViewToScreenRatio(textView: TextView, textSizeDP: Float) {
//            val displayMetrics = textView.context.resources.displayMetrics
//            val screenWidth = displayMetrics.widthPixels.toFloat()
//            val screenHeight = displayMetrics.heightPixels.toFloat()
//
//            val textSizeRatio = textSizeDP / screenWidth
//            val newTextSize = screenWidth * textSizeRatio
//
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize)
//        }
//
//        fun resizeImageViewToScreenRatio(imageView: ImageView, widthPercent: Float, heightPercent: Float) {
//            val displayMetrics = imageView.context.resources.displayMetrics
//            val screenWidth = displayMetrics.widthPixels.toFloat()
//            val screenHeight = displayMetrics.heightPixels.toFloat()
//
//            val width = screenWidth * widthPercent
//            val height = screenHeight * heightPercent
//
//            val params = imageView.layoutParams
//            params.width = width.toInt()
//            params.height = height.toInt()
//            imageView.layoutParams = params
//        }



        // 버튼을 찾아서 사이즈를 조정하는 예시
//        val myButton = findViewById<Button>(R.id.login_button)
//        resizeButtonToScreenRatio(myButton, 324f, 137f)
//        val where = findViewById<TextView>(R.id.main_logo_text)
//        resizeTextViewToScreenRatio(where, 65f)
//        val  location = findViewById<TextView>(R.id.intro_text)
//        resizeTextViewToScreenRatio(location, 28f)
//        val myImageView = findViewById<ImageView>(R.id.help_button)
//        resizeImageViewToScreenRatio(myImageView, 0.1f, 0.05f)
//
//
//        fun resizeButtonTextToScreenRatio(button: Button, textSizePercent: Float) {
//            val displayMetrics = button.context.resources.displayMetrics
//            val screenWidth = displayMetrics.widthPixels.toFloat()
//
//            val newTextSize = screenWidth * textSizePercent
//
//            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize)
//        }
//        val myButton = findViewById<Button>(R.id.login_button)
//        resizeButtonTextToScreenRatio(myButton, 0.05f) // 5% 크기로 텍스트 크기를 조절
//
//

        val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }, 3000)
    }
}
