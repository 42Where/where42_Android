package com.example.where42android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.CookieManager
import android.webkit.WebView
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
import kotlinx.coroutines.async


class UserSettings private constructor() {
    var token: String = ""
    var intraId: Int = -1
    var agreement: Boolean = false
    var defaultGroup : Int = -1

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //동호 행님 논리로 가보자

        //1. 메모리에 저장된 토큰 검사
        //- 토큰 검사를 한 후 만약 토큰이 유효하면 -> 그냥 MainPageActivity 넘기자
//        val token = getTokenFromSharedPreferences(this@MainActivity) ?: "notoken"
//        if (token != "notoken") {
//            //토큰이 있는 경우 걍 넘겨야함 -> MainPageAcitivty
//            Log.e("checking" , "${token}")
//            val intent = Intent(this, MainPageActivity::class.java)
//            val intraId = getIntraidFromSharedPreferences(this@MainActivity)
//            val agreement = getAgreementFromSharedPreferences((this@MainActivity))
//            intent.putExtra("TOKEN_KEY", token)
//            intent.putExtra("INTRAID_KEY", intraId)
//            intent.putExtra("AGREEMENT_KEY", agreement)
//            startActivity(intent)
//            finish()
//        }

        //webView 초기화
        webView = findViewById(R.id.webView)
        val loginButton = findViewById<ImageButton>(R.id.loginbutton)
//        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true // JavaScript 활성화 여부 설정

//        val cookieManager = CookieManager.getInstance()
//        cookieManager.removeAllCookies(null)

        //token 값 일부러 바꾸기 -> 나중에 삭제해야됨
//        saveTokenToSharedPreferences(this, "a")


        val token = getTokenFromSharedPreferences(this@MainActivity) ?: "notoken"
        val intraId = getIntraidFromSharedPreferences(this@MainActivity)
        val agreement = getAgreementFromSharedPreferences((this@MainActivity))


        val userSettings = UserSettings.getInstance()

        Log.e("refre", " token : ${userSettings.token}")
        if (userSettings.token == "" || userSettings.intraId == -1){
            userSettings.token = token
            if (intraId != null) {
                userSettings.intraId = intraId.toInt()
            }
        }



        loginButton.setOnClickListener {
            //안드로이드 AVD에 저장된 쿠키값 없애기

            val token = getTokenFromSharedPreferences(this@MainActivity) ?: "notoken"
            val intraId = getIntraidFromSharedPreferences(this@MainActivity)
            val agreement = getAgreementFromSharedPreferences((this@MainActivity))
            Log.d("MainActivity2", "Stored Token: ${token}")
            Log.d("MainActivity2", "Stored intraId: ${intraId}")
            Log.d("MainActivity2", "Stored agreement: ${agreement}")


            val intraid : Int = intraId?.toInt() ?: -1

//            val memberAPI = MemberAPI.create()
            val memberAPI = RetrofitConnection.getInstance(token).create(MemberAPI::class.java)
//            val call = memberAPI.getMember(intraid)
            CoroutineScope(Dispatchers.IO).launch{
                try {
                        val response = memberAPI.getMember(intraid)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                when (response.code()) {
                                    200 -> {
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
                                        Log.d("SUC", "header : ${response.headers()}")
//                                val originalString = response.message()
                                        val headers = response.headers()
                                        val originalString = headers["redirectUrl"]
                                        val modifiedString =
                                            originalString?.replace("{", "")?.replace("}", "")
                                        Log.d("SUC", "modifiedString : ${modifiedString}")
//                                val modifiedString = originalString.replace("{", "").replace("}", "")
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
                                }
                            }
                            else {
                                when (response.code()) {
                                    401 -> {
                                        //여기는 토큰이 만료되었다는 거임
                                        Log.d(
                                            "redirectURL",
                                            "401, ${response.headers()}, ${response}"
                                        )

                                        val headers = response.headers()
                                        val originalString = headers["redirectUrl"]
                                        val modifiedString =
                                            originalString?.replace("{", "")?.replace("}", "")
                                        Log.d("SUC", "modifiedString : ${modifiedString}")
//                                val modifiedString = originalString.replace("{", "").replace("}", "")
                                        val customWebViewClient =
                                            CustomWebViewClient(this@MainActivity, this@MainActivity)
                                        runOnUiThread {
                                            webView.visibility = VISIBLE
                                            webView.webViewClient = customWebViewClient
                                            if (modifiedString != null) {
                                                webView.loadUrl(modifiedString)
//                                        webView.visibility = GONE
//                                        Log.d("SUC", "SUC ${response.code()}")
//                                    val intent = Intent(this@MainActivity, MainPageActivity::class.java)
//                                        intent.putExtra("TOKEN_KEY", token)
//                                        intent.putExtra("INTRAID_KEY", intraId)
//                                        intent.putExtra("AGREEMENT_KEY", agreement)
//                                        startActivity(intent)
//                                        finish()
                                            }
                                        }
                                    }
                                    else -> {
                                        // 기본적으로 어떻게 처리할지 작성
                                    }
                                }
                            }
                        }
                    }
                    catch (e:IOException){
                        Log.e("fail" , " fail")
                    }
            }
//
//            call.enqueue(object : Callback<Member> {
//                override fun onResponse(call: Call<Member>,
//                                        response: Response<Member>) {
//                    val res = response.body()
//                    if (response.isSuccessful) {
//                        when (response.code())
//                        {
//                            200 -> {
//                                Log.d("SUC", "SUC ${response.code()}")
//                                val intent = Intent(this@MainActivity, MainPageActivity::class.java)
//                                intent.putExtra("TOKEN_KEY", token)
//                                intent.putExtra("INTRAID_KEY", intraId)
//                                intent.putExtra("AGREEMENT_KEY", agreement)
//                                startActivity(intent)
//                                finish()
//                            }
//                            201 ->
//                            {
//                                //여기가 리다이렉트
//                                Log.d("SUC", "header : ${response.headers()}")
////                                val originalString = response.message()
//                                val headers = response.headers()
//                                val originalString = headers["redirectUrl"]
//                                val modifiedString =
//                                    originalString?.replace("{", "")?.replace("}", "")
//                                Log.d("SUC", "modifiedString : ${modifiedString}")
////                                val modifiedString = originalString.replace("{", "").replace("}", "")
//                                val customWebViewClient = CustomWebViewClient(this@MainActivity)
//                                runOnUiThread {
//                                    webView.visibility = VISIBLE
//                                    webView.webViewClient = customWebViewClient
//                                    if (modifiedString != null) {
//                                        webView.loadUrl(modifiedString)
//                                    }
//                                }
//                            }
//                        }

