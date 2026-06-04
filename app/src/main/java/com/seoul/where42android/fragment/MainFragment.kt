package com.seoul.where42android.fragment

import com.seoul.where42android.ViewModel.SharedViewModelGroupsMembers
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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


class MainFragment() : Fragment() {
    private lateinit var binding: ActivityMainPageFragmentBinding
    private val emptyItemList = mutableListOf<RecyclerOutViewModel>()
    private var intraId: Int = -1
    private var accesstoken : String = "notoken"
    private lateinit var context: Context

    private lateinit var sharedViewModel: SharedViewModelGroupsMembers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accesstoken = it.getString("TOKEN").toString()
            intraId = it.getInt("INTRA_ID")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    companion object {
        fun newInstance(receivedToken: String, intraId: Int): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            args.putString("TOKEN", receivedToken)
            args.putInt("INTRA_ID", intraId)
            fragment.arguments = args
            return fragment
        }
    }

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
//        availableButton?.setOnClickListener {
//            checkBox?.isChecked = !checkBox?.isChecked!!
//        }
        availableButton?.setOnClickListener {
            val cur = checkBox?.isChecked ?: false
            checkBox?.isChecked = !cur
        }

        checkBox?.setOnCheckedChangeListener { _, isChecked ->
//            Log.d("checkbox", "isChecked : ${isChecked}")
            val adapter = binding.outRecyclerview.adapter as? OutRecyclerViewAdapter
            adapter?.setShowNonLeaveMembersOnly(isChecked)

        }
        // Create ViewModel instance
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModelGroupsMembers::class.java)
//        viewModel = ViewModelProvider(this).get(GroupsMembersList::class.java)

        // Observe changes in LiveData
        sharedViewModel.groupsMembersListLiveData.observe(viewLifecycleOwner) { groupList ->
            // 체크박스 상태 초기화
            if (checkBox?.isChecked == true) checkBox.isChecked = false

            if (groupList.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList, sharedViewModel)
                binding.outRecyclerview.adapter = adapter
                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
                return@observe
            }

            // 1) itemList 구성: 람다 대신 for문으로 단순화
            val itemList = mutableListOf<RecyclerOutViewModel>()
            for (groupDetail in groupList) {
                var comeCount = 0
                val inner = mutableListOf<RecyclerInViewModel>()

                for (m in groupDetail.members) {
                    val vm = RecyclerInViewModel(
                        emoji = m.image ?: "",
                        location = m.location ?: "",
                        comment = m.comment ?: "",
                        intra_name = m.intraName ?: "",
                        included_group = groupDetail.groupId ?: -1,
                        intra_id = m.intraId ?: -1
                    )
                    if (vm.location != "퇴근") comeCount++
                    inner.add(vm)
                }

                val out = RecyclerOutViewModel(
                    title = groupDetail.groupName ?: "",
                    innerList = inner,
                    groupId = groupDetail.groupId ?: 0,
                    viewgroup = groupDetail.toggle,
                    comeCluster = comeCount
                )

                if (!friendListObject.searchGroupName(out.title)) {
                    friendListObject.groupAdd(out.title)
                }
                itemList.add(out)
            }

            // 2) "친구 목록"을 마지막으로 이동 (인덱스 -1 명시 처리)
            val lastIndex = itemList.lastIndex
            val friendIdx = itemList.indexOfFirst { it.title == "친구 목록" } // Int (nullable 아님)
            if (friendIdx >= 0 && friendIdx != lastIndex) {
                val friend = itemList[friendIdx]
                itemList.removeAt(friendIdx)
                itemList.add(friend)
            }

            // 3) 어댑터 설정
            val adapter = OutRecyclerViewAdapter(requireContext(), itemList, sharedViewModel)
            binding.outRecyclerview.adapter = adapter
            binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            GroupsList.setToggleStat(itemList)
            binding.progressBar.visibility = View.GONE
        }

