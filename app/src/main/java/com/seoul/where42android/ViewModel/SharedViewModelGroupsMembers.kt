package com.seoul.where42android.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.seoul.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.seoul.where42android.Base_url_api_Retrofit.groups_memberlist
import com.seoul.where42android.LiveData.LiveDataGroupsMembers
//import com.seoul.where42android.main.TokenManager

class SharedViewModelGroupsMembers : ViewModel() {
//    private val viewModel = GroupsMembersList() // 공유할 ViewModel 인스턴스
    private val viewModel: LiveDataGroupsMembers = LiveDataGroupsMembers.getInstance()
    // ViewModel의 LiveData를 가져옴
    val groupsMembersListLiveData: LiveData<List<groups_memberlist.groups_memberlistItem>>
        get() = viewModel.groupsMembersListLiveData


    // ViewModel에서 데이터 가져오는 함수
    fun getGroupMemberList(intraId: Int, Context:Context) {
//        Log.d("ViewModel", "ViewModel");
        viewModel.getGroupMemberList(intraId, Context)
    }

    // ViewModel에서 그룹 삭제하는 함수
    fun deleteGroup(groupId: Int, context: Context) {
        viewModel.deleteGroup(groupId, context)
    }

    //새 그룹 추가
    fun addGroup(NewGroupRequest : NewGroupRequest, context: Context)
    {
        viewModel.addGroup(NewGroupRequest, context)
    }


    //새 그룹에 멤버를 추가하는 함수
//    fun addMembersToGroup(groupId_members : AddMembersRequest) {
//        viewModel.addMembersToGroup(groupId_members)
//    }

    fun addMembersToGroup(newgroupName : String, members: MutableList<Int>, context: Context ) {
        viewModel.addMembersToGroup(newgroupName, members, context)
    }


    //그룹에서 친구 삭제하기
    fun deleteFriendGroup (groupId: Int, member: MutableList<Int>, context: Context)
    {
//        Log.e ("deleteFriendGroup", "GroupId : ${groupId}, member : ${member}")
        viewModel.deleteFriendGroup(groupId, member, context)
    }

//    fun getGroupMemberList(groupId: Int)
//    {
//        viewModel.getGroupMemberList(groupId)
//    }

    fun editGroupName(groupName:String, groupId:Int, context: Context)
    {
        viewModel.editGroupName(groupName, groupId, context)
    }

//    fun groupToggleChange(groupName : String, toggle : Boolean)
//    {
//        viewModel.groupToggleChange(groupName, toggle)
//    }

}


