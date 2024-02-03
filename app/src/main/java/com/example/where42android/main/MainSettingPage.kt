package com.example.where42android.main

import SharedViewModel_Profile
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.where42android.Base_url_api_Retrofit.ApiObject
import com.example.where42android.Base_url_api_Retrofit.CommentChangeMember
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection_data
import com.example.where42android.Base_url_api_Retrofit.UpdateCommentRequest
import com.example.where42android.Base_url_api_Retrofit.locationCustomMemberRequest
import com.example.where42android.Base_url_api_Retrofit.locationCustomMemberResponse
import com.example.where42android.Base_url_api_Retrofit.member_custom_location
import com.example.where42android.R
import com.example.where42android.SearchPage
import com.example.where42android.UserSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainSettingPage : AppCompatActivity() {
    val userSettings = UserSettings.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_page)

        val receivedIntent = intent
        val profileIntraId: Int = receivedIntent.getIntExtra("PROFILE_DATA", -1)


//        val intraIdString: String? = receivedIntent.getStringExtra("INTRA_ID")
//        Log.e("MainSettingPage", "intraIdString : ${intraIdString}")
//        val intraId : Int = intraIdString?.toInt() ?: -1

        var intraId_class : Int = receivedIntent.getIntExtra("INTRA_ID", -1)
        var intraId = userSettings.intraId
        Log.e("MainSettingPage", "intraId : ${intraId}")

        val token: String? = receivedIntent.getStringExtra("TOKEN")
        Log.e("MainSettingPage", "token : ${token}")

//        val token = "awd"
//        val intraId = 5

        //환경 설정 텍스트 크기 자동으로 구하기
//        fun adjustTextViewSize(textView: TextView) {
//            val displayMetrics = textView.resources.displayMetrics
//            val screenWidth = displayMetrics.widthPixels
//
//            val desiredWidth = screenWidth * 0.412 // 원하는 폭 (현재 레이아웃에서의 비율)
//            val text = textView.text.toString()
//
//            // 적절한 텍스트 크기 계산
//            var textSize = 74 // 초기 텍스트 크기
//            var paint = Paint()
//            paint.textSize = textSize.toFloat()
//
//            while (paint.measureText(text) > desiredWidth) {
//                textSize--
//                paint.textSize = textSize.toFloat()
//            }
//
//            // TextView에 적절한 텍스트 크기 설정
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
//        }
//        val myTextView = findViewById<TextView>(R.id.text_setting)
//        adjustTextViewSize(myTextView)

