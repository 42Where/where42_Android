import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.where42android.Base_url_api_Retrofit.AddMembersRequest
import com.example.where42android.Base_url_api_Retrofit.Member
import com.example.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.example.where42android.Base_url_api_Retrofit.NewGroupResponses
import com.example.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import com.example.where42android.Base_url_api_Retrofit.groups_memberlist
import com.example.where42android.Base_url_api_Retrofit.locationCustomMemberRequest
import com.example.where42android.LiveData.GroupsMembersList
import com.example.where42android.LiveData.ProfileList

class SharedViewModel_GroupsMembersList : ViewModel() {
//    private val viewModel = GroupsMembersList() // 공유할 ViewModel 인스턴스
    private val viewModel: GroupsMembersList = GroupsMembersList.getInstance()
    // ViewModel의 LiveData를 가져옴
    val groupsMembersListLiveData: LiveData<List<groups_memberlist.groups_memberlistItem>>
        get() = viewModel.groupsMembersListLiveData


    // ViewModel에서 데이터 가져오는 함수
    fun getGroupMemberList(intraId: Int) {
        viewModel.getGroupMemberList(intraId)
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

    fun addMembersToGroup(newgroupName : String, members: MutableList<String> ) {
        viewModel.addMembersToGroup(newgroupName, members)
    }


}

class SharedViewModel_Profile : ViewModel() {
//    ViewModelProvider를 사용하여 새로운 인스턴스를 가져올 때마다 ProfileList가 초기화되는 문제를 해결하려면,
//    앱의 전체 생명 주기 동안 공유되는 싱글톤 패턴을 적용
    private val profile: ProfileList = ProfileList.getInstance()
    //    private val profile = ProfileList() // 공유할 ViewModel 인스턴스
    val profileLiveData: LiveData<Member?>
        get() = profile.profileLiveData
    fun getMemberData(intraId: Int) {
        Log.e("ProfileList", "here")
//        profile.getMemberData(intraId)
    }
    fun updateMemberComment(updateCommentRequest: UpdateCommentRequest)
    {
//        profile.updateMemberComment(updateCommentRequest)
    }

    fun updateMemberCustomLocaton(locationCustomMemberRequest: locationCustomMemberRequest)
    {
        profile.updateMemberCustomLocaton(locationCustomMemberRequest)
    }


//    private val _commentLiveData = MutableLiveData<String>()
//    val commentLiveData: LiveData<String>
//        get() = _commentLiveData
//
//    fun updateComment(comment: String) {
//        _commentLiveData.value = comment
//    }

}
