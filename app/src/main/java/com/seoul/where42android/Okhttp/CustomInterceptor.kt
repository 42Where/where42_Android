//package com.seoul.where42android.Okhttp
//
//import okhttp3.Interceptor
//import okhttp3.Response
//import okhttp3.ResponseBody.Companion.toResponseBody
//import okhttp3.logging.HttpLoggingInterceptor
//
//class CustomInterceptor : Interceptor {
//    private var url: String? = null
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val response = chain.proceed(chain.request())
//
//        if (url != null) {
//            return response.newBuilder()
//                .code(200)
//                .body(url!!.toResponseBody())
//                .build()
//        }
//
//        return response.newBuilder()
//            .code(502)
//            .message("CustomInterceptor: There is no value starting with \"https:\"")
//            .build()
//    }
//}
//
//fun httpLoggingInterceptor(): HttpLoggingInterceptor {
//    val interceptor = HttpLoggingInterceptor { message ->
//        if (message.startsWith("\"https:")) {
//            // Assuming `url` is a global variable accessible to the Interceptor
//            url = message
//        }
//    }
//    return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//}
