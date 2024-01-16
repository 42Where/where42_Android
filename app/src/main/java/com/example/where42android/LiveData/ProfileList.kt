package com.example.where42android.LiveData

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.where42android.Base_url_api_Retrofit.CommentChangeMember
import com.example.where42android.Base_url_api_Retrofit.Member
import com.example.where42android.Base_url_api_Retrofit.MemberAPI
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection_data
import com.example.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import com.example.where42android.Base_url_api_Retrofit.locationCustomMemberRequest
import com.example.where42android.Base_url_api_Retrofit.locationCustomMemberResponse
import com.example.where42android.Base_url_api_Retrofit.member_custom_location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//의존성 주입(Dependency Injection) 라이브러리를 사용하거나, 직접 싱글톤 패턴을 구현할 수 있습니다.
// 여기에는 Kotlin의 object 키워드를 활용하여 싱글톤 객체를 만드는 방법이 있습니다.
class ProfileList private constructor(): ViewModel() {

//    private val retrofitAPI = RetrofitConnection.getInstance().create(MemberAPI::class.java)
private lateinit var retrofitAPI : MemberAPI
    private val profile = MutableLiveData<Member?>()
    val profileLiveData: LiveData<Member?>
        get() = profile

    // 싱글톤으로 사용할 객체 선언
    companion object {
        @Volatile
        private var instance: ProfileList? = null

        fun getInstance(): ProfileList {
            return instance ?: synchronized(this) {
                instance ?: ProfileList().also { instance = it }
            }
        }
    }

    fun getMemberData(intraId: Int, token: String)
    {
        retrofitAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)
        val call = retrofitAPI.getMember(intraId)

        call.enqueue(object : Callback<Member> {
            override fun onResponse(
                call: Call<Member>,
                response: Response<Member>
            ) {
//                Log.e("check2", "res Suc ${response}")
//                Log.e("check2", "res Suc body ${response.body()}")
                Log.e("check2", "res Suc $response")
                Log.e("check2", "res Suc code ${response.code()}")
                Log.e("check2", "res Suc message ${response.message()}")
//                val bodyString = response.body()?.string()
//                Log.e("check2", "res Suc body $bodyString")

                val res = response.body()

                Log.e("check2", "res Suc ${res}")
                if (response.isSuccessful) {
                    val member: Member? = res
                    Log.e("check2", "member Suc ${member}")
                    member?.let {
                        profile.value = it
                        Log.e("check2", "Suc ${response.code()}")
                        Log.e("check2", "Suc ${token}")
                    } ?: run {
                        profile.value = Member(
                            intraId = -1,
                            intraName = "",
                            grade = "",
                            image = "",
                            comment = "",
                            inCluster = false,
                            agree = false,
                            defaultGroupId = -1,
                            location = ""
                        )
                        Log.d("check2", "run ${profile.value}")
                        Log.e("check2", "run ${token}")
                    }
                }
                else {
                    when (response.code()) {
                        401 -> {
                            //여기는 토큰이 만료되었다는 거임
                            profile.value = null
                            Log.d("check2", "401, ${response.headers()}, ${response}")
                            Log.e("check2", "401 ${token}")
                            //원래는 여기 reissue가 와야함.

                        }
                        else -> {
                            // 기본적으로 어떻게 처리할지 작성
                        }
                    }
//                    profile.value = null
//                    Log.d("ProfileList", "error1")
                }
            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
                profile.value = null // Setting null in case of network failure or exceptions
                Log.d("ProfileList", "error : ${t.message}")
            }

        })
    }


//
//
//        viewModelScope.launch {
//            try {
//                val response = retrofitAPI.getMember(intraId)
//                if (response.isSuccessful) {
//                    val member: Member? = response.body()
//                    member?.let {
//                        profile.value = it
//                        Log.e("ProfileList", "${profile.value}")
//                    } ?: run {
//                        profile.value = Member(
//                            intraId = -1,
//                            intraName = "",
//                            grade = "",
//                            image = "",
//                            comment = "",
//                            inCluster = false,
//                            agree = false,
//                            defaultGroupId = -1,
//                            location = ""
//                        )
//                        Log.d("ProfileList", "${profile.value}")
//                    }
//                } else {
//                    // Handle unsuccessful response
//                    profile.value = null
//                    Log.d("ProfileList", "error1")
//                }
//            } catch (e: Exception) {
//                profile.value = null // Setting null in case of network failure or exceptions
//                Log.d("ProfileList", "error2")
//                e.printStackTrace()
//            }
//        }
//
//    }


