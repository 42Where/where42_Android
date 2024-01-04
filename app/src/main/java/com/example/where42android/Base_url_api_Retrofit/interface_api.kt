package com.example.where42android.Base_url_api_Retrofit


import com.example.where42android.Base_url_api_Retrofit.groups_memberlist
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

import retrofit2.http.DELETE
//Member
interface MemberAPI {
    @GET("v3/member")
    suspend fun getMember(@Query("intraId") intraId: Int): Response<Member>

    @GET("v3/member/all")
    fun getMembers(): Call<List<Member>>

    @POST("v3/member/comment")
    fun updateMemberComment(
        @Body request: UpdateCommentRequest
    ): Call<CommentChangeMember> // YourResponseModel은 서버 응답에 따라 실제 응답 모델로 변경되어야 합니다

}

interface MemberallListService {

    // Define endpoint and query parameter
    @GET("/v3/member/all")
    fun getMemberAllList(): Call<List<MemberAll.MemberAllItem>>
}


//Group


interface GroupMemberListService {

    // Define endpoint and query parameter
    @GET("/v3/group")
    fun getGroupMemberList(@Query("intraId") intraId: Int): Call<List<groups_memberlist.groups_memberlistItem>>
}

//----------------------------
//group 편집 기능
//1. group 이름 바꾸기
interface GroupChangeName{
    @POST("v3/group/name")
    fun groupChangeName(@Body groupData: GroupNameRequest):Call<GroupNameResponse>
}

//2. Group 삭제
interface GroupDelete {
    @DELETE("v3/group")
    fun deleteGroup(@Query("groupId") groupId: Int): Call<GroupDeleteResponse>
}
//----------------------------

//새로운 그룹 만들기
interface NewGroup {
    @POST("v3/group")
    fun newGroup(@Body request: NewGroupRequest
    ): Call<NewGroupResponses> // YourResponseModel은 서버 응답에 따라 실제 응답 모델로 변경되어야 합니다

}

//새로운 그룹 만들고 나서 member추가하기
interface GroupAddMemberlist {
    @POST("/v3/group/groupmember/members")
    fun addMembersToGroup(@Body request: AddMembersRequest): Call<List<addMembersResponse.addMembersResponseItem>>
}


//default group memberlist 들고오기
interface Deafult_friendGroup_memberlist {
    @GET("v3/group/groupmember")
    fun getdefaultGroupList(@Query("groupId") groupId: Int): Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
}

//location/custom
interface member_custom_location {
    @POST("v3/location/custom")
    fun customLocationChange(@Body request: locationCustomMemberRequest) : Call <locationCustomMemberResponse>
}
