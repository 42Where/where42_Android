package com.example.where42android.Base_url_api_Retrofit


import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
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
        private var INSTANCE: Retrofit? = null

        fun getInstance(): Retrofit {
            if (INSTANCE == null) {  // null인 경우에만 생성

                val interceptor = createInterceptor()

                var loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                val gson = GsonBuilder().setLenient().create()
                val nullOnEmptyConverterFactory = NullOnEmptyConverterFactory()

                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor) // 생성한 Interceptor 추가
                    .addInterceptor(loggingInterceptor)

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)  // API 베이스 URL 설정
                    .client(okHttpClient.build()) // OkHttp 클라이언트를 Retrofit에 설정
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(
                        GsonConverterFactory.create(
                            GsonBuilder().setLenient().create()
                        )
                    )
                    .build()
            }
            return INSTANCE!!
        }


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

        class NullOnEmptyConverterFactory : Converter.Factory() {
            fun converterFactory() = this
            override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object :
                Converter<ResponseBody, Any?> {
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


//        private var url: String? = null
//
//        private fun createCustomInterceptor(): Interceptor {
//            return object : Interceptor {
//                override fun intercept(chain: Interceptor.Chain): Response {
//                    val response = chain.proceed(chain.request())
//
//                    if (url != null) {
//                        return response.newBuilder()
//                            .code(200)
//                            .body(url!!.toResponseBody())
//                            .build()
//                    }
//
//                    return response.newBuilder()
//                        .code(502)
//                        .message("CustomInterceptor: There is no value starting with \"https:\"")
//                        .build()
//                }
//            }
//        }
//
//        private fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
//            val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
//                override fun log(message: String) {
//                    if (message.startsWith("\"https:")) {
//                        url = message
//                    }
//                }
//            })
//            return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
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