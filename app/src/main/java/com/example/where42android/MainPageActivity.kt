package com.example.where42android

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.where42android.ApiObject.service
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.example.where42android.databinding.ActivityMainPageBinding
import com.example.where42android.fragment.MainFragment
import java.io.IOException
import java.net.SocketTimeoutException


class MainPageActivity : AppCompatActivity() {

//    var groupProfileList = arrayListOf<profile_list>(
//        profile_list("Jaeyojun","handsome", "퇴근", "profile_photo_example",),
//        profile_list("Jooypark","beautiful", "퇴근", "profile_photo_example" ),
//        profile_list("jaju","graphics master", "개포 c2r5s6", "profile_photo_example"),
//    )
//
//
//    var friendProfileList = arrayListOf<profile_list>(
//        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
//        profile_list("Jaeyojun","handsome", "퇴근", "profile_photo_example",),
//        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
//        profile_list("Jooypark","beautiful", "개포 c2r5s6", "profile_photo_example" ),
//        profile_list("jaju","graphics master", "퇴근", "profile_photo_example"),
//    )
//
//    var isFilterChecked = false
//    var isGroupListVisible = false
//    var isFriendListVisible = true


    lateinit var binding: ActivityMainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        /*에시 버튼*/
        val myButton = findViewById<Button>(R.id.examplebutton)
        myButton.setOnClickListener {
            try {
                // Action perform when the user clicks on the button.
                Toast.makeText(this@MainPageActivity, "You clicked me.", Toast.LENGTH_SHORT).show()

                // 여기에 예외가 발생할 수 있는 코드 추가
                // 예를 들어, 아래 코드는 인위적인 ArithmeticException을 발생시킵니다.

            } catch (e: Exception) {
                // 예외가 발생하면 해당 예외를 처리하는 부분
                Toast.makeText(
                    this@MainPageActivity,
                    "Exception occurred: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //set listener
//        myButton.setOnClickListener {
//            //Action perform when the user clicks on the button.
//            Toast.makeText(this@MainPageActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
//        }
//
//        val examplebut : Button = this.findViewById(R.id.examplebutton)
//        examplebut.setOnClickListener {
//            Log.d("MainActivity", "설정 버튼 클릭됨")
//            Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//
//        }

        /* header의 환경 설정 버튼을 눌렀을 때 */
        val settingButton: ImageButton = this.findViewById(R.id.setting_button)
        settingButton.setOnClickListener {
            Log.d("MainActivity", "설정 버튼 클릭됨")
            try {
                val intent = Intent(this, SettingPage::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }

        }

        //12_18 사용자 프로필 가져오기
        val intraId = 6 // Replace with the actual intraId
        Log.e("check", "hello")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getMember(intraId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful)
                    {
                        Log.e("check", "fuck")
                        val member = response.body()
                        // TODO: Handle the member data
                        if (member != null) {
                            val intraIdTextView =
                                findViewById<TextView>(R.id.intra_id) // XML에서 TextView 식별
                            intraIdTextView.text =
                                member.intraName // member에서 Intra ID를 가져와 TextView에 설정
                            val comment = findViewById<TextView>(R.id.Comment)
                            comment.text = member.comment
                            val location = findViewById<TextView>(R.id.location_info)
                            location.text = member.location
                            val main_image = findViewById<CircleImageView>(R.id.profile_photo)
                            val imageUrl = member.image
                            Glide.with(this@MainPageActivity)
                                .load(imageUrl)
                                .apply(RequestOptions().circleCrop()) // CircleImageView를 사용하므로 원형으로 자르기 위해 circleCrop을 적용합니다.
                                .into(main_image)
                        }
                        else
                        {
                            Log.e("check", "fuck2")
                            throw IOException("Network Error Occurred")
                        }
                    }
                    else
                    {
                        Log.e("check", "fuck3")
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = response.message() // HTTP 에러 메시지
                        val errorCode = response.code() // HTTP 에러 코드

                        val detailedErrorMessage =
                            "Failed to get member. Error code: $errorCode, Message: $errorMessage, Error Body: $errorBody"
                        Log.e("API Error", detailedErrorMessage)
//                    Log.e("API Error", "Failed to get member: ${response.errorBody()}")
                    }
                }
            } catch (e: IOException) {
                Log.e("check", "fuck4")
                Log.e("Network Error", "IOException: ${e.message}")

            } catch (e: SocketTimeoutException) {
                Log.e("check", "fuck5")
                Log.e("Network Error", "SocketTimeoutException: ${e.message}")
            }
        }


//        fun getDeviceScreenSize(context: Context): Pair<Int, Int> {
//            val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
//            val display = windowManager.defaultDisplay
//
//            val metrics = DisplayMetrics()
//            display.getMetrics(metrics)
//
//            val widthPixels = metrics.widthPixels
//            val heightPixels = metrics.heightPixels
//
//            return Pair(widthPixels, heightPixels)
//        }
//
//        val screenSize = getDeviceScreenSize(this)
//        val screenWidth = screenSize.first
//        val screenHeight = screenSize.second
//
//// 너비와 높이 출력
//        Log.d("ScreenSize", "Width: $screenWidth pixels, Height: $screenHeight pixels")




        /*새 그룹 기능 구현*/
        val newGroupButton: Button = this.findViewById(R.id.newGroupButton)

        newGroupButton.setOnClickListener {
            Log.e("clickGroupButton", "fuck")
            Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.new_group_popup)

            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val editText = dialog.findViewById<EditText>(R.id.input)
            val btnCancel = dialog.findViewById<Button>(R.id.cancel)
            val btnSubmit = dialog.findViewById<Button>(R.id.submit)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSubmit.setOnClickListener {
                val userInput = editText.text.toString()
                val intent = Intent(this, CreateGroupActivity::class.java)
                intent.putExtra("userInputKey", userInput)
                startActivity(intent)
            }
            dialog.show()
        }

