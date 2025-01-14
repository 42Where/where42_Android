package com.seoul.where42android.LiveData

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seoul.where42android.Base_url_api_Retrofit.AddMembersRequest
import com.seoul.where42android.Base_url_api_Retrofit.GroupAddMemberlist
import com.seoul.where42android.Base_url_api_Retrofit.GroupChangeName
import com.seoul.where42android.Base_url_api_Retrofit.GroupDelete
import com.seoul.where42android.Base_url_api_Retrofit.GroupDeleteResponse
import com.seoul.where42android.Base_url_api_Retrofit.GroupMemberListService
import com.seoul.where42android.Base_url_api_Retrofit.GroupNameRequest
import com.seoul.where42android.Base_url_api_Retrofit.GroupNameResponse
import com.seoul.where42android.Base_url_api_Retrofit.MemberAPI
import com.seoul.where42android.Base_url_api_Retrofit.NewGroup
import com.seoul.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.deleteFriendListRequest
import com.seoul.where42android.Base_url_api_Retrofit.deleteFriendListResponse
import com.seoul.where42android.Base_url_api_Retrofit.groups_memberlist
import com.seoul.where42android.Base_url_api_Retrofit.ReissueAPI
import com.seoul.where42android.Base_url_api_Retrofit.intraIdRequest
import com.seoul.where42android.R
import com.seoul.where42android.main.UserSettings
import com.seoul.where42android.main.friendCheckedList
import com.seoul.where42android.main.friendListObject
import com.seoul.where42android.utils.ApiUtils

import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class LiveDataGroupsMembers() : ViewModel() {

    private val groupsMembersList = MutableLiveData<List<groups_memberlist.groups_memberlistItem>>()
    val groupsMembersListLiveData: LiveData<List<groups_memberlist.groups_memberlistItem>>
        get() = groupsMembersList
    val userSetting = UserSettings.getInstance()

    companion object {
        @Volatile
        private var instance: LiveDataGroupsMembers? = null

        fun getInstance(): LiveDataGroupsMembers {
            return instance ?: synchronized(this) {
                instance ?: LiveDataGroupsMembers().also { instance = it }
            }
        }
    }


    fun getGroupMemberList(intraId: Int, context: Context) {
        viewModelScope.launch {
            try {
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(GroupMemberListService::class.java)
                    retrofitAPI.getGroupMemberList(intraId)
                }

                if (response != null && response.isSuccessful) {
                    val groupList = response.body()?.toMutableList()

                    // default 그룹을 찾아서 이름 변경
                    val defaultGroupIndex = groupList?.indexOfFirst { it.groupName == "default" }
                    if (defaultGroupIndex != -1) {
                        val defaultGroup = defaultGroupIndex?.let { groupList?.get(it) }
                        defaultGroup?.groupName = "친구 목록"
                        if (defaultGroupIndex != null && defaultGroup != null) {
                            groupList?.set(defaultGroupIndex, defaultGroup)
                        }
                    }

                    // 기존의 "친구 목록" 그룹이 있으면 삭제
                    val existingFriendListIndex = groupList?.indexOfFirst { it.groupName == "친구 목록" }
                    val defaultFriendList = groupList?.firstOrNull { it.groupName == "친구 목록" }
                    if (existingFriendListIndex != -1 && defaultFriendList != null) {
                        groupList.removeAt(existingFriendListIndex!!)
                        groupList.add(defaultFriendList)
                    }

                    // 그룹 데이터 업데이트
                    groupList?.forEach { groupDetail ->
                        groupDetail.toggle = groupDetail.groupName == "친구 목록"
                        if (groupDetail.groupName == "친구 목록") {
                            groupDetail.members.forEach { defaultMember ->
                                friendListObject.addItem(defaultMember.intraId, defaultMember.intraName)
                            }
                        }
                        groupDetail.members.forEach { member ->
                            member.location = member.location ?: if (member.inCluster == true) "개포" else "퇴근"
                        }
                    }

                    groupsMembersList.value = groupList.orEmpty()
                } else {
                    groupsMembersList.value = emptyList() // 실패 시 빈 리스트 설정
                }
            } catch (e: Exception) {
                groupsMembersList.value = emptyList() // 예외 발생 시 빈 리스트 설정
                e.printStackTrace()
            }
        }
    }


