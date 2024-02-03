package com.example.where42android


import SharedViewModel_GroupsMembersList
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.Base_url_api_Retrofit.AddMembersRequest
import com.example.where42android.Base_url_api_Retrofit.Deafult_friendGroup_memberlist
import com.example.where42android.Base_url_api_Retrofit.GroupAddMemberlist
import com.example.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.example.where42android.adapter.RecyclerViewAdapter_defaultList

import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.Base_url_api_Retrofit.addMembersResponse
import com.example.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import com.example.where42android.main.MainPageActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel_GroupsMembersList
//    private val retrofitAPI = RetrofitConnection.getInstance().create(MemberallListService::class.java)
    val userSettings = UserSettings.getInstance()
    private val retrofitAPI = RetrofitConnection.getInstance(userSettings.token).create(Deafult_friendGroup_memberlist::class.java)
//    private val retrofitAPI2 = RetrofitConnection.getInstance().create(GroupAddMemberlist::class.java)

    val friendProfileList = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()
    private fun getSelectedItems(): List<friendGroup_default_memberlist.friendGroup_default_memberlistItem> {
        val selectedItems = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()
        val friendRecyclerView: RecyclerView = findViewById(R.id.new_gorup_friend_list)
        val friendRecyclerViewAdapter = friendRecyclerView.adapter as? RecyclerViewAdapter_defaultList

        friendRecyclerViewAdapter?.let {
            for (item in friendProfileList) {
                if (item in it.checkedItems) {
                    selectedItems.add(item)
                }
            }
        }

        return selectedItems
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        //1. MainPageActivity.kt에서 값 받아오기
//        val newGroupResponseGroupId = intent.getIntExtra("newgroupIdKey", -1)
        val newgroupName = intent.getStringExtra("newgroupNameKey")
        val profileIntraId = intent.getIntExtra("profileintraIdKey", -1)
        val groupId = intent.getIntExtra("groupIdKey", -1)
        //여기로 groupid 받아옴 여기에 이제 checkbox 선택된 members 넣어주면 됨
        Log.e("check_newGroup", "recive :  ${groupId.toString()}")

//        Log.e("check_newGroup", "recive :  ${newGroupResponseGroupId}")
//        Log.e("ID : groupId", newGroupResponseGroupId.toString())

        //2. 그룹 만들기
        val newGroupRequest = NewGroupRequest(newgroupName.toString(), profileIntraId)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel_GroupsMembersList::class.java)
        sharedViewModel.addGroup(newGroupRequest)

        //3.default 그룹 보여주기
        fetchMemberAllData(groupId)

        //4. checkbox 체크한 것만 들고오고 그룹에 멤버 추가 api
        val createGroupButton: AppCompatButton = findViewById(R.id.new_group_make)
        createGroupButton.setOnClickListener {
            //2. 그룹 만들기 API 호출

            // 체크된 체크박스가 있는 항목들 가져오기
            val selectedItems = getSelectedItems()

            //조건문 해야될 듯 만약 list에 아무것도 없으면 리턴 시키기
            // 여기서 가져온 선택된 항목들에 대해 원하는 작업 수행 가능
            // 예를 들어, 선택된 항목들의 이름을 로그에 출력하는 등의 동작 수행

//            val members =  mutableListOf<String>()
//            selectedItems.forEach { selectedItem ->
//                Log.d("선택된 항목", "이름: ${selectedItem.intraName}")
////                members.add(selectedItem.memberIntraName)
//                members.add(selectedItem.intraName)
//            }

            val members =  mutableListOf<Int>()
            selectedItems.forEach { selectedItem ->
                Log.d("선택된 항목", "이름: ${selectedItem.intraName}")
//                members.add(selectedItem.memberIntraName)
                members.add(selectedItem.intraId)
            }

            //newGroupResponseGroupId가 nullable한 문자열(String?)인 경우 .toInt()를 호출할 때 null이 반환될 수 있습니다.
            //
            //이러한 상황을 처리하기 위해 안전한 방법으로 nullable 체크를 하고 값을 가져오는 것이 좋습니다. Kotlin에서는 nullable인 변수를 안전하게 처리하기 위해 다음과 같은 방법을 사용할 수 있습니다:
            //
            //Safe call operator (?.): ?.를 사용하여 nullable한 변수에 안전하게 접근합니다.
            //Elvis operator (?:): ?:를 사용하여 nullable한 경우 대체값을 지정할 수 있습니다.
            //Safe cast operator (as?): as?를 사용하여 안전한 형변환을 시도합니다.

//            val groupIdInt = newGroupResponseGroupId

            //넘겨줄 grorupid, members JSON으로 만들기

//            Log.d("check", "groupInt : ${groupIdInt} , members : ${members}")
//            val groupId_members = AddMembersRequest(groupIdInt, members)
//            Log.d("check", "groupId_members : ${groupId_members}")

            sharedViewModel = ViewModelProvider(this).get(SharedViewModel_GroupsMembersList::class.java)
//            sharedViewModel.addMembersToGroup(groupId_members)
            sharedViewModel.addMembersToGroup(newgroupName.toString(), members)
            finish()
        }

        Log.d("CALL", "fucking here")


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

    // Retrofit을 통한 API 호출 함수
    private fun fetchMemberAllData(groupId:Int) {
//
        retrofitAPI.getdefaultGroupList(groupId).enqueue(object :
            Callback<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>> {
            override fun onResponse(
                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
                response: Response<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
            ) {
                if (response.isSuccessful)
                {
                    Log.d("CALL", "fucking here3")
                    Log.d("CALL2", "API call successful. Response: $response")
                    val friendList = response.body()
                    friendList?.let { members ->
                        // 받은 멤버 데이터를 friendProfileList에 추가
                        for (member in members) {
                            friendProfileList.add(member)
                        }
                        updateAdapterData(friendProfileList)
                    }
                }
                else
                {
                    Log.d("API Error", "API call successful. Response: $response")
                }
            }
            override fun onFailure(
                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
                t: Throwable)
            {
                // API 요청 자체가 실패한 경우 처리
            }
        })
    }

