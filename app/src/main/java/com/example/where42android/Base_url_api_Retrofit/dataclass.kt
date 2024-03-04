package com.example.where42android.Base_url_api_Retrofit

import com.google.gson.annotations.SerializedName
import retrofit2.http.PUT
import java.time.LocalDateTime
import java.util.Date


import java.net.URLDecoder
import java.util.Objects

//search
class searchMemberResponse : ArrayList<searchMemberResponse.searchMemberResponseItem>(){
    data class searchMemberResponseItem(
        val agree: Boolean,
        val comment: String,
        val friend: Boolean,
        val image: String,
        val inOrOut: Boolean,
        val intraId: Int,
        val intraName: String,
        val location: String
        )
}


//logout
data class logoutResponse(
    val logout: String
)


// 응답 데이터 클래스
data class ReissueResponse(
    val refreshToken: String
)

data class JoinResponse(
    val message: String
)

data class CustomException(
    val errorCode: Int,
    val errorMessage: String
)

//Member
data class Member(
    val intraId: Int,
    val intraName: String,
    val grade: String,
    val image: String,
    var comment: String?,
    val inCluster: Boolean,
    val agree: Boolean,
    val defaultGroupId: Int,
    var location: String,
    var responsecode : Int
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

//@PUT("v3/group/groupmember")
data class deleteFriendListRequest(
    val groupId: Int,
    val members: List<Int>
)


class deleteFriendListResponse : ArrayList<deleteFriendListResponse.deleteFriendListResponseItem>(){
    data class deleteFriendListResponseItem(
        val comment: Any,
        val groupId: Int,
        val groupName: Any,
        val image: Any,
        val inCluster: Boolean,
        val intraId: Int,
        val location: Any,
        val memberIntraName: Any
    )
}

//Group

//--------------------------
//group 편집 기능
//group 이름 바꾸기
data class GroupNameRequest(
    val groupId: Int,
    val groupName: String
)

data class GroupNameResponse(
    val groupId: Int,
    val groupName: String
)


//Group 삭제
data class GroupDeleteResponse(
    val groupId: Int,
    val groupName: String
)

//--------------------------
//Group 전부 보이기
class groups_memberlist : ArrayList<groups_memberlist.groups_memberlistItem>(){
    data class groups_memberlistItem(
//        val count: Int,
        val groupId: Int,
        var groupName: String,
        val members: List<Member>,
        var toggle : Boolean
    ) {
        data class Member(
            val intraId: Int,
            val intraName : String,
            val grade: String,
            val image: String,
            val comment: String,
            val inCluster: Boolean,
            val agree: Boolean,
            val defaultGroupId: Int,
            var location: String,
        )
//        data class Member(
//            val comment: String,
////            val groupId: Any,
//            val groupId: Int,
//            val groupName: String,
//            val image: String,
//            val inCluster: Boolean,
//            val location: String,
////            val memberId: Int,
//            val memberIntraName: String
//        )
    }
}

//Get deault(friend) group member list
class friendGroup_default_memberlist : ArrayList<friendGroup_default_memberlist.friendGroup_default_memberlistItem>(){
    data class friendGroup_default_memberlistItem(
        val intraId: Int,
        val intraName: String,
        val grade: String,
        val image: String,
        var comment: String?,
        val inCluster: Boolean,
        val agree: Boolean,
        val defaultGroupId: Int,
        var location: String,
//        val intraId: Int,
//        val IntraName: String,
//
//        val comment: String,
//        val groupId: Any,
//        val groupName: Any,
//        val image: String,
//        val inCluster: Boolean,
//
//        val location: String,

        val isChecked: Boolean = false
    )
}

//-------------------------------
//Newgroup 추가
data class  NewGroupRequest(
    val groupName: String,
    val intraId: Int
)

data class NewGroupResponses(
    val groupId: Int,
    val groupName: String,
)

//Grop에 memeberlist 추가
data class AddMembersRequest(
    val groupId: Int,
    val members: List<Int>
)

class addMembersResponse : ArrayList<addMembersResponse.addMembersResponseItem>(){
    data class addMembersResponseItem(
        val intraId: Int,
        val intraName: String,
        var grade : String,
        val image: String,
        val comment: String,
        val inCluster: Boolean,
        val agree : Boolean,
        var defaultGroupId: Int,
        val location: String,

    )
}

//-------------------------------

//v3/location
//1. v3/location/custom
data class locationCustomMemberRequest (
    val intraId: Int,
    val customLocation : String
)

data class locationCustomMemberResponse (
    val intraId: Int,
    val imacLocation: String,
    val imacUpdatedAt: Date,
    val customLocation: String,
    val customUpdatedAt: Date
)
