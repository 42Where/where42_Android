package com.seoul.where42android.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.SearchView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seoul.where42android.Base_url_api_Retrofit.AddMembersRequest
import com.seoul.where42android.Base_url_api_Retrofit.GroupAddMemberlist
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.R
import com.seoul.where42android.databinding.ActivitySearchPageBinding
import com.seoul.where42android.fragment.MainSearchFragment
import com.seoul.where42android.utils.ApiUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object intraNameObject {
    private var Name = ""
    private var checkFriendList =  mutableListOf<Int>()
    fun setName(setName: String) {
        Name = setName
    }

    fun getName(): String {
        return Name
    }
    fun clearName() {
        Name = ""
    }



    fun sizeFriendList() : Int
    {
        return checkFriendList.size
    }


    fun setcheckFriendList(intraId: Int) {
        checkFriendList.add(intraId)
    }

    fun removecheckFriendList(intraId: Int) {
        checkFriendList.remove(intraId)
    }


    fun getcheckFriendList(): MutableList<Int> {
        return checkFriendList
    }

    fun clearIntList() {
        checkFriendList.clear()
    }
}

class MainSearchPage : AppCompatActivity() {

    private lateinit var searchView: SearchView
    lateinit var binding: ActivitySearchPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_page)
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intraNameObject.sizeFriendList() == 0)
        {
            binding.addMember.visibility = View.GONE
        }


        //친구 추가 버튼
        // 친구 추가 버튼
        val addFriendButton = binding.addMember
        addFriendButton.setOnClickListener {
            val userSetting = UserSettings.getInstance()
            val addMemberList = intraNameObject.getcheckFriendList()
            val groupIdMember = AddMembersRequest(userSetting.defaultGroup, addMemberList)

            // CoroutineScope 선언
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // ApiUtils를 사용하여 API 요청 수행
                    val response = ApiUtils.performApiRequest(this@MainSearchPage) { accessToken ->
                        val retrofitAPI2 = RetrofitConnection.getInstance(accessToken)
                            .create(GroupAddMemberlist::class.java)
                        retrofitAPI2.addMembersToGroup(groupIdMember)
                    }

                    if (response != null && response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            // 성공적으로 응답 데이터를 처리
                            intraNameObject.clearName()
                            intraNameObject.clearIntList()
                            responseBody.forEach { item ->
                                friendListObject.addItem(item.intraId, item.intraName)
                            }
                            // UI 업데이트 및 페이지 이동
                            withContext(Dispatchers.Main) {
                                val intent = Intent(this@MainSearchPage, MainPageActivity::class.java)
                                finish() // 현재 Activity 종료
                                startActivity(intent)
                            }
                        } else {
                            // 응답 데이터가 없는 경우
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainSearchPage,
                                    "응답 데이터가 없습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        // 요청 실패
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainSearchPage,
                                "API 요청이 실패했습니다: ${response?.code() ?: "응답 없음"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    // 예외 처리
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainSearchPage,
                            "친구 추가 중 오류가 발생했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.e("AddFriendError", "Error adding friends", e)
                }
            }
        }

        //헤더
        val headerBinding = binding.header
        val settingbutton : ImageButton = headerBinding.settingButton
        settingbutton.visibility = View.GONE

        //footer
        val footerBinding = binding.footer
        val searchButton : ImageButton = footerBinding.searchButton
        searchButton.visibility = View.GONE


        val homeButton: ImageButton = footerBinding.homeButton
        homeButton.setOnClickListener {
            try {
                intraNameObject.clearName()
                intraNameObject.clearIntList()
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        /* 검색할 떄의 기능 구현*/
        searchView = binding.searchView
        // 검색 기능 구현
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                Log.d("onQueryName", "query out : ${query}")

                if (!query.isNullOrEmpty() && query.length > 1) {
                    if (isValidQuery(query))
                    {
                        val Name = intraNameObject.getName()
                        if ( Name != query) {
                            val name = query
//                            Log.d("onQueryName", "name : ${name}")
//                            supportFragmentManager.beginTransaction()
//                                .replace(binding.container.id, MainSearchFragment(name)).commit()


                            val mainserachFragment = MainSearchFragment.newInstance(name)
                            supportFragmentManager.beginTransaction()
                                .replace(binding.container.id, mainserachFragment)
                                .commit()
                            intraNameObject.setName(query)
                        }
                    }
                    else
                    {
                        val isinValidQuery = Dialog(this@MainSearchPage)
                        isinValidQuery.setContentView(R.layout.activity_editstatus_popup)

                        val cancel = isinValidQuery.findViewById<Button>(R.id.cancel)
                        cancel.visibility = View.GONE

                        val title = isinValidQuery.findViewById<TextView>(R.id.title)
                        title.text = "올바른 이름을 입력해주세요."

                        isinValidQuery.window?.setGravity(Gravity.CENTER)
                        isinValidQuery.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        val submit = isinValidQuery.findViewById<Button>(R.id.submit)
                        submit.setOnClickListener {
                            isinValidQuery.dismiss()
                        }
                        isinValidQuery.show()
                    }
                }
                else if (query.isNullOrEmpty())
                {

                }
                else
                {
                    val noEditDefaultDialog = Dialog(this@MainSearchPage)
                    noEditDefaultDialog.setContentView(R.layout.activity_editstatus_popup)

                    val cancel = noEditDefaultDialog.findViewById<Button>(R.id.cancel)
                    cancel.visibility = View.GONE

                    val title = noEditDefaultDialog.findViewById<TextView>(R.id.title)
                    title.text = "이름은 2글자 이상 입력해주세요."

                    noEditDefaultDialog.window?.setGravity(Gravity.CENTER)
                    noEditDefaultDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val submit = noEditDefaultDialog.findViewById<Button>(R.id.submit)
                    submit.setOnClickListener {
                        noEditDefaultDialog.dismiss()
                    }
                    noEditDefaultDialog.show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


    }
    private fun isValidQuery(query: String): Boolean {
        val regex = Regex("[a-zA-Z0-9_-]+")
        return query.matches(regex)
    }

    fun onCheckBoxClicked(checked: Boolean, position: Int, intraId:Int) {

        if (checked)
        {
            var intraObject = intraNameObject.getcheckFriendList()
            var flag = false
            for (objectintraId in intraObject)
            {
                if (objectintraId == intraId)
                {
                    flag = true
                }
            }
//            Log.d("intraNameObject", "flag : ${flag}, intraId : ${intraId}")

            // 체크박스가 체크되었을 때 하단에 버튼 표시
            // 버튼을 추가하고 필요한 동작 수행
            if (flag == false) 
            {
//                Log.d("intraNameObject", "flag : ${flag}, intraId : ${intraId}")
                intraNameObject.setcheckFriendList(intraId)
            }
            binding.addMember.visibility = View.VISIBLE
            binding.footer.guide.visibility = View.GONE
//            Log.d("checkBoxClicked", " checked : ${checked}, position : ${position}, intraId : ${intraId}")
        } else {
            // 체크박스가 해제되었을 때 하단 버튼 숨김
            // 필요한 동작 수행
            intraNameObject.removecheckFriendList(intraId)
            if (intraNameObject.sizeFriendList() == 0)
            {
                binding.footer.guide.visibility = View.VISIBLE
                binding.addMember.visibility = View.GONE
            }
        }
    }
}
