package com.example.where42android.WebView

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.View
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

//    private fun splitToken(token: String): Map<String, String> {
//        val tokenSplit = token.split("&").associate {
//            val (key, value) = it.split("=")
//            key to value
//        }
//        return tokenSplit
//    }

    private fun saveTokenToSharedPreferences(token: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
//        eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIiwiaW50cmFJZCI6MTQxNDQ3LCJpbnRyYU5hbWUiOiJqYWV5b2p1biIsInJvbGVzIjoiQ2FkZXQiLCJpYXQiOjE3MDUwMzEzNjUsImlzcyI6IndoZXJlNDIiLCJleHAiOjE3MDUwMzE0MjV9.-A30zCF_vE8oQHYk0fD5f63Xeckyw7K_X4VIEFK4jk8&intraId=141447&agreement=true
        Log.e("AuthToken", "here ${token}")
//        val tokenMap = splitToken(token)
        val tokenSplit = token.split("&")
        Log.e("AuthToken", "here ${tokenSplit}")
//        for ((key, value) in tokenMap) {
//            Log.e("AuthToken", "$key: $value")
//        }

        val intraid = tokenSplit[1].split("=")
        val agreement = tokenSplit[2].split("=")
        Log.e("AuthToken", "here ${intraid}")
        Log.e("AuthToken", "here ${agreement}")

        editor.putString("intraId", intraid[1])
        editor.putString("agreement", agreement[1])
        editor.putString("AuthToken", tokenSplit[0])

        val userSettings = UserSettings.getInstance()
        userSettings.token = tokenSplit[0]
        userSettings.intraId = intraid[1].toInt()
        userSettings.agreement = agreement[1].toBoolean()

//        editor.putString("AuthToken", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIiwiaW50cmFJZCI6MTQxNDQ3LCJpbnRyYU5hbWUiOiJqYWV5b2p1biIsInJvbGVzIjoiQ2FkZXQiLCJpYXQiOjE3MDUzOTE1MTUsImlzcyI6IndoZXJlNDIiLCJleHAiOjE3MDUzOTUxMTV9.J5akdcuH2X0l94cYbAX95petu9fYYK8HWXVWQ9T-O-k")
        editor.apply()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.e("onPageFinished", "${url}")
        Log.e("onPageFinished", "here")
        // 특정 조건을 만족하면 토큰을 가져와서 SharedPreferences에 저장
        if (url != null && shouldSaveToken(url)) {
//            WebView.GONE
            WebView.GONE
            activity.runOnUiThread {
                activity.findViewById<WebView>(R.id.webView).visibility = View.GONE
            }
            Log.e("onPageFinished", "here2")
            val token = retrieveTokenFromUrl(url)
            Log.e("onPageFinished", "${token}")
            saveTokenToSharedPreferences(token)

            val token1 = getTokenFromSharedPreferences(context) ?: "notoken"
            Log.d("token_check", "Stored Token after_custom : ${token1}")

            //여기에 Dialog 넣어주면 될 듯
            val contexttoken = getTokenFromSharedPreferences(context) ?: "notoken"
            Log.d("token_check", "Stored Token  contexttoken after_custom : ${contexttoken}")
            val intraId = getIntraidFromSharedPreferences(context)
            val agreement = getAgreementFromSharedPreferences((context))

            val agreeDialog = AgreeDialog(context)
            agreeDialog.showAgreeDialog(contexttoken, intraId, agreement, context) { result ->
                if (result)
                {
                    showMainPageActivity()
                }
                else
                {

                }
            }
//            showMainPageActivity()
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