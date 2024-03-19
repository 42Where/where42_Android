package com.seoul.where42android.main


import SharedViewModel_GroupsMembersList
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainAddGroupDetailList : AppCompatActivity() {

    val friendProfileList = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()

//    private fun getSelectedItems(): List<friendGroup_default_memberlist.friendGroup_default_memberlistItem> {
//        val selectedItems = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()
//        val friendRecyclerView: RecyclerView = findViewById(R.id.new_gorup_friend_list)
//        val friendRecyclerViewAdapter = friendRecyclerView.adapter as? RecyclerViewAdapter_defaultList
//
//        friendRecyclerViewAdapter?.let {
//            for (item in friendProfileList) {
//                if (item in it.checkedItems) {
//                    selectedItems.add(item)
//                }
//            }
//        }
//
//        return selectedItems
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addgroup_detail_list)

//        viewModel = ViewModelProvider(this).get(GroupDetailViewModel::class.java)

        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            try {
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        val groupIdNumber: Number = intent.getIntExtra("GROUP_ID", -1)
        val groupId : Int = groupIdNumber.toInt()
//        Log.e("herehere", "here : ${groupId}")

        val createGroupButton: AppCompatButton = findViewById(R.id.delete_group_member)
        val recyclerview : RecyclerView = findViewById(R.id.new_gorup_friend_list)
        val nosearchmember : TextView = findViewById(R.id.noItemsTextView)
        //1. group member 보여주기 -> v3/group/groupmember/not-ingroup?groupId=(groupId) 사용
        fetchMemberAllData(groupId) { isSuccess ->
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

        // 색상 리소스 가져오기
        // 배경색 설정
        createGroupButton.visibility = View.GONE
        createGroupButton.setOnClickListener {
            //2. 그룹 만들기 API 호출

            // 체크된 체크박스가 있는 항목들 가져오기
//            val selectedItems = getSelectedItems()
//
//            val members =  mutableListOf<Int>()
//            selectedItems.forEach { selectedItem ->
////                Log.d("선택된 항목", "이름: ${selectedItem.intraName}")
//                members.add(selectedItem.intraId)
//            }
            val sharedViewModel = ViewModelProvider(this).get(SharedViewModel_GroupsMembersList::class.java)

//            sharedViewModel.deleteFriendGroup(groupId, members)
            Log.d("whatproblem", " members : ${friendCheckedList.getfriendCheckedList()}")
            if (groupName != null) {
//                sharedViewModel.addMembersToGroup(groupName, members)
                sharedViewModel.addMembersToGroup(groupName, friendCheckedList.getfriendCheckedList())
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
//                    val searchText = query.toLowerCase().trim() // 입력된 검색어 소문자로 변환
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
    private fun fetchMemberAllData(groupId:Int, callback: (Boolean) -> Unit) {
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
                    Log.d("CALL3", "friendList: ${friendList}")
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
                        callback(true) // 성공적으로 처리되었으므로 true를 콜백으로 반환
                    }
                    if (friendList != null) {
                        if (friendList.isEmpty()) {
                            callback (false)
                        }
                        else
                        {
                            callback (true)
                        }
                    }
                }
                else
                {
                    Log.d("API Error", "API call successful. Response: $response")
                    callback(false) // 실패한 경우 false를 콜백으로 반환
                }
            }
            override fun onFailure(
                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
                t: Throwable)
            {
                // API 요청 자체가 실패한 경우 처리
                callback(false) // 실패한 경우 false를 콜백으로 반환
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