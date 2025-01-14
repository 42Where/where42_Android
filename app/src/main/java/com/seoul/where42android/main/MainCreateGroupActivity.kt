package com.seoul.where42android.main


import com.seoul.where42android.ViewModel.SharedViewModelGroupsMembers
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seoul.where42android.Base_url_api_Retrofit.Deafult_friendGroup_memberlist
import com.seoul.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import com.seoul.where42android.R
import com.seoul.where42android.adapter.RecyclerViewCreatGroupActivity
import com.seoul.where42android.utils.ApiUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainCreateGroupActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModelGroupsMembers
    val userSettings = UserSettings.getInstance()
    val friendProfileList = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        //1. MainPageActivity.kt에서 값 받아오기
        val newgroupName = intent.getStringExtra("newgroupName")
        val defaultgroupIdKey = intent.getIntExtra("defaultgroupId", -1)
        Log.d("addGroup", "newgroupName2 = ${newgroupName}")
        //2. 그룹 만들기
        val newGroupRequest = NewGroupRequest(newgroupName.toString())
        sharedViewModel = ViewModelProvider(this).get(SharedViewModelGroupsMembers::class.java)
        sharedViewModel.addGroup(newGroupRequest, this@MainCreateGroupActivity)

        //3.default 그룹 보여주기
        fetchMemberAllData(defaultgroupIdKey)

        //4. checkbox 체크한 것만 들고오고 그룹에 멤버 추가 api
        val createGroupButton: AppCompatButton = findViewById(R.id.new_group_make)
        createGroupButton.visibility = View.GONE
        createGroupButton.setOnClickListener {
            sharedViewModel.addMembersToGroup(newgroupName.toString(), friendCheckedList.getfriendCheckedList(), this@MainCreateGroupActivity)
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
                        if (memberName.contains(searchText))
                        {
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

    // Retrofit을 통한 API 호출 함수
    private fun fetchMemberAllData(groupId:Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ApiUtils를 사용하여 API 요청 수행
                val response = ApiUtils.performApiRequest(this@MainCreateGroupActivity) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken)
                        .create(Deafult_friendGroup_memberlist::class.java)
                    retrofitAPI.getdefaultGroupList(groupId)
                }

                if (response != null && response.isSuccessful) {
                    val friendList = response.body()
                    friendList?.let { members ->
                        // 받은 멤버 데이터를 friendProfileList에 추가
                        members.forEach { member ->
                            if (member.location == null) {
                                member.location = if (member.inCluster == true) "개포" else "퇴근"
                            }
                            friendProfileList.add(member)
                        }
                        withContext(Dispatchers.Main){
                            updateAdapterData(friendProfileList)
                        }
                    }
                } else {
                    Log.e(
                        "ApiError",
                        "API 요청 실패: ${response?.code()} - ${response?.message() ?: "응답 없음"}"
                    )
                }
            } catch (e: Exception) {
                Log.e("ApiError", "API 호출 중 오류 발생", e)
            }
        }

//
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
//                    }
//                }
//                else
//                {
////                    Log.d("API Error", "API call successful. Response: $response")
//                }
//            }
//            override fun onFailure(
//                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
//                t: Throwable)
//            {
//                // API 요청 자체가 실패한 경우 처리
//            }
//        })
    }
    private fun updateAdapterData(data: List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>) {
        val friendRecyclerView: RecyclerView = findViewById(R.id.new_gorup_friend_list)
        val friendRecyclerViewAdapter = RecyclerViewCreatGroupActivity(this, data, false) // 데이터 타입 변경
        friendRecyclerView.layoutManager = LinearLayoutManager(this)
        friendRecyclerView.adapter = friendRecyclerViewAdapter

        friendRecyclerViewAdapter.setOnCheckBoxClickListener { isChecked, position ->
            // 클릭 이벤트 처리 코드 작성
            val createGroupButton: AppCompatButton = findViewById(R.id.new_group_make)
            if (isChecked)
            {
                createGroupButton.visibility = View.VISIBLE
            }
            else
            {
                if (friendCheckedList.sizefriendCheckedList() == 0)
                {
                    createGroupButton.visibility=View.GONE
                }
            }
        }
    }
}

