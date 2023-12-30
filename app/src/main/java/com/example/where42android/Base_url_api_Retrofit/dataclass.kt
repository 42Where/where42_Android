package com.example.where42android.Base_url_api_Retrofit

import com.google.gson.annotations.SerializedName

//Member
data class Member(
    val intraId: Int,
    val intraName: String,
    val grade: String,
    val image: String,
    val comment: String?,
    val inCluster: Boolean,
    val agree: Boolean,
    val defaultGroupId: Int,
    val location: String
)

//@POST("v3/member/comment")
data class CommentChangeMember(
    val intraId: Int,
    val intraName: String,
    val grade: String,
    val image: String,
    val comment: String,
    val inCluster: Boolean,
    val agree: Boolean,
    val defaultGroupId: Int,
    val location: String
)

data class UpdateCommentRequest(
    val intraId: Int,
    val comment: String
)

class MemberAll : ArrayList<MemberAll.MemberAllItem>(){
    data class MemberAllItem(
        val agree: Boolean,
        val comment: String,
        val defaultGroupId: Int,
        val grade: String,
        val image: String,
        val inCluster: Boolean,
        val intraId: Int,
        val intraName: String,
        val location: String
    )
}


//Group

//Group 삭제
data class GroupDeleteResponse(
    val groupId: Int,
    val groupName: String
)

//Group 전부 보이기
class groups_memberlist : ArrayList<groups_memberlist.groups_memberlistItem>(){
    data class groups_memberlistItem(
        val count: Int,
        val groupId: Int,
        val groupName: String,
        val members: List<Member>
    ) {
        data class Member(
            val comment: String,
            val groupId: Any,
            val groupName: Any,
            val image: String,
            val inCluster: Boolean,
            val location: String,
            val memberId: Int,
            val memberIntraName: String
        )
    }
}

//Newgroup 추가
data class  NewGroupRequest(
    val groupName: String,
    val intraId: Int
)

data class NewGroupResponses(
    val groupId: Int,
    val groupName: String,
)