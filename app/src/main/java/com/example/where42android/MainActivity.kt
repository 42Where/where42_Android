package com.example.where42android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.VISIBLE
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.where42android.Base_url_api_Retrofit.Member
import com.example.where42android.Base_url_api_Retrofit.MemberAPI
import com.example.where42android.Base_url_api_Retrofit.NewGroup
import com.example.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.example.where42android.Base_url_api_Retrofit.OAuthResponse
import com.example.where42android.Base_url_api_Retrofit.OAuthService
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.WebView.CustomWebViewClient
import com.example.where42android.main.MainPageActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        webView = findViewById(R.id.webView)

        val loginButton = findViewById<ImageButton>(R.id.loginbutton)
//        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true // JavaScript 활성화 여부 설정

        loginButton.setOnClickListener {

            //이 부분 코드는 interface에 응답이
            ////    suspend fun getMember(@Query("intraId") intraId: Int): Response<Member> 이거 일 때
//            val memberAPI = MemberAPI.create()
//
//            // 예를 들어, intraId가 123인 사용자 정보를 가져오는 API 호출을 하려면
//            val intraId = 6
//
//            GlobalScope.launch {
//                try {
//                    val response = memberAPI.getMember(intraId)
//                    if (response.isSuccessful) {
//                        Log.e("MemberAPI", "Error:")
//                        Log.e("MemberAPI", "Error:2 ${response}")
//                        Log.e("MemberAPI", "${response.headers()}")
//                        // 응답이 성공적으로 도착했을 때 처리할 작업을 수행합니다.
//                        val member = response.body()
//                        if (member != null) {
//                            Log.e("MemberAPI", "Error:")
//                            Log.e("MemberAPI", "Error:1 ${member}")
//                            // Member 객체를 성공적으로 가져왔습니다.
//                            // 여기에서 UI 업데이트 등을 수행할 수 있습니다.
//                        }
//                        else
//                        {
//                            runOnUiThread {
//                                webView.visibility = VISIBLE
//                                val originalString = response.message()
//                                val modifiedString = originalString.replace("{", "").replace("}", "")
//                                val customWebViewClient = CustomWebViewClient()
//                                webView.webViewClient = customWebViewClient
//                                webView.loadUrl(modifiedString)
//                                val currentUrl = customWebViewClient.getCurrentUrl()
//
//                                Log.e("currentURL", "${currentUrl}")
////                                webView.loadDataWithBaseURL(modifiedString, null, "text/html", "utf-8", null)
//                            }
//                            Log.e("c", "c")
//                        }
//                    } else {
//                        // 응답이 성공적이지 않은 경우 처리할 작업을 수행합니다.
//                        Log.e("c", "c1")
//                        Log.e("MemberAPI", "Request failed with code: ${response.code()}")
//                    }
//                } catch (e: Exception) {
//                    // 오류가 발생한 경우 처리합니다.
//                    Log.e("MemberAPI", "Error: here")
//                    Log.e("MemberAPI", "Error: ${e.message}")
//
//                }
//            }
//

            //이거는 interface의 응답이
            // fun getMember(@Query("intraId") intraId: Int): Call<Member>
//            val retrofit = RetrofitConnection.getInstance().create(MemberAPI::class.java)
            val intraid : Int = 6

//            val memberAPI = MemberAPI.create()
            val memberAPI = RetrofitConnection.getInstance().create(MemberAPI::class.java)

            val call = memberAPI.getMember(intraid)


//            val call = service.getMember(6)
            call.enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>,
                                        response: Response<Member>) {
                    Log.e("plz", " response ${response}")
                    val res = response.body()
                    Log.e("plz", "res ${response}")

                    if (!response.isSuccessful) {
                        // API 요청은 성공했으나 응답 코드가 200이 아닌 경우 HTML을 로드
                        Log.e("MemberAPI", "Error:")
                        Log.e("MemberAPI", "Error:2 ${response}")
                        Log.e("MemberAPI", "${response.headers()}")
                        runOnUiThread {
                                webView.visibility = VISIBLE
                                val originalString = response.message()
                                val modifiedString = originalString.replace("{", "").replace("}", "")
                                val customWebViewClient = CustomWebViewClient()
                                webView.webViewClient = customWebViewClient
                                webView.loadUrl(modifiedString)
                                val currentUrl = customWebViewClient.getCurrentUrl()

                                Log.e("currentURL", "${currentUrl}")
//                                webView.loadDataWithBaseURL(modifiedString, null, "text/html", "utf-8", null)
                            }
                    } else {
                        // 원하는 처리를 수행하고자 한다면 여기에 추가적인 로직을 작성할 수 있습니다.
                        // 예: JSON 응답을 처리하거나 필요한 다른 작업 수행
                        Log.e("MemberAPI", "Error:")
                        Log.e("MemberAPI", "Error:2 ${response}")
                        Log.e("MemberAPI", "${response.headers()}")
                        runOnUiThread {
                            webView.visibility = VISIBLE
                            val originalString = response.message()
                            val modifiedString = originalString.replace("{", "").replace("}", "")
                            val customWebViewClient = CustomWebViewClient()
                            webView.webViewClient = customWebViewClient
                            webView.loadUrl(modifiedString)
                            val currentUrl = customWebViewClient.getCurrentUrl()

                            Log.e("currentURL", "${currentUrl}")
//                                webView.loadDataWithBaseURL(modifiedString, null, "text/html", "utf-8", null)
                        }

                    }
//                    if (response.isSuccessful) {
//                        // Handle successful response if needed
//                        Log.e("awd", "awd")
//                    } else {
//                        Log.e("awd", "awd1")
//                        val contentType = response.headers()["Content-Type"]
//                        if (contentType != null && contentType.contains("text/html")) {
//                            // HTML 응답을 WebView에 로드하여 리다이렉트
//                            val responseBody = response.errorBody()?.string()
//                            responseBody?.let {
//                                webView.visibility = VISIBLE
//                                webView.loadDataWithBaseURL(null, it, "text/html", "utf-8", null)
//                            }
//                        } else {
//                            // Handle other error cases
//                            Log.e("ErrorBody", "Error Body: ${response.errorBody()?.string()}")
//                        }
//                    }
                }

                override fun onFailure(call: Call<Member>, t: Throwable) {
                    // Handle failure cases
                    Log.e("awd", "awd2")


                }
            })



