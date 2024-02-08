package com.example.where42android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.where42android.Base_url_api_Retrofit.Member
import com.example.where42android.Base_url_api_Retrofit.MemberAPI
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.WebView.CustomWebViewClient
import com.example.where42android.dialog.AgreeDialog
import com.example.where42android.main.MainPageActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.io.IOException
import kotlin.properties.Delegates
import com.example.where42android.Base_url_api_Retrofit.ApiObject.service
import com.example.where42android.Base_url_api_Retrofit.reissueAPI
import kotlinx.coroutines.async


class UserSettings private constructor() {
    var token: String = ""
    var intraId: Int = -1
    var agreement: Boolean = false
    var defaultGroup : Int = -1
    var refreshToken : String = ""

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
    private lateinit var customWebViewClient: CustomWebViewClient

//    val intent = Intent(this, MainPageActivity::class.java)
    fun saveTokenToSharedPreferences(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
//        editor.putString("AuthToken", token)
     editor.putString("AuthToken", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIiwiaW50cmFJZCI6MTQxNDQ3LCJpbnRyYU5hbWUiOiJqYWV5b2p1biIsInJvbGVzIjoiQ2FkZXQiLCJpYXQiOjE3MDUzOTE1MTUsImlzcyI6IndoZXJlNDIiLCJleHAiOjE3MDUzOTUxMTV9.J5akdcuH2X0l94cYbAX95petu9fYYK8HWXVWQ9T-O-k")
        editor.apply()
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

    fun getRefreshTokenFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("RefreshToken", null)
    }

        private fun saveaccesTokenToSharedPreferences(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("AuthToken", token)

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //webView 초기화
        webView = findViewById(R.id.webView)
        val loginButton = findViewById<ImageButton>(R.id.loginbutton)
        webView.settings.javaScriptEnabled = true // JavaScript 활성화 여부 설정

//        val cookieManager = CookieManager.getInstance()
//        cookieManager.removeAllCookies(null)
//
//        token 값 일부러 바꾸기 -> 나중에 삭제해야됨
//        saveTokenToSharedPreferences(this, "a")

        val token = getTokenFromSharedPreferences(this@MainActivity) ?: "notoken"
        val intraId = getIntraidFromSharedPreferences(this@MainActivity)
        val agreement = getAgreementFromSharedPreferences((this@MainActivity))
        val refreshtoken = getRefreshTokenFromSharedPreferences(this@MainActivity) ?: "norefresh"

        Log.e("refre", "memory token : ${token}")
        Log.e("refre", "memory retoken : ${refreshtoken}")
        val userSettings = UserSettings.getInstance()
        Log.e("refre", " token : ${userSettings.token}")



        if (userSettings.token == "" || userSettings.intraId == -1)
        {
            userSettings.token = token
            if (intraId != null)
            {
                userSettings.intraId = intraId.toInt()
            }
            userSettings.agreement = agreement.toBoolean()
            userSettings.refreshToken = refreshtoken
        }


        //agreement 동의를 하였고, token intraId, accestoken이 있으면 MainPageAcitivty로 넘기기 -> 이 코드는 나중에

//        saveTokenToSharedPreferences(this, "a")
//        userSettings.token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIiwiaW50cmFJZCI6MTQxNDQ3LCJpbnRyYU5hbWUiOiJqYWV5b2p1biIsInJvbGVzIjoiQ2FkZXQiLCJpYXQiOjE3MDUzOTE1MTUsImlzcyI6IndoZXJlNDIiLCJleHAiOjE3MDUzOTUxMTV9.J5akdcuH2X0l94cYbAX95petu9fYYK8HWXVWQ9T-O-k"
//

        val intraid : Int = intraId?.toInt() ?: -1
        val memberAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = memberAPI.getMember(intraid)
                withContext(Dispatchers.IO) {
                    when (response.code())
                    {
                        200 -> {
                            Log.d("MainPageAcitivty_kt", "no login memberAPI SUC")
                            val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                            intent.putExtra("TOKEN_KEY", token)
                            intent.putExtra("INTRAID_KEY", intraId)
                            intent.putExtra("AGREEMENT_KEY", agreement)
                            startActivity(intent)
                            finish()
                        }
                        else ->
                        {
                            Log.d("MainPageAcitivty_kt", "no login memberAPI response.code : ${response.code()}")
                            //여기는 아무것도 없음. 밑 버튼이 보이게 해야됨
                        }
                    }
                }
            }
            catch (e:IOException){
                Log.d("MainPageAcitivty_kt", "no login memberAPI Fail")
            }
        }

