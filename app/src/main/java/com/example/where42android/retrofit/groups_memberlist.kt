package com.example.where42android.retrofit

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