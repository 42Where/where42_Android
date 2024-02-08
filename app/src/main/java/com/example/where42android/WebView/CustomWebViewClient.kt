package com.example.where42android.WebView

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.where42android.MainActivity
import com.example.where42android.R
import com.example.where42android.UserSettings
import com.example.where42android.dialog.AgreeDialog
import com.example.where42android.main.MainPageActivity

class CustomWebViewClient(private val context: Context, private val activity: Activity) : WebViewClient() {



    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    private fun shouldSaveToken(url: String?): Boolean {
        // 특정 조건을 만족하면 토큰을 저장 예를 들어, URL에 "saveToken"이라는 문자열이 포함되어 있을 때
        Log.e("ag", "ag ${url?.contains("https") != true}")
        return url?.contains("https") != true
    }

    private fun retrieveTokenFromUrl(url: String?): String {
        // 특정 방식으로 URL에서 토큰을 추출
        // 여기서는 간단하게 URL의 일부를 토큰으로 사용하는 예시를 보여줍니다.
        return url?.substringAfter("token=") ?: ""
    }

    private fun saveTokenToSharedPreferences(accesstoken: String, refreshToken:String, url: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val uri = Uri.parse(url)
        val intraid = uri.getQueryParameter("intraId")
        val agreement = uri.getQueryParameter("agreement")

        Log.e("ParsedCookies", "saveTokenToSharedPreferences_intraid: $intraid")
        Log.e("ParsedCookies", "saveTokenToSharedPreferences_agreement: $agreement")

        editor.putString("intraId", intraid)
        editor.putString("agreement", agreement)
        editor.putString("AuthToken", accesstoken)
        editor.putString("RefreshToken", refreshToken)


        val userSettings = UserSettings.getInstance()
        userSettings.token = accesstoken
        if (intraid != null)
        {
            userSettings.intraId = intraid.toInt()
        }
        userSettings.agreement = agreement.toBoolean()
        userSettings.refreshToken = refreshToken
        editor.apply()
    }

    fun parseCookies(cookieString: String): Map<String, String> {
        val cookieMap = mutableMapOf<String, String>()
        val cookiePairs = cookieString.split(";")

        for (pair in cookiePairs) {
            val keyValue = pair.trim().split("=")
            if (keyValue.size == 2) {
                val key = keyValue[0].trim()
                val value = keyValue[1].trim()
                cookieMap[key] = value
            }
        }

        return cookieMap
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.e("onPageFinished", "${url}")
        Log.e("onPageFinished", "here")
        // 특정 조건을 만족하면 토큰을 가져와서 SharedPreferences에 저장
//                && shouldSaveToken(url)
        if (url != null && url.startsWith("http://localhost:3000"))
        {
            val cookies = CookieManager.getInstance().getCookie("http://13.209.149.15:8080/")
            Log.e("HeaderInfo_Page", "http://13.209.149.15:8080/의 쿠키2: $cookies")
            activity.runOnUiThread {
                activity.findViewById<WebView>(R.id.webView).visibility = View.GONE
            }
            val cookiesMap = parseCookies(cookies)
            val jsessionId = cookiesMap["JSESSIONID"]
            val accessToken = cookiesMap["accessToken"]
            val refreshToken = cookiesMap["refreshToken"]
            Log.e("ParsedCookies", "JSESSIONID: $jsessionId")
            Log.e("ParsedCookies", "accessToken: $accessToken")
            Log.e("ParsedCookies", "refreshToken: $refreshToken")
//            val token = retrieveTokenFromUrl(url)
//            Log.e("onPageFinished", "${token}")
//            saveTokenToSharedPreferences(token)
            if (accessToken != null && refreshToken != null)
            {
                saveTokenToSharedPreferences(accessToken, refreshToken, url)
            }
            val token1 = getTokenFromSharedPreferences(context) ?: "notoken"
            Log.d("token_check", "Stored Token after_custom : ${token1}")
            //여기에 Dialog 넣어주면 될 듯
            val contexttoken = getTokenFromSharedPreferences(context) ?: "notoken"
            Log.d("token_check", "Stored Token  contexttoken after_custom : ${contexttoken}")
            val intraId = getIntraidFromSharedPreferences(context)
            val agreement = getAgreementFromSharedPreferences((context))

            val userSettings_agreement = UserSettings.getInstance().agreement

            if (userSettings_agreement == false)
            {
                val agreeDialog = AgreeDialog(context)
                agreeDialog.showAgreeDialog(contexttoken, intraId, agreement, context) { result ->
                    if (result) {
                        showMainPageActivity()
                    } else {

                    }
                }
            }
            else
            {
                showMainPageActivity()
            }

            // ------------------------- 전에 작성했더 코드
//            WebView.GONE
//            WebView.GONE
//            activity.runOnUiThread {
//                activity.findViewById<WebView>(R.id.webView).visibility = View.GONE
//            }


//            val token = retrieveTokenFromUrl(url)
//            Log.e("onPageFinished", "${token}")
//            saveTokenToSharedPreferences(token)
//
//            val token1 = getTokenFromSharedPreferences(context) ?: "notoken"
//            Log.d("token_check", "Stored Token after_custom : ${token1}")
//            //여기에 Dialog 넣어주면 될 듯
//            val contexttoken = getTokenFromSharedPreferences(context) ?: "notoken"
//            Log.d("token_check", "Stored Token  contexttoken after_custom : ${contexttoken}")
//            val intraId = getIntraidFromSharedPreferences(context)
//            val agreement = getAgreementFromSharedPreferences((context))

//            val agreeDialog = AgreeDialog(context)
//            agreeDialog.showAgreeDialog(contexttoken, intraId, agreement, context) { result ->
//                if (result)
//                {
//                    showMainPageActivity()
//                }
//                else
//                {
//
//                }
//            }
            // ------------------------- 전에 작성했더 코드

        }
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
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
        return super.shouldOverrideUrlLoading(view, request)
    }

    private fun showMainPageActivity() {
        val intent = Intent(context, MainPageActivity::class.java)
        val intraId = getIntraidFromSharedPreferences(context)
        val agreement = getAgreementFromSharedPreferences(context)
        val token = getTokenFromSharedPreferences(context)
        Log.d("MainActivity", "Stored Token: ${token}")
        intent.putExtra("TOKEN_KEY", token)
        intent.putExtra("INTRAID_KEY", intraId)
        intent.putExtra("AGREEMENT_KEY", agreement)
        context.startActivity(intent)
        // MainActivity 종료하려면 finish() 호출
        (context as? Activity)?.finish()
    }

    fun getTokenFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("AuthToken", null)
    }

    fun getIntraidFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("intraId", null)
    }

    fun getAgreementFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("agreement", null)
    }
}