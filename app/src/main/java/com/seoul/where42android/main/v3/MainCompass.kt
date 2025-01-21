package com.seoul.where42android.main.v3

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.seoul.where42android.Base_url_api_Retrofit.AddMembersRequest
import com.seoul.where42android.Base_url_api_Retrofit.CompassApi
import com.seoul.where42android.Base_url_api_Retrofit.CompassMember
import com.seoul.where42android.Base_url_api_Retrofit.GroupAddMemberlist
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.R
import com.seoul.where42android.adapter.adjustBackgroundSizeWithPadding
import com.seoul.where42android.databinding.ActivityCompassBinding
import com.seoul.where42android.fragment.C1C5Fragment
import com.seoul.where42android.fragment.C2C6Fragment
import com.seoul.where42android.fragment.CX1Fragment
import com.seoul.where42android.fragment.CX2Fragment
import com.seoul.where42android.main.MainPageActivity
import com.seoul.where42android.utils.ApiUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainCompass : AppCompatActivity() {
    private lateinit var binding: ActivityCompassBinding
    private var defaultgroupId : Int = -1
    private var previousCluster: String? = null // 이전 클러스터 값을 저장
    private var addFriendList =  mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        defaultgroupId = intent.getStringExtra("DEFAULT_GROUP")?.toInt() ?: -1
        // 내 친구가 있는 지 보기 위해 api 호출

        val rooms = listOf("C1", "C2", "C5", "C6", "CX1", "CX2")
        val adapter = ArrayAdapter(
            this,
            R.layout.custom_spinner_item, // 선택된 아이템의 레이아웃
            rooms
        )
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item) // 드롭다운 항목의 레이아웃
        binding.roomSelector.adapter = adapter


        // Spinner 선택 이벤트
        binding.roomSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedRoom = rooms[position]
                if (selectedRoom == previousCluster) {
                    // 이전에 선택된 값과 동일하면 호출하지 않음
                    return
                }
                previousCluster = selectedRoom // 이전 클러스터 업데이트

                when (selectedRoom) {
                    "C1" -> {
                        loadFragment(C1C5Fragment())
                        fetchClusterData("c1")
                    }
                    "C2" -> {
                        loadFragment(C2C6Fragment())
                        fetchClusterData("c2")
                    }
                    "C5" -> {
                        loadFragment(C1C5Fragment())
                        fetchClusterData("c5")
                    }
                    "C6" -> {
                        loadFragment(C2C6Fragment())
                        fetchClusterData("c6")
                    }
                    "CX1" -> {
                        loadFragment(CX1Fragment())
                        fetchClusterData("cx1")
                    }
                    "CX2" -> {
                        loadFragment(CX2Fragment())
                        fetchClusterData("cx2")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 기본 Fragment 설정
        if (savedInstanceState == null) {
            loadFragment(C1C5Fragment()) // 기본값으로 C1Fragment를 로드
            fetchClusterData("c1")
            previousCluster = "C1" // 초기 설정값 저장
        }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun fetchClusterData(cluster: String) {
        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                ApiUtils.performApiRequest(this@MainCompass) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(CompassApi::class.java)
                    retrofitAPI.getCompassMember(cluster)
                }
            }

            response?.body()?.members?.forEach { member ->
                // row와 seat 값을 기반으로 ImageView ID 생성
                val imageViewId = resources.getIdentifier(
                    "seat_r${member.row}_${member.seat}",
                    "id",
                    packageName
                )

                // ImageView 가져오기
                val imageView = findViewById<ImageView>(imageViewId)
                imageView?.let {
                    // Glide로 이미지를 항상 설정
                    updateSeatImage(it, member) // 상태 업데이트
                    it.setOnClickListener {
                        showMemberDetail(member, this@MainCompass) // 클릭 시 상세 프로필 다이얼로그 표시
                    }
                }
            } ?: run {
                Log.d("Compass", "Error: Response is null or empty")
            }
        }
    }

    private fun updateSeatImage(imageView: ImageView, member: CompassMember) {
        // Glide로 이미지를 항상 설정
        Glide.with(this@MainCompass)
            .load(member.image) // API에서 받은 이미지 URL
            .error(R.drawable.nointraimage) // 오류 시 이미지
            .into(imageView)

        if (member.isFriend) {
            val strokeWidth = 10 // 테두리 두께
            val strokeColor = Color.parseColor("#95FFB5B5") // 테두리 색상
            val drawable = GradientDrawable().apply {
                setStroke(strokeWidth, strokeColor) // 테두리 설정
                setColor(Color.TRANSPARENT) // 배경 투명
            }
            // 테두리를 이미지 위에 오버레이
            imageView.foreground = drawable
        } else {
            // 친구가 아닐 경우 테두리 제거
            imageView.foreground = null
        }
    }


    private fun showMemberDetail(member: CompassMember, context : Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.activity_compass_member_deatil)

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        // 다이얼로그 뷰에 데이터 바인딩
        val memberImage = dialog.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.member_image)
        val memberName = dialog.findViewById<TextView>(R.id.member_name)
        val memberCluster = dialog.findViewById<TextView>(R.id.member_cluster)
        val addFriendButton = dialog.findViewById<View>(R.id.add_friend) // add_friend 버튼 ID 가져오기
        if (member.isFriend == true) {
            addFriendButton.visibility = View.GONE
        } else {
            addFriendButton.visibility = View.VISIBLE
        }

        updateFriendStatusUI(member, addFriendButton, memberImage)

        Glide.with(this)
            .load(member.image)
            .error(R.drawable.nointraimage)
            .into(memberImage)

        memberName.text = member.intraName
        memberCluster.text = "${member.cluster}r${member.row}s${member.seat}"
        val leftPadding = 20 // 왼쪽 여백 값
        val rightPadding = 20 // 오른쪽 여백 값
        memberCluster.setPadding(leftPadding, 0, rightPadding, 0)
        adjustBackgroundSizeWithPadding(memberCluster, leftPadding, rightPadding)
        // add_friend 버튼 클릭 리스너 추가
        addFriendButton.setOnClickListener {
            addFriendList.add(member.intraId)
            val addMemberRequestData = AddMembersRequest(defaultgroupId, addFriendList)
            lifecycleScope.launch {
                // ApiUtils를 사용하여 API 요청 수행
                val response = ApiUtils.performApiRequest(context) { accessToken ->
                    val retrofitAPI2 = RetrofitConnection.getInstance(accessToken)
                        .create(GroupAddMemberlist::class.java)
                    retrofitAPI2.addMembersToGroup(addMemberRequestData)
                }

                if (response != null && response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        // 성공적으로 응답 데이터를 처리
                        member.isFriend = true
                        addFriendList.clear()
                        updateFriendStatusUI(member, addFriendButton, memberImage)

                        // 메인 화면의 ImageView 업데이트
                        val imageViewId = resources.getIdentifier(
                            "seat_r${member.row}_${member.seat}",
                            "id",
                            packageName
                        )
                        val imageView = findViewById<ImageView>(imageViewId)
                        imageView?.let {
                            updateSeatImage(it, member) // 상태 반영
                        }
                        dialog.dismiss()

                        val intent = Intent(this@MainCompass, MainPageActivity::class.java)
                        finish() // 현재 Activity 종료
                        startActivity(intent)

                    } else {
//                        Toast.makeText(context, "${member.intraName} ${member.intraId}님과 친구가 되었습니다!", Toast.LENGTH_SHORT).show()
                        Toast.makeText(
                            context,
                            "API 요청이 실패했습니다: ${response?.code() ?: "응답 없음"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // 요청 실패
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "API 요청이 실패했습니다: ${response?.code() ?: "응답 없음"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun updateFriendStatusUI(
        member: CompassMember,
        addFriendButton: View,
        memberImage: de.hdodenhof.circleimageview.CircleImageView
    ) {
        if (member.isFriend) {
            addFriendButton.visibility = View.GONE
        } else {
            addFriendButton.visibility = View.VISIBLE
            memberImage.borderColor = Color.WHITE
            memberImage.borderWidth = resources.getDimensionPixelSize(R.dimen.activity_profile_popup_emoji_civ_border_width)
        }
    }

}