        //버튼을 눌렀을 때
        loginButton.setOnClickListener {
//            val token = getTokenFromSharedPreferences(this@MainActivity) ?: "notoken"
//            val intraId = getIntraidFromSharedPreferences(this@MainActivity)
//            val agreement = getAgreementFromSharedPreferences((this@MainActivity))
//            Log.d("MainActivity2", "Stored Token: ${token}")
//            Log.d("MainActivity2", "Stored intraId: ${intraId}")
//            Log.d("MainActivity2", "Stored agreement: ${agreement}")

            val intraid : Int = intraId?.toInt() ?: -1
            val memberAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)
            CoroutineScope(Dispatchers.IO).launch{
                try {
                        val response = memberAPI.getMember(intraid)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                when (response.code())
                                {
                                    200 -> {

                                        Log.d("token_check", "here1")
                                        Log.d("SUC", "SUC ${response.code()}")
                                        val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                                        intent.putExtra("TOKEN_KEY", token)
                                        intent.putExtra("INTRAID_KEY", intraId)
                                        intent.putExtra("AGREEMENT_KEY", agreement)
                                        startActivity(intent)
                                        finish()
                                    }
                                    201 -> {
                                        //여기가 리다이렉트
                                        Log.d("token_check", "here2")
                                        val headers = response.headers()
                                        val originalString = headers["redirectUrl"]
                                        val modifiedString =
                                            originalString?.replace("{", "")?.replace("}", "")
                                        Log.d("SUC", "modifiedString : ${modifiedString}")
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
                                        Log.d("token_check", "here3")
                                        //이 자리는 다시 로그인 해주세요를 띄워야함.
                                    }
                                }
                            }
                            else {
                                when (response.code())
                                {
                                    401 -> {
                                        try {
                                            Log.d("token_check", "here4")
                                            Log.e("Reissue_SUC", "refreshtoken : ${refreshtoken}")
                                            val reissueapi = RetrofitConnection.getInstance(refreshtoken).create(reissueAPI::class.java)
                                            val reissueResponse = reissueapi.reissueToken()
                                            if (reissueResponse.isSuccessful)
                                            {
                                                when (reissueResponse.code())
                                                {
                                                    200 -> {
                                                        Log.d("token_check", "here5")
                                                        Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse}")
                                                        Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse.body()}")
                                                        Log.e("Reissue_SUC", "reissueResponse : ${reissueResponse.code()}")
                                                        val reissue = reissueResponse.body()?.refreshToken
                                                        Log.e("Reissue_SUC", "reissue : ${reissue}")
                                                        if (reissue != null)
                                                        {
                                                            userSettings.token = reissue
                                                            saveaccesTokenToSharedPreferences(this@MainActivity, reissue)
                                                            Log.d("token_check", "here6")
                                                            Log.d("SUC", "SUC ${response.code()}")
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
                                                        Log.d("token_check", "here6")
                                                        Log.e("Reissue_SUC", "reissueResponse fail : ${reissueResponse}")
                                                        Log.e("Reissue_SUC", "reissueResponse fail: ${reissueResponse.code()}")
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
                                                    if (modifiedString != null) {
                                                        Log.e("herehere", "here4")
                                                        webView.loadUrl(modifiedString)
                                                    }
                                                }
                                            }
                                        } catch (reissueException: Exception) {
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
