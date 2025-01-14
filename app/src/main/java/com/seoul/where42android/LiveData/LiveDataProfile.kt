package com.seoul.where42android.LiveData

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seoul.where42android.Base_url_api_Retrofit.Member
import com.seoul.where42android.Base_url_api_Retrofit.MemberAPI
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import com.seoul.where42android.Base_url_api_Retrofit.locationCustomMemberRequest
import com.seoul.where42android.Base_url_api_Retrofit.memberCustomLocation
import com.seoul.where42android.utils.ApiUtils
import kotlinx.coroutines.launch

//의존성 주입(Dependency Injection) 라이브러리를 사용하거나, 직접 싱글톤 패턴을 구현할 수 있습니다.
// 여기에는 Kotlin의 object 키워드를 활용하여 싱글톤 객체를 만드는 방법이 있습니다.
class LiveDataProfile private constructor(): ViewModel() {

    private val profile = MutableLiveData<Member?>()
    val profileLiveData: LiveData<Member?>
        get() = profile

    // 싱글톤으로 사용할 객체 선언
    companion object {
        @Volatile
        private var instance: LiveDataProfile? = null

        fun getInstance(): LiveDataProfile {
            return instance ?: synchronized(this) {
                instance ?: LiveDataProfile().also { instance = it }
            }
        }
    }
    fun fetchProfileData(context: Context) {
        viewModelScope.launch {
            try {
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val memberAPI = RetrofitConnection.getInstance(accessToken).create(MemberAPI::class.java)
                    memberAPI.getMember()
                }

                if (response != null && response.isSuccessful) {
                    val member = response.body()
                    member?.let {
                        it.location = it.location ?: if (it.inCluster) "개포" else "퇴근"
                        profile.value = it // 동기적으로 LiveData 업데이트
                        Log.d("ProfileLiveDataCheck", "fetchProfileData: Member updated: $it")
                    }
                } else {
                    Log.e("SharedViewModel_Profile", "Error: Response unsuccessful or null")
                    profile.value = null // LiveData에 null 설정
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel_Profile", "Error fetching member data", e)
                profile.value = null // LiveData에 null 설정
            }
        }
    }

    fun fetchProfileComment(updateCommentRequest: UpdateCommentRequest, context: Context) {
        viewModelScope.launch {
            try {
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(MemberAPI::class.java)
                    retrofitAPI.updateMemberComment(updateCommentRequest)
                }

                if (response != null && response.isSuccessful) {
                    val updatedComment = response.body()?.comment ?: ""
                    Log.d("ProfileLiveDataCheck", "Updated Comment: $updatedComment")

                    val resp = profile.value // 동기적으로 LiveData 업데이트
                    Log.d("ProfileLiveDataCheck", "fetchProfileComment: Member updated: $resp")

                    // LiveData 값 확인 및 업데이트
                    profile.value?.let { currentProfile ->
                        val updatedProfile = currentProfile.copy(comment = updatedComment)
                        profile.value = updatedProfile // 동기적으로 업데이트
                        Log.d("ProfileLiveDataCheck", "LiveData updated: $updatedProfile")
                    } ?: run {
                        Log.e("ProfileLiveDataCheck", "LiveData is null, creating new profile.")
                        val newProfile = Member(
                            intraId = -1,
                            intraName = "",
                            grade = "",
                            image = "",
                            comment = updatedComment,
                            inCluster = false,
                            agree = false,
                            defaultGroupId = -1,
                            location = ""
                        )
                        profile.value = newProfile // 동기적으로 새로운 값 설정
                    }
                } else {
                    Log.e("SharedViewModel_Profile", "Failed to update comment. Response: ${response?.code()}, Message: ${response?.message()}")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel_Profile", "Error updating comment", e)
            }
        }
    }

