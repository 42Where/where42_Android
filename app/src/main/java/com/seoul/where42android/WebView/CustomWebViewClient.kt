package com.seoul.where42android.WebView

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
//import com.seoul.where42android.dataStore.DataStoreKeys
import com.seoul.where42android.Base_url_api_Retrofit.JoinAPI
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.R
//import com.seoul.where42android.dataStore.DataStoreManager
import com.seoul.where42android.main.MainActivity
import com.seoul.where42android.main.UserSettings
import com.seoul.where42android.main.MainPageActivity
import com.seoul.where42android.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException


// 키 정의
//object DataStoreKeys {
//    val ACCESS_TOKEN = stringPreferencesKey("accesstoken")
//    val INTRA_ID = intPreferencesKey("intraId")
//    val AGREEMENT = booleanPreferencesKey("agreement")
//}

class CustomWebViewClient(private val context: Context, private val activity: Activity) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Log.d("onPageStarted", "URL: $url")
    }

    /**
     * 쿠키 파싱
     */
    private fun parseCookies(cookieString: String): Map<String, String> {
        val cookieMap = mutableMapOf<String, String>()
        val cookiePairs = cookieString.split(";")
        for (pair in cookiePairs) {
            val keyValue = pair.trim().split("=")
            if (keyValue.size == 2) {
                cookieMap[keyValue[0].trim()] = keyValue[1].trim()
            }
        }
        return cookieMap
    }


    private suspend fun saveDataToDataStore(accessToken: String, intraId: Int, agreement: Boolean) {
        try {
//            DataStoreManager.saveData(context, DataStoreKeys.ACCESS_TOKEN, accessToken)
//            DataStoreManager.saveData(context, DataStoreKeys.INTRA_ID, intraId)
//            DataStoreManager.saveData(context, DataStoreKeys.AGREEMENT, agreement)

            TokenManager.setAccessToken(accessToken)
            TokenManager.setIntraId(intraId)
            TokenManager.setAgreement(agreement)
            Log.d("CustomWebView", "Token loaded: token=${TokenManager.getAccessToken()}, intraId=${TokenManager.getIntraId()}, argreement = ${TokenManager.getAgreement()}")
            // UserSettings 업데이트
            val userSettings = UserSettings.getInstance()
            userSettings.token = accessToken
            userSettings.intraId = intraId
            userSettings.agreement = agreement
        } catch (e: Exception) {
            Log.e("DataStoreError", "Failed to save data to DataStore", e)
        }
    }

    // DataStore에서 저장된 데이터를 가져오는 함수
    private suspend fun getSavedDataFromDataStore(): Map<String, Any?> {
        val accessToken = TokenManager.getAccessTokenFromDataStore()
        val intraId = TokenManager.getIntraId()
        val agreement = TokenManager.getAgreement()

        return mapOf(
            "accessToken" to accessToken,
            "intraId" to intraId,
            "agreement" to agreement
        )
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.e("onPageFinished", "━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.e("onPageFinished", "URL: $url")


//        if (url != null && (url.startsWith("https://dev.where42.kr/"))) {
//            val cookies = CookieManager.getInstance().getCookie("https://dev.where42.kr/")
        if (url != null && (url.startsWith("https://where42.kr/"))) {
            val cookies = CookieManager.getInstance().getCookie("https://where42.kr/")
            val cookiesMap = parseCookies(cookies)

            // 모든 쿠키 키-값 쌍 출력
            Log.d("CookiesMap", "All Cookies:")
            for ((key, value) in cookiesMap) {
                Log.d("CookiesMap", "Key: $key, Value: $value")
            }

//            // 데이터 저장
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val accessToken = cookiesMap["accessToken"] ?: ""
//                    val intraId = cookiesMap["intraId"]?.toIntOrNull() ?: -1
                    val intraId = url?.let { Uri.parse(it).getQueryParameter("intraId")?.toIntOrNull() ?: -1 } ?: -1
                    val agreement = url.let { Uri.parse(it).getQueryParameter("agreement")?.toBoolean() ?: false }
                    Log.e("MainActivty", "accessToken: $accessToken")
                    Log.e("MainActivty", "intraId: $intraId")
                    Log.e("MainActivty", "agreement: $agreement")
                    if (accessToken.isNotEmpty() && agreement == true) {
                        saveDataToDataStore(accessToken, intraId, agreement)
                    }
                    // 로그로 저장된 데이터 확인
                    val savedData = getSavedDataFromDataStore()
                    Log.d("DataStore", "Saved Data: $savedData")

                    withContext(Dispatchers.Main) {
                        if (!agreement) {
                            // 동의하지 않은 경우, 동의 다이얼로그 표시
                            showAgreeDialog(
                                accessToken = accessToken,
                                intraId = intraId, // `intraId`가 Int이므로 문자열로 변환
                                agreement = agreement,
                                context = context // `CustomWebViewClient`에 전달된 context
                            ){
                                if (it) {
                                    // 사용자가 동의한 경우
                                    CoroutineScope(Dispatchers.IO).launch {
                                        TokenManager.setAgreement(true)
//                                        DataStoreManager.saveData(context, DataStoreKeys.AGREEMENT, true)
                                        withContext(Dispatchers.Main) {
                                            showMainPageActivity()
                                        }
                                    }
                                } else {
                                    // 동의하지 않은 경우, 동의 필요 알림
                                    showAgreementRequiredDialog()
                                }
                            }
                        } else {
                            // 동의한 상태이면 바로 메인 페이지로 이동
                            showMainPageActivity()
                        }
                    }
                    
                } catch (e: Exception) {
                    Log.e("DataStoreError", "Failed to save token", e)
                }
            }
        }
    }

    private fun showAgreementRequiredDialog() {
        val noEditDefaultDialog = Dialog(context)
        noEditDefaultDialog.setContentView(R.layout.activity_editstatus_popup)

        val cancel = noEditDefaultDialog.findViewById<Button>(R.id.cancel)
        cancel.visibility = View.GONE

        val title = noEditDefaultDialog.findViewById<TextView>(R.id.title)
        title.text = "동의를 해야 서비스를 이용 가능합니다."

        noEditDefaultDialog.window?.setGravity(Gravity.CENTER)
        noEditDefaultDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val submit = noEditDefaultDialog.findViewById<Button>(R.id.submit)
        submit.setOnClickListener {
            noEditDefaultDialog.dismiss()
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish() // 현재 Activity 종료
        }
        noEditDefaultDialog.show()
    }

    private fun showAgreeDialog(accessToken: String, intraId: Int, agreement:Boolean,context: Context, callback: (Boolean) -> Unit) {
        val agreedialog = Dialog(context)
        agreedialog.setContentView(R.layout.activity_profile_agree)
        agreedialog.setCanceledOnTouchOutside(true)
        agreedialog.setCancelable(true)

        agreedialog.window?.setGravity(Gravity.CENTER)
        agreedialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnCancel = agreedialog.findViewById<TextView>(R.id.cancel)
        val btnSubmit = agreedialog.findViewById<Button>(R.id.submit)

        btnSubmit.setOnClickListener {
            // Retrofit API 호출
            val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(JoinAPI::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = retrofitAPI.join(intraId)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            saveDataToDataStore(accessToken, intraId, agreement)
                            callback(true)
                        } else {
                            // Join API 실패 처리
                            when (response.code()) {
                                401 -> {
                                    Log.e("join_api", "401 Error")
                                    callback(false)
                                }
                                else -> callback(false)
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e("join_api", "IOException: ${e.message}")
                    callback(false) // 네트워크 오류 콜백
                }
            }
            agreedialog.dismiss()
        }

        btnCancel.setOnClickListener {
            agreedialog.dismiss()
            callback(false) // 사용자가 취소 버튼 클릭 시 콜백
        }
        agreedialog.show()
    }


    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
    }

    //이 부분이 추적가능하게 하는 곳.
    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        Log.e("WebViewError", "Error: ${error?.description}")
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
    }
    override fun onReceivedSslError(
        view: WebView?, handler: SslErrorHandler,
        error: SslError?
    ) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url?.toString()
        Log.d("shouldOverrideUrl", "URL: $url")
        // 특정 URL로의 로딩을 막기 위한 조건을 설정합니다.
        if (url != null && url.startsWith("https://where42.kr/")) // -- 여기 수정
//        if (url != null && url.startsWith("https://dev.where42.kr/"))
        {
//            Log.d("WebView", "url : ${url}")
            // 해당 URL로의 로딩을 막습니다.
            return true
        }
        // 그 외의 경우에

        return super.shouldOverrideUrlLoading(view, request)
//        return false
    }

    private fun showMainPageActivity() {
        val intent = Intent(context, MainPageActivity::class.java)
        context.startActivity(intent)
        // MainActivity 종료하려면 finish() 호출
        (context as? Activity)?.finish()
    }
}