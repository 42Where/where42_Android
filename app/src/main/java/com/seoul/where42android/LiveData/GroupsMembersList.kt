package com.seoul.where42android.LiveData

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seoul.where42android.Base_url_api_Retrofit.AddMembersRequest
import com.seoul.where42android.Base_url_api_Retrofit.Deafult_friendGroup_memberlist
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
import com.seoul.where42android.Base_url_api_Retrofit.NewGroupResponses
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.addMembersResponse
import com.seoul.where42android.Base_url_api_Retrofit.deleteFriendListRequest
import com.seoul.where42android.Base_url_api_Retrofit.deleteFriendListResponse
import com.seoul.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import com.seoul.where42android.Base_url_api_Retrofit.groups_memberlist
import com.seoul.where42android.main.UserSettings
import com.seoul.where42android.main.friendCheckedList
import com.seoul.where42android.main.friendListObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class GroupsMembersList() : ViewModel() {

    private val groupsMembersList = MutableLiveData<List<groups_memberlist.groups_memberlistItem>>()
    val groupsMembersListLiveData: LiveData<List<groups_memberlist.groups_memberlistItem>>
        get() = groupsMembersList
    val userSetting = UserSettings.getInstance()

//    fun init(sharedViewModel: SharedViewModel_GroupsMembersList) {
//        this.sharedViewModel = sharedViewModel
//    }


    companion object {
        @Volatile
        private var instance: GroupsMembersList? = null

        fun getInstance(): GroupsMembersList {
            return instance ?: synchronized(this) {
                instance ?: GroupsMembersList().also { instance = it }
            }
        }
    }

//    fun groupToggleChange(groupName : String, toggle : Boolean)
//    {
////        val groupDetail =  groupsMembersList.value?.firstOrNull{it.groupName == groupName}
////        if (groupDetail != null) {
////            groupDetail.toggle = toggle
////        }
////        groupsMembersList.value
//
//        // 현재의 groupsMembersList 값을 가져옴
//        val currentList = groupsMembersList.value
//
//        // groupName에 해당하는 groupDetail을 찾음
//        val groupDetail = currentList?.firstOrNull { it.groupName == groupName }
//
//        // groupDetail이 null이 아니면 toggle 값을 변경
//        groupDetail?.toggle = toggle
//
//        // 변경된 리스트를 다시 groupsMembersList에 설정
//        // setValue 또는 postValue를 사용하여 변경된 값을 LiveData에 할당
//        currentList?.let {
//            groupsMembersList.value = it
//        }
//    }


    fun getGroupMemberList(intraId: Int, token: String) {
        val retrofitAPI =
            RetrofitConnection.getInstance(token).create(GroupMemberListService::class.java)
        retrofitAPI.getGroupMemberList(intraId)
            .enqueue(object : Callback<List<groups_memberlist.groups_memberlistItem>> {
                override fun onResponse(
                    call: Call<List<groups_memberlist.groups_memberlistItem>>,
                    response: Response<List<groups_memberlist.groups_memberlistItem>>
                ) {
                    if (response.isSuccessful) {
                        val groupList = response.body()?.toMutableList()

                        // default 그룹을 찾아서 이름 변경
                        val defaultGroupIndex = groupList?.indexOfFirst { it.groupName == "default" }
//                        Log.d("Index", "Index : ${defaultGroupIndex}")
                        if (defaultGroupIndex != -1) {
                            val defaultGroup = defaultGroupIndex?.let { groupList?.get(it) }
                            defaultGroup?.groupName = "친구 목록"
                            if (defaultGroupIndex != null) {
                                if (defaultGroup != null) {
                                    groupList?.set(defaultGroupIndex, defaultGroup)
                                }
                            }
                        }
                        // 기존의 "친구 목록" 그룹이 있으면 삭제
                        val existingFriendListIndex = groupList?.indexOfFirst { it.groupName == "친구 목록" }
                        val defaultFriendList = groupList?.firstOrNull{it.groupName == "친구 목록"}
                        if (existingFriendListIndex != -1) {
                            if (existingFriendListIndex != null && defaultFriendList != null) {
                                groupList.removeAt(existingFriendListIndex)
                                groupList.add(defaultFriendList)
                            }
                        }
                        if (groupList != null) {
                            groupList.forEach { groupDetail ->
                                if (groupDetail.groupName != "친구 목록")
                                    groupDetail.toggle = false
                                else {
                                    groupDetail.toggle = true
                                    groupDetail.members.forEach{defaultmembers ->
                                        friendListObject.addItem(defaultmembers.intraId, defaultmembers.intraName)
                                    }
                                }
                                groupDetail.members.forEach{member ->
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
                                }

//                                Log.d("groupDetail", "Index : ${groupDetail.groupName}, toggle : ${groupDetail.toggle}")
                            }
                        }

                        groupsMembersList.value = groupList.orEmpty()

                    } else {
                        // Handle unsuccessful response
                        groupsMembersList.value = emptyList()
                    }
                }
                override fun onFailure(
                    call: Call<List<groups_memberlist.groups_memberlistItem>>,
                    t: Throwable
                ) {
                    groupsMembersList.value =
                        emptyList() // Setting an empty list in case of network failure
                }
            })

    }


    private val _groupDeleted = MutableLiveData<Boolean>()
    val groupDeleted: LiveData<Boolean>
        get() = _groupDeleted

    fun deleteGroup(groupId: Int) {
        val retrofitAPI =
            RetrofitConnection.getInstance(userSetting.token).create(GroupDelete::class.java)
        val call = retrofitAPI.deleteGroup(groupId)

        call.enqueue(object : Callback<GroupDeleteResponse> {
            override fun onResponse(
                call: Call<GroupDeleteResponse>,
                response: Response<GroupDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    val deletedGroup = response.body()
                    _groupDeleted.postValue(true) // 삭제 성공 시 true를 LiveData로 전달
                    val currentValue = groupsMembersList.value.orEmpty().toMutableList()
                    currentValue.removeAll { it.groupId == groupId }
                    groupsMembersList.value = currentValue
                } else {
                    _groupDeleted.postValue(false) // 삭제 실패 시 false를 LiveData로 전달
                }
            }

            override fun onFailure(call: Call<GroupDeleteResponse>, t: Throwable) {
                // 네트워크 오류 등의 이유로 API 호출이 실패한 경우
//                Log.e("DELETE_ERROR", "Network error occurred. Message: ${t.message}")
                _groupDeleted.postValue(false) // 삭제 실패 시 false를 LiveData로 전달
            }
        })
    }


    fun addGroup(NewGroupRequest: NewGroupRequest) {

        val token = userSetting.token
        val retrofitAP2 = RetrofitConnection.getInstance(token).create(NewGroup::class.java)
        val call = retrofitAP2.newGroup(NewGroupRequest)

        //api 혼합해서 써봄 ㅋ
        call.enqueue(object : Callback<NewGroupResponses> {
            override fun onResponse(
                call: Call<NewGroupResponses>,
                response: Response<NewGroupResponses>
            ) {
                if (response.isSuccessful) {
                    val newGroupResponse = response.body()
                    val currentGroupList = groupsMembersList.value.orEmpty().toMutableList()
                    newGroupResponse?.let {
                        val newGroup = groups_memberlist.groups_memberlistItem(
//                            count = 0,
                            groupId = it.groupId,
                            groupName = it.groupName,
                            members = emptyList(), // 새로운 그룹이므로 멤버는 비어있는 리스트로 설정합니다.
                            toggle = false
                        )
                        currentGroupList.add(newGroup)
                    }
                    groupsMembersList.value = currentGroupList.toList()
//                    Log.d("YourActivity", "New Group ID: ")

//                    val intent = Intent(this@MainPageActivity, CreateGroupActivity::class.java)
//                            //default 아이디 넣어주어야함.
//                            Log.d("check_newGroup", "${newGroupResponse?.groupId}")
//                    intent.putExtra("newgroupIdKey", newGroupResponse?.groupId) // groupIdKey는 key값, newGroupResponse.groupId는 전달할 값
//                    intent.putExtra("groupIdKey", profile?.defaultGroupId) // groupIdKey는 key값, newGroupResponse.groupId는 전달할 값
//                    startActivity(intent)
                } else {
                    // API 호출에 실패한 경우
//                    Log.e("DELETE_ERROR", "Failed to delete group. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<NewGroupResponses>, t: Throwable) {

//                Log.e("CREATE_ERROR", "Network error occurred. Message: ${t.message}")
            }
        })
    }


    fun addMembersToGroup(newgroupName: String, members: MutableList<Int>) {
//        val newgroup = groupsMembersList.value?.any { it.groupName == newgroupName }
        val newgroup = groupsMembersList.value?.find { it.groupName == newgroupName }
//        Log.e("newgroup", "newgroup ${newgroup?.groupName}")

        val userSettings = UserSettings.getInstance()
        val retrofitAPI2 = RetrofitConnection.getInstance(userSettings.token)
            .create(GroupAddMemberlist::class.java)

        val groupId_members = newgroup?.let { AddMembersRequest(it.groupId, members) }

        Log.d("add_member" , "${members}")
        //새로운 그룹에 member 추가하는 코드 작성!
        if (groupId_members != null) {
            retrofitAPI2.addMembersToGroup(groupId_members).enqueue(object :
                Callback<List<addMembersResponse.addMembersResponseItem>> {
                override fun onResponse(
                    call: Call<List<addMembersResponse.addMembersResponseItem>>,
                    response: Response<List<addMembersResponse.addMembersResponseItem>>
                ) {
                    if (response.isSuccessful) {
//                        Log.e("groupsMembersList.value", "${groupsMembersList.value}")
                        val currentMembers = groupsMembersList.value.orEmpty().toMutableList()
                        val addedMembers = response.body() ?: emptyList()
                        //
                        val isGroupIdExists =
                            currentMembers.any { it.groupId == groupId_members.groupId }
//                        Log.e("isGroupIdExists_here", "${isGroupIdExists}")
                        //

                        if (isGroupIdExists) {
                            // 해당하는 groupId_members.groupId를 가진 그룹 찾기
                            val isGroupIdExists =
                                currentMembers.indexOfFirst { it.groupId == groupId_members.groupId }
                            Log.e("isGroupIdExists1-1", "${isGroupIdExists}")
                            if (isGroupIdExists != -1) {
//                                Log.e("isGroupIdExists1", "${isGroupIdExists}")
//                                Log.e("isGroupIdExists1", "${isGroupIdExists}")

                                // 해당하는 그룹에 멤버 추가
//                                Log.e("addedMembers", "addedMembers : ${addedMembers}")
                                val newMembersList = addedMembers.map { member ->
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
                                    groups_memberlist.groups_memberlistItem.Member(
                                        comment = member.comment ?: "",
                                        image = member.image ?: "",
                                        inCluster = member.inCluster ?: false,
                                        location = member.location ?: "",
                                        intraName = member.intraName ?: "",
                                        agree = member.agree,
                                        defaultGroupId = member.defaultGroupId,
                                        grade = member.grade ?: "",
                                        intraId = member.intraId
                                    )
                                }

                                // 해당하는 그룹의 멤버 리스트를 가져와서 새로운 멤버들을 추가
                                val targetGroupMembers =
                                    currentMembers[isGroupIdExists].members.toMutableList()
                                targetGroupMembers.addAll(newMembersList)

                                currentMembers[isGroupIdExists] =
                                    currentMembers[isGroupIdExists].copy(members = targetGroupMembers)
                            }

                        }
                        else
                        {

                        }
                        //                    // 변경된 목록을 LiveData에 설정하여 UI를 업데이트합니다.
                        //                    Log.e("isGroupIdExists2", "${groupsMembersList.value}")
                        //                    Log.e("isGroupIdExists2", "${currentMembers}")
                        Log.e("gogoshing", "groupsMembersList : ${groupsMembersList.value}")
                        Log.e("gogoshing", "currentMembers : ${currentMembers.toList()}")
                        groupsMembersList.value = currentMembers.toList()
                        friendCheckedList.clearItem()

                        //                        val intent = Intent(this@CreateGroupActivity, MainPageActivity::class.java)
                        //                        finish() //인텐트 종료
                        //                        startActivity(intent)
                    } else {
                        Log.d("groupmemberadd_error", "API call failed. Response: $response")
                    }
                }

                override fun onFailure(
                    call: Call<List<addMembersResponse.addMembersResponseItem>>,
                    t: Throwable
                ) {
                    Log.d("groupmemberadd_error", "onFailure API call failed.")
                    // API 요청 자체가 실패한 경우 처리
                }
            })
        }
    }

    fun deleteFriendGroup(groupId: Int, member: MutableList<Int>) {
        val token = userSetting.token
        val retrofitAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)


        val request = deleteFriendListRequest(groupId, member)

        retrofitAPI.deleteFriendList(request).enqueue(object :
            Callback<List<deleteFriendListResponse.deleteFriendListResponseItem>> {
            override fun onResponse(
                call: Call<List<deleteFriendListResponse.deleteFriendListResponseItem>>,
                response: Response<List<deleteFriendListResponse.deleteFriendListResponseItem>>
            ) {
                if (response.isSuccessful) {
                    Log.e("respone2 SUC", "body : ${response.body()}")

                    if (userSetting.defaultGroup == groupId) {
                        Log.e("herehere2", "here1")
                        // 현재 그룹 리스트를 가져옴
                        val currentGroups = groupsMembersList.value.orEmpty().toMutableList()

                        // 각 그룹을 순회
                        for (groupIndex in currentGroups.indices) {
                            val currentGroup = currentGroups[groupIndex]

                            // 현재 그룹의 멤버 리스트를 가져옴
                            val currentMembers = currentGroup.members.toMutableList()

                            // member 리스트의 길이만큼 반복문 실행
                            for (memberId in member) {
                                // 현재 멤버 리스트에서 memberId와 동일한 intraId를 가진 멤버를 찾아 제외
                                currentMembers.removeAll { it.intraId == memberId }

                            }

                            // 현재 그룹의 멤버 리스트를 업데이트
                            currentGroups[groupIndex] = currentGroup.copy(members = currentMembers)
                        }
                        for (memberId in member) {
                            // 현재 멤버 리스트에서 memberId와 동일한 intraId를 가진 멤버를 찾아 제외
                            friendListObject.removeItem(memberId)
                        }
                        // 업데이트된 그룹 리스트를 LiveData에 설정하여 UI를 업데이트
                        groupsMembersList.value = currentGroups
                        friendCheckedList.clearItem()
                    } else {
                        // 현재 그룹 리스트를 가져옴
                        val currentGroups = groupsMembersList.value.orEmpty().toMutableList()
                        val groupIndex = currentGroups.indexOfFirst { it.groupId == groupId }
                        // 각 그룹을 순회
                        val currentGroup = currentGroups[groupIndex]
                        // 현재 그룹의 멤버 리스트를 가져옴
                        val currentMembers = currentGroup.members.toMutableList()
                        // member 리스트의 길이만큼 반복문 실행
                        Log.d("hereList", "here")

                        Log.d("hereList", "${member}")
                        for (memberId in member) {
                            // 현재 멤버 리스트에서 memberId와 동일한 intraId를 가진 멤버를 찾아 제외
                            currentMembers.removeAll { it.intraId == memberId }
                            Log.d("hereList", "${memberId}")
                        }
                        // 현재 그룹의 멤버 리스트를 업데이트
                        currentGroups[groupIndex] = currentGroup.copy(members = currentMembers)
                        // 업데이트된 그룹 리스트를 LiveData에 설정하여 UI를 업데이트
                        for (memberId in member) {
                            // 현재 멤버 리스트에서 memberId와 동일한 intraId를 가진 멤버를 찾아 제외
                            friendListObject.removeItem(memberId)
                        }
                        friendCheckedList.clearItem()
                        groupsMembersList.value = currentGroups
                    }


                } else {
                    // 추가: 실패 응답 로그
//                    Log.e("ProfileList", "onResponse: Failure")
                    Log.e("respone2 fail", "fail1")
                }
            }

            override fun onFailure(
                call: Call<List<deleteFriendListResponse.deleteFriendListResponseItem>>,
                t: Throwable
            ) {
                Log.e("respone2 fail", "fail2")
                // Handle failure
                // For seoul, handle the failure accordingly
            }
        })
    }

    fun getGroupMemberList(groupId: Int) {
        val userSettings = UserSettings.getInstance()
        val retrofitAPI =
            RetrofitConnection.getInstance(userSettings.token).create(Deafult_friendGroup_memberlist::class.java)
//        val service = retrofit.create(Deafult_friendGroup_memberlist::class.java)

        Log.e("GroupID", "${groupId}")
        retrofitAPI.getdefaultGroupList(groupId).enqueue(object :
            Callback<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>> {
            override fun onResponse(
                call: Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>,
                response: Response<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
            ) {
                if (response.isSuccessful) {
                    Log.d("defaultGroupMemberList", "${response.body()}")
                    Log.d("defaultGroupMemberList", "${response.body()}")
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


    fun editGroupName(groupName:String, groupId:Int) {
        val retrofitAPI = RetrofitConnection.getInstance(userSetting.token).create(GroupChangeName::class.java)

        val groupChangedata = GroupNameRequest(groupId, groupName)
        val call = retrofitAPI.groupChangeName(groupChangedata)

        call.enqueue(object : Callback<GroupNameResponse> {
            override fun onResponse(
                call: Call<GroupNameResponse>,
                response: Response<GroupNameResponse>
            ) {
                if (response.isSuccessful) {
                    val editGroupresponse = response.body()
//                    dialog.dismiss()
//                    callback(true) // 삭제 성공 시 true 전달
                    // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.

                    // 현재 그룹 리스트를 가져옴
                    val currentGroups = groupsMembersList.value.orEmpty().toMutableList()

                    val groupIndex = currentGroups.indexOfFirst { it.groupId == groupId }
                    if (groupIndex != -1) { // 그룹을 찾았는지 확인
                        if (editGroupresponse != null) {
                            currentGroups[groupIndex].groupName = editGroupresponse.groupName
                        }
                    } else {
                        // 그룹을 찾지 못한 경우에 대한 처리
                    }

                    groupsMembersList.value = currentGroups

                } else {
                    // API 호출에 실패한 경우
                    Log.e(
                        "DELETE_ERROR",
                        "Failed to delete group. Error code: ${response.code()}"
                    )
                    // 실패 처리 로직을 수행하세요.
//                    callback(false) // 삭제 실패 시 false 전달
                }
            }

            override fun onFailure(call: Call<GroupNameResponse>, t: Throwable) {
                // 네트워크 오류 등의 이유로 API 호출이 실패한 경우
                Log.e(
                    "DELETE_ERROR",
                    "Network error occurred. Message: ${t.message}"
                )
                // 실패 처리 로직을 수행하세요.
//                callback(false) // 삭제 실패 시 false 전달
            }
        })
    }
}