    fun fetchProfileCustomLocation(locationCustomMemberRequest: locationCustomMemberRequest, context: Context) {
        viewModelScope.launch {
            try {
                // API 요청 수행
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(memberCustomLocation::class.java)
                    retrofitAPI.customLocationChange(locationCustomMemberRequest)
                }

                if (response != null && response.isSuccessful) {
                    val updatedLocation = response.body()?.customLocation ?: ""
                    Log.d("ProfileCustomLocation", "Updated Location: $updatedLocation")

                    // LiveData 값 업데이트
                    profile.value?.let { currentProfile ->
                        val updatedProfile = currentProfile.copy(location = updatedLocation)
                        profile.value = updatedProfile // 동기적으로 업데이트
                        Log.d("ProfileCustomLocation", "LiveData updated: $updatedProfile")
                    } ?: run {
                        Log.e("ProfileCustomLocation", "LiveData is null, creating new profile.")
                        val newProfile = Member(
                            intraId = -1,
                            intraName = "",
                            grade = "",
                            image = "",
                            comment = "",
                            inCluster = false,
                            agree = false,
                            defaultGroupId = -1,
                            location = updatedLocation
                        )
                        profile.value = newProfile // 동기적으로 새로운 값 설정
                        Log.d("ProfileCustomLocation", "New Profile Created: $newProfile")
                    }
                } else {
                    Log.e("ProfileCustomLocation", "Failed to update location. Response: ${response?.code()}, Message: ${response?.message()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileCustomLocation", "Error updating location", e)
            }
        }
    }

//    private val editlocationcustom = MutableLiveData<locationCustomMemberResponse>()
//    val editlocationcustomLiveData: LiveData<locationCustomMemberResponse>
//        get() = editlocationcustom

//    fun fetchProfileCustomLocation(locationCustomMemberRequest: locationCustomMemberRequest, context: Context) {
////            val retrofitAPI =
////                RetrofitConnection_data.getInstance().create(member_custom_location::class.java)
//        val retrofitAPI =
//            RetrofitConnection.getInstance(token).create(memberCustomLocation::class.java)
//        val call = retrofitAPI.customLocationChange(locationCustomMemberRequest)
//
//        call.enqueue(object : Callback<locationCustomMemberResponse> {
//            override fun onResponse(
//                call: Call<locationCustomMemberResponse>,
//                response: Response<locationCustomMemberResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val newGroupResponse = response.body()
//                    Log.e(
//                        "DELETE_Suc",
//                        "Sucess to editcustomlocation. code: ${response.code()}"
//                    )
//
//                    newGroupResponse?.let { response ->
//                        Log.d("DELETE_Suc", "newGroupResponse : ${newGroupResponse}")
//                        Log.d("DELETE_Suc", "profile.value  : ${profile.value}")
//                        var profileValue = profile.value
//                        profileValue?.location = response.customLocation
//                        profile.value = profileValue
//                        Log.d("DELETE_Suc", "profile.value_fin  : ${profile.value}")
//                    }
//                    // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
//                } else {
//                    // API 호출에 실패한 경우
//                    Log.e(
//                        "DELETE_ERROR",
//                        "Sucess to editcustomlocation. code: ${response.code()}"
//                    )
//
//                }
//            }
//
//            override fun onFailure(call: Call<locationCustomMemberResponse>, t: Throwable) {
//
//                Log.e("CREATE_ERROR", "Network error occurred. Message: ${t.message}")
//            }
//        })
//        }
    }

//fun getAllMembers() {
//    retrofitAPI.getMembers().enqueue(object : Callback<List<Member>> {
//        override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
//            if (response.isSuccessful) {
//                // Handle successful response
//                val members: List<Member>? = response.body()
//                // Process the list of members as needed
//            } else {
//                // Handle unsuccessful response
//                // Handle unsuccessful response
//                // For seoul, setting an empty list
//                val emptyList: List<Member> = emptyList()
//                // Process empty list or handle the error accordingly
//            }
//        }
//
//        override fun onFailure(call: Call<List<Member>>, t: Throwable) {
//            // Handle failure
//            // For seoul, setting an empty list
//            val emptyList: List<Member> = emptyList()
//            // Process empty list or handle the failure accordingly
//        }
//    })
//}
//
//fun updateMemberComment(updateCommentRequest: UpdateCommentRequest) {
//    retrofitAPI.updateMemberComment(updateCommentRequest).enqueue(object : Callback<CommentChangeMember> {
//        override fun onResponse(call: Call<CommentChangeMember>, response: Response<CommentChangeMember>) {
//            if (response.isSuccessful) {
//                // Handle successful response
//                val commentChangeMember: CommentChangeMember? = response.body()
//                // Process the updated member comment as needed
//            } else {
//                // Handle unsuccessful response
//                // For seoul, handle the error accordingly
//            }
//        }
//
//        override fun onFailure(call: Call<CommentChangeMember>, t: Throwable) {
//            // Handle failure
//            // For seoul, handle the failure accordingly
//        }
//    })