//    fun getGroupMemberList(intraId: Int, token: String, context:Context) {
//        val retrofitAPI =
//            RetrofitConnection.getInstance(token).create(GroupMemberListService::class.java)
//        retrofitAPI.getGroupMemberList(intraId)
//            .enqueue(object : Callback<List<groups_memberlist.groups_memberlistItem>> {
//                override fun onResponse(
//                    call: Call<List<groups_memberlist.groups_memberlistItem>>,
//                    response: Response<List<groups_memberlist.groups_memberlistItem>>
//                ) {
//                    if (response.isSuccessful) {
//                        val groupList = response.body()?.toMutableList()
//
//                        // default 그룹을 찾아서 이름 변경
//                        val defaultGroupIndex = groupList?.indexOfFirst { it.groupName == "default" }
////                        Log.d("Index", "Index : ${defaultGroupIndex}")
//                        if (defaultGroupIndex != -1) {
//                            val defaultGroup = defaultGroupIndex?.let { groupList?.get(it) }
//                            defaultGroup?.groupName = "친구 목록"
//                            if (defaultGroupIndex != null) {
//                                if (defaultGroup != null) {
//                                    groupList?.set(defaultGroupIndex, defaultGroup)
//                                }
//                            }
//                        }
//                        // 기존의 "친구 목록" 그룹이 있으면 삭제
//                        val existingFriendListIndex = groupList?.indexOfFirst { it.groupName == "친구 목록" }
//                        val defaultFriendList = groupList?.firstOrNull{it.groupName == "친구 목록"}
//                        if (existingFriendListIndex != -1) {
//                            if (existingFriendListIndex != null && defaultFriendList != null) {
//                                groupList.removeAt(existingFriendListIndex)
//                                groupList.add(defaultFriendList)
//                            }
//                        }
//                        if (groupList != null) {
//                            groupList.forEach { groupDetail ->
//                                if (groupDetail.groupName != "친구 목록")
//                                    groupDetail.toggle = false
//                                else {
//                                    groupDetail.toggle = true
//                                    groupDetail.members.forEach{defaultmembers ->
//                                        friendListObject.addItem(defaultmembers.intraId, defaultmembers.intraName)
//                                    }
//                                }
//                                groupDetail.members.forEach{member ->
//                                    if (member.location == null)
//                                    {
//                                        if (member.inCluster == true)
//                                        {
//                                            member.location = "개포"
//                                        }
//                                        else
//                                        {
//                                            member.location = "퇴근"
//                                        }
//                                    }
//                                }
//
////                                Log.d("groupDetail", "Index : ${groupDetail.groupName}, toggle : ${groupDetail.toggle}")
//                            }
//                        }
//
//                        groupsMembersList.value = groupList.orEmpty()
//
//                    }
//                }
//                override fun onFailure(
//                    call: Call<List<groups_memberlist.groups_memberlistItem>>,
//                    t: Throwable
//                ) {
//                    groupsMembersList.value =
//                        emptyList() // Setting an empty list in case of network failure
//                }
//            })
//
//    }

    fun deleteGroup(groupId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // ApiUtils를 사용하여 API 요청 수행
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(GroupDelete::class.java)
                    retrofitAPI.deleteGroup(groupId)
                }

                // API 응답 처리
                if (response != null && response.isSuccessful) {
                    val deletedGroup = response.body()
                    val currentValue = groupsMembersList.value.orEmpty().toMutableList()
                    currentValue.removeAll { it.groupId == groupId }
                    if (deletedGroup != null) {
                        friendListObject.groupRemove(deletedGroup.groupName)
                    }
                    groupsMembersList.value = currentValue

                } else {
                    Log.e("deleteGroup", "API 요청 실패: ${response?.code()} - ${response?.message()}")
                }
            } catch (e: Exception) {
                Log.e("deleteGroup", "그룹 삭제 중 오류 발생", e)
            }
        }
    }


