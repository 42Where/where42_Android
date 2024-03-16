package com.seoul.where42android.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.VISIBLE
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.seoul.where42android.Base_url_api_Retrofit.MemberAPI
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.WebView.CustomWebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import com.seoul.where42android.Base_url_api_Retrofit.reissueAPI
import com.seoul.where42android.R

class UserSettings private constructor() {
    var token: String = ""
    var intraId: Int = -1
    var agreement: Boolean = false
    var defaultGroup : Int = -1
    var refreshToken : String = ""
    var inCluster : Boolean = false

    companion object {
        @Volatile
        private var instance: UserSettings? = null

        fun getInstance(): UserSettings =
            instance ?: synchronized(this) {
                instance ?: UserSettings().also { instance = it }
            }
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

//    val intent = Intent(this, MainPageActivity::class.java)
//    fun saveTokenToSharedPreferences(context: Context, token: String) {
//        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
////        editor.putString("AuthToken", token)
//     editor.putString("AuthToken", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIiwiaW50cmFJZCI6MTQxNDQ3LCJpbnRyYU5hbWUiOiJqYWV5b2p1biIsInJvbGVzIjoiQ2FkZXQiLCJpYXQiOjE3MDUzOTE1MTUsImlzcyI6IndoZXJlNDIiLCJleHAiOjE3MDUzOTUxMTV9.J5akdcuH2X0l94cYbAX95petu9fYYK8HWXVWQ9T-O-k")
//        editor.apply()
//    }

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

    fun getRefreshTokenFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("RefreshToken", null)
    }

    fun saveaccesTokenToSharedPreferences(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("AuthToken", token)
    }

//    fun clearSharedPreferences(context: Context) {
//        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.clear()
//        editor.apply() // 또는 editor.commit()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //help Button
        val helpButton = findViewById<ImageButton>(R.id.help_button)

        helpButton.setOnClickListener{
            val helpDialog = Dialog(this@MainActivity)
            helpDialog.setContentView(R.layout.activity_help_popup)

            helpDialog.window?.setGravity(Gravity.CENTER)
            helpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val submit = helpDialog.findViewById<Button>(R.id.submit)
            submit.setOnClickListener {
                helpDialog.dismiss()
            }

            val noiton = helpDialog.findViewById<TextView>(R.id.noiton)
            noiton.setOnClickListener {
                // 웹 페이지 주소
                val url = "https://befitting-balaur-414.notion.site/eff5de2f978a4164b52b68ad2ca2e05a"

                // 웹 페이지로 이동하는 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                helpDialog.dismiss()
                // Intent 실행
                startActivity(intent)
            }

            helpDialog.show()
        }


        //webView 초기화
        webView = findViewById(R.id.webView)
        val loginButton = findViewById<ImageButton>(R.id.loginbutton)
        webView.settings.javaScriptEnabled = true // JavaScript 활성화 여부 설정

//        val cookieManager = CookieManager.getInstance()
//        cookieManager.removeAllCookies(null)

//        token 값 일부러 바꾸기 -> 나중에 삭제해야됨
//        saveTokenToSharedPreferences(this, "a")

//        clearSharedPreferences(this@MainActivity)

        val token = getTokenFromSharedPreferences(this@MainActivity) ?: "notoken"
        val intraId = getIntraidFromSharedPreferences(this@MainActivity)?.toInt() ?: -1
        val agreement = getAgreementFromSharedPreferences((this@MainActivity))
        val refreshtoken = getRefreshTokenFromSharedPreferences(this@MainActivity) ?: "norefresh"

//        Log.e("refre", "memory token : ${token}")
//        Log.e("refre", "memory retoken : ${refreshtoken}")
//        Log.e("refre", "memory agreement : ${agreement}")
//        Log.e("refre", "memory intraId : ${intraId}")

