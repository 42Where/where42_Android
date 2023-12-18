package com.example.where42android

data class Coin(
    val market: String,
    val korean_name: String,
    val english_name: String
)

//data class Member(
//    val intraId: Int,
//    val intraName: String,
//    val grade: String,
//    val image : String,
//    val comment : String,
//    val inCluster : Boolean,
//    val agree : Boolean,
//    val defaultGroupId : Int,
//    val location : String
//)

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


//{
//    "intraId": 1,
//    "intraName": "member0",
//    "grade": "2022-10-31",
//    "image": "https://ibb.co/94KmxcT",
//    "comment": null,
//    "inCluster": true,
//    "agree": true,
//    "defaultGroupId": 1,
//    "location": "c1r1s0"
//}