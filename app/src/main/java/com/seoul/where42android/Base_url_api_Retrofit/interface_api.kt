package com.seoul.where42android.Base_url_api_Retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

import retrofit2.http.DELETE
import retrofit2.http.PUT


//search
interface SearchApiService {
    @GET("v3/search")
    suspend fun searchMember(
        @Query("keyWord") keyWord: String
    ): Response<List<searchMemberResponse.searchMemberResponseItem>> // YourResponseModel에는 실제 응답 모델을 대입해야 합니다.
}

//logout
interface logoutAPI{
    @POST("v3/logout")
    suspend fun logout():Response<logoutResponse>
}

//reissue
interface ReissueAPI{
    // POST 요청을 위한 함수
    @POST("v3/jwt/reissue")
    suspend fun reissueToken(
        @Body intraIdRequest: intraIdRequest
    ): Response<ReissueResponse>

}


//JOIN
interface JoinAPI{
    @POST("v3/join")
    suspend fun join(@Query("intra_id") intraId: Int
    ): Response<JoinResponse>
}


//Member
interface MemberAPI {
    @GET("v3/member")
//    fun getMember(@Query("intraId") intraId: Int): Call<Member>
//    suspend fun getMember(@Query("intraId") intraId: Int): Response<Member>
    suspend fun getMember(): Response<Member>

//    @GET("v3/member/all")
//    fun getMembers(): Call<List<Member>>

    @POST("v3/member/comment")
    suspend fun updateMemberComment(
        @Body request: UpdateCommentRequest
    ): Response<Member> // YourResponseModel은 서버 응답에 따라 실제 응답 모델로 변경되어야 합니다

    @PUT("v3/group/groupmember")
    suspend fun deleteFriendList(
        @Body request: deleteFriendListRequest
    ): Response<List<deleteFriendListResponse.deleteFriendListResponseItem>>

}



//interface MemberallListService {
//
//    // Define endpoint and query parameter
//    @GET("/v3/member/all")
//    fun getMemberAllList(): Call<List<MemberAll.MemberAllItem>>
//}
//

//Group


interface GroupMemberListService {

    // Define endpoint and query parameter
    @GET("/v3/group")
    suspend fun getGroupMemberList(@Query("intraId") intraId: Int): Response<List<groups_memberlist.groups_memberlistItem>>
}

//----------------------------
//group 편집 기능
//1. group 이름 바꾸기
interface GroupChangeName{
    @POST("v3/group/name")
    suspend fun groupChangeName(@Body groupData: GroupNameRequest):Response<GroupNameResponse>
}

//2. Group 삭제
interface GroupDelete {
    @DELETE("v3/group")
    suspend fun deleteGroup(@Query("groupId") groupId: Int): Response<GroupDeleteResponse>
}
//----------------------------

//새로운 그룹 만들기
interface NewGroup {
    @POST("v3/group")
    suspend fun newGroup(@Body request: NewGroupRequest
    ): Response <NewGroupResponses> // YourResponseModel은 서버 응답에 따라 실제 응답 모델로 변경되어야 합니다

}

//새로운 그룹 만들고 나서 member추가하기
interface GroupAddMemberlist {
    @POST("/v3/group/groupmember/members")
    suspend fun addMembersToGroup(@Body request: AddMembersRequest): Response<List<addMembersResponse.addMembersResponseItem>>
}


//group memberlist 들고오기
interface Deafult_friendGroup_memberlist {
    @GET("v3/group/groupmember")
    suspend fun getdefaultGroupList(@Query("groupId") groupId: Int): Response<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
    @POST("/v3/group/groupmember/not-ingroup")
    suspend fun getGroupMembersNotInGroup(@Query("groupId") groupId: Int): Response <List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>

}

//location/custom
interface memberCustomLocation {
    @POST("v3/location/custom")
    suspend fun customLocationChange(@Body request: locationCustomMemberRequest) : Response<locationCustomMemberResponse>
}
