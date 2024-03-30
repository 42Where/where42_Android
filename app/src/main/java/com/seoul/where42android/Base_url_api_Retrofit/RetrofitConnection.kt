package com.seoul.where42android.Base_url_api_Retrofit


import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.converter.scalars.ScalarsConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Response
import java.lang.reflect.Type


class RetrofitConnection {

    companion object {
        // API 서버의 주소가 BASE_URL이 됩니다.
        private const val BASE_URL = "http://13.209.149.15:8080/"
//        private const val BASE_URL = "http://test.where42.kr/"
        private var INSTANCE: Retrofit? = null

        fun getInstance(token: String): Retrofit {
            val tokenInterceptor = addToken(token)

//            if (INSTANCE == null)
//            {  // null인 경우에만 생성

                val interceptor = createInterceptor()
                var loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                val gson = GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
                    .create()

//                val nullOnEmptyConverterFactory = NullOnEmptyConverterFactory()

                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor) // 생성한 Interceptor 추가
                    .addInterceptor(tokenInterceptor)
                    .addInterceptor(loggingInterceptor)

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)  // API 베이스 URL 설정
                    .client(okHttpClient.build()) // OkHttp 클라이언트를 Retrofit에 설정
                    .addConverterFactory(ScalarsConverterFactory.create()) // 칼라(Scalar) 형식의 응답을 변환하기 위한 스칼라 컨버터를 추가합니다. 스칼라는 문자열이나 기본 타입과 같이 단일 값으로 이루어진 응답을 처리하는 데 사용됩니다.
                    .addConverterFactory(NullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // 스칼라(Scalar) 형식의 응답을 변환하기 위한 스칼라 컨버터를 추가합니다. 스칼라는 문자열이나 기본 타입과 같이 단일 값으로 이루어진 응답을 처리하는 데 사용됩니다.
                    .build()
//            }
            return INSTANCE!!
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
//                Log.e("createInterceptor_request", "request : ${request}, request : ${request.body}")

                //response 출력
                //->  Interceptor is invoked for URL: Response{protocol=h2, code=200, message=, url=https://auth.42.fr/auth/realms/students-42/protocol/openid-connect/auth?client_id=intra&redirect_uri=https%3A%2F%2Fprofile.intra.42.fr%2Fusers%2Fauth%2Fkeycloak_student%2Fcallback&response_type=code&state=1251f8f333239c9642c220b6df4e6b8e6900155d80d1bb64}
                val response = chain.proceed(request)
//                Log.e("createInterceptor_response", "response : ${response}, response.body() : ${response.body}")

//                Log.d("woonshin", "re")

                //requestURL JSON 처럼 만들어주기
                val requestURL =  "{" + request.url + "}"

                //contentType : text/html;charset=utf-8
//                val contentType = response.body?.contentType()

                //body에는 html 파일 들어있음.
//                val bodyString = response.body?.string() ?: ""
                val url = response.request.url.toString()
//                Log.e("redirectURL", " redirectURL ${url}")
//                try {
////                    val jsonObject = JSONObject()
////                    val url2 = "{" + url + "}"
////                    jsonObject.put("url", "{" + url + "}")
//                    // 원래의 response를 다시 만들어줍니다.
//                    val newResponse = response.newBuilder()
//                        .code(200)
//                        .addHeader("X-Naver-Client-Secret", "fyfwt9PCUN")
//                        .body(requestURL.toString().toResponseBody())
//                        .message(requestURL)
//                        .build()
//
//                    // newResponse로 반환합니다.
//                    return@Interceptor newResponse
//                } catch (e: JSONException) {
//                    // JSON 파싱 중 에러 발생 시, 예외 처리
//                    return@Interceptor response
//                }
//                Log.d("Interceptor", "code ${response.code}}")
                var responseBodyString = response.body?.string() ?: ""
//                Log.d("Interceptor", "responseBodyString ${responseBodyString}")

                //이건 토큰 재발급
                if (url.startsWith("http://13.209.149.15:8080") && response.code == 401)
                {

//                    Log.d("Interceptor", "url : ${url}")
//                    Log.d("Interceptor", "url.startsWith(\"http://13.209.149.15:8080\") && response.code == 401")

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
//                        .body(requestURL.toResponseBody())
                        .body(jsonString.toResponseBody())
                        .build()
//                    return@Interceptor response.newBuilder()
//                        .code(200)
//                        .body(requestURL.toResponseBody())
//                        .build()
                }
                //여기는 token이 없음
                else if (url.startsWith("https://auth.42.fr"))
                {
//                    Log.d("Interceptor", "url.startsWith(\"https://auth.42.fr\")")

                    return@Interceptor response.newBuilder()
                        .code(201)
                        .addHeader("redirectUrl", requestURL)
                        .message("CustomInterceptor: There is no value starting with \"https:\"")
                        .build()
                }
                //이미 동의한 유저임
                else if (response.code == 400 || response.code == 404) {
//                    Log.d("Interceptor", "response.code == 400 || response.code == 404)")
//                    val customException = Gson().fromJson(responseBodyString, ErrorResponse::class.java)
//                    val customExceptionJson = Gson().toJson(customException)
//                    val mediaType = "application/json; charset=utf-8".toMediaType()
//                    val responseBody = customExceptionJson.toResponseBody(mediaType)

//                    val regex = Regex("errorCode=(\\d+), errorMessage=(.*?)}")
//                    val matchResult = regex.find(responseBodyString)
//
//                    val errorCode = matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
//                    val errorMessage = matchResult?.groupValues?.get(2) ?: ""

//                    println("errorCode: $errorCode, errorMessage: $errorMessage")
//                    Log.e("hi", "${errorCode}, ${errorMessage}")

//                    val responseBody = responseBodyString.toResponseBody(mediaType)
//                    val gson = Gson()
//                    val customException = gson.fromJson(responseBodyString, CustomException::class.java)


                    val responseString = "{" + responseBodyString


//                    val responseString = "{" + responseBodyString

//                    val input = "CustomException(errorCode=1700, errorMessage=이미 동의한 유저입니다)"
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
                else {
//                    Log.d("Interceptor", "else")
//                    Log.e("plz", "plz ${response}")
//                    Log.e("plz", "plz body : ${response.body}")
//                    Log.e("plz", "plz body : ${responseBodyString}")

                    val doubleQuoteCount = responseBodyString.count { it == '"' }
                    val curlyBraceCount = responseBodyString.count { it == '{' || it == '}' }

//                    Log.d("ResponseStringAnalysis", "Double quote count: $doubleQuoteCount")
//                    Log.d("ResponseStringAnalysis", "Curly brace count: $curlyBraceCount")
                    if (responseBodyString == "[]")
                    {
//                        val modifiedResponseBodyString = "{" + responseBodyString + "}"
//                        val modifiedResponseBodyString = "{ \"data\": $responseBodyString }"
//                        responseBodyString = modifiedResponseBodyString
                        val modifiedResponseBodyString = "[]"
                        responseBodyString = modifiedResponseBodyString
//                        Log.d("plz" , " plz : here")
                    }
                    else if (doubleQuoteCount < 2 && curlyBraceCount < 2) {
//                        Log.w("ResponseStringAnalysis", "Response body does not seem to be in expected JSON format.")
                        val modifiedResponseBodyString = "{" + "\"logout\"" + ":" + "\"" + responseBodyString + "\"" + "}"
                        responseBodyString = modifiedResponseBodyString
//                        Log.d("plz" , "responseBodyString : ${responseBodyString} ")
                    }

                    return@Interceptor response.newBuilder()
                        .code(200) // 변경하고자 하는 새로운 HTTP 코드
                        .message("CustomInterceptor: SUC")
                        //.body(response.body) // 기존의 body를 그대로 사용
//                        .body(bodyString)
//                        .body(response.peekBody(Long.MAX_VALUE))
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

//        class NullOnEmptyConverterFactory : Converter.Factory() {
//            fun converterFactory() = this
//            override fun responseBodyConverter(
//                type: Type,
//                annotations: Array<out Annotation>,
//                retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
//                val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
//                override fun convert(value: ResponseBody): Any? {
//                    Log.e("herenull", "where?")
//                    Log.e("herenull", "value : ${value}?")
//                    Log.e("herenull", "type : ${type}?")
//                    Log.e("herenull", "value len : ${value.contentLength()}")
//                    return if (value.contentLength() != 0L) {
//                        try {
//                            Log.e("herenull", "where?1")
//                            Log.e("herenull", "value : ${nextResponseBodyConverter.convert(value)}")
//                            nextResponseBodyConverter.convert(value)
//                        } catch (e: Exception) {
//                            Log.e("herenull", "where?2")
//                            e.printStackTrace()
//                            getDefaultForType(type)
//                        }
//                    } else {
//                        Log.e("herenull", "where?3")
//                        getDefaultForType(type)
//                    }
//                }
//
//                private fun getDefaultForType(type: Type): Any? {
//                    // 여기에서 기본값을 설정하거나, 타입에 따라 다른 기본값을 설정할 수 있습니다.
//                    // 예를 들어, 클래스 타입인 경우에는 빈 인스턴스를 생성하여 반환할 수 있습니다.
//                    return when (type) {
//                        String::class.java -> ""
//                        Int::class.java -> 0
//                        Boolean::class.java -> false
//                        // 기타 타입에 대한 기본값을 설정할 수 있음
//                        else -> null
//                    }
//
//                }
//
////                override fun convert(value: ResponseBody) =
////                    if (value.contentLength() != 0L)
////                    {
////                        try{
////                            Log.e("nextResponseBodyConverter", "valuee : ${nextResponseBodyConverter}?")
////                            Log.e("herenull", "value a: ${nextResponseBodyConverter.convert(value)}")
////                            nextResponseBodyConverter.convert(value)
////                        }catch (e:Exception){
////                            e.printStackTrace()
////                            null
////                        }
////                    } else{
////                        Log.e("nextResponseBodyConverter", "valuee : ${nextResponseBodyConverter}?")
////                        Log.e("herenull", "value a: ${nextResponseBodyConverter.convert(value)}")
////                        null
////                    }
//
//            }
//        }

    }
}


class RetrofitConnection_data {

    companion object {
        private const val BASE_URL = "http://13.209.149.15:8080/"
        private var INSTANCE: Retrofit? = null

        fun getInstance(): Retrofit {
            if (INSTANCE == null) {
                val gson = GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
                    .create()

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return INSTANCE!!
        }
    }
}