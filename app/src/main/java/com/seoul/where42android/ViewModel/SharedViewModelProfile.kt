package com.seoul.where42android.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.seoul.where42android.Base_url_api_Retrofit.Member
import com.seoul.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import com.seoul.where42android.Base_url_api_Retrofit.locationCustomMemberRequest
import com.seoul.where42android.LiveData.LiveDataProfile

class SharedViewModelProfile() : ViewModel() {
    private val profile: LiveDataProfile = LiveDataProfile.getInstance()

    val SharedProfileLiveData: LiveData<Member?>
        get() = profile.profileLiveData

    fun fetchProfileData(context: Context){
        profile.fetchProfileData(context)
    }
    fun fetchProfileComment(updateCommentRequest: UpdateCommentRequest, context: Context) {
        profile.fetchProfileComment(updateCommentRequest, context)
    }

    fun fetchProfileCustomLocation(locationCustomMemberRequest: locationCustomMemberRequest, context: Context) {
        profile.fetchProfileCustomLocation(locationCustomMemberRequest, context)
    }
}