package com.example.where42android

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton

class SettingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_page)

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

        /* 코멘트 설정 버튼을 눌렀을 떄 기능 구현해야함. 주여이 팝업 기능 들고오기*/
        //val commentbutton: AppCompatButton = this.findViewById(R.id.comment_button)
        val commentButton: Button = this.findViewById(R.id.comment_button)

        commentButton.setOnClickListener {
            try {
                val builder = AlertDialog.Builder(this)
                    .setTitle("코멘트 설정")
                val type_view = layoutInflater.inflate(R.layout.new_group_popup, null)
                val editText = type_view.findViewById<EditText>(R.id.editText)
                builder.setView(type_view)
                val listener = DialogInterface.OnClickListener { _, _ ->
                    val userInput = editText.text.toString()
                    Toast.makeText(this, "$userInput", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("취소", null)
                builder.setPositiveButton("확인", listener)
                builder.show()
            }catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}