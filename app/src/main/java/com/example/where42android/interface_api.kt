package com.example.where42android


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

//interface UpbitAPI {
//    @GET("v3/member")
//    suspend fun getMember(@Query("intraId") intraId: Int): Response<Member>
//}
interface MemberAPI {
    @GET("v3/member")
    suspend fun getMember(@Query("intraId") intraId: Int): Response<Member>

    @GET("v3/member/")
    fun getMembers(): Call<List<Member>>

//    @GET("v3/group/")
//    fun getGroupData(@Query("memberId") memberId: Int): Call<GroupList>
//    @GET("v3/group/info")
//    fun getGroupInfo(@Query("memberId") memberId: Int): Response<List<GroupList>>
}

//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface MemberAPI {
//    @GET("v3/member")
//    suspend fun getMember(@Query("intraId") intraId: Int): Response<Member>
//}