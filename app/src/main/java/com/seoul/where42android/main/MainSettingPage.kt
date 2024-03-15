package com.seoul.where42android.main

import SharedViewModel_Profile
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.webkit.CookieManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import com.seoul.where42android.Base_url_api_Retrofit.locationCustomMemberRequest
import com.seoul.where42android.Base_url_api_Retrofit.logoutAPI
import com.seoul.where42android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSettingPage : AppCompatActivity() {

    fun clearSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply() // 또는 editor.commit()
    }

    val userSettings = UserSettings.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_page)

        val receivedIntent = intent
        val profileIntraId: Int = receivedIntent.getIntExtra("PROFILE_DATA", -1)
        var intraId_class : Int = receivedIntent.getIntExtra("INTRA_ID", -1)

        var intraId = userSettings.intraId
//        Log.e("MainSettingPage", "intraId : ${intraId}")

        val token: String? = receivedIntent.getStringExtra("TOKEN")
//        Log.e("MainSettingPage", "token : ${token}")

        //logout
        val logoutButton : Button = this.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            val logout_dialog = Dialog(this)
            logout_dialog.setContentView(R.layout.activity_editstatus_popup)

            logout_dialog.setCanceledOnTouchOutside(true)
            logout_dialog.setCancelable(true)
            logout_dialog.window?.setGravity(Gravity.CENTER)
            logout_dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title = logout_dialog.findViewById<TextView>(R.id.title)
            title.text = "정말 로그아웃 하시겠습니까?"


            val cancel = logout_dialog.findViewById<Button>(R.id.cancel)
            val submit = logout_dialog.findViewById<Button>(R.id.submit)
            submit.setOnClickListener {
//                Log.e("logout", "submit")
                //logout api 부르기
                logout_dialog.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val usersetting = UserSettings.getInstance()
//                        Log.e("logout", "api 요청1")
                        val logoutapi = RetrofitConnection.getInstance(usersetting.token).create(
                            logoutAPI::class.java
                        )
//                        Log.e("logout", "api 요청2")
                        val logoutResponse = logoutapi.logout()
//                        Log.e("logout", "api 요청2")
//                        Log.e("logout", "api : ${logoutResponse.code()}")
//                        Log.e("logout", "api : ${logoutResponse.body()}")
                        if (logoutResponse.isSuccessful)
                        {
                            when (logoutResponse.code()) {
                                200 -> {
//                                    Log.e("logout", "logout Response : ${logoutResponse}")
//                                    Log.e(
//                                        "logout",
//                                        "logout Response : ${logoutResponse.body()}"
//                                    )
//                                    Log.e(
//                                        "logout",
//                                        "logout Response : ${logoutResponse.code()}"
//                                    )
                                    //쿠키 지우기

                                    val cookieManager = CookieManager.getInstance()
                                    cookieManager.removeAllCookies(null)

                                    //메모리 정보 지우기
                                    clearSharedPreferences(this@MainSettingPage)


                                    val intent = Intent(this@MainSettingPage, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()


                                }
                                // 추가적인 상태 코드에 대한 처리 필요
                                else -> {
//                                    Toast.makeText(this@MainSettingPage, "api 응답 코드 실패1 logout 안됨", Toast.LENGTH_SHORT).show()
//                                    Log.e("logout", "logout Response fail : ${logoutResponse}")
//                                    Log.e(
//                                        "logout",
//                                        "logout Response fail: ${logoutResponse.code()}"
//                                    )
                                    // 기본적으로 어떻게 처리할지 작성
                                }
                            }
                        } else {
//                            Log.e("logout", "api 응답 코드 실패2 logout 안됨")
//                            Toast.makeText(this@MainSettingPage, "api 응답 코드 실패2 logout 안됨", Toast.LENGTH_SHORT).show()
                        }
                    } catch (reissueException: Exception) {
//                        Log.e("logout", "throw logout 안됨, ${reissueException}")

//                        Toast.makeText(this@MainSettingPage, "throw logout 안됨", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            cancel.setOnClickListener {
                logout_dialog.dismiss()
            }
            logout_dialog.show()
        }

        //코멘트 설정 코드
        val commentButton: Button = this.findViewById(R.id.comment_button)

        commentButton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.activity_edittext_popup)

            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title = dialog.findViewById<TextView>(R.id.title)
            title.text = "코멘트 설정"

            val editText = dialog.findViewById<EditText>(R.id.input)
            val typeface = ResourcesCompat.getFont(this, R.font.gmarketsans_bold)
            editText.typeface = typeface
            editText.hint = "코멘트를 바꿔주세요. "
            val btnCancel = dialog.findViewById<Button>(R.id.cancel)
            val btnSubmit = dialog.findViewById<Button>(R.id.submit)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSubmit.setOnClickListener {
                val comment :String = editText.text.toString()
                //intraid 바꿔주기
                //JSON 만들기
//                Log.e("MainSettingPage", "intraId : ${intraId}")
                val updateRequest = UpdateCommentRequest(intraId, comment)

                // SharedViewModel_Profile 인스턴스 생성
                val sharedViewModel = ViewModelProvider(this).get(SharedViewModel_Profile::class.java)
//                sharedViewModel.updateComment(comment)
                // updateMemberComment 함수 호출
                if (token != null) {
                    sharedViewModel.updateMemberComment(updateRequest, token)
                }

//                val call = ApiObject.service.updateMemberComment(updateRequest)
//                call.enqueue(object : Callback<CommentChangeMember> {
//                    override fun onResponse(
//                        call: Call<CommentChangeMember>,
//                        response: Response<CommentChangeMember>
//                    ) {
//                        // 성공적으로 응답을 받았을 때 처리
//                        if (response.isSuccessful) {
//                            val result = response.body()
//                            // 처리할 작업 수행
//                        } else {
//                            // 응답은 받았지만 실패했을 때 처리
//                            val errorBody = response.errorBody()?.string()
//                            val errorMessage = response.message() // HTTP 에러 메시지
//                            val errorCode = response.code() // HTTP 에러 코드
//
//                            val detailedErrorMessage =
//                                "Failed to update comment. Error code: $errorCode, Message: $errorMessage, Error Body: $errorBody"
//                            Log.e("Update Error", detailedErrorMessage)
//                        }
//                    }
//
//                    override fun onFailure(call: Call<CommentChangeMember>, t: Throwable) {
//                        // 요청 자체가 실패했을 때 처리
//                        val errorMessage = "코멘트가 설정 되지 않아요! 안 되면 where42 team을 찾아주세요!."
//                        Toast.makeText(this@MainSettingPage, errorMessage, Toast.LENGTH_SHORT).show()
//                        Log.e("Network Error", "Retrofit Failure: ${t.message}")
//                    }
//                })
                dialog.dismiss()
//
//                val intent = Intent(this@MainSettingPage, MainPageActivity::class.java)
                finish() //인텐트 종료
//                startActivity(intent)

            }
            dialog.show()
        }

        //수동 자리 설정
        val manualDigitSetting : Button = this.findViewById(R.id.place_setting_button)
        manualDigitSetting.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.activity_editseat_popup_version2)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            //1층
            val onefloor = dialog.findViewById<Button>(R.id.btnFloor1)
            onefloor.setOnClickListener {

                val onefloordialog = Dialog(this)
                onefloordialog.setContentView(R.layout.activity_editseat1floor_popup)
                onefloordialog.setCanceledOnTouchOutside(true)
                onefloordialog.setCancelable(true)
                onefloordialog.window?.setGravity(Gravity.CENTER)
                onefloordialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                onefloordialog.show()


                var openstudio = onefloordialog.findViewById<Button>(R.id.btnFloor1)
                openstudio.setOnClickListener {
                    changeSeatApi("1층 42LAB", intraId)
                    onefloordialog.dismiss()
                    dialog.dismiss()
                }

                var openlounge = onefloordialog.findViewById<Button>(R.id.btnFloor2)
                openlounge.setOnClickListener {
                    changeSeatApi("1층 오픈스튜디오", intraId)
                    onefloordialog.dismiss()
                    dialog.dismiss()
                }

                var LAB = onefloordialog.findViewById<Button>(R.id.btnFloor3)
                LAB.setOnClickListener {
                    changeSeatApi("1층 오락실", intraId)
                    onefloordialog.dismiss()
                    dialog.dismiss()
                }

//                var youtubestudio = onefloordialog.findViewById<Button>(R.id.btnFloor4)
//                youtubestudio.setOnClickListener {
//                    changeSeatApi("1층 유튜브스튜디오", intraId)
//                    onefloordialog.dismiss()
//                    dialog.dismiss()
//                }

            }

            //2층
            val second_fourfloor = dialog.findViewById<Button>(R.id.btnFloor2)
            second_fourfloor.setOnClickListener {
                val second_fourfloordialog = Dialog(this)
//                second_fourfloordialog.setContentView(R.layout.activity_editseat2floor_4floor_popup)
                second_fourfloordialog.setContentView(R.layout.activity_editseat2floor_popup)
                second_fourfloordialog.setCanceledOnTouchOutside(true)
                second_fourfloordialog.setCancelable(true)
                second_fourfloordialog.window?.setGravity(Gravity.CENTER)
                second_fourfloordialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                second_fourfloordialog.show()

                var youtubestudio = second_fourfloordialog.findViewById<Button>(R.id.btnFloor1)
                youtubestudio.setOnClickListener {
                    changeSeatApi("2층 1클러스터", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var oasis = second_fourfloordialog.findViewById<Button>(R.id.btnFloor2)
                oasis.setOnClickListener {
                    changeSeatApi("2층 2클러스터", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var meetingroomA = second_fourfloordialog.findViewById<Button>(R.id.btnFloor3)
                meetingroomA.setOnClickListener {
                    changeSeatApi("2층 회의실", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var meetingroomB = second_fourfloordialog.findViewById<Button>(R.id.btnFloor4)
                meetingroomB.setOnClickListener {
                    changeSeatApi("2층 직선테이블", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var stonetable = second_fourfloordialog.findViewById<Button>(R.id.btnFloor5)
                stonetable.setOnClickListener {
                    changeSeatApi("2층 원형테이블", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var studyplace = second_fourfloordialog.findViewById<Button>(R.id.btnFloor6)
                studyplace.setOnClickListener {
                    changeSeatApi("2층 사각테이블", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var Terrace = second_fourfloordialog.findViewById<Button>(R.id.btnFloor7)
                Terrace.setOnClickListener {
                    changeSeatApi("2층 테라스", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }
            }

            //3층
            val third = dialog.findViewById<Button>(R.id.btnFloor3)
            third.setOnClickListener {

                val third = Dialog(this)
                third.setContentView(R.layout.activity_editseat3floor_popup)
                third.setCanceledOnTouchOutside(true)
                third.setCancelable(true)
                third.window?.setGravity(Gravity.CENTER)
                third.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                third.show()

                var oasis = third.findViewById<Button>(R.id.btnFloor1)
                oasis.setOnClickListener {
                    changeSeatApi("3층 X1클러스터", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }

                var rectangleA = third.findViewById<Button>(R.id.btnFloor2)
                rectangleA.setOnClickListener {
                    changeSeatApi("3층 X2클러스터", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }

                var rectangleB = third.findViewById<Button>(R.id.btnFloor3)
                rectangleB.setOnClickListener {
                    changeSeatApi("3층 반원테이블", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }

                var centerTable = third.findViewById<Button>(R.id.btnFloor4)
                centerTable.setOnClickListener {
                    changeSeatApi("3층 중앙테이블", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }

                var straightTable = third.findViewById<Button>(R.id.btnFloor5)
                straightTable.setOnClickListener {
                    changeSeatApi("3층 직선테이블", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }

                var Terrace = third.findViewById<Button>(R.id.btnFloor6)
                Terrace.setOnClickListener {
                    changeSeatApi("3층 직선테이블", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }
            }

            //4층
            val four = dialog.findViewById<Button>(R.id.btnFloor4)
            four.setOnClickListener {
                val four = Dialog(this)
                four.setContentView(R.layout.activity_editseat4floor_5floor_popup)
                four.setCanceledOnTouchOutside(true)
                four.setCancelable(true)
                four.window?.setGravity(Gravity.CENTER)
                four.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                four.show()


                var youtubestudio = four.findViewById<Button>(R.id.btnFloor1)
                youtubestudio.setOnClickListener {
                    changeSeatApi("4층 3클러스터", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var oasis = four.findViewById<Button>(R.id.btnFloor2)
                oasis.setOnClickListener {
                    changeSeatApi("4층 4클러스터", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var meetingroomA = four.findViewById<Button>(R.id.btnFloor3)
                meetingroomA.setOnClickListener {
                    changeSeatApi("4층 회의실", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var meetingroomB = four.findViewById<Button>(R.id.btnFloor4)
                meetingroomB.setOnClickListener {
                    changeSeatApi("4층 원형테이블", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var stonetable = four.findViewById<Button>(R.id.btnFloor5)
                stonetable.setOnClickListener {
                    changeSeatApi("4층 직선테이블", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }
            }

            //5층
            val five = dialog.findViewById<Button>(R.id.btnFloor5)
            five.setOnClickListener {
                val five = Dialog(this)
                five.setContentView(R.layout.activity_editseat4floor_5floor_popup)
                five.setCanceledOnTouchOutside(true)
                five.setCancelable(true)
                five.window?.setGravity(Gravity.CENTER)
                five.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                five.show()

                var oasis = five.findViewById<Button>(R.id.btnFloor1)
                oasis.text = "5클러스터"
                oasis.setOnClickListener {
                    changeSeatApi("5층 5클러스터", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }

                var chair = five.findViewById<Button>(R.id.btnFloor2)
                chair.text = "6클러스터"
                chair.setOnClickListener {
                    changeSeatApi("5층 6클러스터", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }

                var stonetable = five.findViewById<Button>(R.id.btnFloor3)
                stonetable.text = "집현전"
                stonetable.setOnClickListener {
                    changeSeatApi("5층 집현전", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }

                var studyplace = five.findViewById<Button>(R.id.btnFloor4)
                studyplace.text = "원형테이블"
                studyplace.setOnClickListener {
                    changeSeatApi("5층 원형테이블", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }

                var Terrace = five.findViewById<Button>(R.id.btnFloor5)
                Terrace.text = "직선테이블"
                Terrace.setOnClickListener {
                    changeSeatApi("5층 직선테이블", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }
            }

            //옥상
            val rooftop_basement = dialog.findViewById<Button>(R.id.btnFloor6)
            rooftop_basement.setOnClickListener {
                val rooftop_basement = Dialog(this)
                rooftop_basement.setContentView(R.layout.activity_editseatrooftop_popup)
                rooftop_basement.setCanceledOnTouchOutside(true)
                rooftop_basement.setCancelable(true)
                rooftop_basement.window?.setGravity(Gravity.CENTER)
                rooftop_basement.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                rooftop_basement.show()

                var openstudio = rooftop_basement.findViewById<Button>(R.id.btnFloor1)
                openstudio.setOnClickListener {
                    changeSeatApi("옥상 탁구대", intraId)
                    rooftop_basement.dismiss()
                    dialog.dismiss()
                }

                var pingPong = rooftop_basement.findViewById<Button>(R.id.btnFloor2)
                pingPong.setOnClickListener {
                    changeSeatApi("옥상 야외정원", intraId)
                    rooftop_basement.dismiss()
                    dialog.dismiss()
                }
            }

            //지하
            val basement = dialog.findViewById<Button>(R.id.btnFloor7)
            basement.setOnClickListener {
                    changeSeatApi("지하", intraId)
                    dialog.dismiss()
            }
            dialog.show()
        }


        //footer 홈, 검색 버튼
        val homeButton: ImageButton = this.findViewById(R.id.home_button)

        homeButton.setOnClickListener {
            try {
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }


        }
        val searchButton: ImageButton = this.findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            try {
                //Toast.makeText(this, "버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainSearchPage::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeSeatApi(changeseat: String, profileIntraId : Int) {
        val sharedViewModel = ViewModelProvider(this).get(SharedViewModel_Profile::class.java)
        val textcustomlocation : String = changeseat.trim()
        val editlocationcustom = locationCustomMemberRequest(profileIntraId, textcustomlocation)
        val token = userSettings.token
        if (token != null) {
            sharedViewModel.updateMemberCustomLocaton(editlocationcustom, token)
        }
        val intent = Intent(this@MainSettingPage, MainPageActivity::class.java)
        finish() //인텐트 종료
        startActivity(intent)

    }





}