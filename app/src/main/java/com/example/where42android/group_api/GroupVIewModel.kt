//package com.example.where42android.group_api
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.liveData
//import com.example.where42android.GroupList
//import com.example.where42android.MemberAPI
//import kotlinx.coroutines.Dispatchers
//
//class GroupViewModel(private val memberAPI: MemberAPI) : ViewModel() {
//
//    fun getGroupInfo(memberId: Int) = liveData(Dispatchers.IO) {
//        val response = memberAPI.getGroupData(memberId)
//        emit(response.body())
//    }
//}