package com.seoul.where42android.main


import SharedViewModel_GroupsMembersList
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainCreateGroupActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel_GroupsMembersList
//    private val retrofitAPI = RetrofitConnection.getInstance().create(MemberallListService::class.java)
    val userSettings = UserSettings.getInstance()
    private val retrofitAPI = RetrofitConnection.getInstance(userSettings.token).create(Deafult_friendGroup_memberlist::class.java)
//    private val retrofitAPI2 = RetrofitConnection.getInstance().create(GroupAddMemberlist::class.java)

    val friendProfileList = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()


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


        //2. 그룹 만들기
        val newGroupRequest = NewGroupRequest(newgroupName.toString(), profileIntraId)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel_GroupsMembersList::class.java)
        sharedViewModel.addGroup(newGroupRequest)

        //3.default 그룹 보여주기
        fetchMemberAllData(groupId)

        //4. checkbox 체크한 것만 들고오고 그룹에 멤버 추가 api
        val createGroupButton: AppCompatButton = findViewById(R.id.new_group_make)
        createGroupButton.visibility = View.GONE
        createGroupButton.setOnClickListener {
            sharedViewModel = ViewModelProvider(this).get(SharedViewModel_GroupsMembersList::class.java)
//            sharedViewModel.addMembersToGroup(groupId_members)
            Log.d("whatproblem", " members : ${friendCheckedList.getfriendCheckedList()}")
            sharedViewModel.addMembersToGroup(newgroupName.toString(), friendCheckedList.getfriendCheckedList())
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
                            if (member.location == null)
                            {
                                if (member.inCluster == true)
                                {
                                    member.location = "개포 클러스터 내"
                                }
                                else
                                {
                                    member.location = "퇴근"
                                }
                            }
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

