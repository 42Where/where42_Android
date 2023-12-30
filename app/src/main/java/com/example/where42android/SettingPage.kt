package com.example.where42android

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.where42android.Base_url_api_Retrofit.ApiObject
import com.example.where42android.Base_url_api_Retrofit.CommentChangeMember
import com.example.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_page)

        fun adjustTextViewSize(textView: TextView) {
            val displayMetrics = textView.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels

            val desiredWidth = screenWidth * 0.412 // 원하는 폭 (현재 레이아웃에서의 비율)
            val text = textView.text.toString()

            // 적절한 텍스트 크기 계산
            var textSize = 74 // 초기 텍스트 크기
            var paint = Paint()
            paint.textSize = textSize.toFloat()

            while (paint.measureText(text) > desiredWidth) {
                textSize--
                paint.textSize = textSize.toFloat()
            }

            // TextView에 적절한 텍스트 크기 설정
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }
        val myTextView = findViewById<TextView>(R.id.text_setting)
        adjustTextViewSize(myTextView)


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
//        val myTextView = findViewById<TextView>(R.id.text_setting)
//        val textSizeInDP = 16.0f // 설정하려는 텍스트 크기 (DP 단위)
//
//        resizeTextViewToScreenRatio(myTextView, textSizeInDP)

        /*footer 홈, 검색 버튼*/
        val homeButton: ImageButton = this.findViewById(R.id.home_button)

        homeButton.setOnClickListener {
            try {
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }


        }

        val searchButton: ImageButton = this.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            try {
                //Toast.makeText(this, "버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SearchPage::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val commentButton: Button = this.findViewById(R.id.comment_button)

        commentButton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.new_group_popup)

            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title = dialog.findViewById<TextView>(R.id.title)
            title.text = "코멘트 설정"

            val editText = dialog.findViewById<EditText>(R.id.input)
            val btnCancel = dialog.findViewById<Button>(R.id.cancel)
            val btnSubmit = dialog.findViewById<Button>(R.id.submit)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSubmit.setOnClickListener {
                val comment :String = editText.text.toString()
                //intraid 바꿔주기
                //JSON 만들기
                val updateRequest = UpdateCommentRequest(6, comment)

                val call = ApiObject.service.updateMemberComment(updateRequest)
                call.enqueue(object : Callback<CommentChangeMember> {
                    override fun onResponse(
                        call: Call<CommentChangeMember>,
                        response: Response<CommentChangeMember>
                    ) {
                        // 성공적으로 응답을 받았을 때 처리
                        if (response.isSuccessful) {
                            val result = response.body()
                            // 처리할 작업 수행
                        } else {
                            // 응답은 받았지만 실패했을 때 처리
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = response.message() // HTTP 에러 메시지
                            val errorCode = response.code() // HTTP 에러 코드

                            val detailedErrorMessage =
                                "Failed to update comment. Error code: $errorCode, Message: $errorMessage, Error Body: $errorBody"
                            Log.e("Update Error", detailedErrorMessage)
                        }
                    }

                    override fun onFailure(call: Call<CommentChangeMember>, t: Throwable) {
                        // 요청 자체가 실패했을 때 처리
                        val errorMessage = "코멘트가 설정 되지 않아요! 안 되면 where42 team을 찾아주세요!."
                        Toast.makeText(this@SettingPage, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("Network Error", "Retrofit Failure: ${t.message}")
                    }
                })
                dialog.dismiss()

                val intent = Intent(this@SettingPage, MainPageActivity::class.java)
                finish() //인텐트 종료
                startActivity(intent)

            }
            dialog.show()
        }
    }
}