package com.seoul.where42android.model

//data class RecyclerInViewModel(var emoji: String, var content: String)

data class RecyclerInViewModel(
    var emoji: String,
    var location: String,
    var comment: String,
    var intra_name: String,
    val intra_id: Int,
    val included_group: Int
)

data class SearchRecyclerInViewModel(
    var emoji: String,
//    var location: String,
//    var comment: String,
    var intra_name: String,
    val intra_id: Int
)