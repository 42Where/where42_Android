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
interface GroupDelete {
    @DELETE("v3/group")
    fun deleteGroup(@Query("groupId") groupId: Int): Call<GroupDeleteResponse>
}

interface NewGroup {
    @POST("v3/group")
    fun newGroup(@Body request: NewGroupRequest
    ): Call<NewGroupResponses> // YourResponseModel은 서버 응답에 따라 실제 응답 모델로 변경되어야 합니다


}