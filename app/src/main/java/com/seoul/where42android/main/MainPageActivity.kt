package com.seoul.where42android.main

import SharedViewModel_Profile
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import de.hdodenhof.circleimageview.CircleImageView

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
    lateinit var profile : Member
    private lateinit var sharedViewModel_profile: SharedViewModel_Profile


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //token 들고오기
//        val intent = intent
//        val receivedToken1 = intent.getStringExtra("TOKEN_KEY")?: "notoken"
//        val receivedIntraId1 = intent.getStringExtra("INTRAID_KEY")?.toInt() ?: -1
//        val receivedAgreement = intent.getStringExtra("AGREEMENT_KEY")



        val userSettings = UserSettings.getInstance()
        val receivedToken = userSettings.token
        val receivedIntraId = userSettings.intraId
        val receivedrefreshToken = userSettings.refreshToken
        val receivedargreement = userSettings.agreement

//        Log.e("checkcheck", "here receivedToken : receive token ${receivedToken}")
//        Log.e("checkcheck", "here receivedrefreshToken : receive receivedrefreshToken ${receivedrefreshToken}")
//        Log.e("checkcheck", "here intraId : intraId ${receivedIntraId}")
//        Log.e("checkcheck", "here receivedargreement : intraId ${receivedargreement}")


//        2. group list을 보여주기 위해 binding으로 MainFragment 설정
        supportFragmentManager.beginTransaction().replace(binding.container.id, MainFragment(receivedToken, receivedIntraId)).commit()

//        2. 12_18 api를 통해 사용자 프로필 가져오기
//        Log.d("PageCheck", "here")
        sharedViewModel_profile = ViewModelProvider(this).get(SharedViewModel_Profile::class.java)

        val intraId = receivedIntraId// Replace with the actual intraId
//        Log.d("PageCheck", "intraId: ${intraId}")

        val checkreissue = sharedViewModel_profile.getMemberData(this@MainPageActivity, intraId, receivedToken)
//        Log.d("PageCheck", "intraId: getMemberData")
//        Log.d("PageCheck", "checkreissue : ${checkreissue}")

        // LiveData 객체 관찰
        sharedViewModel_profile.profileLiveData.observe(this) { member ->
            if (member != null) {
//                Log.d("PageCheck", "member responsecode : ${member.responsecode} ")
            }
            member?.let {
                profile = member

                val mainImage = findViewById<CircleImageView>(R.id.profile_photo)
                val imageUrl = member.image
                Glide.with(this@MainPageActivity)
                    .load(imageUrl)
                    .apply(RequestOptions().circleCrop())
                    .error(R.drawable.nointraimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 디스크 캐시 사용
                    .into(mainImage)
                userSettings.defaultGroup = member.defaultGroupId
                val intraIdTextView = binding.intraId
                intraIdTextView.text = member.intraName
                binding.Comment.text = member.comment
                binding.locationInfo.text = member.location
//                Log.d("checkIocation", "lo : ${binding.locationInfo.text}")
                if (binding.locationInfo.text == "퇴근") {
//                    Log.d("checkIocation", "lo2 : ${binding.locationInfo.text}")
                    binding.locationInfo.setBackgroundResource(R.drawable.location_outcluster)
                    val strokeColor = Color.parseColor("#132743")
//                    binding.locationInfo.setPadding(20, 0, 20, 0)
                    binding.locationInfo.setTextColor(strokeColor)

                    mainImage.borderWidth  = 0

                }
                binding.locationInfo.setPadding(20, 0, 20, 0)
            }
        }


        val refresh = binding.swipe
        // 새로고침 이벤트 처리
        refresh.setOnRefreshListener {
            // 여기에 새로고침을 위한 작업을 수행하세요.
            val fragment = MainFragment(receivedToken, receivedIntraId) // 현재 프래그먼트를 다시 생성
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(binding.container.id, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
            // 작업이 완료되면 아래 코드를 호출하여 새로 고침을 종료합니다.
            refresh.isRefreshing = false
        }

        //1. header의 환경 설정 버튼을 눌렀을 때 -> SettingPage.kt로 가게 하기
        val headerBinding = binding.header // Change to your actual ID for the included header
        val settingButton: ImageButton = headerBinding.settingButton

        settingButton.setOnClickListener {
            try {
                val intent = Intent(this, MainSettingPage::class.java)
                //값 넘겨주기
                intent.putExtra("PROFILE_DATA", profile.intraId)
                intent.putExtra("TOKEN", receivedToken)
//                Log.e("MainSettingPage", "receivedIntraId : ${receivedIntraId}")
                intent.putExtra("INTRA_ID", receivedIntraId)
                startActivity(intent)
//                finish()
            } catch (e: Exception) {
                e.printStackTrace()
//                Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        //3. footer의 홈버튼과 검색 버튼 기능 구현
        val footerBinding = binding.footer
        val searchButton : ImageButton = footerBinding.searchButton

        searchButton.setOnClickListener {
            try {
                //Toast.makeText(this, "버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainSearchPage::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        val homeButton : ImageButton = footerBinding.homeButton

        homeButton.setOnClickListener {
            try {
                if (this::class.java != MainPageActivity::class.java) {
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
//                    Toast.makeText(this, "이미 로그인 페이지에 있습니다!", Toast.LENGTH_SHORT).show()
                    val nochange = Dialog(this)
                    nochange.setContentView(R.layout.activity_prohibition_smalltext_popup)
                    nochange.setCanceledOnTouchOutside(true)
                    nochange.setCancelable(true)
                    nochange.window?.setGravity(Gravity.CENTER)
                    nochange.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val submitButton = nochange.findViewById<Button>(R.id.submit)
                    // 확인 버튼 클릭 시 원하는 동작을 수행합니다.
                    submitButton.setOnClickListener {
                        nochange.dismiss()
                    }
                    nochange.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        //4. 새 그룹 기능 구현
        val newGroupButton: Button = binding.newGroupButton // 레이아웃 바인딩 객체에서 버튼 가져오기

        newGroupButton.setOnClickListener {
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
//                val newGroupRequest = NewGroupRequest(groupname, intra_id)
//                sharedViewModel = ViewModelProvider(this).get(SharedViewModel_GroupsMembersList::class.java)
                    val intent = Intent(this@MainPageActivity, MainCreateGroupActivity::class.java)
                    intent.putExtra("newgroupNameKey", groupname)
                    intent.putExtra("profileintraIdKey", profile.intraId)
                    intent.putExtra("groupIdKey", profile.defaultGroupId) // groupIdKey는 key값, newGroupResponse.groupId는 전달할 값
                    startActivity(intent)
                    dialog.dismiss()
                }
            }
            dialog.show()
        }

    }
}