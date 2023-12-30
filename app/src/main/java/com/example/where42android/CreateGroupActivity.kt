package com.example.where42android


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.Base_url_api_Retrofit.MemberAll
import com.example.where42android.Base_url_api_Retrofit.MemberallListService
import com.example.where42android.adapter.RecyclerViewAdapter_MemberAll

import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateGroupActivity : AppCompatActivity() {

    private val retrofitAPI = RetrofitConnection.getInstance().create(MemberallListService::class.java)
    val friendProfileList = mutableListOf<MemberAll.MemberAllItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val groupId = intent.getIntExtra("groupIdKey", -1)
        //여기로 groupid 받아옴 여기에 이제 checkbox 선택된 members 넣어주면 됨
        Log.e("groupId", groupId.toString())

        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra("userInputKey")) {
            val userInput = receivedIntent.getStringExtra("userInputKey")
            //Toast.makeText(this, "$userInput", Toast.LENGTH_SHORT).show()
        }
        Log.d("CALL", "fucking here")
        // API 호출
        fetchMemberAllData()

//
//        val friendRecyclerView: RecyclerView = findViewById<RecyclerView?>(R.id.new_gorup_friend_list)
//
//
//        val friendRecyclerViewAdapter = RecyclerViewAdapter_new_group(this, friendProfileList, false)
//        friendRecyclerView.layoutManager = LinearLayoutManager(this)
//        friendRecyclerView.adapter = friendRecyclerViewAdapter

        //검색바 기능
        val searchView: SearchView = findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = ArrayList<MemberAll.MemberAllItem>()

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
    private fun fetchMemberAllData() {

//        val service = retrofit.create(MemberallListService::class.java)

//        val call = service.getMemberAllList()
        Log.d("CALL", "fucking here2")
        retrofitAPI.getMemberAllList().enqueue(object : Callback<List<MemberAll.MemberAllItem>> {
            override fun onResponse(
                call: Call<List<MemberAll.MemberAllItem>>,
                response: Response<List<MemberAll.MemberAllItem>>
            ) {
                Log.d("CALL3", "fucking here3")
                if (response.isSuccessful) {
                    Log.d("CALL", "fucking here3")
                    Log.d("CALL2", "API call successful. Response: $response")
                    val memberAllList  = response.body()
                    memberAllList?.let { members ->


                        // 받은 멤버 데이터를 friendProfileList에 추가
                        for (member in members) {
                            friendProfileList.add(member)
                        }
                        updateAdapterData(friendProfileList)
                        //                    friendProfileList.clear() // 기존 데이터를 초기화
//                    response.body()?.let { members ->
//                        friendProfileList.addAll(members) // 받은 멤버 데이터를 리스트에 추가
//                        // 어댑터에 데이터가 갱신되었음을 알리고 UI를 업데이트할 수 있도록 호출
//                        updateAdapterData(friendProfileList)
//                    }
                    }
                } else {
                    Log.d("CALL", "fucking here4")
                    // API 요청은 성공했지만 응답이 성공적이지 않은 경우 처리
                    Log.e("CALL2", "API call failed with code: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<MemberAll.MemberAllItem>>, t: Throwable) {
                // API 요청 자체가 실패한 경우 처리
            }
        })
    }
    private fun updateAdapterData(data: List<MemberAll.MemberAllItem>) {
        val friendRecyclerView: RecyclerView = findViewById(R.id.new_gorup_friend_list)
        val friendRecyclerViewAdapter = RecyclerViewAdapter_MemberAll(this, data, false)
        friendRecyclerView.layoutManager = LinearLayoutManager(this)
        friendRecyclerView.adapter = friendRecyclerViewAdapter
    }
}


//class CreateGroupActivity : AppCompatActivity() {
//    var friendProfileList = arrayListOf<profile_list>(
//        profile_list("Jaeyojun", "handsome", "개포 c2r5s6", "profile_photo_example",),
//        profile_list("Jaeyojun", "handsome", "퇴근", "profile_photo_example",),
//        profile_list("Jaeyojun", "handsome", "개포 c2r5s6", "profile_photo_example",),
//        profile_list("Jooypark", "beautiful", "개포 c2r5s6", "profile_photo_example"),
//        profile_list("jaju", "graphics master", "퇴근", "profile_photo_example"),
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_create_group)
//
//        val receivedIntent = intent
//        if (receivedIntent != null && receivedIntent.hasExtra("userInputKey")) {
//            val userInput = receivedIntent.getStringExtra("userInputKey")
//            //Toast.makeText(this, "$userInput", Toast.LENGTH_SHORT).show()
//        }
//
//
//        val friendRecyclerView: RecyclerView = findViewById<RecyclerView?>(R.id.new_gorup_friend_list)
//
//
//        val friendRecyclerViewAdapter = RecyclerViewAdapter_new_group(this, friendProfileList, false)
//        friendRecyclerView.layoutManager = LinearLayoutManager(this)
//        friendRecyclerView.adapter = friendRecyclerViewAdapter
//
//
//
//        val searchView: SearchView = findViewById(R.id.searchView)
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                val filteredList = ArrayList(friendProfileList.filter { profile ->
//                    profile.intraId.contains(newText.orEmpty(), ignoreCase = true)
//                })
//                friendRecyclerViewAdapter.updateList(filteredList)
//                return true
//            }
//        })
//    }
//}
