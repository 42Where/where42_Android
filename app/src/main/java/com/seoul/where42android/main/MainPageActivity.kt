package com.seoul.where42android.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.seoul.where42android.Base_url_api_Retrofit.Member
import com.seoul.where42android.R
import com.seoul.where42android.databinding.ActivityMainPageBinding
import com.seoul.where42android.fragment.MainFragment
import com.seoul.where42android.utils.TokenManager
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import com.seoul.where42android.ViewModel.SharedViewModelProfile
import com.seoul.where42android.main.v3.MainAnnouncement
import com.seoul.where42android.main.v3.MainCompass
import com.seoul.where42android.main.v3.MainVisualization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object friendListObject {
    // HashMap 선언
    private val myfriendList = HashMap<Int, String>()
    //그룹 이름 저장
    val groupNameList = mutableListOf<String>()

    // 추가 함수
    fun addItem(key: Int, value: String) {
        myfriendList[key] = value
    }

    // 삭제 함수
    fun removeItem(key: Int) {
        myfriendList.remove(key)
    }

    // 검색 함수
    fun searchItem(key: Int): String? {
        return myfriendList[key]
    }

    fun groupAdd(name : String)
    {
        groupNameList.add(name)
    }

    fun groupRemove(name:String)
    {
        groupNameList.remove(name)
    }

    fun searchGroupName(name: String): Boolean {
        return name in groupNameList
    }
}

object friendCheckedList {
    val checkedItemsInt = mutableListOf<Int>()
    fun addItem(intraId : Int){
        checkedItemsInt.add(intraId)
    }

    fun removeItem(intraId : Int){
        checkedItemsInt.remove(intraId)
    }

    fun clearItem(){
        checkedItemsInt.clear()
    }

    fun searchfriendChecked(name: Int): Boolean {
        return name in checkedItemsInt
    }

    fun getfriendCheckedList(): MutableList<Int> {
        return checkedItemsInt
    }

    fun sizefriendCheckedList(): Int {
        return checkedItemsInt.size
    }
}

class MainPageActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainPageBinding
    private lateinit var profile : Member
    private lateinit var sharedViewModelProfile: SharedViewModelProfile

    private var accesstoken: String = "notoken"
    private var intraId: Int = -1
    private var agreement: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        TokenManager.initialize(this@MainPageActivity)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModelProfile = ViewModelProvider(this).get(SharedViewModelProfile::class.java)
        sharedViewModelProfile.fetchProfileData(this@MainPageActivity)
        // Profile 데이터 관찰 및 UI 업데이트
        sharedViewModelProfile.SharedProfileLiveData.observe(this) { member ->
            Log.d("MainActivityMember", "Main ${member}")
            member?.let { updateUI(it) }
        }

        CoroutineScope(Dispatchers.IO).launch {
            accesstoken = TokenManager.getAccessToken()
            intraId = TokenManager.getIntraId() ?: -1
            agreement = TokenManager.getAgreement() ?: false

            withContext(Dispatchers.Main) {
                val mainFragment = MainFragment.newInstance(accesstoken, intraId)
                supportFragmentManager.beginTransaction()
                    .replace(binding.container.id, mainFragment)
                    .commit()
            }
        }

        //1. header의 환경 설정 버튼을 눌렀을 때 -> SettingPage.kt로 가게 하기
        val headerBinding = binding.header
        val settingButton: ImageButton = headerBinding.settingButton
        settingButton.setOnClickListener {
            clickHeader()
        }

        //2. footer의 홈버튼과 검색 버튼 기능 구현
        val footerBinding = binding.footer
        val searchButton : ImageButton = footerBinding.searchButton
        val homeButton : ImageButton = footerBinding.homeButton
        searchButton.setOnClickListener {
            clickSearch()
        }
        homeButton.setOnClickListener {
            try {
//                mainFragment.refreshData()
            } catch (e: Exception) {
                Log.d("errorerror" , e.toString())
            }
        }

        //3. 새 그룹 기능 구현
        val newGroupButton: Button = binding.newGroupButton // 레이아웃 바인딩 객체에서 버튼 가져오기
        newGroupButton.setOnClickListener {
            clickNewGroup()
        }

        //4. 나침반 버튼
        val compassButton: ImageButton = headerBinding.compassButton
        compassButton.setOnClickListener {
            clickCompass()
        }

        //5. 공지사항 버튼
        val annButton: ImageButton = headerBinding.annButton
        annButton.setOnClickListener {
            clickAnn()
        }

        //6. 시각화 버튼
        val vaisualButton : ImageButton = footerBinding.visuallButton
        vaisualButton.setOnClickListener{
            clickVaisual()
        }
    }

    private fun clickVaisual() {
        val intent = Intent(this@MainPageActivity, MainVisualization::class.java)
        startActivity(intent)
    }

    private fun clickCompass() {
        val intent = Intent(this@MainPageActivity, MainCompass::class.java)
        intent.putExtra("DEFAULT_GROUP", profile.defaultGroupId.toString())
        startActivity(intent)
    }

    private fun clickAnn() {
        val intent = Intent(this@MainPageActivity, MainAnnouncement::class.java)
        startActivity(intent)
    }

    private fun clickNewGroup() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_edittext_popup)

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val editText = dialog.findViewById<EditText>(R.id.input)
        val typeface = ResourcesCompat.getFont(this, R.font.gmarketsans_bold)
        editText.typeface = typeface
        editText.hint = "그룹명을 지정해주세요."

        val btnCancel = dialog.findViewById<Button>(R.id.cancel)
        val btnSubmit = dialog.findViewById<Button>(R.id.submit)


        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnSubmit.setOnClickListener {
            //동일 이름 그룹 생성 막기
            //새그룹 버튼 확인 누르면 api 요청
            //groupname, intraid 필요
            val groupname : String = editText.text.toString()
            if (friendListObject.searchGroupName(groupname))
            {
//                    //동일 이름 있음.
                val samegroup = Dialog(this)
                samegroup.setContentView(R.layout.activtiy_prohibition_popup)

                samegroup.setCanceledOnTouchOutside(true)
                samegroup.setCancelable(true)
                samegroup.window?.setGravity(Gravity.CENTER)
                samegroup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val textname = samegroup.findViewById<TextView>(R.id.title)
                textname.text = "동일 이름을 가진 그룹이 존재합니다."

                val btnsubmit = samegroup.findViewById<Button>(R.id.submit)
                btnsubmit.setOnClickListener {
                    samegroup.dismiss()
                }
                samegroup.show()
            }
            else if (groupname.length > 20)
            {
                val longgroupname = Dialog(this)
                longgroupname.setContentView(R.layout.activtiy_prohibition_popup)

                longgroupname.setCanceledOnTouchOutside(true)
                longgroupname.setCancelable(true)
                longgroupname.window?.setGravity(Gravity.CENTER)
                longgroupname.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val textname = longgroupname.findViewById<TextView>(R.id.title)
                textname.text = "그룹 이름은 20이하로 해주세요."

                val btnsubmit = longgroupname.findViewById<Button>(R.id.submit)
                btnsubmit.setOnClickListener {
                    longgroupname.dismiss()
                }
                longgroupname.show()
            }
            else
            {
                //intraid 불러오자
                //JSON 만들어주기
                //NewGroup @POST("v3/group")
                val intent = Intent(this@MainPageActivity, MainCreateGroupActivity::class.java)
                Log.d("addGroup", "newgroupName = ${groupname}")
                intent.putExtra("newgroupName", groupname)
//                    intent.putExtra("profileintraIdKey", profile.intraId)
                intent.putExtra("defaultgroupId", profile.defaultGroupId) // groupIdKey는 key값, newGroupResponse.groupId는 전달할 값
                startActivity(intent)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun clickSearch() {
        try {
            //Toast.makeText(this, "버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainSearchPage::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickHeader() {
        try {
            Log.d("MainPageActivtiy", "click")
            val intent = Intent(this, MainSettingPage::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * UI 업데이트
     */
    private fun updateUI(member: Member) {
        profile = member
        val mainImage = findViewById<CircleImageView>(R.id.profile_photo)
        val imageUrl = member.image
        Glide.with(this@MainPageActivity)
            .load(imageUrl)
            .apply(RequestOptions().circleCrop())
            .error(R.drawable.nointraimage)
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 디스크 캐시 사용
            .into(mainImage)

        val userSettings = UserSettings.getInstance()
        userSettings.defaultGroup = member.defaultGroupId

        val intraIdTextView = binding.intraId
        intraIdTextView.text = member.intraName
        binding.Comment.text = member.comment
        binding.locationInfo.text = member.location

        if (binding.locationInfo.text == "퇴근") {
            binding.locationInfo.setBackgroundResource(R.drawable.location_outcluster)
            val strokeColor = Color.parseColor("#132743")
            binding.locationInfo.setTextColor(strokeColor)
            mainImage.borderWidth = 0
        }
        binding.locationInfo.setPadding(20, 0, 20, 0)
}
}