import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seoul.where42android.Base_url_api_Retrofit.Member
import com.seoul.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.SearchApiService
import com.seoul.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import com.seoul.where42android.Base_url_api_Retrofit.groups_memberlist
import com.seoul.where42android.Base_url_api_Retrofit.locationCustomMemberRequest
import com.seoul.where42android.Base_url_api_Retrofit.searchMemberResponse
import com.seoul.where42android.LiveData.GroupsMembersList
import com.seoul.where42android.LiveData.ProfileList
import com.seoul.where42android.main.UserSettings
import kotlinx.coroutines.launch

class SharedViewModel_GroupsMembersList : ViewModel() {
//    private val viewModel = GroupsMembersList() // 공유할 ViewModel 인스턴스
    private val viewModel: GroupsMembersList = GroupsMembersList.getInstance()
    // ViewModel의 LiveData를 가져옴
    val groupsMembersListLiveData: LiveData<List<groups_memberlist.groups_memberlistItem>>
        get() = viewModel.groupsMembersListLiveData


    // ViewModel에서 데이터 가져오는 함수
    fun getGroupMemberList(intraId: Int, token : String) {
//        Log.d("ViewModel", "ViewModel");
        viewModel.getGroupMemberList(intraId, token)
    }

    // ViewModel에서 그룹 삭제하는 함수
    fun deleteGroup(groupId: Int) {
        viewModel.deleteGroup(groupId)
    }

    //새 그룹 추가
    fun addGroup(NewGroupRequest : NewGroupRequest)
    {
        viewModel.addGroup(NewGroupRequest)
    }


    //새 그룹에 멤버를 추가하는 함수
//    fun addMembersToGroup(groupId_members : AddMembersRequest) {
//        viewModel.addMembersToGroup(groupId_members)
//    }

    fun addMembersToGroup(newgroupName : String, members: MutableList<Int> ) {
        viewModel.addMembersToGroup(newgroupName, members)
    }


    //그룹에서 친구 삭제하기
    fun deleteFriendGroup (groupId: Int, member: MutableList<Int>)
    {
//        Log.e ("deleteFriendGroup", "GroupId : ${groupId}, member : ${member}")
        viewModel.deleteFriendGroup(groupId, member)
    }

    fun getGroupMemberList(groupId: Int)
    {
        viewModel.getGroupMemberList(groupId)
    }

    fun editGroupName(groupName:String, groupId:Int)
    {
        viewModel.editGroupName(groupName, groupId)
    }

//    fun groupToggleChange(groupName : String, toggle : Boolean)
//    {
//        viewModel.groupToggleChange(groupName, toggle)
//    }

}

class SharedViewModel_Profile (
): ViewModel() {
//    ViewModelProvider를 사용하여 새로운 인스턴스를 가져올 때마다 ProfileList가 초기화되는 문제를 해결하려면,
//    앱의 전체 생명 주기 동안 공유되는 싱글톤 패턴을 적용
    private val profile: ProfileList = ProfileList.getInstance()
    //    private val profile = ProfileList() // 공유할 ViewModel 인스턴스
    val profileLiveData: LiveData<Member?>
        get() = profile.profileLiveData

    fun getMemberData(context: Context, intraId: Int, token: String):Boolean {
//        Log.e("ProfileList", "here")
        return profile.getMemberData(intraId, token, context)
    }

    fun updateMemberComment(updateCommentRequest: UpdateCommentRequest, token: String)
    {
        profile.updateMemberComment(updateCommentRequest, token)
    }

    fun updateMemberCustomLocaton(locationCustomMemberRequest: locationCustomMemberRequest, token: String)
    {
        profile.updateMemberCustomLocaton(locationCustomMemberRequest, token)
    }


//    private val _commentLiveData = MutableLiveData<String>()
//    val commentLiveData: LiveData<String>
//        get() = _commentLiveData
//
//    fun updateComment(comment: String) {
//        _commentLiveData.value = comment
//    }
}

class SearchSharedViewModel : ViewModel() {
    //    private val viewModel = GroupsMembersList() // 공유할 ViewModel 인스턴스
    private val viewModel: SearchViewModel = SearchViewModel.getInstance()

    // ViewModel의 LiveData를 가져옴
    val searchListLiveData: LiveData<List<searchMemberResponse.searchMemberResponseItem>?>
        get() = viewModel.searchListLiveData

    // ViewModel에서 데이터 가져오는 함수
    fun getSearchMemberList(searchMember:String) {
//        Log.d("ViewModel", "ViewModel");
        viewModel.getSearchMemberList(searchMember)
    }


}

class SearchViewModel private constructor(): ViewModel() {


    private val searchList = MutableLiveData<List<searchMemberResponse.searchMemberResponseItem>?>()
    val searchListLiveData: LiveData<List<searchMemberResponse.searchMemberResponseItem>?>
        get() = searchList


    val usersetting = UserSettings.getInstance()

    // 싱글톤으로 사용할 객체 선언
    companion object {
        @Volatile
        private var instance: SearchViewModel? = null

        fun getInstance(): SearchViewModel {
            return instance ?: synchronized(this) {
                instance ?: SearchViewModel().also { instance = it }
            }
        }
    }

    fun getSearchMemberList(searchMember:String)
    {
//        val retrofitAPI = RetrofitConnection.getInstance(usersetting.token).create(SearchApiService::class.java)
//        retrofitAPI.searchMember(searchMember).enqueue(object :
//            Callback<List<searchMemberResponse.searchMemberResponseItem>> {
//            override fun onResponse(
//                call: Call<List<searchMemberResponse.searchMemberResponseItem>>,
//                response: Response<List<searchMemberResponse.searchMemberResponseItem>>
//            ) {
//                if (response.isSuccessful) {
//                    val searchMember: List<searchMemberResponse.searchMemberResponseItem>? = response.body()
//                    Log.d("PrfoileList", "response : ${response}")
//                    Log.d("ProfileList", "onResponse: Success searchMember : ${searchMember}")
//                    Log.d("searchList", "searchList ${searchList.value}")
//                        searchList.value = searchMember.orEmpty()
//                } else {
//                    Log.e("ProfileList", "onResponse: Failure")
//                }
//
//            }
//            override fun onFailure(call: Call<List<searchMemberResponse.searchMemberResponseItem>>, t: Throwable) {
//                Log.e("respone2 fail", "response : ${t}")
//                Log.e("respone2 fail", "fail")
//                // Handle failure
//                // For seoul, handle the failure accordingly
//            }
//        })

        val retrofitAPI = RetrofitConnection.getInstance(usersetting.token).create(SearchApiService::class.java)
        viewModelScope.launch {
            try {
                val response = retrofitAPI.searchMember(searchMember)
                Log.d("searchList", "search response code : ${response.code()}")
                if (response.isSuccessful)
                {
                    val searchListResponse = response.body()
                    if (searchListResponse != null)
                    {
                        searchList.value = searchListResponse
                    }
                    else
                    {
                        searchList.value = null
                    }
                    Log.d("searchList", "searchList : ${searchListResponse}")
                }
                else
                {

                }
            }
            catch  (e: Exception)
            {
                Log.d("serachList", "searchList api fail ${e}" )
            }
        }
    }
}
//}

