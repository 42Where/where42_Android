package com.example.where42android

import android.app.Dialog
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
import com.example.where42android.Base_url_api_Retrofit.ApiObject.service
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.where42android.Base_url_api_Retrofit.Member
import com.example.where42android.Base_url_api_Retrofit.NewGroup
import com.example.where42android.Base_url_api_Retrofit.NewGroupRequest
import com.example.where42android.Base_url_api_Retrofit.NewGroupResponses
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.main.MainSettingPage

import com.example.where42android.databinding.ActivityMainPageBinding
import com.example.where42android.fragment.MainFragment
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
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        progressBar = binding.myProgressBar // 프로그레스 바 찾기
        progressBar.visibility = View.VISIBLE



//        supportFragmentManager.beginTransaction()
//            .replace(binding.container.id, fragment)
//            .commit()
        supportFragmentManager.beginTransaction().replace(binding.container.id, MainFragment()).commit()
        //2. group list을 보여주기 위해 binding으로 MainFragment 설정




        //2. 12_18 api를 통해 사용자 프로필 가져오기
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
                            //전역에 프로필 넣어주자
                            profile = member
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

        val locationTextView = findViewById<TextView>(R.id.location_info)
        locationTextView.post {
            val maxWidth = locationTextView.width // TextView의 최대 너비
            val textPaint = locationTextView.paint // TextView의 Paint 객체
            val text = locationTextView.text.toString() // TextView에 표시되는 텍스트

            val textWidth = textPaint.measureText(text) // 텍스트의 폭 계산
            if (textWidth > maxWidth) {
                locationTextView.layoutParams.width = maxWidth // TextView의 너비를 최대 너비로 설정
            }
        }


        //1. header의 환경 설정 버튼을 눌렀을 때 -> SettingPage.kt로 가게 하기
        val headerBinding = binding.header // Change to your actual ID for the included header
        val settingButton: ImageButton = headerBinding.settingButton

        settingButton.setOnClickListener {
            try {
                val intent = Intent(this, MainSettingPage::class.java)
                //값 넘겨주기
                intent.putExtra("PROFILE_DATA", profile.intraId)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        //3. footer의 홈버튼과 검색 버튼 기능 구현
        val footerBinding = binding.footer
        val searchButton : ImageButton = footerBinding.searchButton

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

        //수정 전 코드
        //        val searchButton: ImageButton = this.findViewById(R.id.search_button)
        //
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

        val homeButton : ImageButton = footerBinding.homeButton

        homeButton.setOnClickListener {
            try {
                if (this::class.java != MainPageActivity::class.java) {
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else
                    Toast.makeText(this, "이미 로그인 페이지에 있습니다!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //수정 전 코드
//        val homeButton: ImageButton = this.findViewById(R.id.home_button)
//
//        homeButton.setOnClickListener {
//            try {
//                if (this::class.java != MainPageActivity::class.java) {
//                    val intent = Intent(this, MainPageActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else
//                    Toast.makeText(this, "이미 로그인 페이지에 있습니다!.", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }


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

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnSubmit.setOnClickListener {
                //새그룹 버튼 확인 누르면 api 요청
                val retrofitAPI = RetrofitConnection.getInstance().create(NewGroup::class.java)
                //groupname, intraid 필요
                val groupname : String = editText.text.toString()
                //intraid 불러오자
                val intra_id = profile.intraId

                //JSON 만들어주기
                //NewGroup @POST("v3/group")
                val newGroupRequest = NewGroupRequest(groupname, intra_id)
                val call = retrofitAPI.newGroup(newGroupRequest)

                //api 혼합해서 써봄 ㅋ
                call.enqueue(object : Callback<NewGroupResponses> {
                    override fun onResponse(
                        call: Call<NewGroupResponses>,
                        response: Response<NewGroupResponses>
                    ) {
                        if (response.isSuccessful) {
                            val newGroupResponse  = response.body()
                            val intent = Intent(this@MainPageActivity, CreateGroupActivity::class.java)
                            //default 아이디 넣어주어야함.
                            Log.d("check_newGroup", "${newGroupResponse?.groupId}")
                            intent.putExtra("newgroupIdKey", newGroupResponse?.groupId) // groupIdKey는 key값, newGroupResponse.groupId는 전달할 값
                            intent.putExtra("groupIdKey", profile?.defaultGroupId) // groupIdKey는 key값, newGroupResponse.groupId는 전달할 값
                            startActivity(intent)
                            // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
                        } else {
                            // API 호출에 실패한 경우
                            Log.e("DELETE_ERROR", "Failed to delete group. Error code: ${response.code()}")
                            // 실패 처리 로직을 수행하세요.

                        }
                    }

                    override fun onFailure(call: Call<NewGroupResponses>, t: Throwable) {

                        Log.e("CREATE_ERROR", "Network error occurred. Message: ${t.message}")
                    }
                })
//                val userInput = editText.text.toString()
//                val intent = Intent(this, CreateGroupActivity::class.java)
//                intent.putExtra("userInputKey", userInput)
//                startActivity(intent)
            }
            dialog.show()
        }

        //수정 전 코드
//        val newGroupButton: Button = this.findViewById(R.id.newGroupButton)
//
//        newGroupButton.setOnClickListener {
//            Log.e("clickGroupButton", "fuck")
//            Toast.makeText(this, "환경 세팅 작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            val dialog = Dialog(this)
//            dialog.setContentView(R.layout.new_group_popup)
//
//            dialog.setCanceledOnTouchOutside(true)
//            dialog.setCancelable(true)
//            dialog.window?.setGravity(Gravity.CENTER)
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//            val editText = dialog.findViewById<EditText>(R.id.input)
//            val btnCancel = dialog.findViewById<Button>(R.id.cancel)
//            val btnSubmit = dialog.findViewById<Button>(R.id.submit)
//
//            btnCancel.setOnClickListener {
//                dialog.dismiss()
//            }
//            btnSubmit.setOnClickListener {
//                val userInput = editText.text.toString()
//                val intent = Intent(this, CreateGroupActivity::class.java)
//                intent.putExtra("userInputKey", userInput)
//                startActivity(intent)
//            }
//            dialog.show()
//        }




//        val recyclerView: RecyclerView = findViewById(R.id.all_group)
//        val adapter = RecyclerViewAdapterAll(this)
//
//        // LinearLayoutManager를 설정해야 합니다.
//        val layoutManager = LinearLayoutManager(this)
//        recyclerView.layoutManager = layoutManager
//
//        recyclerView.adapter = adapter


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


        //이 함수는 왜 필요한거지..?
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


        //-------------------------------------
//        여기가 그룹임
//        val recyclerView: RecyclerView = findViewById(R.id.all_group)
//        val adapter = RecyclerViewAdapterAll(this)
//        recyclerView.adapter = adapter

//        val recyclerView: RecyclerView = findViewById(R.id.all_group)
//        val adapter = RecyclerViewAdapterAll(this)
//        recyclerView.adapter = adapter


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

        progressBar.visibility = View.GONE // ProgressBar 숨기기
    }

}