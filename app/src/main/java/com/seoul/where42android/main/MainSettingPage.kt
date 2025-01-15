package com.seoul.where42android.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.seoul.where42android.ViewModel.SharedViewModelProfile
import com.seoul.where42android.utils.ApiUtils
import com.seoul.where42android.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSettingPage : AppCompatActivity() {

    val userSettings = UserSettings.getInstance()
    private var intraId: Int = -1 // 멤버 변수로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_page)

        // TokenManager에서 intraId 초기화
        CoroutineScope(Dispatchers.IO).launch {
            intraId = TokenManager.getIntraId() ?: -1
            Log.d("MainSettingPage", "intraId initialized: $intraId")
        }

        //logout
        val logoutButton: Button = this.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            logout()
        }

        //코멘트 설정 코드
        val commentButton: Button = this.findViewById(R.id.comment_button)
        commentButton.setOnClickListener {
            commentSet()
        }

        //수동 자리 설정
        val manualDigitSetting: Button = this.findViewById(R.id.place_setting_button)
        manualDigitSetting.setOnClickListener {
            if (userSettings.inCluster == false) {
                noInCluster()
            } else {
                inCluster()
            }
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

        //이용가이드
        val guideButton: Button = this.findViewById(R.id.guide)
        guideButton.setOnClickListener {
            guideButton.setOnClickListener {
                val url = "https://holy-seatbelt-ff0.notion.site/where42-Android-d776288e21a0407dbbf1dc237063e306?pvs=4"
                // 웹 페이지로 이동하는 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                // Intent 실행
                startActivity(intent)
            }
        }

        //앱 피드백
        val feedbackButton: Button = this.findViewById(R.id.feedback)
        feedbackButton.setOnClickListener {
            feedbackButton.setOnClickListener {
                val url = "https://forms.gle/bGNz5n7rdnG4DbZV6"
                // 웹 페이지로 이동하는 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                // Intent 실행
                startActivity(intent)
            }
        }
    }

    private fun commentSet() {
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
            val sharedViewModelProfile = ViewModelProvider(this).get(SharedViewModelProfile::class.java)
            val comment: String = editText.text.toString()
            val updateRequest = UpdateCommentRequest(intraId, comment)
            sharedViewModelProfile.fetchProfileComment(updateRequest, this)
            Log.d("MainSettingPage", "Updated Comment: $intraId")
            dialog.dismiss()
            finish() //인텐트 종료
        }
        dialog.show()
    }

    private fun logout() {
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
            //logout api 부르기
            logout_dialog.dismiss()
            CoroutineScope(Dispatchers.IO).launch {
                val response = ApiUtils.performApiRequest(this@MainSettingPage) { token ->
                    val logoutApi =
                        RetrofitConnection.getInstance(token).create(logoutAPI::class.java)
                    logoutApi.logout()
                }
                if (response?.isSuccessful == true && response.code() == 200) {
                    val cookieManager = CookieManager.getInstance()
                    cookieManager.removeAllCookies(null)
                    TokenManager.clearAllData()
                    val intent = Intent(this@MainSettingPage, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        cancel.setOnClickListener {
            logout_dialog.dismiss()
        }
        logout_dialog.show()
    }

    private fun inCluster() {
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


            val openstudio = onefloordialog.findViewById<Button>(R.id.btnFloor1)
            openstudio.setOnClickListener {
                changeSeatApi("1층 42LAB", intraId)
                onefloordialog.dismiss()
                dialog.dismiss()
            }

            val openlounge = onefloordialog.findViewById<Button>(R.id.btnFloor2)
            openlounge.setOnClickListener {
                changeSeatApi("1층 오픈스튜디오", intraId)
                onefloordialog.dismiss()
                dialog.dismiss()
            }

            val LAB = onefloordialog.findViewById<Button>(R.id.btnFloor3)
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

            val youtubestudio =
                second_fourfloordialog.findViewById<Button>(R.id.btnFloor1)
            youtubestudio.setOnClickListener {
                changeSeatApi("2층 1클러스터", intraId)
                second_fourfloordialog.dismiss()
                dialog.dismiss()
            }

            val oasis = second_fourfloordialog.findViewById<Button>(R.id.btnFloor2)
            oasis.setOnClickListener {
                changeSeatApi("2층 2클러스터", intraId)
                second_fourfloordialog.dismiss()
                dialog.dismiss()
            }

            val meetingroomA =
                second_fourfloordialog.findViewById<Button>(R.id.btnFloor3)
            meetingroomA.setOnClickListener {
                changeSeatApi("2층 회의실", intraId)
                second_fourfloordialog.dismiss()
                dialog.dismiss()
            }

            val meetingroomB =
                second_fourfloordialog.findViewById<Button>(R.id.btnFloor4)
            meetingroomB.setOnClickListener {
                changeSeatApi("2층 직선테이블", intraId)
                second_fourfloordialog.dismiss()
                dialog.dismiss()
            }

            val stonetable = second_fourfloordialog.findViewById<Button>(R.id.btnFloor5)
            stonetable.setOnClickListener {
                changeSeatApi("2층 원형테이블", intraId)
                second_fourfloordialog.dismiss()
                dialog.dismiss()
            }

            val studyplace = second_fourfloordialog.findViewById<Button>(R.id.btnFloor6)
            studyplace.setOnClickListener {
                changeSeatApi("2층 사각테이블", intraId)
                second_fourfloordialog.dismiss()
                dialog.dismiss()
            }

            val Terrace = second_fourfloordialog.findViewById<Button>(R.id.btnFloor7)
            Terrace.setOnClickListener {
                changeSeatApi("2층 테라스", intraId)
                second_fourfloordialog.dismiss()
                dialog.dismiss()
            }
        }

        //3층
        val third = dialog.findViewById<Button>(R.id.btnFloor3)
        third.setOnClickListener {

            val thirdd = Dialog(this)
            thirdd.setContentView(R.layout.activity_editseat3floor_popup)
            thirdd.setCanceledOnTouchOutside(true)
            thirdd.setCancelable(true)
            thirdd.window?.setGravity(Gravity.CENTER)
            thirdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            thirdd.show()

            val oasis = thirdd.findViewById<Button>(R.id.btnFloor1)
            oasis.setOnClickListener {
                changeSeatApi("3층 X1클러스터", intraId)
                thirdd.dismiss()
                dialog.dismiss()
            }

            val rectangleA = thirdd.findViewById<Button>(R.id.btnFloor2)
            rectangleA.setOnClickListener {
                changeSeatApi("3층 X2클러스터", intraId)
                thirdd.dismiss()
                dialog.dismiss()
            }

            val rectangleB = thirdd.findViewById<Button>(R.id.btnFloor3)
            rectangleB.setOnClickListener {
                changeSeatApi("3층 반원테이블", intraId)
                thirdd.dismiss()
                dialog.dismiss()
            }

            val centerTable = thirdd.findViewById<Button>(R.id.btnFloor4)
            centerTable.setOnClickListener {
                changeSeatApi("3층 중앙테이블", intraId)
                thirdd.dismiss()
                dialog.dismiss()
            }

            val straightTable = thirdd.findViewById<Button>(R.id.btnFloor5)
            straightTable.setOnClickListener {
                changeSeatApi("3층 직선테이블", intraId)
                thirdd.dismiss()
                dialog.dismiss()
            }

            val Terrace = thirdd.findViewById<Button>(R.id.btnFloor6)
            Terrace.setOnClickListener {
                changeSeatApi("3층 직선테이블", intraId)
                thirdd.dismiss()
                dialog.dismiss()
            }
        }

        //4층
        val fourr = dialog.findViewById<Button>(R.id.btnFloor4)
        fourr.setOnClickListener {
            val four = Dialog(this)
            four.setContentView(R.layout.activity_editseat4floor_5floor_popup)
            four.setCanceledOnTouchOutside(true)
            four.setCancelable(true)
            four.window?.setGravity(Gravity.CENTER)
            four.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            four.show()


            val youtubestudio = four.findViewById<Button>(R.id.btnFloor1)
            youtubestudio.setOnClickListener {
                changeSeatApi("4층 3클러스터", intraId)
                four.dismiss()
                dialog.dismiss()
            }

            val oasis = four.findViewById<Button>(R.id.btnFloor2)
            oasis.setOnClickListener {
                changeSeatApi("4층 4클러스터", intraId)
                four.dismiss()
                dialog.dismiss()
            }

            val meetingroomA = four.findViewById<Button>(R.id.btnFloor3)
            meetingroomA.setOnClickListener {
                changeSeatApi("4층 회의실", intraId)
                four.dismiss()
                dialog.dismiss()
            }

            val meetingroomB = four.findViewById<Button>(R.id.btnFloor4)
            meetingroomB.setOnClickListener {
                changeSeatApi("4층 원형테이블", intraId)
                four.dismiss()
                dialog.dismiss()
            }

            val stonetable = four.findViewById<Button>(R.id.btnFloor5)
            stonetable.setOnClickListener {
                changeSeatApi("4층 직선테이블", intraId)
                four.dismiss()
                dialog.dismiss()
            }
        }

        //5층
        val fivee = dialog.findViewById<Button>(R.id.btnFloor5)
        fivee.setOnClickListener {
            val five = Dialog(this)
            five.setContentView(R.layout.activity_editseat4floor_5floor_popup)
            five.setCanceledOnTouchOutside(true)
            five.setCancelable(true)
            five.window?.setGravity(Gravity.CENTER)
            five.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            five.show()

            val oasis = five.findViewById<Button>(R.id.btnFloor1)
            oasis.text = "5클러스터"
            oasis.setOnClickListener {
                changeSeatApi("5층 5클러스터", intraId)
                five.dismiss()
                dialog.dismiss()
            }

            val chair = five.findViewById<Button>(R.id.btnFloor2)
            chair.text = "6클러스터"
            chair.setOnClickListener {
                changeSeatApi("5층 6클러스터", intraId)
                five.dismiss()
                dialog.dismiss()
            }

            val stonetable = five.findViewById<Button>(R.id.btnFloor3)
            stonetable.text = "집현전"
            stonetable.setOnClickListener {
                changeSeatApi("5층 집현전", intraId)
                five.dismiss()
                dialog.dismiss()
            }

            val studyplace = five.findViewById<Button>(R.id.btnFloor4)
            studyplace.text = "원형테이블"
            studyplace.setOnClickListener {
                changeSeatApi("5층 원형테이블", intraId)
                five.dismiss()
                dialog.dismiss()
            }

            val Terrace = five.findViewById<Button>(R.id.btnFloor5)
            Terrace.text = "직선테이블"
            Terrace.setOnClickListener {
                changeSeatApi("5층 직선테이블", intraId)
                five.dismiss()
                dialog.dismiss()
            }
        }

        //옥상
        val rooftop_basemented = dialog.findViewById<Button>(R.id.btnFloor6)
        rooftop_basemented.setOnClickListener {
            val rooftop_basement = Dialog(this)
            rooftop_basement.setContentView(R.layout.activity_editseatrooftop_popup)
            rooftop_basement.setCanceledOnTouchOutside(true)
            rooftop_basement.setCancelable(true)
            rooftop_basement.window?.setGravity(Gravity.CENTER)
            rooftop_basement.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            rooftop_basement.show()

            val openstudio = rooftop_basement.findViewById<Button>(R.id.btnFloor1)
            openstudio.setOnClickListener {
                changeSeatApi("옥상 탁구대", intraId)
                rooftop_basement.dismiss()
                dialog.dismiss()
            }

            val pingPong = rooftop_basement.findViewById<Button>(R.id.btnFloor2)
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

    private fun noInCluster() {
        val nochange = Dialog(this)
        nochange.setContentView(R.layout.activtiy_prohibition_popup)
        nochange.setCanceledOnTouchOutside(true)
        nochange.setCancelable(true)
        nochange.window?.setGravity(Gravity.CENTER)
        nochange.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 안의 버튼 등의 View를 찾아서 이벤트를 처리할 수 있습니다.
        val submitButton = nochange.findViewById<Button>(R.id.submit)

        // 확인 버튼 클릭 시 원하는 동작을 수행합니다.
        submitButton.setOnClickListener {
            nochange.dismiss()
        }
        nochange.show()
    }

    private fun changeSeatApi(changeseat: String, profileIntraId : Int) {
        val sharedViewModel = ViewModelProvider(this).get(SharedViewModelProfile::class.java)
        val textcustomlocation : String = changeseat.trim()
        val editlocationcustom = locationCustomMemberRequest(profileIntraId, textcustomlocation)
        sharedViewModel.fetchProfileCustomLocation(editlocationcustom, this)
        val intent = Intent(this@MainSettingPage, MainPageActivity::class.java)
        finish() //인텐트 종료
        startActivity(intent)

    }

}