//        sharedViewModel.groupsMembersListLiveData.observe(viewLifecycleOwner) { groupList ->
////            Log.d("datachange", "datachange1")
//            if (checkBox?.isChecked == true)
//            {
//                checkBox?.isChecked = false
//            }
//            if (groupList.isNotEmpty()) {
////                Log.d("checkfreind", "checkfriend")
////                Log.d("boolean_check", " checkBox?.isChecked  : ${checkBox?.isChecked }")
//                val itemList = mutableListOf<RecyclerOutViewModel>()
//                groupList.forEach { groupDetail ->
//                    var count = 0
//                    val innerItemList = mutableListOf<RecyclerInViewModel>()
//                    groupDetail.members.forEach { intraId ->
//                        val recyclerInViewModel = RecyclerInViewModel(
//                            emoji = intraId.image ?: "",
//                            location = intraId.location ?: "",
//                            comment = intraId.comment ?: "",
//                            intra_name = intraId.intraName ?: "",
//                            included_group = groupDetail.groupId ?: -1,
//                            intra_id = intraId.intraId ?: -1,
//                        )
//                        if (recyclerInViewModel.location != "퇴근")
//                        {
//                            count++
//                        }
////                        Log.d("groupId", "id : ${groupDetail.groupId} location :  ${intraId.location}")
//                        innerItemList.add(recyclerInViewModel)
//                    }
//                    val recyclerOutViewModel = RecyclerOutViewModel(
//                        title = groupDetail.groupName ?: "",
//                        innerItemList,
//                        groupId = groupDetail.groupId ?: 0,
//                        viewgroup = groupDetail.toggle,
//                        comeCluster = count
//                    )
////                    Log.d("title_check", "title_check : ${recyclerOutViewModel.title}")
//                    if (!friendListObject.searchGroupName(recyclerOutViewModel.title))
//                    {
//                        friendListObject.groupAdd(recyclerOutViewModel.title)
//                    }
//                    itemList.add(recyclerOutViewModel)
//                }
//
//                val lastIndex = itemList.lastIndex
////                val lastItem = itemList[lastIndex]
//                val defaultGroupIndex = itemList?.indexOfFirst { it.title == "친구 목록"}
//                if (lastIndex == defaultGroupIndex) {
////                    Log.d("LastItemCheck", "친구 목록이 마지막에 있습니다.")
//                } else {
//                    val defaultFriendListremove = itemList?.firstOrNull{it.title == "친구 목록"}
//                    if (defaultFriendListremove != null) {
//                        if (defaultGroupIndex != null) {
//                            itemList.removeAt(defaultGroupIndex)
//                            itemList.add(defaultFriendListremove)
//                        }
//
//                    }
////                    Log.d("LastItemCheck", "친구 목록이 마지막에 없습니다.")
//                }
//
//
//
////                Log.d("DiffUtil", "here1")
//                // Set up RecyclerView Adapter
//                val adapter = OutRecyclerViewAdapter(requireContext(), itemList, sharedViewModel)
//                binding.outRecyclerview.adapter = adapter
//                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
//                GroupsList.setToggleStat(itemList)
//                binding.progressBar.visibility = View.GONE
//            }
//            else {
//                // Handle empty or null data
//                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
//                val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList, sharedViewModel)
//                binding.outRecyclerview.adapter = adapter
//                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
//            }
//        }

//        sharedViewModel.groupsMembersListLiveData.observeForever { groupList ->
////            Log.d("datachange", "datachange2")
//            // 데이터가 변경될 때 실행되는 코드
//        }


        // Call function to fetch data
//        val intraId = 6 // Replace this with your memberId value
        sharedViewModel.getGroupMemberList(intraId, context)
    }

//    fun refreshData() {
//        // ViewModel을 사용하여 데이터를 다시 로드하는 로직
//        sharedViewModel.getGroupMemberList(intraId, accesstoken, context)
//    }
}
