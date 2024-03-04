package com.example.where42android.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.where42android.Base_url_api_Retrofit.AddMembersRequest
import com.example.where42android.Base_url_api_Retrofit.GroupAddMemberlist
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.Base_url_api_Retrofit.addMembersResponse
import com.example.where42android.R
import com.example.where42android.UserSettings
import com.example.where42android.databinding.ActivitySearchPageBinding
import com.example.where42android.fragment.MainSearchFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private lateinit var listView: ListView
//    private lateinit var adapter: ArrayAdapter<String>

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
        val addFriendButton = binding.addMember
        addFriendButton.setOnClickListener{
            val userSetting = UserSettings.getInstance()


            val retrofitAPI2 = RetrofitConnection.getInstance(userSetting.token)
                .create(GroupAddMemberlist::class.java)
            val add_member = intraNameObject.getcheckFriendList()
            val groupId_member = AddMembersRequest( userSetting.defaultGroup, add_member)

            retrofitAPI2.addMembersToGroup(groupId_member).enqueue(object :
                Callback<List<addMembersResponse.addMembersResponseItem>> {
                override fun onResponse(
                    call: Call<List<addMembersResponse.addMembersResponseItem>>,
                    response: Response<List<addMembersResponse.addMembersResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        Log.d("SearchPageAddFriend", "SearchPageAddFriend : ${response.body()}")
                        intraNameObject.clearName()
                        intraNameObject.clearIntList()
                        for (item in response.body()!!) {
                            friendListObject.addItem(item.intraId, item.intraName)
                        }
                        val intent = Intent(this@MainSearchPage, MainPageActivity::class.java)
                        finish() //인텐트 종료
                        startActivity(intent)
                    }
                    else
                    {

                    }
                }
                override fun onFailure(
                    call: Call<List<addMembersResponse.addMembersResponseItem>>,
                    t: Throwable
                ) {
                    Log.d("groupmemberadd_error", "onFailure API call failed.")
                    // API 요청 자체가 실패한 경우 처리
                }
            })
        }

        //헤더
        val headerBinding = binding.header
        val settingbutton : ImageButton = headerBinding.settingButton
        settingbutton.visibility = View.GONE

//        val homeButton: ImageButton = headerBinding.homeButton
//        homeButton.setOnClickListener {
//            try {
//                intraNameObject.clearName()
//                intraNameObject.clearIntList()
//                val intent = Intent(this, MainPageActivity::class.java)
//                startActivity(intent)
//                finish()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
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
                Log.d("onQueryName", "query out : ${query}")

                if (!query.isNullOrEmpty() && query.length < 10) {
                    if (isValidQuery(query))
                    {
                        val Name = intraNameObject.getName()
                        if ( Name != query) {
                            val name = query ?: ""
                            Log.d("onQueryName", "name : ${name}")
                            supportFragmentManager.beginTransaction()
                                .replace(binding.container.id, MainSearchFragment(name)).commit()
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
                    title.text = "이름은 9글자 이내로 작성해주세요."

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
            Log.d("intraNameObject", "flag : ${flag}, intraId : ${intraId}")

            // 체크박스가 체크되었을 때 하단에 버튼 표시
            // 버튼을 추가하고 필요한 동작 수행
            if (flag == false) 
            {
                Log.d("intraNameObject", "flag : ${flag}, intraId : ${intraId}")
                intraNameObject.setcheckFriendList(intraId)
            }
            binding.addMember.visibility = View.VISIBLE
            Log.d("checkBoxClicked", " checked : ${checked}, position : ${position}, intraId : ${intraId}")
        } else {
            // 체크박스가 해제되었을 때 하단 버튼 숨김
            // 필요한 동작 수행
            intraNameObject.removecheckFriendList(intraId)
            if (intraNameObject.sizeFriendList() == 0)
            {
                binding.addMember.visibility = View.GONE
            }
        }
    }
}
