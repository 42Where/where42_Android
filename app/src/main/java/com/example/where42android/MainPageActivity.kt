package com.example.where42android

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainPageActivity : AppCompatActivity() {

    var groupProfileList = arrayListOf<profile_list>(
        profile_list("Jaeyojun","handsome", "퇴근", "profile_photo_example",),
        profile_list("Jooypark","beautiful", "퇴근", "profile_photo_example" ),
        profile_list("jaju","graphics master", "개포 c2r5s6", "profile_photo_example"),
    )

    var friendProfileList = arrayListOf<profile_list>(
        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
        profile_list("Jaeyojun","handsome", "퇴근", "profile_photo_example",),
        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
        profile_list("Jooypark","beautiful", "개포 c2r5s6", "profile_photo_example" ),
        profile_list("jaju","graphics master", "퇴근", "profile_photo_example"),
    )

    var isFilterChecked = false

    var isGroupListVisible = false
    var isFriendListVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        /* header의 환경 설정 버튼을 눌렀을 때 */
        val settingButton : ImageButton = this.findViewById(R.id.setting_button)
        settingButton.setOnClickListener{
            try {
                val intent = Intent(this, SettingPage::class.java)
                startActivity(intent)
                finish()
            }
            catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }

        }

        /*새 그룹 기능 구현*/
        val newGroupButton: Button = this.findViewById(R.id.newGroupButton)

        newGroupButton.setOnClickListener {
            try {
                val builder = AlertDialog.Builder(this)
                    .setTitle("새 그룹 만들기")
                val type_view = layoutInflater.inflate(R.layout.new_group_popup, null)
                val editText = type_view.findViewById<EditText>(R.id.editText)
                builder.setView(type_view)
                val listener = DialogInterface.OnClickListener { _, _ ->
                    val userInput = editText.text.toString()
                    Toast.makeText(this, "$userInput", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CreateGroupActivity::class.java)
                    intent.putExtra("userInputKey", userInput)
                    startActivity(intent)
                }
                builder.setNegativeButton("취소", null)
                builder.setPositiveButton("확인", listener)
                builder.show()
            }catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        /* footer의 홈버튼과 검색 버튼 기능 구현 */
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


        /* rcyclerView 기능 구현*/
        val groupRecyclerView: RecyclerView = findViewById<RecyclerView?>(R.id.group_list)
        val groupRecyclerViewAdapter = RecyclerViewAdapter(this, groupProfileList, isFilterChecked)
        groupRecyclerView.layoutManager = LinearLayoutManager(this)
        groupRecyclerView.adapter = groupRecyclerViewAdapter

        val friendRecyclerView: RecyclerView = findViewById<RecyclerView?>(R.id.friend_list)
        val friendRecyclerViewAdapter = RecyclerViewAdapter(this, friendProfileList, isFilterChecked)
        friendRecyclerView.layoutManager = LinearLayoutManager(this)
        friendRecyclerView.adapter = friendRecyclerViewAdapter

        val checkBox: CheckBox = findViewById(R.id.checkBox)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            isFilterChecked = isChecked
            groupRecyclerView.adapter = RecyclerViewAdapter(this, groupProfileList, isFilterChecked)
            friendRecyclerView.adapter = RecyclerViewAdapter(this, friendProfileList, isFilterChecked)
        }

        val groupToggleButton: ImageButton = findViewById(R.id.group_toggle)
        groupToggleButton.setOnClickListener {
            if (isGroupListVisible) {
                groupRecyclerView.visibility = View.GONE
            } else {
                groupRecyclerView.visibility = View.VISIBLE
            }
            isGroupListVisible = !isGroupListVisible
        }

        val friendToggleButton: ImageButton = findViewById(R.id.friend_toggle)
        friendToggleButton.setOnClickListener {
            if (isFriendListVisible) {
                friendRecyclerView.visibility = View.GONE
            } else {
                friendRecyclerView.visibility = View.VISIBLE
            }
            isFriendListVisible = !isFriendListVisible
        }
    }
}