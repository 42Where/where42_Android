package com.example.where42android.LiveData

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.where42android.Base_url_api_Retrofit.Deafult_friendGroup_memberlist
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//ViewModel: GroupDetailViewModel은 Android Jetpack의 ViewModel 클래스를 확장하여,
// 앱의 UI 관련 데이터를 처리하고 관리합니다. ViewModel은 화면 회전과 같은 구성 변경으로 인해 액티비티 또는 프래그먼트가
// 다시 생성되어도 데이터를 유지하는 데 사용됩니다.
class GroupDetailViewModel : ViewModel() {


//    MutableLiveData: defaultGroupMemberList는 그룹의 멤버 목록을 저장하기 위한 MutableLiveData입니다. LiveData는
//    수명 주기를 인식하여 데이터의 변경 사항을 관찰할 수 있는 데이터 홀더입니다. MutableLiveData는 데이터를 변경할 수 있는 LiveData입니다.
    private val defaultGroupMemberList = MutableLiveData<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>()
    val defaultGroupMemberLiveData: LiveData<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
        get() = defaultGroupMemberList

    fun getDefaultGroupMemberList(groupId: Int) {
        val retrofitAPI = RetrofitConnection.getInstance().create(Deafult_friendGroup_memberlist::class.java)
//        val service = retrofit.create(Deafult_friendGroup_memberlist::class.java)

        Log.e("GroupID", "${groupId}")
        retrofitAPI.getdefaultGroupList(groupId).enqueue(object :
            Callback<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>> {
            override fun onResponse(
                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
                response: Response<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
            ) {
                if (response.isSuccessful) {
                    defaultGroupMemberList.value = response.body()
                    Log.d("defaultGroupMemberList", "${defaultGroupMemberList.value}")
//                    val friendList = response.body()
//                    friendList?.let { members ->
//                        for (member in members) {
//                            // 기존 LiveData에 새로운 값을 추가하기 위해 기존 값에 새로운 값(member)을 추가합니다.
//                            val currentList = defaultGroupMemberList.value.orEmpty().toMutableList()
//                            currentList.add(member)
//                            Log.d("currentItem", "${currentList}")
//                            defaultGroupMemberList.postValue(currentList)
//                        }
//                    }
                    Log.d("defaultGroupMemberList", "${defaultGroupMemberList}")
//                    friendList?.let { members ->
//                        // 받은 멤버 데이터를 friendProfileList에 추가
//                        for (member in members) {
//                            defaultGroupMemberList.value.add(member)
//                        }
//                    }
                    Log.e("viewModel suc", "here1")
                } else {
                    // Handle unsuccessful response
                    Log.e("viewModel Error", "here1")
                }
            }

            override fun onFailure(
                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
                t: Throwable
            ) {
                Log.e("viewModel Error", "here2")
                // Handle network failure
            }
        })
    }
}
