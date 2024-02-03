package com.example.where42android.main


import SharedViewModel_GroupsMembersList
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.Base_url_api_Retrofit.Deafult_friendGroup_memberlist
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import com.example.where42android.R
import com.example.where42android.UserSettings
import com.example.where42android.adapter.RecyclerViewAdapter_defaultList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainAddGroupDetailList : AppCompatActivity() {

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
        setContentView(R.layout.activity_deletegroup_detail_list)

//        viewModel = ViewModelProvider(this).get(GroupDetailViewModel::class.java)


        val groupIdNumber: Number = intent.getIntExtra("GROUP_ID", -1)
        val groupId : Int = groupIdNumber.toInt()
        Log.e("herehere", "here : ${groupId}")


        //1. group member 보여주기 -> v3/group/groupmember/not-ingroup?groupId=(groupId) 사용
        fetchMemberAllData(groupId)

        val groupName: String? = intent.getStringExtra("GROUP_NAME")
        val groupChangeName : TextView = findViewById(R.id.GroupName)
        groupChangeName.text = groupName

        val createGroupButton: AppCompatButton = findViewById(R.id.delete_group_member)
        createGroupButton.text = "멤버 추가하기"

        // 색상 리소스 가져오기
        // 배경색 설정
        val backgroundColor = ContextCompat.getColor(this, R.color.blue_add)
        createGroupButton.setBackgroundColor(backgroundColor)

        createGroupButton.setOnClickListener {
            //2. 그룹 만들기 API 호출

            // 체크된 체크박스가 있는 항목들 가져오기
            val selectedItems = getSelectedItems()

            val members =  mutableListOf<Int>()
            selectedItems.forEach { selectedItem ->
                Log.d("선택된 항목", "이름: ${selectedItem.intraName}")
                members.add(selectedItem.intraId)
            }
            val sharedViewModel = ViewModelProvider(this).get(SharedViewModel_GroupsMembersList::class.java)

//            sharedViewModel.deleteFriendGroup(groupId, members)
            if (groupName != null) {
                sharedViewModel.addMembersToGroup(groupName, members)
                finish()
            }
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
    private fun fetchMemberAllData(groupId:Int) {
//
        val userSettings = UserSettings.getInstance()
        val retrofitAPI =
            RetrofitConnection.getInstance(userSettings.token).create(Deafult_friendGroup_memberlist::class.java)
        retrofitAPI.getGroupMembersNotInGroup(groupId).enqueue(object :
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
    private fun updateAdapterData(data: List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>) {
        val friendRecyclerView: RecyclerView = findViewById(R.id.new_gorup_friend_list)
        val friendRecyclerViewAdapter = RecyclerViewAdapter_defaultList(this, data, false) // 데이터 타입 변경
        friendRecyclerView.layoutManager = LinearLayoutManager(this)
        friendRecyclerView.adapter = friendRecyclerViewAdapter
    }
}