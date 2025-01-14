package com.seoul.where42android.utils

import android.content.Intent
import android.util.Log
import com.seoul.where42android.Base_url_api_Retrofit.ReissueAPI
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.intraIdRequest
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import android.content.Context
import com.seoul.where42android.main.MainActivity


object ApiUtils {

    /**
     * Perform API request with automatic token reissue on 401 error.
     */
    suspend fun <T> performApiRequest(
        context: Context,
        apiCall: suspend (String) -> Response<T>
    ): Response<T>? {
        return try {
            val currentToken = TokenManager.getAccessToken()
            val response = apiCall(currentToken)
            if (response.isSuccessful) {
                response
            } else if (response.code() == 401) {
                // 토큰 갱신 처리
                val newToken = reissueAccessToken()
                if (newToken != null) {
                    TokenManager.setAccessToken(newToken)
                    val retryResponse = apiCall(newToken) // 새 토큰으로 재시도
                    if (retryResponse.code() == 401) {
                        // 재시도 후에도 401이면 MainActivity로 이동
                        Log.d("ApiUtils", "Reissued token also failed. Navigating to login.")
                        navigateToLogin(context)
                    }
                    retryResponse
                } else {
                    // 토큰 재발급 실패 시 MainActivity로 이동
                    Log.d("ApiUtils", "Token reissue failed. Navigating to login.")
                    navigateToLogin(context)
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ApiUtils", "Error in API request", e)
            null
        }
    }

    private fun navigateToLogin(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }


//    suspend fun <T> performApiRequest(
//        apiCall: suspend (String) -> Response<T>
//    ): Response<T>? {
//        return try {
//            val currentToken = TokenManager.getAccessToken()
//            val response = apiCall(currentToken)
//            if (response.isSuccessful) {
//                response
//            } else if (response.code() == 401) {
//                // 토큰 갱신 처리
//                val newToken = reissueAccessToken()
//                if (newToken != null) {
//                    TokenManager.setAccessToken(newToken)
//                    apiCall(newToken) // 새 토큰으로 재시도
//                } else {
//                    null
//                }
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            Log.e("ApiUtils", "Error in API request", e)
//            null
//        }
//    }


    /**
     * Reissue a new access token.
     */
    suspend fun reissueAccessToken(): String? {
        // intraId 가져오기
        val intraId = TokenManager.getIntraId() ?: return null.also {
            Log.e("TokenManager", "IntraId is null or invalid. Reissue aborted.")
        }

        // Reissue API 인스턴스 생성
        val reissueAPI = RetrofitConnection.getNoAuthInstance().create(ReissueAPI::class.java)

        return try {
            // Reissue API 호출
            val response = reissueAPI.reissueToken(intraIdRequest(intraId))

            if (response.isSuccessful) {
                val newToken = response.body()?.accessToken

                if (newToken != null) {
                    // 새 토큰 저장
                    TokenManager.setAccessToken(newToken)
                    Log.d("TokenManager", "Access token successfully reissued: $newToken")
                    return newToken
                } else {
                    Log.e("TokenManager", "Reissue successful but response body is null.")
                }
            } else {
                Log.e("TokenManager", "Reissue failed with HTTP status code: ${response.code()}")
            }
            null
        } catch (e: IOException) {
            Log.e("TokenManager", "Network error during token reissue: ${e.message}", e)
            null
        } catch (e: HttpException) {
            Log.e("TokenManager", "HTTP error during token reissue: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e("TokenManager", "Unexpected error during token reissue: ${e.message}", e)
            null
        }
    }

//    private suspend fun reissueAccessToken(): String? {
//        val intraId = TokenManager.getIntraId() ?: -1
//        val reissueAPI = RetrofitConnection.getNoAuthInstance().create(ReissueAPI::class.java)
//
//        return try {
//            val response = reissueAPI.reissueToken(intraIdRequest(intraId))
//            if (response.isSuccessful) {
//                val newToken = response.body()?.accessToken
//                if (newToken != null) {
//                    TokenManager.setAccessToken(newToken) // 새 토큰을 TokenManager에 저장
//                    return newToken
//                }
//            }
//            null
//        } catch (e: IOException) {
//            Log.e("ApiUtils", "Failed to reissue token.", e)
//            null
//        } catch (e: HttpException) {
//            Log.e("ApiUtils", "HTTP error during token reissue.", e)
//            null
//        }
//    }
}
