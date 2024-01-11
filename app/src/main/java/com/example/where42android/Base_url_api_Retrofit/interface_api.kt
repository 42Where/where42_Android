package com.example.where42android.Base_url_api_Retrofit


import android.util.Log
import com.example.where42android.Base_url_api_Retrofit.groups_memberlist
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Converter
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import java.lang.reflect.Type
import java.util.logging.Logger

//Oauth 2.0
interface OAuthService {
    @FormUrlEncoded
    @POST("/auth/realms/students-42/protocol/openid-connect/auth")
    fun getAuthorizationCode(
        @Field("client_id") clientId: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("response_type") responseType: String,
        @Field("state") state: String
    ): Call<OAuthResponse> // OAuthResponse는 서버에서 반환되는 응답을 모델링하는 데이터 클래스입니다.
}


//Member
interface MemberAPI {
    @GET("v3/member")
//    suspend fun getMember(@Query("intraId") intraId: Int): Response<Member>
    fun getMember(@Query("intraId") intraId: Int): Call<Member>

    @GET("v3/member/all")
    fun getMembers(): Call<List<Member>>

    @POST("v3/member/comment")
    fun updateMemberComment(
        @Body request: UpdateCommentRequest
    ): Call<CommentChangeMember> // YourResponseModel은 서버 응답에 따라 실제 응답 모델로 변경되어야 합니다

    companion object {
        private const val BASE_URL = "http://13.209.149.15:8080/"

        // Interceptor를 생성하는 함수
        private fun createInterceptor(): Interceptor {
            return Interceptor { chain ->
                val request = chain.request()
                Log.d("Interceptor", "Interceptor is invoked for URL: ${request.url}")

                val response = chain.proceed(request)
                Log.d("Interceptor", "Interceptor is invoked for URL: ${response}")

                val requestURL =  "{" + request.url + "}"
                Log.e("Interceptor", "a ${requestURL}")
                val contentType = response.body?.contentType()
                val bodyString = response.body?.string() ?: ""
                val url = response.request.url.toString()
                Log.e("Interceptor", "${url}")

                try {
                    Log.e("Interceptor", "contentType : ${contentType}")
                    Log.e("Interceptor", "bodyString ${bodyString}")
//                    val jsonObject = JSONObject(bodyString)
                    val jsonObject = JSONObject()
                    val url2 = "{" + url + "}"
                    jsonObject.put("url", "{" + url + "}")
//                    val jsonObject = JSONObject(url)
                    // JSONObject로 변환된 응답을 이용한 작업 수행
//                    Log.d("Interceptor", "JSON Response: $jsonObject")

//                    val dataPayload = if (responseJson.has(KEY_DATA)) responseJson[KEY_DATA] else EMPTY_JSON
                    // 원래의 response를 다시 만들어줍니다.
                    val newResponse = response.newBuilder()
                        .code(200)
                        .addHeader("X-Naver-Client-Secret", "fyfwt9PCUN")
                        .body(requestURL.toString().toResponseBody())
                        .message(requestURL)
                        .build()

                    // newResponse로 반환합니다.
                     Log.e ("Interceptor", "plz")
//                    return@Interceptor newResponse
                    return@Interceptor newResponse
//                    return@Interceptor response.newBuilder()
//                        .code(200)
//                        .body(url.toResponseBody())
//                        .build()


                } catch (e: JSONException) {
                    // JSON 파싱 중 에러 발생 시, 예외 처리
                    Log.e("Interceptor", "Error parsing JSON: ${e.message}")
                    Log.e("Interceptor", " ${response}")
                    return@Interceptor response
                }


                Log.d("Interceptor", "Interceptor is invoked for URL: ${url}")
                if (url.startsWith("https:")) {
                    Log.d("Interceptor", "here")
                    return@Interceptor response.newBuilder()
                        .code(200)
                        .body(requestURL.toResponseBody())
                        .build()

                } else {
                    return@Interceptor response.newBuilder()
                        .code(401)
                        .message("CustomInterceptor: There is no value starting with \"https:\"")
                        .build()
                }
            }


        }

//        private fun response.extractResponseJson(): JSONObject {
//            val jsonString = this.body?.string() ?: EMPTY_JSON
//            return try {
//                JSONObject(jsonString)
//            } catch (exception: Exception) {
//                Logger.d(
//                    "VinylaResponseUnboxingInterceptor",
//                    "서버 응답이 json이 아님 : $jsonString"
//                )
//                throw UnexpectedServerError(cause = exception)
//            }
//        }

        // HttpLoggingInterceptor를 생성하는 함수
        class NullOnEmptyConverterFactory : Converter.Factory() {
            fun converterFactory() = this
            override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
                val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
                override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
                    try{
                        nextResponseBodyConverter.convert(value)
                    }catch (e:Exception){
                        e.printStackTrace()
                        null
                    }
                } else{
                    null
                }
            }
        }

        private fun httpLoggingInterceptor(): HttpLoggingInterceptor {

            val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if (message.startsWith("\"https:")) {
                        // 원하는 작업을 수행하고자 할 때 처리 코드를 추가하세요.
                        Log.e("Interceptor", "vb")
                        var url = message
                    }
                }
            })
            Log.e("Interceptor", "hg ${HttpLoggingInterceptor.Level.BODY}")
            return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        fun create(): MemberAPI {
            val interceptor = createInterceptor()

//            val loggingInterceptor = httpLoggingInterceptor()
            var loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor) // 생성한 Interceptor 추가
                .addInterceptor(loggingInterceptor)

            val gson = GsonBuilder().setLenient().create()
            val nullOnEmptyConverterFactory = NullOnEmptyConverterFactory()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient.build())
                .addConverterFactory(nullOnEmptyConverterFactory)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .build()