//        //자리비움 설정
//        val awaySettingButton : Button = this.findViewById(R.id.unavailable_button)
//        awaySettingButton.setOnClickListener {
//            val dialog = Dialog(this)
//            dialog.setContentView(R.layout.activity_editstatus_popup)
//
//            dialog.setCanceledOnTouchOutside(true)
//            dialog.setCancelable(true)
//            dialog.window?.setGravity(Gravity.CENTER)
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//
////            val title = dialog.findViewById<TextView>(R.id.title)
//            val btnCancel = dialog.findViewById<Button>(R.id.cancel)
//            val btnSubmit = dialog.findViewById<Button>(R.id.submit)
//
//            btnCancel.setOnClickListener {
//                dialog.dismiss()
//            }
//            dialog.show()
//
//        }

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
                Log.e("MainSettingPage", "intraId : ${intraId}")
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
            dialog.setContentView(R.layout.activity_editseat_popup)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val title = dialog.findViewById<TextView>(R.id.title)

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
                    changeSeatApi("1층 오픈스튜디오", intraId)
                    onefloordialog.dismiss()
                    dialog.dismiss()
                }

                var openlounge = onefloordialog.findViewById<Button>(R.id.btnFloor2)
                openlounge.setOnClickListener {
                    changeSeatApi("1층 오픈라운지", intraId)
                    onefloordialog.dismiss()
                    dialog.dismiss()
                }

                var LAB = onefloordialog.findViewById<Button>(R.id.btnFloor3)
                LAB.setOnClickListener {
                    changeSeatApi("1층 42LAB", intraId)
                    onefloordialog.dismiss()
                    dialog.dismiss()
                }

                var youtubestudio = onefloordialog.findViewById<Button>(R.id.btnFloor4)
                youtubestudio.setOnClickListener {
                    changeSeatApi("1층 유튜브스튜디오", intraId)
                    onefloordialog.dismiss()
                    dialog.dismiss()
                }

            }

            //2층
            val second_fourfloor = dialog.findViewById<Button>(R.id.btnFloor2)
            second_fourfloor.setOnClickListener {
                val second_fourfloordialog = Dialog(this)
                second_fourfloordialog.setContentView(R.layout.activity_editseat2floor_4floor_popup)
                second_fourfloordialog.setCanceledOnTouchOutside(true)
                second_fourfloordialog.setCancelable(true)
                second_fourfloordialog.window?.setGravity(Gravity.CENTER)
                second_fourfloordialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                second_fourfloordialog.show()

                var youtubestudio = second_fourfloordialog.findViewById<Button>(R.id.btnFloor1)
                youtubestudio.setOnClickListener {
                    changeSeatApi("2층 유튜브스튜디오", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var oasis = second_fourfloordialog.findViewById<Button>(R.id.btnFloor2)
                oasis.setOnClickListener {
                    changeSeatApi("2층 오아시스", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var meetingroomA = second_fourfloordialog.findViewById<Button>(R.id.btnFloor3)
                meetingroomA.setOnClickListener {
                    changeSeatApi("2층 회의실A", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var meetingroomB = second_fourfloordialog.findViewById<Button>(R.id.btnFloor4)
                meetingroomB.setOnClickListener {
                    changeSeatApi("2층 회의실B", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var stonetable = second_fourfloordialog.findViewById<Button>(R.id.btnFloor5)
                stonetable.setOnClickListener {
                    changeSeatApi("2층 스톤테이블", intraId)
                    second_fourfloordialog.dismiss()
                    dialog.dismiss()
                }

                var studyplace = second_fourfloordialog.findViewById<Button>(R.id.btnFloor6)
                studyplace.setOnClickListener {
                    changeSeatApi("2층 기타학습공간", intraId)
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
                    changeSeatApi("3층 오아시스", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }

                var rectangleA = third.findViewById<Button>(R.id.btnFloor2)
                rectangleA.setOnClickListener {
                    changeSeatApi("3층 다각형책상A", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }

                var rectangleB = third.findViewById<Button>(R.id.btnFloor3)
                rectangleB.setOnClickListener {
                    changeSeatApi("3층 다각형책상A", intraId)
                    third.dismiss()
                    dialog.dismiss()
                }

            }

            //4층
            val four = dialog.findViewById<Button>(R.id.btnFloor4)
            four.setOnClickListener {
                val four = Dialog(this)
                four.setContentView(R.layout.activity_editseat2floor_4floor_popup)
                four.setCanceledOnTouchOutside(true)
                four.setCancelable(true)
                four.window?.setGravity(Gravity.CENTER)
                four.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                four.show()


                var youtubestudio = four.findViewById<Button>(R.id.btnFloor1)
                youtubestudio.setOnClickListener {
                    changeSeatApi("4층 유튜브스튜디오", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var oasis = four.findViewById<Button>(R.id.btnFloor2)
                oasis.setOnClickListener {
                    changeSeatApi("4층 오아시스", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var meetingroomA = four.findViewById<Button>(R.id.btnFloor3)
                meetingroomA.setOnClickListener {
                    changeSeatApi("4층 회의실A", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var meetingroomB = four.findViewById<Button>(R.id.btnFloor4)
                meetingroomB.setOnClickListener {
                    changeSeatApi("4층 회의실B", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var stonetable = four.findViewById<Button>(R.id.btnFloor5)
                stonetable.setOnClickListener {
                    changeSeatApi("4층 스톤테이블", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }

                var studyplace = four.findViewById<Button>(R.id.btnFloor6)
                studyplace.setOnClickListener {
                    changeSeatApi("4층 기타학습공간", intraId)
                    four.dismiss()
                    dialog.dismiss()
                }
            }

            //5층
            val five = dialog.findViewById<Button>(R.id.btnFloor5)
            five.setOnClickListener {
                val five = Dialog(this)
                five.setContentView(R.layout.activity_editseat5floor_popup)
                five.setCanceledOnTouchOutside(true)
                five.setCancelable(true)
                five.window?.setGravity(Gravity.CENTER)
                five.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                five.show()


                var oasis = five.findViewById<Button>(R.id.btnFloor1)
                oasis.setOnClickListener {
                    changeSeatApi("5층 오아시스", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }

                var chair = five.findViewById<Button>(R.id.btnFloor2)
                chair.setOnClickListener {
                    changeSeatApi("5층 좌식공간", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }

                var stonetable = five.findViewById<Button>(R.id.btnFloor3)
                stonetable.setOnClickListener {
                    changeSeatApi("5층 스톤테이블", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }

                var studyplace = five.findViewById<Button>(R.id.btnFloor4)
                studyplace.setOnClickListener {
                    changeSeatApi("5층 기타학습공간", intraId)
                    five.dismiss()
                    dialog.dismiss()
                }
            }

            //지하/옥상
            val rooftop_basement = dialog.findViewById<Button>(R.id.btnFloor6)
            rooftop_basement.setOnClickListener {
                val rooftop_basement = Dialog(this)
                rooftop_basement.setContentView(R.layout.activity_editseatrooftop_basement_popup)
                rooftop_basement.setCanceledOnTouchOutside(true)
                rooftop_basement.setCancelable(true)
                rooftop_basement.window?.setGravity(Gravity.CENTER)
                rooftop_basement.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                rooftop_basement.show()



                var openstudio = rooftop_basement.findViewById<Button>(R.id.btnFloor1)
                openstudio.setOnClickListener {
                    changeSeatApi("지하 오픈스튜디오", intraId)
                    rooftop_basement.dismiss()
                    dialog.dismiss()
                }


                var pingPong = rooftop_basement.findViewById<Button>(R.id.btnFloor2)
                pingPong.setOnClickListener {
                    changeSeatApi("6층 탁구대", intraId)
                    rooftop_basement.dismiss()
                    dialog.dismiss()
                }

                var outdoorGarden = rooftop_basement.findViewById<Button>(R.id.btnFloor3)
                outdoorGarden.setOnClickListener {
                    changeSeatApi("6층 야외정원", intraId)
                    rooftop_basement.dismiss()
                    dialog.dismiss()
                }

                var studyplace = rooftop_basement.findViewById<Button>(R.id.btnFloor4)
                studyplace.setOnClickListener {
                    changeSeatApi("6층 기타학습공간", intraId)
                    rooftop_basement.dismiss()
                    dialog.dismiss()
                }
            }

            dialog.show()

        }


        //밑 옛날 버전
//        //수동 자리 설정
//        val manualDigitSetting : Button = this.findViewById(R.id.place_setting_button)
//        manualDigitSetting.setOnClickListener {
//            val dialog = Dialog(this)
//
//
//            dialog.setContentView(R.layout.activity_edittext_popup)
//
//
//
//            dialog.setCanceledOnTouchOutside(true)
//            dialog.setCancelable(true)
//            dialog.window?.setGravity(Gravity.CENTER)
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//            val title = dialog.findViewById<TextView>(R.id.title)
//            title.text = "수동 자리 설정"
//
//
//            val editText = dialog.findViewById<EditText>(R.id.input)
//            val typeface = ResourcesCompat.getFont(this, R.font.gmarketsans_bold)
//            editText.typeface = typeface
//            editText.hint = "설정할 자리를 입력해주세요"
//
//            val btnCancel = dialog.findViewById<Button>(R.id.cancel)
//            val btnSubmit = dialog.findViewById<Button>(R.id.submit)
//
//
//            btnCancel.setOnClickListener {
//                dialog.dismiss()
//            }
//
//            btnSubmit.setOnClickListener {
//                val textcustomlocation : String = editText.text.toString().trim()
//                val editlocationcustom = locationCustomMemberRequest(profileIntraId, textcustomlocation)
//                val sharedViewModel = ViewModelProvider(this).get(SharedViewModel_Profile::class.java)
////                sharedViewModel.updateComment(comment)
//                // updateMemberComment 함수 호출
//                if (token != null) {
//                    sharedViewModel.updateMemberCustomLocaton(editlocationcustom, token)
//                }
//
////                val retrofitAPI = RetrofitConnection_data.getInstance().create(member_custom_location::class.java)
////                val textcustomlocation : String = editText.text.toString().trim()
////                val editlocationcustom = locationCustomMemberRequest(profileIntraId, textcustomlocation)
////                val call = retrofitAPI.customLocationChange(editlocationcustom)
////
////                call.enqueue(object : Callback<locationCustomMemberResponse> {
////                    override fun onResponse(
////                        call: Call<locationCustomMemberResponse>,
////                        response: Response<locationCustomMemberResponse>
////                    ) {
////                        if (response.isSuccessful) {
////                            val newGroupResponse  = response.body()
////                            // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
////                        } else {
////                            // API 호출에 실패한 경우
////                            Log.e("DELETE_ERROR", "Failed to delete group. Error code: ${response.code()}")
////
////                        }
////                    }
////                    override fun onFailure(call: Call<locationCustomMemberResponse>, t: Throwable) {
////
////                        Log.e("CREATE_ERROR", "Network error occurred. Message: ${t.message}")
////                    }
////                })
//                dialog.dismiss()
//
////                val intent = Intent(this@MainSettingPage, MainPageActivity::class.java)
//                finish() //인텐트 종료
////                startActivity(intent)
//            }
//            dialog.show()
//
//        }


        //-------------------------------------- footer --------------------------------------

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
                val intent = Intent(this, SearchPage::class.java)
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
//                sharedViewModel.updateComment(comment)
                // updateMemberComment 함수 호출


        val retrofitAPI = RetrofitConnection_data.getInstance().create(member_custom_location::class.java)
        val textcustomlocation : String = changeseat.trim()
        val editlocationcustom = locationCustomMemberRequest(profileIntraId, textcustomlocation)
//        val call = retrofitAPI.customLocationChange(editlocationcustom)

        val token = userSettings.token

        if (token != null) {
            sharedViewModel.updateMemberCustomLocaton(editlocationcustom, token)
        }

        val intent = Intent(this@MainSettingPage, MainPageActivity::class.java)
        finish() //인텐트 종료
        startActivity(intent)

//        call.enqueue(object : Callback<locationCustomMemberResponse> {
//            override fun onResponse(
//                call: Call<locationCustomMemberResponse>,
//                response: Response<locationCustomMemberResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val newGroupResponse  = response.body()
//                    // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
//                } else {
//                    // API 호출에 실패한 경우
//                    Log.e("DELETE_ERROR", "Failed to delete group. Error code: ${response.code()}")
//
//                }
//            }
//            override fun onFailure(call: Call<locationCustomMemberResponse>, t: Throwable) {
//
//                Log.e("CREATE_ERROR", "Network error occurred. Message: ${t.message}")
//            }
//        })

    }





}