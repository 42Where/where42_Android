package com.example.where42android

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainPageActivity : AppCompatActivity() {

    var profileList = arrayListOf<profile_list>(
        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
        profile_list("Jooypark","beautiful", "개포 c2r5s6", "profile_photo_example" ),
        profile_list("jaju","graphics master", "개포 c2r5s6", "profile_photo_example"),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val mainListView: ListView = findViewById(R.id.peopleListView)
        val ListAdapter = ListViewAdapter(this, profileList)
        mainListView.setAdapter(ListAdapter)

        val searchButton: ImageButton = this.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            try {
                //Toast.makeText(this, "버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SearchPage::class.java)
                startActivity(intent)
            }catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val homeButton: ImageButton = this.findViewById(R.id.home_button)

        homeButton.setOnClickListener {
            try {
                if (this::class.java != MainPageActivity::class.java) {
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                    Toast.makeText(this, "이미 로그인 페이지에 있습니다!.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}