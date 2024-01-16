package com.example.where42android.main

import SharedViewModel_GroupsMembersList
import SharedViewModel_Profile
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.where42android.Base_url_api_Retrofit.Member
import com.example.where42android.Base_url_api_Retrofit.NewGroup
import com.example.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.example.where42android.Base_url_api_Retrofit.NewGroupResponses
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.CreateGroupActivity
import com.example.where42android.LiveData.GroupsMembersList
import com.example.where42android.MainActivity
import com.example.where42android.R
import com.example.where42android.SearchPage

import com.example.where42android.databinding.ActivityMainPageBinding
import com.example.where42android.fragment.MainFragment
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

interface OnOperationCompleteListener {
    fun onOperationComplete()
}
class MainPageActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainPageBinding
    lateinit var profile : Member
//    lateinit var progressBar: ProgressBar

    private lateinit var sharedViewModel: SharedViewModel_GroupsMembersList

    private lateinit var sharedViewModel_profile: SharedViewModel_Profile



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        progressBar = binding.myProgressBar // 프로그레스 바 찾기
//        progressBar.visibility = View.VISIBLE

        //token 들고오기
        val intent = intent
        val receivedToken = intent.getStringExtra("TOKEN_KEY")?: "notoken"
        val receivedIntraId = intent.getStringExtra("INTRAID_KEY")?.toInt() ?: -1
        val receivedAgreement = intent.getStringExtra("AGREEMENT_KEY")
//        Log.d("MainActivity", "Stored Token: ${receivedToken}")
//        Log.d("MainActivity", "Stored intraId: ${receivedIntraId}")
//        Log.d("MainActivity", "Stored agreement: ${receivedAgreement}")


//        2. group list을 보여주기 위해 binding으로 MainFragment 설정
//        supportFragmentManager.beginTransaction().replace(binding.container.id, MainFragment()).commit()

//        2. 12_18 api를 통해 사용자 프로필 가져오기
        Log.d("PageCheck", "here")
        sharedViewModel_profile = ViewModelProvider(this).get(SharedViewModel_Profile::class.java)


        val intraId = receivedIntraId// Replace with the actual intraId
        Log.d("PageCheck", "intraId: ${intraId}")

        sharedViewModel_profile.getMemberData(intraId, receivedToken)
        Log.d("PageCheck", "intraId: getMemberData")

        // LiveData 객체 관찰
        sharedViewModel_profile.profileLiveData.observe(this) { member ->
            member?.let {
                profile = member
                val intraIdTextView = binding.intraId
                intraIdTextView.text = member.intraName
                binding.Comment.text = member.comment
                binding.locationInfo.text = member.location
                val mainImage = findViewById<CircleImageView>(R.id.profile_photo)
                val imageUrl = member.image
                Glide.with(this@MainPageActivity)
                    .load(imageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(mainImage)
            }
        }


//        val locationTextView = findViewById<TextView>(R.id.location_info)
//        locationTextView.post {
//            val maxWidth = locationTextView.width // TextView의 최대 너비
//            val textPaint = locationTextView.paint // TextView의 Paint 객체
//            val text = locationTextView.text.toString() // TextView에 표시되는 텍스트
//
//            val textWidth = textPaint.measureText(text) // 텍스트의 폭 계산
//            if (textWidth > maxWidth) {
//                locationTextView.layoutParams.width = maxWidth // TextView의 너비를 최대 너비로 설정
//            }
//        }
//
//
//        //1. header의 환경 설정 버튼을 눌렀을 때 -> SettingPage.kt로 가게 하기
//        val headerBinding = binding.header // Change to your actual ID for the included header
//        val settingButton: ImageButton = headerBinding.settingButton
//
//        settingButton.setOnClickListener {
//            try {
//                val intent = Intent(this, MainSettingPage::class.java)
//                //값 넘겨주기
//                intent.putExtra("PROFILE_DATA", profile.intraId)
//                startActivity(intent)
////                finish()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//        //3. footer의 홈버튼과 검색 버튼 기능 구현
//        val footerBinding = binding.footer
//        val searchButton : ImageButton = footerBinding.searchButton
//
//        searchButton.setOnClickListener {
//            try {
//                //Toast.makeText(this, "버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, SearchPage::class.java)
//                startActivity(intent)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//        val homeButton : ImageButton = footerBinding.homeButton
//
//        homeButton.setOnClickListener {
//            try {
//                if (this::class.java != MainPageActivity::class.java) {
//                    val intent = Intent(this, MainPageActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else
//                    Toast.makeText(this, "이미 로그인 페이지에 있습니다!", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//        //4. 새 그룹 기능 구현
//        val newGroupButton: Button = binding.newGroupButton // 레이아웃 바인딩 객체에서 버튼 가져오기
//
//
//        newGroupButton.setOnClickListener {
//            val dialog = Dialog(this)
//            dialog.setContentView(R.layout.activity_edittext_popup)
//
//            dialog.setCanceledOnTouchOutside(true)
//            dialog.setCancelable(true)
//            dialog.window?.setGravity(Gravity.CENTER)
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//            val editText = dialog.findViewById<EditText>(R.id.input)
//            val typeface = ResourcesCompat.getFont(this, R.font.gmarketsans_bold)
//            editText.typeface = typeface
//            editText.hint = "그룹명을 지정해주세요."
//
//            val btnCancel = dialog.findViewById<Button>(R.id.cancel)
//            val btnSubmit = dialog.findViewById<Button>(R.id.submit)
//
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//            btnCancel.setOnClickListener {
//                dialog.dismiss()
//            }
//            btnSubmit.setOnClickListener {
//                //새그룹 버튼 확인 누르면 api 요청
//                val retrofitAPI = RetrofitConnection.getInstance().create(NewGroup::class.java)
//                //groupname, intraid 필요
//                val groupname : String = editText.text.toString()
//                //intraid 불러오자
//                val intra_id = profile.intraId
//
//                //JSON 만들어주기
//                //NewGroup @POST("v3/group")
////                val newGroupRequest = NewGroupRequest(groupname, intra_id)
//
////                sharedViewModel = ViewModelProvider(this).get(SharedViewModel_GroupsMembersList::class.java)
//                val intent = Intent(this@MainPageActivity, CreateGroupActivity::class.java)
//                intent.putExtra("newgroupNameKey", groupname)
//                intent.putExtra("profileintraIdKey", profile.intraId)
//                intent.putExtra("groupIdKey", profile?.defaultGroupId) // groupIdKey는 key값, newGroupResponse.groupId는 전달할 값
//                startActivity(intent)
//
//
//                dialog.dismiss()
//            }
//            dialog.show()
//        }
//
    }
}