//        retrofitAPI.getMemberAllList().enqueue(object : Callback<List<MemberAll.MemberAllItem>> {
//            override fun onResponse(
//                call: Call<List<MemberAll.MemberAllItem>>,
//                response: Response<List<MemberAll.MemberAllItem>>
//            ) {
//                Log.d("CALL3", "fucking here3")
//                if (response.isSuccessful) {
//                    Log.d("CALL", "fucking here3")
//                    Log.d("CALL2", "API call successful. Response: $response")
//                    val memberAllList  = response.body()
//                    memberAllList?.let { members ->
//
//
//                        // 받은 멤버 데이터를 friendProfileList에 추가
//                        for (member in members) {
//                            friendProfileList.add(member)
//                        }
//                        updateAdapterData(friendProfileList)
//                        //                    friendProfileList.clear() // 기존 데이터를 초기화
////                    response.body()?.let { members ->
////                        friendProfileList.addAll(members) // 받은 멤버 데이터를 리스트에 추가
////                        // 어댑터에 데이터가 갱신되었음을 알리고 UI를 업데이트할 수 있도록 호출
////                        updateAdapterData(friendProfileList)
////                    }
//                    }
//                } else {
//                    Log.d("CALL", "fucking here4")
//                    // API 요청은 성공했지만 응답이 성공적이지 않은 경우 처리
//                    Log.e("CALL2", "API call failed with code: ${response.code()}")
//                }
//            }
//            override fun onFailure(call: Call<List<MemberAll.MemberAllItem>>, t: Throwable) {
//                // API 요청 자체가 실패한 경우 처리
//            }
//        })
//    }
    private fun updateAdapterData(data: List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>) {
        val friendRecyclerView: RecyclerView = findViewById(R.id.new_gorup_friend_list)
        val friendRecyclerViewAdapter = RecyclerViewAdapter_defaultList(this, data, false) // 데이터 타입 변경
        friendRecyclerView.layoutManager = LinearLayoutManager(this)
        friendRecyclerView.adapter = friendRecyclerViewAdapter
    }
}