//            // WebView에서 URL 변경 감지 및 처리 (선택 사항)
//            webView.webViewClient = object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                    if (url != null && url.startsWith("https://your_redirect_url")) {
//                        // 특정 URL에 도달했을 때 처리
//                        // 예시: 'https://your_redirect_url'에 도달하면 MainActivity를 종료하거나 다른 작업 수행
//                        finish()
//                        return true
//                    }
//                    return super.shouldOverrideUrlLoading(view, url)
//                }
//            }

//            webView.loadUrl(targetUrl)
//            val retrofit = RetrofitConnection.getInstance()
//            val service = retrofit.create(MemberAPI::class.java)
//
//            CoroutineScope(Dispatchers.Main).launch {
//                try {
//                    val response = service.getMember(6)
//                    Log.e("response" , "${response}")
//                    if (response.isSuccessful) {
//                        val contentType = response.headers()["Content-Type"]
//                        if (contentType != null && contentType.contains("text/html")) {
//                            val responseBody = response.body()?.toString()
//                            Log.e("responseBody", "c${responseBody}heck")
//                            responseBody?.let {
//                                // Retrofit으로부터 받은 HTML 응답(body)을 WebView에 로드
//                                webView.loadDataWithBaseURL(null, it, "text/html", "utf-8", null)
//                                webView.loadUrl(targetUrl)
//                            }
//                        }
//                    } else {
//                        val errorBody = response.errorBody()?.string()
//                        Log.e("Error Body", errorBody ?: "No error body")
//                    }
//                } catch (e: Exception) {
//                    // Handle exception
//                    Log.e("Exception", "Error during API request: ${e.message}")
//                    e.printStackTrace()
//                    webView.visibility =  VISIBLE
//                    webView.loadUrl(targetUrl)
//                }
//            }
        }


//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            val intent = Intent(this, MainPageActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 3000)
    }
    private fun createHtmlPage(url: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Error Page</title>
            </head>
            <body>
                <h1>API Request Failed</h1>
                <p>The request to the API failed. The URL used in the request was:</p>
                <p><a href="$url">$url</a></p>
            </body>
            </html>
        """
    }


}
