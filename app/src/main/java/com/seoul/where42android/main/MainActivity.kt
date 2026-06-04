package com.seoul.where42android.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.seoul.where42android.Base_url_api_Retrofit.MemberAPI
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.WebView.CustomWebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import com.seoul.where42android.Base_url_api_Retrofit.ReissueAPI
import com.seoul.where42android.Base_url_api_Retrofit.intraIdRequest
import com.seoul.where42android.R
import com.seoul.where42android.utils.TokenManager

class UserSettings private constructor() {
    var token: String = ""
    var intraId: Int = -1
    var agreement: Boolean = false
    var defaultGroup : Int = -1
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

// DataStore 키 정의


class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    private var accesstoken: String? = null
    private var intraId: Int? = null
    private var agreement: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //다크 모드 제한 코드
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        // TokenManager 초기화 (앱의 Application 클래스에서 호출되었을 가능성 있음)
        TokenManager.initialize(applicationContext)

        val loginButton = findViewById<ImageButton>(R.id.loginbutton)
        //help Button
        val helpButton = findViewById<ImageButton>(R.id.help_button)
        helpButton.setOnClickListener{
            val intent = Intent(this@MainActivity, MainHelpPage::class.java)
            startActivity(intent)
        }
        //webView 초기화
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true // JavaScript 활성화 여부 설정
        webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // HTTPS 트래픽 허용

        //쿠키값 지우기
//         val cookieManager = CookieManager.getInstance()
//         cookieManager.removeAllCookies(null)

        // DataStore에서 데이터 읽기 예제
        CoroutineScope(Dispatchers.IO).launch {
//            TokenManager.setAccessToken(("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIiwiaW50cmFJZCI6MTQxNDQ3LCJpbnRyYU5hbWUiOiJqYWV5b2p1biIsInR5cGUiOiJhY2Nlc3NUb2tlbiIsInJvbGUiOiJDYWRldCIsImlhdCI6MTczNTc5NTMxMSwiaXNzIjoid2hlcmU0MiIsImV4cCI6MTczNTc5ODkxMX0.cwycJ7xzQ7oDll6jxMCsWFbsqqXJKZQjsRo6D4q4WvE"))
//            TokenManager.clearAllData()
            TokenManager.printAllData()

            accesstoken = TokenManager.getAccessToken()
            intraId = TokenManager.getIntraId()
            agreement = TokenManager.getAgreement() ?: false
            Log.d("CustomWebView", "Token loaded: token=$accesstoken, intraId=$intraId, argreement = ${agreement}")
            withContext(Dispatchers.Main) {
                Log.d("MainActivity", "DataStore loaded: token=$accesstoken, intraId=$intraId, agreement=$agreement")

                // 동의 및 세션 검증 후 MainPage로 이동
                if (agreement == true && accesstoken != "notoken") {
                    val memberAPI = RetrofitConnection.getInstance(accesstoken!!).create(MemberAPI::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = memberAPI.getMember()
                            withContext(Dispatchers.Main) {
                                when (response.code()) {
                                    200 -> {

//                                        DataStoreManager.saveData(this@MainActivity, DataStoreKeys.ACCESS_TOKEN, accesstoken.toString())
                                        Log.d("ReissueAPI", "New accessToken received: $accesstoken, member=${response.body()}")
                                        // MainPage로 이동
                                        val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                                        intent.putExtra("TOKEN_KEY", accesstoken)
                                        intent.putExtra("INTRAID_KEY", intraId)
                                        intent.putExtra("AGREEMENT_KEY", agreement)
                                        startActivity(intent)
                                        finish()
                                    }
                                    401 -> {
                                        //1. accesstoken 만료 -> reissue를 통해서 다시 accesstoken 요청 -> 하고 다시 DataStoreManager update
                                        //1.1accesstoken이 만료되어서 이제 다시 reissue를 요청했는데도 만료이면 그냥 로그인 버튼 누르게 해야됨.
                                        val reissueAPI = RetrofitConnection.getNoAuthInstance().create(ReissueAPI::class.java)

                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                val intraIdRequest = intraIdRequest(intraId ?: -1)
                                                val response = reissueAPI.reissueToken(intraIdRequest)

                                                withContext(Dispatchers.Main) {
                                                    when (response.code())
                                                    {
                                                        200 -> { // refreshtoken이 아직 살아있음
                                                            val newAccessToken = response.body()?.accessToken // `ReissueResponse`에서 필요한 데이터 가져오기
                                                            Log.d("ReissueAPI", "New accessToken received: $newAccessToken")
                                                            // DataStore에 새로운 accessToken 저장
//                                                            DataStoreManager.saveData(this@MainActivity, DataStoreKeys.ACCESS_TOKEN, newAccessToken.toString())
                                                            TokenManager.setAccessToken(newAccessToken.toString())
                                                            // UserSettings 업데이트
                                                            val userSettings = UserSettings.getInstance()
                                                            userSettings.token = newAccessToken.toString()

                                                            // MainPage로 이동
                                                            val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                                                            intent.putExtra("TOKEN_KEY", newAccessToken.toString())
                                                            intent.putExtra("INTRAID_KEY", intraId)
                                                            intent.putExtra("AGREEMENT_KEY", agreement)
                                                            startActivity(intent)
                                                            finish()
                                                        }
                                                        401 -> { //refreshtoken이 만료됨 그래서 다시 로그인 but 하는 거는 없음 밑 로그인 버튼 눌러서 진행하게 해야됨

                                                        }
                                                    }
                                                }
                                            } catch (e: IOException) {
                                                Log.e("ReissueAPI", "Failed to call reissueAPI", e)
                                                withContext(Dispatchers.Main) {
                                                    // 네트워크 오류 또는 기타 예외 상황 처리 -> 다이얼로그 에러라고 띄워주면 될 듯 앱을 다시 시작하라고
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        Log.d("MainActivity", "Unexpected response code: ${response.code()}")
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            Log.e("MainActivity", "Failed to connect to MemberAPI", e)
                        }
                    }
                }
            }
        }

        // 로그인 버튼 클릭 시
        //버튼을 눌렀을 때
        loginButton.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val memberAPI = RetrofitConnection.getInstance(accesstoken ?: "notoken").create(MemberAPI::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = memberAPI.getMember()
                withContext(Dispatchers.Main) {
                    when (response.code())
                    {
                        200 -> {
//                                    DataStoreManager.saveData(this@MainActivity, DataStoreKeys.ACCESS_TOKEN, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIiwiaW50cmFJZCI6MTQxNDQ3LCJpbnRyYU5hbWUiOiJqYWV5b2p1biIsInR5cGUiOiJhY2Nlc3NUb2tlbiIsInJvbGUiOiJDYWRldCIsImlhdCI6MTczNTc5NTMxMSwiaXNzIjoid2hlcmU0MiIsImV4cCI6MTczNTc5ODkxMX0.cwycJ7xzQ7oDll6jxMCsWFbsqqXJKZQjsRo6D4q4WvE")
                            Log.d("token_check", "here1")
                            val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                            intent.putExtra("TOKEN_KEY", accesstoken)
                            intent.putExtra("INTRAID_KEY", intraId)
                            intent.putExtra("AGREEMENT_KEY", agreement)
                            startActivity(intent)
                            finish()
                        }
                        401 -> {
                            Log.d("MainActivty", "401로 옴")
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
                                    Log.d("MainActivty", "third");
                                    webView.loadUrl(modifiedString)
                                }
                            }
                        }
                    }
                }
            }
            catch (e:IOException){
                Log.e("MainActivity", "Failed to connect to MemberAPI", e)
            }
        }
    }
}