//                    .addConverterFactory(GsonConverterFactory.create())// 1)
            return retrofit.create(MemberAPI::class.java)
        }
    }

}



interface MemberallListService {

    // Define endpoint and query parameter
    @GET("/v3/member/all")
    fun getMemberAllList(): Call<List<MemberAll.MemberAllItem>>
}


//Group


interface GroupMemberListService {

    // Define endpoint and query parameter
    @GET("/v3/group")
    fun getGroupMemberList(@Query("intraId") intraId: Int): Call<List<groups_memberlist.groups_memberlistItem>>
}

//----------------------------
//group 편집 기능
//1. group 이름 바꾸기
interface GroupChangeName{
    @POST("v3/group/name")
    fun groupChangeName(@Body groupData: GroupNameRequest):Call<GroupNameResponse>
}

//2. Group 삭제
interface GroupDelete {
    @DELETE("v3/group")
    fun deleteGroup(@Query("groupId") groupId: Int): Call<GroupDeleteResponse>
}
//----------------------------

//새로운 그룹 만들기
interface NewGroup {
    @POST("v3/group")
    fun newGroup(@Body request: NewGroupRequest
    ): Call<NewGroupResponses> // YourResponseModel은 서버 응답에 따라 실제 응답 모델로 변경되어야 합니다

}

//새로운 그룹 만들고 나서 member추가하기
interface GroupAddMemberlist {
    @POST("/v3/group/groupmember/members")
    fun addMembersToGroup(@Body request: AddMembersRequest): Call<List<addMembersResponse.addMembersResponseItem>>
}


//default group memberlist 들고오기
interface Deafult_friendGroup_memberlist {
    @GET("v3/group/groupmember")
    fun getdefaultGroupList(@Query("groupId") groupId: Int): Call<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>
}

//location/custom
interface member_custom_location {
    @POST("v3/location/custom")
    fun customLocationChange(@Body request: locationCustomMemberRequest) : Call <locationCustomMemberResponse>
}
