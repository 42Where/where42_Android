package com.example.where42android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ListView
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