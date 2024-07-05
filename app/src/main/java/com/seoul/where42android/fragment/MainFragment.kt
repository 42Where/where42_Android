package com.seoul.where42android.fragment

import SharedViewModel_GroupsMembersList
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.seoul.where42android.Base_url_api_Retrofit.GroupMemberListService
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.R
import com.seoul.where42android.adapter.OutRecyclerViewAdapter
import com.seoul.where42android.databinding.ActivityMainPageFragmentBinding
import com.seoul.where42android.main.friendListObject
import com.seoul.where42android.model.RecyclerInViewModel
import com.seoul.where42android.model.RecyclerOutViewModel

object GroupsList {
    var newItemList = mutableListOf<RecyclerOutViewModel>()
    var wentToWorkFriend = false

    fun setToggleStat(setGroupsList: MutableList<RecyclerOutViewModel>) {
        newItemList = setGroupsList
    }

    fun getGroupsList(): MutableList<RecyclerOutViewModel> {
        return newItemList
    }

//    fun clearToggleStat() {
//        togglestat.clear()
//    }
}


class MainFragment(receivedToken : String, IntraId : Int, Context : Context) : Fragment() {
    private lateinit var binding: ActivityMainPageFragmentBinding
    private val token = receivedToken
    private val emptyItemList = mutableListOf<RecyclerOutViewModel>()
    private val intraid = IntraId
    private val Context = Context

    private lateinit var sharedViewModel: SharedViewModel_GroupsMembersList
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityMainPageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val availableButton: Button? = activity?.findViewById(R.id.available)
        val checkBox = activity?.findViewById<CheckBox>(R.id.checkBox)

        //출근한 친구만 보기 라는 버튼을 눌렀을 때 밑 checkBox가 체크로 변함
        availableButton?.setOnClickListener {
            checkBox?.isChecked = !checkBox?.isChecked!!
        }

        checkBox?.setOnCheckedChangeListener { _, isChecked ->
//            Log.d("checkbox", "isChecked : ${isChecked}")
            val adapter = binding.outRecyclerview.adapter as? OutRecyclerViewAdapter
            adapter?.setShowNonLeaveMembersOnly(isChecked)

        }
        // Create ViewModel instance
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel_GroupsMembersList::class.java)
//        viewModel = ViewModelProvider(this).get(GroupsMembersList::class.java)

        // Observe changes in LiveData
        sharedViewModel.groupsMembersListLiveData.observe(viewLifecycleOwner) { groupList ->
//            Log.d("datachange", "datachange1")
            if (checkBox?.isChecked == true)
            {
                checkBox?.isChecked = false
            }
            if (groupList.isNotEmpty()) {
//                Log.d("checkfreind", "checkfriend")
//                Log.d("boolean_check", " checkBox?.isChecked  : ${checkBox?.isChecked }")
                val itemList = mutableListOf<RecyclerOutViewModel>()
                groupList.forEach { groupDetail ->
                    var count = 0
                    val innerItemList = mutableListOf<RecyclerInViewModel>()
                    groupDetail.members.forEach { intraId ->
                        val recyclerInViewModel = RecyclerInViewModel(
                            emoji = intraId.image ?: "",
                            location = intraId.location ?: "",
                            comment = intraId.comment ?: "",
                            intra_name = intraId.intraName ?: "",
                            included_group = groupDetail.groupId ?: -1,
                            intra_id = intraId.intraId ?: -1,
                        )
                        if (recyclerInViewModel.location != "퇴근")
                        {
                            count++
                        }
//                        Log.d("groupId", "id : ${groupDetail.groupId} location :  ${intraId.location}")
                        innerItemList.add(recyclerInViewModel)
                    }
                    val recyclerOutViewModel = RecyclerOutViewModel(
                        title = groupDetail.groupName ?: "",
                        innerItemList,
                        groupId = groupDetail.groupId ?: 0,
                        viewgroup = groupDetail.toggle,
                        comeCluster = count
                    )
//                    Log.d("title_check", "title_check : ${recyclerOutViewModel.title}")
                    if (!friendListObject.searchGroupName(recyclerOutViewModel.title))
                    {
                        friendListObject.groupAdd(recyclerOutViewModel.title)
                    }
                    itemList.add(recyclerOutViewModel)
                }

                val lastIndex = itemList.lastIndex
//                val lastItem = itemList[lastIndex]
                val defaultGroupIndex = itemList?.indexOfFirst { it.title == "친구 목록"}
                if (lastIndex == defaultGroupIndex) {
//                    Log.d("LastItemCheck", "친구 목록이 마지막에 있습니다.")
                } else {
                    val defaultFriendListremove = itemList?.firstOrNull{it.title == "친구 목록"}
                    if (defaultFriendListremove != null) {
                        if (defaultGroupIndex != null) {
                            itemList.removeAt(defaultGroupIndex)
                            itemList.add(defaultFriendListremove)
                        }

                    }
//                    Log.d("LastItemCheck", "친구 목록이 마지막에 없습니다.")
                }



//                Log.d("DiffUtil", "here1")
                // Set up RecyclerView Adapter
                val adapter = OutRecyclerViewAdapter(requireContext(), itemList, sharedViewModel)
                binding.outRecyclerview.adapter = adapter
                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
                GroupsList.setToggleStat(itemList)
                binding.progressBar.visibility = View.GONE
            }
            else {
                // Handle empty or null data
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList, sharedViewModel)
                binding.outRecyclerview.adapter = adapter
                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            }
        }

        sharedViewModel.groupsMembersListLiveData.observeForever { groupList ->
//            Log.d("datachange", "datachange2")
            // 데이터가 변경될 때 실행되는 코드
        }


        // Call function to fetch data
//        val intraId = 6 // Replace this with your memberId value
        sharedViewModel.getGroupMemberList(intraid, token, Context)
    }
}
