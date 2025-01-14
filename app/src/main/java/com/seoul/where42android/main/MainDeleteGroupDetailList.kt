package com.seoul.where42android.main

import com.seoul.where42android.ViewModel.SharedViewModelGroupsMembers
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seoul.where42android.Base_url_api_Retrofit.Deafult_friendGroup_memberlist
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import com.seoul.where42android.R
import com.seoul.where42android.adapter.RecyclerViewCreatGroupActivity
import com.seoul.where42android.utils.ApiUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainDeleteGroupDetailList : AppCompatActivity() {

    val friendProfileList = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()

    override fun onBackPressed() {
        super.onBackPressed()
        // 여기에 원하는 동작을 추가.
        friendCheckedList.clearItem()
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deletegroup_detail_list)

//        viewModel = ViewModelProvider(this).get(GroupDetailViewModel::class.java)

        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            try {
                val intent = Intent(this, MainPageActivity::class.java)
                friendCheckedList.clearItem()
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        val groupIdNumber: Number = intent.getIntExtra("GROUP_ID", -1)
        val groupId : Int = groupIdNumber.toInt()

        val createGroupButton: AppCompatButton = findViewById(R.id.delete_group_member)
        val recyclerview : RecyclerView = findViewById(R.id.new_gorup_friend_list)
        val nosearchmember : TextView = findViewById(R.id.noItemsTextView)
        nosearchmember.text = "그룹에 삭제할 친구가 없습니다."
        //1. group member 보여주기
        fetchMemberAllData(groupId){ isSuccess ->
            if (isSuccess)
            {
//                createGroupButton.visibility = View.VISIBLE
                recyclerview.visibility = View.VISIBLE
                nosearchmember.visibility = View.GONE
            }
            else
            {
                createGroupButton.visibility = View.GONE
                recyclerview.visibility = View.GONE
                nosearchmember.visibility = View.VISIBLE
            }
        }

        val groupName: String? = intent.getStringExtra("GROUP_NAME")
        val groupChangeName : TextView = findViewById(R.id.GroupName)
        groupChangeName.text = groupName
        createGroupButton.visibility = View.GONE
        createGroupButton.setOnClickListener {

            val sharedViewModel = ViewModelProvider(this).get(SharedViewModelGroupsMembers::class.java)
//            sharedViewModel.addMembersToGroup(groupId_members)
//            sharedViewModel.deleteFriendGroup(groupId, members)
            sharedViewModel.deleteFriendGroup(groupId, friendCheckedList.getfriendCheckedList(), this@MainDeleteGroupDetailList)
            finish()
        }

        //검색바 기능
        val searchView: SearchView = findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = ArrayList<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()

                // 검색어에 따라 데이터 필터링
                newText?.let { query ->
                    val searchText = query.toLowerCase().trim() // 입력된 검색어 소문자로 변환
                    for (member in friendProfileList) {
                        val memberName = member.intraName.toLowerCase()
                        if (memberName.contains(searchText)) {
                            filteredList.add(member)
                        }
                    }
                }
                // 어댑터에 필터링된 데이터 업데이트
                updateAdapterData(filteredList)
                return true
            }

        })

    }

    //group 안에 member 보여주기
    private fun fetchMemberAllData(groupId: Int, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ApiUtils를 사용하여 API 요청 수행
                val response = ApiUtils.performApiRequest(this@MainDeleteGroupDetailList) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken)
                        .create(Deafult_friendGroup_memberlist::class.java)
                    retrofitAPI.getdefaultGroupList(groupId)
                }

                // API 응답 처리
                if (response != null && response.isSuccessful) {
                    val friendList = response.body()
                    if (friendList != null) {
                        if (friendList.isEmpty()) {
                            withContext(Dispatchers.Main) { callback(false) }
                        } else {
                            // 받은 멤버 데이터를 friendProfileList에 추가
                            friendList.forEach { member ->
                                member.location = member.location ?: if (member.inCluster == true) "개포" else "퇴근"
                                friendProfileList.add(member)
                            }
                            // 어댑터 데이터 업데이트
                            withContext(Dispatchers.Main) {
                                updateAdapterData(friendProfileList)
                                callback(true) // 성공적으로 처리되었으므로 true 반환
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) { callback(false) }
                    }
                } else {
                    // API 요청 실패 처리
                    withContext(Dispatchers.Main) { callback(false) }
                    Log.e("API Error", "API 요청 실패: ${response?.code()} - ${response?.message()}")
                }
            } catch (e: Exception) {
                // 예외 처리
                withContext(Dispatchers.Main) {
                    callback(false)
                    Log.e("ApiError", "fetchMemberAllData API 호출 중 오류 발생", e)
                }
            }
        }
    }

    //    private fun fetchMemberAllData(groupId:Int, callback: (Boolean) -> Unit) {
////
//        val userSettings = UserSettings.getInstance()
//        val retrofitAPI =
//            RetrofitConnection.getInstance(userSettings.token).create(Deafult_friendGroup_memberlist::class.java)
//        retrofitAPI.getdefaultGroupList(groupId).enqueue(object :
//            Callback<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>> {
//            override fun onResponse(
//                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
//                response: Response<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
//            ) {
//                if (response.isSuccessful)
//                {
////                    Log.d("CALL", "fucking here3")
////                    Log.d("CALL2", "API call successful. Response: $response")
//                    val friendList = response.body()
//                    friendList?.let { members ->
//                        // 받은 멤버 데이터를 friendProfileList에 추가
//                        for (member in members) {
//                            if (member.location == null)
//                            {
//                                if (member.inCluster == true)
//                                {
//                                    member.location = "개포"
//                                }
//                                else
//                                {
//                                    member.location = "퇴근"
//                                }
//                            }
//                            friendProfileList.add(member)
//                        }
//                        updateAdapterData(friendProfileList)
//                        callback(true) // 성공적으로 처리되었으므로 true를 콜백으로 반환
//                    }
//                    if (friendList != null) {
//                        if (friendList.isEmpty()) {
//                            callback (false)
//                        }
//                        else
//                        {
//                            callback (true)
//                        }
//                    }
//                }
//                else
//                {
////                    Log.d("API Error", "API call successful. Response: $response")
//                    callback(false)
//                }
//            }
//            override fun onFailure(
//                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
//                t: Throwable)
//            {
//                // API 요청 자체가 실패한 경우 처리
//                callback(false)
//            }
//        })
//    }
    private fun updateAdapterData(data: List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>) {
        val friendRecyclerView: RecyclerView = findViewById(R.id.new_gorup_friend_list)
        val friendRecyclerViewAdapter = RecyclerViewCreatGroupActivity(this, data, false) // 데이터 타입 변경
        friendRecyclerView.layoutManager = LinearLayoutManager(this)
        friendRecyclerView.adapter = friendRecyclerViewAdapter

        friendRecyclerViewAdapter.setOnCheckBoxClickListener { isChecked, position ->
            // 클릭 이벤트 처리 코드 작성
            val deleteGroupButton: AppCompatButton = findViewById(R.id.delete_group_member)
            if (isChecked)
            {
                deleteGroupButton.visibility = View.VISIBLE
            }
            else
            {
                if (friendCheckedList.sizefriendCheckedList() == 0)
                {
                    deleteGroupButton.visibility=View.GONE
                }
            }
        }

    }
}