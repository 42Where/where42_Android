package com.example.where42android.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GroupMemberListService {

    // Define endpoint and query parameter
    @GET("/v3/group")
    fun getGroupMemberList(@Query("intraId") intraId: Int): Call<List<groups_memberlist.groups_memberlistItem>>
}