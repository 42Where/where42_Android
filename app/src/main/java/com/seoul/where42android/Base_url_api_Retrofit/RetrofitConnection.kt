package com.seoul.where42android.Base_url_api_Retrofit


import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.converter.scalars.ScalarsConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Converter
import java.lang.reflect.Type


class RetrofitConnection {

    companion object {
        // API 서버의 주소가 BASE_URL이 됩니다.
//        private const val BASE_URL = "http://13.209.149.15:8080/" -- 난중 수정
        private const val BASE_URL = "https://api-test.where42.kr/" // -- 난중 수정
        private var INSTANCE: Retrofit? = null

        fun getInstance(token: String): Retrofit {
            val tokenInterceptor = addToken(token)
            val cookieInterceptor = addCookieInterceptor(token)

                val interceptor = createInterceptor()
                var loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                val gson = GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
                    .create()

                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor) // 생성한 Interceptor 추가
                    .addInterceptor(tokenInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(cookieInterceptor)
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)  // API 베이스 URL 설정
                    .client(okHttpClient) // OkHttp 클라이언트를 Retrofit에 설정
                    .addConverterFactory(ScalarsConverterFactory.create()) // 칼라(Scalar) 형식의 응답을 변환하기 위한 스칼라 컨버터를 추가합니다. 스칼라는 문자열이나 기본 타입과 같이 단일 값으로 이루어진 응답을 처리하는 데 사용됩니다.
                    .addConverterFactory(NullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // 스칼라(Scalar) 형식의 응답을 변환하기 위한 스칼라 컨버터를 추가합니다. 스칼라는 문자열이나 기본 타입과 같이 단일 값으로 이루어진 응답을 처리하는 데 사용됩니다.
                    .build()
//            }
            return INSTANCE!!
        }

        //reissue를 위한 Retorift 인스턴스
        fun getNoAuthInstance(): Retrofit {
            val interceptor = createInterceptor()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val gson = GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
                .create()

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor) // 기본 인터셉터만 추가
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL) // API 베이스 URL 설정
                .client(okHttpClient) // OkHttp 클라이언트를 Retrofit에 설정
                .addConverterFactory(ScalarsConverterFactory.create()) // 문자열 또는 기본 타입의 응답 처리
                .addConverterFactory(NullOnEmptyConverterFactory)
                .addConverterFactory(GsonConverterFactory.create(gson)) // JSON 변환기
                .build()
        }

        // 쿠키를 추가하는 Interceptor
        private fun addCookieInterceptor(refreshToken: String): Interceptor {
            return Interceptor { chain ->
                val originalRequest = chain.request()

                // 쿠키를 추가하여 새로운 요청을 만듭니다.
                val newRequest = originalRequest.newBuilder()
                    .header("Cookie", "refreshToken=$refreshToken") // 쿠키 헤더에 refreshToken 추가
                    .build()

                chain.proceed(newRequest)
            }
        }

        private fun addToken(token: String) : Interceptor {
            return Interceptor { chain ->
                val originalRequest = chain.request()
                val modifiedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token") // 여기서 "Bearer"는 토큰 타입에 따라 다를 수 있습니다.
                    .build()
                chain.proceed(modifiedRequest)
            }
        }


        private fun createInterceptor(): Interceptor {
            return Interceptor { chain ->
                //request.url -> Interceptor is invoked for URL: http://13.209.149.15:8080/v3/member?intraId=6
                val request = chain.request()
                //response 출력
                //->  Interceptor is invoked for URL: Response{protocol=h2, code=200, message=, url=https://auth.42.fr/auth/realms/students-42/protocol/openid-connect/auth?client_id=intra&redirect_uri=https%3A%2F%2Fprofile.intra.42.fr%2Fusers%2Fauth%2Fkeycloak_student%2Fcallback&response_type=code&state=1251f8f333239c9642c220b6df4e6b8e6900155d80d1bb64}
                val response = chain.proceed(request)
                //requestURL JSON 처럼 만들어주기
                val requestURL =  "{" + request.url + "}"
                //body에는 html 파일 들어있음.
                val url = response.request.url.toString()
                var responseBodyString = response.body?.string() ?: ""
                Log.d("MainActivty", "third8");

                val accessTokenPattern = "accessToken" // accessToken의 패턴에 따라 수정

//                if (url.startsWith("http://13.209.149.15:8080") && response.code == 401) -- 난중 수정
                //이건 토큰 재발급
                if (url.startsWith("https://api-test.where42.kr") && response.code == 401)
                {
                    Log.d("MainActivty", "401 토큰 재발급");

                    val regex = Regex("errorCode=(\\d+), errorMessage=(.*?)\\)")
                    val matchResult = regex.find(responseBodyString)

                    val errorCode = matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    val errorMessage = matchResult?.groupValues?.get(2) ?: ""

                    val jsonObject = mapOf(
                        "CustomException" to mapOf(
                            "errorCode" to errorCode,
                            "errorMessage" to errorMessage
                        )
                    )

                    val jsonString = Gson().toJson(jsonObject)

                    return@Interceptor response.newBuilder()
                        .code(401)
                        .addHeader("redirectUrl", requestURL)
                        .body(jsonString.toResponseBody())
                        .build()
                }
                //이미 동의한 유저임
                else if (response.code == 400 || response.code == 404) {
                    val responseString = "{" + responseBodyString
                    val regex = Regex("CustomException\\(errorCode=(\\d+), errorMessage=(.*)\\)")
                    val matchResult = regex.find(responseBodyString)

                        val errorCode = matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
                        val errorMessage = matchResult?.groupValues?.get(2)

                        val jsonObject = mapOf(
                            "CustomException" to mapOf(
                                "errorCode" to errorCode,
                                "errorMessage" to errorMessage
                            )
                        )
                        val jsonString = Gson().toJson(jsonObject)
                        println(jsonString)
                    return@Interceptor response.newBuilder()
                        .code(200)
                        .body(jsonString.toResponseBody())
                        .build()

                }
                //reissue
                else if (responseBodyString.contains(accessTokenPattern))
                {
                    Log.d("MainActivty", "third10");
                    return@Interceptor response.newBuilder()
                        .code(200) // 변경하고자 하는 새로운 HTTP 코드
                        .message(responseBodyString)
                        //.body(response.body) // 기존의 body를 그대로 사용
//                        .body(bodyString)
//                        .body(response.peekBody(Long.MAX_VALUE))
                        .body(responseBodyString.toResponseBody(response.body?.contentType()))
                        .build()
                }
                else
                {
                    Log.d("MainActivty", "third11");
                    val doubleQuoteCount = responseBodyString.count { it == '"' }
                    val curlyBraceCount = responseBodyString.count { it == '{' || it == '}' }
                    if (responseBodyString == "[]")
                    {
                        Log.d("Interceptor", "5")
                        val modifiedResponseBodyString = "[]"
                        responseBodyString = modifiedResponseBodyString
                    }
                    else if (doubleQuoteCount < 2 && curlyBraceCount < 2) {
                        val modifiedResponseBodyString = "{" + "\"logout\"" + ":" + "\"" + responseBodyString + "\"" + "}"
                        responseBodyString = modifiedResponseBodyString
                    }
                    return@Interceptor response.newBuilder()
                        .code(200)
                        .message("CustomInterceptor: SUC")
                        .body(responseBodyString.toResponseBody(response.body?.contentType()))
                        .build()
                }
            }
        }

        //NullOnEmptyConverterFactory 클래스는 Retrofit에서 사용되는 Converter.Factory를 상속하며, 주로 빈 응답(Empty Response)에 대한 처리를 담당합니다.
        // 이 클래스는 응답이 비어있는 경우에 null을 반환하도록 동작합니다.
        //여러 Retrofit Converter.Factory 중 하나로서, 주로 서버로부터의 응답을 변환하기 위한 컨버터를 제공합니다
        private val NullOnEmptyConverterFactory = object : Converter.Factory() {
            fun converterFactory() = this
            override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
                val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
                override fun convert(value: ResponseBody) =
                    if (value.contentLength() == 0L)
                    {
//                        Log.e("Null_here", " NULLhere")
                        null
                    }
                    else
                    {
//                        Log.e("Null_here", " NULLhere2")
                        nextResponseBodyConverter.convert(value)
                    }
            }
        }

    }
}
