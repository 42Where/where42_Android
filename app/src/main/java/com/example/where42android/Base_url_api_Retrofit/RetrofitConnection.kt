package com.example.where42android.Base_url_api_Retrofit


import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class RetrofitConnection {

    companion object {
        // API 서버의 주소가 BASE_URL이 됩니다.
        private const val BASE_URL = "http://13.209.149.15:8080/"
        private var INSTANCE: Retrofit? = null

        fun getInstance(): Retrofit {
            if (INSTANCE == null) {  // null인 경우에만 생성
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)  // API 베이스 URL 설정
                    .addConverterFactory(GsonConverterFactory.create())// 1)
                    .build()
            }
            return INSTANCE!!
        }
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