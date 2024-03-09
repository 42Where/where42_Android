package com.example.where42android.WebView

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
import com.example.where42android.MainActivity
import com.example.where42android.R
import com.example.where42android.UserSettings
import com.example.where42android.dialog.AgreeDialog
import com.example.where42android.main.MainPageActivity

class CustomWebViewClient(private val context: Context, private val activity: Activity) : WebViewClient() {



    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        Log.d("PageStatred", "url : ${url}")
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
    private fun parseRedirectUri(url: String): String? {
        // url에서 redirect_uri를 파싱하여 추출하는 로직을 작성합니다.
        // 여기서는 간단한 예시를 보여드리겠습니다. 실제로는 더 복잡한 로직이 필요할 수 있습니다.
        Log.e("onPageFinished", "parseRedirectUriurl : ${url}")
        val splitUrl = url.split("redirect_uri=")
        if (splitUrl.size > 1) {
            // redirect_uri= 다음에 나오는 값을 추출합니다.
            val redirectUriPart = splitUrl[1]
            // & 기호 이전의 부분을 가져와서 반환합니다.
            val redirectUri = redirectUriPart.substringBefore("&")
            // URL 디코딩을 수행하여 반환합니다.
            return Uri.decode(redirectUri)
        }
        return null
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.e("onPageFinished", "firsturl : ${url}")
        // 특정 조건을 만족하면 토큰을 가져와서 SharedPreferences에 저장
//                && shouldSaveToken(url)

        //여기 onPageFinished          com.example.where42android    이거 리다이렉트 해야됨 파싱->
        // redirect_uri를 파싱하여 추출합니다.

//        val checkredirect = redirectparseUri?.contains("http://13.209.149.15:8080/login/oauth2/code/42seoul")
//        val redirectparseUri = url?.let { parseRedirectUri(it) }
//        val checkredirect = redirectparseUri?.contains("http://test.where42.kr/oauth2/authorization/42seoul")

//        val cookies = CookieManager.getInstance().getCookie("http://13.209.149.15:8080/")
//        Log.e("HeaderInfo_Page", "http://13.209.149.15:8080/의 쿠키2: $cookies")
//        val cookiess = CookieManager.getInstance().getCookie("https://test.where42.kr/")
//        Log.e("HeaderInfo_Page", "https://test.where42.kr/의 쿠키2: $cookiess")

        // || checkredirect == true
        if (url != null && (url.startsWith("https://test.where42.kr:3000")))
        {
//            val cookies = CookieManager.getInstance().getCookie("http://13.209.149.15:8080/")
//            Log.e("HeaderInfo_Page", "http://13.209.149.15:8080/의 쿠키2: $cookies")

            val cookies = CookieManager.getInstance().getCookie("https://test.where42.kr/")
            Log.e("HeaderInfo_Page", "https://test.where42.kr/의 쿠키2: $cookies")
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
//            if (accessToken != null || refreshToken != null)
//            {
//                    saveTokenToSharedPreferences(accessToken, refreshToken, url)
//            }
            if (accessToken != null || refreshToken != null) {
                saveTokenToSharedPreferences(accessToken ?: "", refreshToken ?: "", url)
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
                        //동의를 하지 않았을 경우
                        val noEditDefaultDialog = Dialog(context)
                        noEditDefaultDialog.setContentView(R.layout.activity_editstatus_popup)


                        val cancel = noEditDefaultDialog.findViewById<Button>(R.id.cancel)
                        cancel.visibility = View.GONE

                        val title = noEditDefaultDialog.findViewById<TextView>(R.id.title)
                        title.text = "동의를 해야 서비스를 이용 가능합니다."
//                            "친구 그룹은 이름을 바꿀 수 없습니다."

                        noEditDefaultDialog.window?.setGravity(Gravity.CENTER)
                        noEditDefaultDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                        val submit = noEditDefaultDialog.findViewById<Button>(R.id.submit)
                        submit.setOnClickListener {
                            noEditDefaultDialog.dismiss()
                        }
                        noEditDefaultDialog.show()
                    }
                }
            }
            else
            {
                showMainPageActivity()
            }
        }
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
        // 특정 URL로의 로딩을 막기 위한 조건을 설정합니다.
        if (url != null && url.startsWith("https://test.where42.kr:3000")) {
            Log.d("WebView", "url : ${url}")
            // 해당 URL로의 로딩을 막습니다.
            return true
        }
        // 그 외의 경우에

        return super.shouldOverrideUrlLoading(view, request)
//        return false
    }

    private fun showMainPageActivity() {
        val intent = Intent(context, MainPageActivity::class.java)
        val intraId = getIntraidFromSharedPreferences(context)
        val agreement = getAgreementFromSharedPreferences(context)
        val token = getTokenFromSharedPreferences(context)
        val refreshtoken = getrefreshToSharedPreferences(context)

        Log.d("customWebView", "Stored Token: ${token}")
        Log.d("customWebView", "Stored intraId: ${intraId}")
        Log.d("customWebView", "Stored agreement: ${agreement}")
        Log.d("customWebView", "Stored refreshtoken: ${refreshtoken}")


//        Log.d("customWebView", "Stored Token: ${token}")
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

    fun getrefreshToSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("RefreshToken", null)
    }
}