                        // API 요청은 성공했으나 응답 코드가 200이 아닌 경우 HTML을 로드
                        // 원하는 처리를 수행하고자 한다면 여기에 추가적인 로직을 작성할 수 있습니다.
                        // 예: JSON 응답을 처리하거나 필요한 다른 작업 수행
//                        Log.e("MemberAPI", "Error:")
//                        Log.e("MemberAPI", "Error:2 ${response}")
//                        Log.e("MemberAPI", "${response.headers()}")
//                        val originalString = response.message()
//                        val modifiedString = originalString.replace("{", "").replace("}", "")
//                        val customWebViewClient = CustomWebViewClient(this@MainActivity)
//                        runOnUiThread {
//                            webView.visibility = View.VISIBLE
//                            webView.webViewClient = customWebViewClient
//                            webView.loadUrl(modifiedString)
//                        }
//                        val token = getTokenFromSharedPreferences(this@MainActivity)
//                        val intraId = getIntraidFromSharedPreferences(this@MainActivity)
//                        val agreement = getAgreementFromSharedPreferences((this@MainActivity))
//                        Log.d("MainActivity", "Stored Token: ch ${token}")
//                        Log.d("MainActivity", "Stored intraId:  ch${intraId}")
//                        Log.d("MainActivity", "Stored agreement:  ch ${agreement}")



//                        val sharedPreferences = this@MainActivity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
//                        Log.d("MainActivity", "Stored Token: ${token}")

//                            webView.webViewClient = customWebViewClient
//                            webView.loadUrl(modifiedString)
//                        }
//                            val currentUrl = customWebViewClient.getCurrentUrl()
//                            Log.e("curren", "a ${currentUrl}")
//                            Log.e("currentURL", "${currentUrl}")
//                                webView.loadDataWithBaseURL(modifiedString, null, "text/html", "utf-8", null)

//                    } else {
//                        when (response.code()) {
//                            401 -> {
//                                //여기는 토큰이 만료되었다는 거임
//                                Log.d("redirectURL", "401, ${response.headers()}, ${response}")
//
//                                val headers = response.headers()
//                                val originalString = headers["redirectUrl"]
//                                val modifiedString =
//                                    originalString?.replace("{", "")?.replace("}", "")
//                                Log.d("SUC", "modifiedString : ${modifiedString}")
////                                val modifiedString = originalString.replace("{", "").replace("}", "")
//                                val customWebViewClient = CustomWebViewClient(this@MainActivity)
//
//
//                                runOnUiThread {
//                                    webView.visibility = VISIBLE
//                                    webView.webViewClient = customWebViewClient
//                                    if (modifiedString != null) {
//                                        webView.loadUrl(modifiedString)
////                                        webView.visibility = GONE
////                                        Log.d("SUC", "SUC ${response.code()}")
////                                    val intent = Intent(this@MainActivity, MainPageActivity::class.java)
////                                        intent.putExtra("TOKEN_KEY", token)
////                                        intent.putExtra("INTRAID_KEY", intraId)
////                                        intent.putExtra("AGREEMENT_KEY", agreement)
////                                        startActivity(intent)
////                                        finish()
//                                    }
//                                }
//                            }
//                            else -> {
//                                // 기본적으로 어떻게 처리할지 작성
//                            }
//                        }
//                    }
////                    if (response.isSuccessful) {
////                        // Handle successful response if needed
////                        Log.e("awd", "awd")
////                    } else {
////                        Log.e("awd", "awd1")
////                        val contentType = response.headers()["Content-Type"]
////                        if (contentType != null && contentType.contains("text/html")) {
////                            // HTML 응답을 WebView에 로드하여 리다이렉트
////                            val responseBody = response.errorBody()?.string()
////                            responseBody?.let {
////                                webView.visibility = VISIBLE
////                                webView.loadDataWithBaseURL(null, it, "text/html", "utf-8", null)
////                            }
////                        } else {
////                            // Handle other error cases
////                            Log.e("ErrorBody", "Error Body: ${response.errorBody()?.string()}")
////                        }
////                    }
//                }
//
//                override fun onFailure(call: Call<Member>, t: Throwable) {
//                    // Handle failure cases
//                    Log.e("awd", "awd2")
//
//
//                }
//            })
//
//        }

//        val intent = Intent(this, MainPageActivity::class.java)
//        startActivity(intent)
//        finish()
//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            val intent = Intent(this, MainPageActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 3000)
//        }
//        private fun createHtmlPage(url: String): String {
//            return """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <title>Error Page</title>
//            </head>
//            <body>
//                <h1>API Request Failed</h1>
//                <p>The request to the API failed. The URL used in the request was:</p>
//                <p><a href="$url">$url</a></p>
//            </body>
//            </html>
//        """
        }

    }
}