        /* footer의 홈버튼과 검색 버튼 기능 구현 */
        val searchButton: ImageButton = this.findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            try {
                //Toast.makeText(this, "버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SearchPage::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val homeButton: ImageButton = this.findViewById(R.id.home_button)

        homeButton.setOnClickListener {
            try {
                if (this::class.java != MainPageActivity::class.java) {
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else
                    Toast.makeText(this, "이미 로그인 페이지에 있습니다!.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        /* rcyclerView 기능 구현*/
        //12_18 리사이클 뷰 넣어주기
        //api 예제 모든 데이터르 들고와보자
//        val listView: ListView = findViewById(R.id.listView) // 리스트뷰의 ID를 적절하게 변경해야 합니다.
//        val adapter = ArrayAdapter<Member>(this, android.R.layout.simple_list_item_1)
//        listView.adapter = adapter
//
//        getMemberService.getMembers().enqueue(object : Callback<List<Member>> {
//            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
//                if (response.isSuccessful) {
//                    val members = response.body() ?: return
//                    adapter.clear()
//                    adapter.addAll(members)
//                } else {
//                    Log.e("API Error", "Failed to get members: ${response.errorBody()}")
//                    // 서버에서 에러 응답을 받았을 때의 처리를 작성합니다.
//                }
//            }
//            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
//                Log.e("API Error", "Failed to get members", t)
//                // 네트워크 요청이 실패했을 때의 처리를 작성합니다.
//            }})

//-------------------------------------
//        여기가 그룹임
//        val recyclerView: RecyclerView = findViewById(R.id.all_group)
//        val adapter = RecyclerViewAdapterAll(this)
//        recyclerView.adapter = adapter

//        val recyclerView: RecyclerView = findViewById(R.id.all_group)
//        val adapter = RecyclerViewAdapterAll(this)
//        recyclerView.adapter = adapter

        //------



        binding = ActivityMainPageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(binding.container.id, MainFragment()).commit()

        //이 부분이 새 그룹 버튼 눌렀을 때 나오는 팝업



//        val recyclerView: RecyclerView = findViewById(R.id.all_group)
//        val adapter = RecyclerViewAdapterAll(this)
//
//        // LinearLayoutManager를 설정해야 합니다.
//        val layoutManager = LinearLayoutManager(this)
//        recyclerView.layoutManager = layoutManager
//
//        recyclerView.adapter = adapter
        //----------

//        recyclerView.layoutManager = LinearLayoutManager(this)


//        val recyclerView: RecyclerView = findViewById(R.id.all_group)
//        val layoutManager = LinearLayoutManager(this)
//        recyclerView.layoutManager = layoutManager
//
//// Adapter 초기화 및 RecyclerView에 연결
//        val groupProfileList = arrayListOf<profile_list>(
//            profile_list("Jaeyojun","handsome", "퇴근", "profile_photo_example"),
//            profile_list("Jooypark","beautiful", "퇴근", "profile_photo_example"),
//            // ... (원하는 데이터 계속 추가)
//        )
//
//        val adapter = RecyclerViewAdapterAll(this, groupProfileList, isFilterChecked)
//        recyclerView.adapter = adapter

        //----------------------------------------
//        val groupRecyclerView: RecyclerView = findViewById<RecyclerView?>(R.id.group_list)
//        val groupRecyclerViewAdapter = RecyclerViewAdapter(this, groupProfileList, isFilterChecked)
//        groupRecyclerView.layoutManager = LinearLayoutManager(this)
//        groupRecyclerView.adapter = groupRecyclerViewAdapter


//        val friendRecyclerView: RecyclerView = findViewById<RecyclerView?>(R.id.friend_list)
//        val friendRecyclerViewAdapter = RecyclerViewAdapter(this, friendProfileList, isFilterChecked)
//        friendRecyclerView.layoutManager = LinearLayoutManager(this)
//        friendRecyclerView.adapter = friendRecyclerViewAdapter

//        val checkBox: CheckBox = findViewById(R.id.checkBox)
//        checkBox.setOnCheckedChangeListener { _, isChecked ->
//            isFilterChecked = isChecked
//            groupRecyclerView.adapter = RecyclerViewAdapter(this, groupProfileList, isFilterChecked)
////            friendRecyclerView.adapter = RecyclerViewAdapter(this, friendProfileList, isFilterChecked)
//        }
//
//        val groupEditButton: ImageButton = findViewById(R.id.group_edit)
//        groupEditButton.setOnClickListener {
//            val groupDialog = GroupDialog(this)
//            groupDialog.showGroupDialog("그룹1")
//        }
//
//        val groupToggleButton: ImageButton = findViewById(R.id.group_toggle)
//        groupToggleButton.setOnClickListener {
//            if (isGroupListVisible) {
//                groupRecyclerView.visibility = View.GONE
//            } else {
//                groupRecyclerView.visibility = View.VISIBLE
//            }
//            isGroupListVisible = !isGroupListVisible
//        }

//        val friendToggleButton: ImageButton = findViewById(R.id.friend_toggle)
//        friendToggleButton.setOnClickListener {
//            if (isFriendListVisible) {
//                friendRecyclerView.visibility = View.GONE
//            } else {
//                friendRecyclerView.visibility = View.VISIBLE
//            }
//            isFriendListVisible = !isFriendListVisible
//        }

        /* 그룹 설정 기능 */
    }

}