        //진짜 제일 처음 킬 때 memory, usersetting 전부 null임 -> 로그인 페이지
        if (intraId == -1)
        {

        }
        else {
            val userSettings = UserSettings.getInstance()
//            Log.e("refre", " token : ${userSettings.token}")

            if (userSettings.token == "" || userSettings.intraId == -1) {
                userSettings.token = token
                userSettings.intraId = intraId
                userSettings.agreement = agreement.toBoolean()
                userSettings.refreshToken = refreshtoken
            }

            //agreement 동의를 하였고, token intraId, accestoken이 있으면 MainPageAcitivty로 넘기기 -> 이 코드는 나중에

//        saveTokenToSharedPreferences(this, "a")
//        userSettings.token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIiwiaW50cmFJZCI6MTQxNDQ3LCJpbnRyYU5hbWUiOiJqYWV5b2p1biIsInJvbGVzIjoiQ2FkZXQiLCJpYXQiOjE3MDUzOTE1MTUsImlzcyI6IndoZXJlNDIiLCJleHAiOjE3MDUzOTUxMTV9.J5akdcuH2X0l94cYbAX95petu9fYYK8HWXVWQ9T-O-k"
//

            val intraid: Int = intraId
            val memberAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = memberAPI.getMember(intraid)
                    withContext(Dispatchers.IO) {
                        when (response.code()) {
                            200 -> {
//                                Log.d("MainPageAcitivty_kt", "no login memberAPI SUC")
                                val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                                intent.putExtra("TOKEN_KEY", token)
                                intent.putExtra("INTRAID_KEY", intraId)
                                intent.putExtra("AGREEMENT_KEY", agreement)
                                startActivity(intent)
                                finish()
                            }

                            else -> {
//                                Log.d(
//                                    "MainPageAcitivty_kt",
//                                    "no login memberAPI response.code : ${response.code()}"
//                                )
                                //여기는 아무것도 없음. 밑 버튼이 보이게 해야됨
                            }
                        }
                    }
                } catch (e: IOException) {
//                    Log.d("MainPageAcitivty_kt", "no login memberAPI Fail")
                }
            }
        }

        //버튼을 눌렀을 때
        loginButton.setOnClickListener {
            val userSettings = UserSettings.getInstance()
            val intraid : Int = intraId
            val memberAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)
            CoroutineScope(Dispatchers.IO).launch{
                try {
                        val response = memberAPI.getMember(intraid)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                when (response.code())
                                {
                                    200 -> {

//                                        Log.d("token_check", "here1")
//                                        Log.d("SUC", "SUC ${response.code()}")
                                        val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                                        intent.putExtra("TOKEN_KEY", token)
                                        intent.putExtra("INTRAID_KEY", intraId)
                                        intent.putExtra("AGREEMENT_KEY", agreement)
                                        startActivity(intent)
                                        finish()
                                    }
                                    201 -> {
                                        //여기가 리다이렉트
//                                        Log.d("token_check", "here2")
                                        val headers = response.headers()
                                        val originalString = headers["redirectUrl"]
                                        val modifiedString =
                                            originalString?.replace("{", "")?.replace("}", "")
//                                        Log.d("SUC", "modifiedString : ${modifiedString}")
                                        val customWebViewClient =
                                            CustomWebViewClient(this@MainActivity, this@MainActivity)
                                        runOnUiThread {
                                            webView.visibility = VISIBLE
                                            webView.webViewClient = customWebViewClient
                                            if (modifiedString != null) {
                                                webView.loadUrl(modifiedString)
                                            }
                                        }
                                    }
                                    else ->
                                    {
//                                        Log.d("token_check", "here3")
                                        //이 자리는 다시 로그인 해주세요를 띄워야함.
                                    }
                                }
                            }
                            else {
                                when (response.code())
                                {
                                    401 -> {
                                        try {
//                                            Log.d("token_check", "here4")
//                                            Log.e("Reissue_SUC", "refreshtoken : ${refreshtoken}")
                                            val reissueapi = RetrofitConnection.getInstance(refreshtoken).create(reissueAPI::class.java)
                                            val reissueResponse = reissueapi.reissueToken()
//                                            Log.d("token_check", "reissueResponse : ${reissueResponse.headers()}")
//                                            Log.d("token_check", "reissueResponse : ${reissueResponse.body()}")
//                                            Log.d("token_check", "reissueResponse : ${reissueResponse.code()}")
                                            if (reissueResponse.isSuccessful)
                                            {
                                                when (reissueResponse.code())
                                                {
                                                    200 -> {
//                                                        Log.d("token_check", "here5")
//                                                        Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse}")
//                                                        Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse.body()}")
//                                                        Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse.code()}")
                                                        val reissue = reissueResponse.body()?.refreshToken
//                                                        Log.e("Reissue_SUC", "reissue : ${reissue}")
                                                        if (reissue != null)
                                                        {
                                                            userSettings.token = reissue
                                                            saveaccesTokenToSharedPreferences(this@MainActivity, reissue)
//                                                            Log.d("token_check", "here6")
//                                                            Log.d("SUC", "SUC ${response.code()}")
                                                            val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                                                            intent.putExtra("TOKEN_KEY", userSettings.token)
                                                            intent.putExtra("INTRAID_KEY", intraId)
                                                            intent.putExtra("AGREEMENT_KEY", agreement)
                                                            startActivity(intent)
                                                            finish()
                                                        }
                                                        else
                                                        {
                                                            userSettings.token = "notoken"
                                                            saveaccesTokenToSharedPreferences(this@MainActivity, "notoken")
                                                        }
                                                    }
                                                    // 추가적인 상태 코드에 대한 처리 필요
                                                    else ->
                                                    {
//                                                        Log.d("token_check", "here6")
//                                                        Log.e("Reissue_SUC", "reissueResponse fail : ${reissueResponse}")
//                                                        Log.e("Reissue_SUC", "reissueResponse fail: ${reissueResponse.code()}")
                                                        // 기본적으로 어떻게 처리할지 작성
                                                    }
                                                }
                                            } else {
                                                Log.d("token_check", "here7")
                                                //401이며 Reissue API 호출 실패 시 처리 리다이렉트로 보내야함.
                                                Log.e("Reissue_SUC", "reissueResponse fail1: ${reissueResponse}")
                                                Log.e("Reissue_SUC", "reissueResponse fail1: ${reissueResponse.body()}")
                                                Log.e("Reissue_SUC", "reissueResponse fail1: ${reissueResponse.headers()}")
                                                Log.e("Reissue_SUC", "reissueResponse fail1: ${reissueResponse.code()}")
                                                val headers = response.headers()
                                                val originalString = headers["redirectUrl"]
                                                val modifiedString =
                                                    originalString?.replace("{", "")?.replace("}", "")
                                                Log.d("SUC", "modifiedString : ${modifiedString}")
                                                Log.e("herehere", "here1")
                                                val customWebViewClient =
                                                    CustomWebViewClient(this@MainActivity, this@MainActivity)
                                                Log.e("herehere", "here2")
                                                runOnUiThread {
                                                    Log.e("herehere", "here3")
                                                    webView.visibility = VISIBLE
                                                    webView.webViewClient = customWebViewClient
//                                                    webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                                                    if (modifiedString != null) {
                                                        Log.e("herehere", "here4")

//                                                        webView.loadUrl(modifiedString)
                                                        webView.loadUrl("https://test.where42.kr/")
//                                                        webView.loadUrl("https://auth.42.fr/auth/realms/students-42/protocol/openid-connect/auth?client_id=intra&redirect_uri=https%3A%2F%2Fprofile.intra.42.fr%2Fusers%2Fauth%2Fkeycloak_student%2Fcallback&response_type=code&state=41a172bbce265c02e6c0f91cab615f90dae945f51b0308c5")
//                                                        webView.loadUrl("http://test.where42.kr/oauth2/authorization/42seoul")
                                                    }
                                                }
                                            }
                                        } catch (reissueException: Exception)
                                        {
                                        }
                                    }
                                    else -> {
                                    }
                                }
                            }
                        }
                    }
                    catch (e:IOException){
                        Log.e("fail" , " fail")
                    }
            }
        }

    }
}