//    fun deleteGroup(groupId: Int) {
//        val retrofitAPI =
//            RetrofitConnection.getInstance(userSetting.token).create(GroupDelete::class.java)
//        val call = retrofitAPI.deleteGroup(groupId)
//
//        call.enqueue(object : Callback<GroupDeleteResponse> {
//            override fun onResponse(
//                call: Call<GroupDeleteResponse>,
//                response: Response<GroupDeleteResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val deletedGroup = response.body()
//                    _groupDeleted.postValue(true) // 삭제 성공 시 true를 LiveData로 전달
//                    val currentValue = groupsMembersList.value.orEmpty().toMutableList()
//                    currentValue.removeAll { it.groupId == groupId }
//                    if (deletedGroup != null) {
//                        friendListObject.groupRemove(deletedGroup.groupName)
//                    }
//                    groupsMembersList.value = currentValue
//                } else {
//                    _groupDeleted.postValue(false) // 삭제 실패 시 false를 LiveData로 전달
//                }
//            }
//
//            override fun onFailure(call: Call<GroupDeleteResponse>, t: Throwable) {
//                // 네트워크 오류 등의 이유로 API 호출이 실패한 경우
////                Log.e("DELETE_ERROR", "Network error occurred. Message: ${t.message}")
//                _groupDeleted.postValue(false) // 삭제 실패 시 false를 LiveData로 전달
//            }
//        })
//    }


    fun addGroup(newGroupRequest: NewGroupRequest, context: Context) {
        viewModelScope.launch {
            try {
                // ApiUtils를 사용하여 API 요청 수행
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(NewGroup::class.java)
                    retrofitAPI.newGroup(newGroupRequest)
                }

                // API 응답 처리
                if (response != null && response.isSuccessful) {
                    val newGroupResponse = response.body()
                    newGroupResponse?.let { group ->
                        val newGroup = groups_memberlist.groups_memberlistItem(
                            groupId = group.groupId,
                            groupName = group.groupName,
                            members = emptyList(), // 새로운 그룹이므로 멤버는 비어있는 리스트로 설정
                            toggle = false
                        )
                        val currentGroupList = groupsMembersList.value.orEmpty().toMutableList()
                        currentGroupList.add(newGroup)
                        Log.e("addGroup", "API 성공 ${currentGroupList}")
                        groupsMembersList.value = currentGroupList.toList()
                    }
                } else {
                    // API 요청 실패 처리
                    Log.e("addGroup", "API 요청 실패: ${response?.code()} - ${response?.message()}")
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.e("addGroup", "그룹 추가 중 오류 발생", e)
            }
        }
    }


    fun addMembersToGroup(newgroupName: String, members: MutableList<Int>, context: Context) {
        val newGroup = groupsMembersList.value?.find { it.groupName == newgroupName }
        val groupIdMembers = newGroup?.let { AddMembersRequest(it.groupId, members) }

        viewModelScope.launch {
            try {
                if (groupIdMembers != null) {
                    // ApiUtils를 사용하여 API 요청 수행
                    val response = ApiUtils.performApiRequest(context) { accessToken ->
                        val retrofitAPI2 = RetrofitConnection.getInstance(accessToken)
                            .create(GroupAddMemberlist::class.java)
                        retrofitAPI2.addMembersToGroup(groupIdMembers)
                    }

                    if (response != null && response.isSuccessful) {
                        // 그룹 업데이트 로직
                        val currentMembers = groupsMembersList.value.orEmpty().toMutableList()
                        val addedMembers = response.body()

                        val groupIndex = currentMembers.indexOfFirst { it.groupId == groupIdMembers.groupId }

                        if (groupIndex != -1) {
                            // 멤버 추가
                            val newMembersList = addedMembers?.map { member ->
                                groups_memberlist.groups_memberlistItem.Member(
                                    comment = member.comment ?: "",
                                    image = member.image ?: "",
                                    inCluster = member.inCluster ?: false,
                                    location = member.location ?: if (member.inCluster == true) "개포" else "퇴근",
                                    intraName = member.intraName ?: "",
                                    agree = member.agree,
                                    defaultGroupId = member.defaultGroupId,
                                    grade = member.grade ?: "",
                                    intraId = member.intraId
                                )
                            }

                            val targetGroupMembers =
                                currentMembers[groupIndex].members.toMutableList()
                            if (newMembersList != null) {
                                targetGroupMembers.addAll(newMembersList)
                            }

                            currentMembers[groupIndex] =
                                currentMembers[groupIndex].copy(members = targetGroupMembers)
                        }

                        groupsMembersList.value = currentMembers.toList()
                        friendCheckedList.clearItem()
                    } else {
                        Log.e("addMembersToGroup", "API 요청이 실패했습니다: ${response?.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("addMembersToGroup", "Error adding members to group", e)
            }
        }
    }

    fun deleteFriendGroup(groupId: Int, member: MutableList<Int>, context: Context) {
        val request = deleteFriendListRequest(groupId, member)
        viewModelScope.launch {
            try {
                // ApiUtils를 사용하여 API 요청 수행
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(MemberAPI::class.java)
                    retrofitAPI.deleteFriendList(request)
                }

                // 응답 처리
                if (response != null && response.isSuccessful) {
                    val deletedMembers = response.body()
                    if (userSetting.defaultGroup == groupId) {
                        val currentGroups = groupsMembersList.value.orEmpty().toMutableList()

                        // 그룹 업데이트
                        for (groupIndex in currentGroups.indices) {
                            val currentGroup = currentGroups[groupIndex]
                            val currentMembers = currentGroup.members.toMutableList()

                            member.forEach { memberId ->
                                currentMembers.removeAll { it.intraId == memberId }
                            }

                            currentGroups[groupIndex] = currentGroup.copy(members = currentMembers)
                        }

                        member.forEach { memberId ->
                            friendListObject.removeItem(memberId)
                        }

                        groupsMembersList.postValue(currentGroups)
                        friendCheckedList.clearItem()
                    } else {
                        val currentGroups = groupsMembersList.value.orEmpty().toMutableList()
                        val groupIndex = currentGroups.indexOfFirst { it.groupId == groupId }

                        if (groupIndex != -1) {
                            val currentGroup = currentGroups[groupIndex]
                            val currentMembers = currentGroup.members.toMutableList()

                            member.forEach { memberId ->
                                currentMembers.removeAll { it.intraId == memberId }
                            }

                            currentGroups[groupIndex] = currentGroup.copy(members = currentMembers)
                            member.forEach { memberId ->
                                friendListObject.removeItem(memberId)
                            }

                            friendCheckedList.clearItem()
                            groupsMembersList.postValue(currentGroups)
                        }
                    }
                } else {
                    // 실패 처리
                    Log.e("deleteFriendGroup", "API 요청 실패: ${response?.code()} - ${response?.message()}")
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.e("deleteFriendGroup", "API 요청 중 오류 발생", e)
            }
        }
    }


//    fun deleteFriendGroup(groupId: Int, member: MutableList<Int>, context: Context) {
//        val token = userSetting.token
//        val retrofitAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)
//
//
//        val request = deleteFriendListRequest(groupId, member)
//
//        retrofitAPI.deleteFriendList(request).enqueue(object :
//            Callback<List<deleteFriendListResponse.deleteFriendListResponseItem>> {
//            override fun onResponse(
//                call: Call<List<deleteFriendListResponse.deleteFriendListResponseItem>>,
//                response: Response<List<deleteFriendListResponse.deleteFriendListResponseItem>>
//            ) {
//                if (response.isSuccessful) {
//                    Log.e("respone2 SUC", "body : ${response.body()}")
//
//                    if (userSetting.defaultGroup == groupId) {
//                        Log.e("herehere2", "here1")
//                        // 현재 그룹 리스트를 가져옴
//                        val currentGroups = groupsMembersList.value.orEmpty().toMutableList()
//
//                        // 각 그룹을 순회
//                        for (groupIndex in currentGroups.indices) {
//                            val currentGroup = currentGroups[groupIndex]
//
//                            // 현재 그룹의 멤버 리스트를 가져옴
//                            val currentMembers = currentGroup.members.toMutableList()
//
//                            // member 리스트의 길이만큼 반복문 실행
//                            for (memberId in member) {
//                                // 현재 멤버 리스트에서 memberId와 동일한 intraId를 가진 멤버를 찾아 제외
//                                currentMembers.removeAll { it.intraId == memberId }
//
//                            }
//
//                            // 현재 그룹의 멤버 리스트를 업데이트
//                            currentGroups[groupIndex] = currentGroup.copy(members = currentMembers)
//                        }
//                        for (memberId in member) {
//                            // 현재 멤버 리스트에서 memberId와 동일한 intraId를 가진 멤버를 찾아 제외
//                            friendListObject.removeItem(memberId)
//                        }
//                        // 업데이트된 그룹 리스트를 LiveData에 설정하여 UI를 업데이트
//                        groupsMembersList.value = currentGroups
//                        friendCheckedList.clearItem()
//                    } else {
//                        // 현재 그룹 리스트를 가져옴
//                        val currentGroups = groupsMembersList.value.orEmpty().toMutableList()
//                        val groupIndex = currentGroups.indexOfFirst { it.groupId == groupId }
//                        // 각 그룹을 순회
//                        val currentGroup = currentGroups[groupIndex]
//                        // 현재 그룹의 멤버 리스트를 가져옴
//                        val currentMembers = currentGroup.members.toMutableList()
//                        // member 리스트의 길이만큼 반복문 실행
//                        Log.d("hereList", "here")
//
//                        Log.d("hereList", "${member}")
//                        for (memberId in member) {
//                            // 현재 멤버 리스트에서 memberId와 동일한 intraId를 가진 멤버를 찾아 제외
//                            currentMembers.removeAll { it.intraId == memberId }
//                            Log.d("hereList", "${memberId}")
//                        }
//                        // 현재 그룹의 멤버 리스트를 업데이트
//                        currentGroups[groupIndex] = currentGroup.copy(members = currentMembers)
//                        // 업데이트된 그룹 리스트를 LiveData에 설정하여 UI를 업데이트
//                        for (memberId in member) {
//                            // 현재 멤버 리스트에서 memberId와 동일한 intraId를 가진 멤버를 찾아 제외
//                            friendListObject.removeItem(memberId)
//                        }
//                        friendCheckedList.clearItem()
//                        groupsMembersList.value = currentGroups
//                    }
//
//
//                } else {
//                    // 추가: 실패 응답 로그
////                    Log.e("ProfileList", "onResponse: Failure")
//                    Log.e("respone2 fail", "fail1")
//                }
//            }
//
//            override fun onFailure(
//                call: Call<List<deleteFriendListResponse.deleteFriendListResponseItem>>,
//                t: Throwable
//            ) {
//                Log.e("respone2 fail", "fail2")
//                // Handle failure
//                // For seoul, handle the failure accordingly
//            }
//        })
//    }

//    fun getGroupMemberList(groupId: Int) {
//        val userSettings = UserSettings.getInstance()
//        val retrofitAPI =
//            RetrofitConnection.getInstance(userSettings.token).create(Deafult_friendGroup_memberlist::class.java)
////        val service = retrofit.create(Deafult_friendGroup_memberlist::class.java)
//
//        Log.e("GroupID", "${groupId}")
//        retrofitAPI.getdefaultGroupList(groupId).enqueue(object :
//            Callback<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>> {
//            override fun onResponse(
//                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
//                response: Response<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("defaultGroupMemberList", "${response.body()}")
//                    Log.d("defaultGroupMemberList", "${response.body()}")
//                    Log.e("viewModel suc", "here1")
//                } else {
//                    // Handle unsuccessful response
//                    Log.e("viewModel Error", "here1")
//                }
//            }
//
//            override fun onFailure(
//                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
//                t: Throwable
//            ) {
//                Log.e("viewModel Error", "here2")
//                // Handle network failure
//            }
//        })
//    }

    fun editGroupName(groupName: String, groupId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // ApiUtils를 사용하여 API 요청 수행
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken)
                        .create(GroupChangeName::class.java)
                    val groupChangeData = GroupNameRequest(groupId, groupName)
                    retrofitAPI.groupChangeName(groupChangeData)
                }

                // 응답 처리
                if (response != null && response.isSuccessful) {
                    val editGroupResponse = response.body()

                    // 그룹 이름 업데이트
                    val currentGroups = groupsMembersList.value.orEmpty().toMutableList()
                    val groupIndex = currentGroups.indexOfFirst { it.groupId == groupId }

                    if (groupIndex != -1 && editGroupResponse != null) {
                        currentGroups[groupIndex].groupName = editGroupResponse.groupName
                        groupsMembersList.value = currentGroups // LiveData 업데이트
                    } else {
                        Log.e("editGroupName", "그룹을 찾을 수 없거나 응답 데이터가 없습니다.")
                    }

                } else {
                    // 실패 처리
                    Log.e(
                        "editGroupName",
                        "API 요청 실패: ${response?.code()} - ${response?.message()}"
                    )
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.e("editGroupName", "그룹 이름 변경 중 오류 발생", e)

            }
        }
    }

