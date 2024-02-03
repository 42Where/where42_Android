package com.example.where42android.model

//data class RecyclerInViewModel(var emoji: String, var content: String)

data class RecyclerInViewModel(
    var emoji: String,
    var location: String,
    var comment: String,
    var intra_name: String,
    val intra_id: Int,
    val included_group: Int
)