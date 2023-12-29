//package com.example.where42android.group_api
//
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.where42android.MemberAPI
//
//class GroupViewModelFactory(private val memberAPI: MemberAPI) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return GroupViewModel(memberAPI) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}