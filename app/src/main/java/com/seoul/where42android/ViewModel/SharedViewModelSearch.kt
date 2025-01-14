package com.seoul.where42android.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seoul.where42android.Base_url_api_Retrofit.MemberAPI
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.SearchApiService
import com.seoul.where42android.Base_url_api_Retrofit.searchMemberResponse
import com.seoul.where42android.main.UserSettings
import com.seoul.where42android.utils.ApiUtils
import kotlinx.coroutines.launch

class SharedViewModelSearch private constructor(): ViewModel() {


    private val searchList = MutableLiveData<List<searchMemberResponse.searchMemberResponseItem>?>()
    val searchListLiveData: LiveData<List<searchMemberResponse.searchMemberResponseItem>?>
        get() = searchList


    val usersetting = UserSettings.getInstance()

    // 싱글톤으로 사용할 객체 선언
    companion object {
        @Volatile
        private var instance: SharedViewModelSearch? = null

        fun getInstance(): SharedViewModelSearch {
            return instance ?: synchronized(this) {
                instance ?: SharedViewModelSearch().also { instance = it }
            }
        }
    }

    fun getSearchMemberList(searchMember: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = ApiUtils.performApiRequest(context) {accessToken ->
                    val SearchAPI = RetrofitConnection.getInstance(accessToken).create(SearchApiService::class.java)
                    SearchAPI.searchMember(searchMember)
                }

                if (response != null && response.isSuccessful) {
                    val searchListResponse = response.body()
                    if (searchListResponse != null) {
                        searchList.value = searchListResponse
                    } else {
                        searchList.value = null
                    }
                    Log.d("searchList", "searchList : $searchListResponse")
                } else {
                    Log.e(
                        "getSearchMemberList",
                        "API 요청 실패: ${response?.code()} - ${response?.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("getSearchMemberList", "searchList API 호출 실패", e)
            }
        }
    }
}

