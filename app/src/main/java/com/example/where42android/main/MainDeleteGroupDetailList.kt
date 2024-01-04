package com.example.where42android.main

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.LiveData.GroupDetailViewModel
import com.example.where42android.R
import com.example.where42android.adapter.GroupMemberAdapter

class MainDeleteGroupDetailList : AppCompatActivity() {

    private lateinit var viewModel: GroupDetailViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupMemberAdapter // GroupMemberAdapter는 RecyclerView에 데이터를 제공하는 역할을 합니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deletegroup_detail_list)

        viewModel = ViewModelProvider(this).get(GroupDetailViewModel::class.java)

        // RecyclerView 및 Adapter 설정
        recyclerView = findViewById(R.id.new_gorup_friend_list)
        adapter = GroupMemberAdapter(viewModel.defaultGroupMemberLiveData) // 여기서 GroupMemberAdapter는 RecyclerView.Adapter를 상속한 커스텀 Adapter입니다.
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저 설정

        val groupIdNumber: Number = intent.getIntExtra("GROUP_ID", -1)
        val groupId : Int = groupIdNumber.toInt()
        // ViewModel을 통해 데이터 가져오기
        viewModel.defaultGroupMemberLiveData.observe(this, { memberList ->
            // 데이터가 변경될 때마다 실행됨
            // Adapter에 데이터 설정하여 RecyclerView 업데이트
            adapter.submitList(memberList)
        })

        // 데이터 요청
        viewModel.getDefaultGroupMemberList(groupId)

        val groupName: String? = intent.getStringExtra("GROUP_NAME")
        val groupChangeName : TextView = findViewById(R.id.GroupName)
        groupChangeName.text = groupName


    }
}