//    fun getMemberData(intraId: Int) {
//        viewModelScope.launch {
//            try {
//                val response = retrofitAPI.getMember(intraId)
//                if (response.isSuccessful) {
//                    val member: Member? = response.body()
//                    member?.let {
//                        profile.value = it
//                        Log.e("ProfileList", "${profile.value}")
//                    } ?: run {
//                        profile.value = Member(
//                            intraId = -1,
//                            intraName = "",
//                            grade = "",
//                            image = "",
//                            comment = "",
//                            inCluster = false,
//                            agree = false,
//                            defaultGroupId = -1,
//                            location = ""
//                        )
//                        Log.d("ProfileList", "${profile.value}")
//                    }
//                } else {
//                    // Handle unsuccessful response
//                    profile.value = null
//                    Log.d("ProfileList", "error1")
//                }
//            } catch (e: Exception) {
//                profile.value = null // Setting null in case of network failure or exceptions
//                Log.d("ProfileList", "error2")
//                e.printStackTrace()
//            }
//        }
//
//    }

//    fun updateMemberComment(updateCommentRequest: UpdateCommentRequest) {
//        Log.d("ProfileList", "updateMemberComment : ${profile.value}")
//        retrofitAPI.updateMemberComment(updateCommentRequest).enqueue(object :
//            Callback<CommentChangeMember> {
//            override fun onResponse(
//                call: Call<CommentChangeMember>,
//                response: Response<CommentChangeMember>
//            ) {
//                if (response.isSuccessful) {
//                    val commentChangeMember: CommentChangeMember? = response.body()
//                    val currentProfile = profile.value
//                    Log.d("ProfileList", "${profile.value}")
//                    val updatedComment = commentChangeMember?.comment ?: ""
//
//                    currentProfile?.let {
//                        val updatedProfile = it.copy(comment = updatedComment)
//                        profile.postValue(updatedProfile) // Update LiveData with new value
//                    } ?: run {
//                        // Handle the case when profile.value is null
//                        val newProfile = Member(
//                            intraId = -1,
//                            intraName = "",
//                            grade = "",
//                            image = "",
//                            comment = updatedComment,
//                            inCluster = false,
//                            agree = false,
//                            defaultGroupId = -1,
//                            location = ""
//                            data = null
//                        )
//                        profile.postValue(newProfile)
//                    }
//                    // 추가: 성공 응답 로그
//                    Log.d("ProfileList", "onResponse: Success")
//                } else {
//                    // 추가: 실패 응답 로그
//                    Log.e("ProfileList", "onResponse: Failure")
//                }
//            }
//
//            override fun onFailure(call: Call<CommentChangeMember>, t: Throwable) {
//                Log.e("respone2 fail", "fail")
//                // Handle failure
//                // For example, handle the failure accordingly
//            }
//        })
//    }

        private val editlocationcustom = MutableLiveData<locationCustomMemberResponse>()
        val editlocationcustomLiveData: LiveData<locationCustomMemberResponse>
        get() = editlocationcustom

        fun updateMemberCustomLocaton(locationCustomMemberRequest: locationCustomMemberRequest) {
            val retrofitAPI =
                RetrofitConnection_data.getInstance().create(member_custom_location::class.java)
            val call = retrofitAPI.customLocationChange(locationCustomMemberRequest)

            call.enqueue(object : Callback<locationCustomMemberResponse> {
                override fun onResponse(
                    call: Call<locationCustomMemberResponse>,
                    response: Response<locationCustomMemberResponse>
                ) {
                    if (response.isSuccessful) {
                        val newGroupResponse = response.body()
                        Log.e(
                            "DELETE_Suc",
                            "Sucess to editcustomlocation. code: ${response.code()}"
                        )

                        newGroupResponse?.let { response ->
                            Log.d("DELETE_Suc", "newGroupResponse : ${newGroupResponse}")
                            Log.d("DELETE_Suc", "profile.value  : ${profile.value}")
                            var profileValue = profile.value
                            profileValue?.location = response.customLocation
                            profile.value = profileValue
                            Log.d("DELETE_Suc", "profile.value_fin  : ${profile.value}")
                        }
                        // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
                    } else {
                        // API 호출에 실패한 경우
                        Log.e(
                            "DELETE_ERROR",
                            "Sucess to editcustomlocation. code: ${response.code()}"
                        )

                    }
                }

                override fun onFailure(call: Call<locationCustomMemberResponse>, t: Throwable) {

                    Log.e("CREATE_ERROR", "Network error occurred. Message: ${t.message}")
                }
            })


        }

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
//                // For example, setting an empty list
//                val emptyList: List<Member> = emptyList()
//                // Process empty list or handle the error accordingly
//            }
//        }
//
//        override fun onFailure(call: Call<List<Member>>, t: Throwable) {
//            // Handle failure
//            // For example, setting an empty list
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
//                // For example, handle the error accordingly
//            }
//        }
//
//        override fun onFailure(call: Call<CommentChangeMember>, t: Throwable) {
//            // Handle failure
//            // For example, handle the failure accordingly
//        }
//    })