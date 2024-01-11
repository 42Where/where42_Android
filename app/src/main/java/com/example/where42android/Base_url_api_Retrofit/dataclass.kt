package com.example.where42android.Base_url_api_Retrofit

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.Date


import java.net.URLDecoder
import java.util.Objects

data class OAuthResponse(
    val code: String,
    val state: String
)

//fun parseOAuthResponse(url: String): OAuthResponse {
//    val uri = URLDecoder.decode(url, "UTF-8")
//    val params = uri.split("?").last().split("&")
//    val codeParam = params.find { it.startsWith("code=") }
//    val stateParam = params.find { it.startsWith("state=") }
//
//    val code = codeParam?.substringAfter("code=")
//    val state = stateParam?.substringAfter("state=")
//
//    return OAuthResponse(code.orEmpty(), state.orEmpty())
//}
//
//fun main() {
//    val url = "https://profile.intra.42.fr/users/auth/keycloak_student/callback?code=received_authorization_code&state=your_state_value"
//    val oAuthResponse = parseOAuthResponse(url)
//
//    println("Authorization Code: ${oAuthResponse.code}")
//    println("State: ${oAuthResponse.state}")
//}
//


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
    var data: Object
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
        val groupName: String,
        val members: List<Member>
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
            val location: String,
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
        val comment: String,
        val groupId: Any,
        val groupName: Any,
        val image: String,
        val inCluster: Boolean,
        val intraId: Int,
        val location: String,
        val memberIntraName: String,
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
    val members: List<String>
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