//    fun editGroupName(groupName:String, groupId:Int, context: Context) {
//        val retrofitAPI = RetrofitConnection.getInstance(userSetting.token).create(GroupChangeName::class.java)
//
//        val groupChangedata = GroupNameRequest(groupId, groupName)
//        val call = retrofitAPI.groupChangeName(groupChangedata)
//
//        call.enqueue(object : Callback<GroupNameResponse> {
//            override fun onResponse(
//                call: Call<GroupNameResponse>,
//                response: Response<GroupNameResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val editGroupresponse = response.body()
////                    dialog.dismiss()
////                    callback(true) // 삭제 성공 시 true 전달
//                    // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
//
//                    // 현재 그룹 리스트를 가져옴
//                    val currentGroups = groupsMembersList.value.orEmpty().toMutableList()
//
//                    val groupIndex = currentGroups.indexOfFirst { it.groupId == groupId }
//                    if (groupIndex != -1) { // 그룹을 찾았는지 확인
//                        if (editGroupresponse != null) {
//                            currentGroups[groupIndex].groupName = editGroupresponse.groupName
//                        }
//                    } else {
//                        // 그룹을 찾지 못한 경우에 대한 처리
//                    }
//
//                    groupsMembersList.value = currentGroups
//
//                } else {
//                    // API 호출에 실패한 경우
//                    Log.e(
//                        "DELETE_ERROR",
//                        "Failed to delete group. Error code: ${response.code()}"
//                    )
//                    // 실패 처리 로직을 수행하세요.
////                    callback(false) // 삭제 실패 시 false 전달
//                }
//            }
//
//            override fun onFailure(call: Call<GroupNameResponse>, t: Throwable) {
//                // 네트워크 오류 등의 이유로 API 호출이 실패한 경우
//                Log.e(
//                    "DELETE_ERROR",
//                    "Network error occurred. Message: ${t.message}"
//                )
//                // 실패 처리 로직을 수행하세요.
////                callback(false) // 삭제 실패 시 false 전달
//            }
//        })
//    }
}
