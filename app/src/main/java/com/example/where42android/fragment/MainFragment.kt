package com.example.where42android.fragment


//import com.example.where42android.databinding.FragmentActivityMainPageBinding
//import com.example.where42android.group_api.GroupViewModel
//import com.example.where42android.group_api.GroupViewModelFactory


import SharedViewModel_GroupsMembersList
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.where42android.Base_url_api_Retrofit.GroupMemberListService
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.R
import com.example.where42android.adapter.OutRecyclerViewAdapter
import com.example.where42android.databinding.ActivityMainPageFragmentBinding
import com.example.where42android.model.RecyclerInViewModel
import com.example.where42android.model.RecyclerOutViewModel


class MainFragment(receivedToken : String, IntraId : Int) : Fragment() {


    private lateinit var binding: ActivityMainPageFragmentBinding
    private val token = receivedToken
    private val retrofitAPI = RetrofitConnection.getInstance(token).create(GroupMemberListService::class.java)
    private val emptyItemList = mutableListOf<RecyclerOutViewModel>()
    private val intraid = IntraId

//    private lateinit var viewModel: GroupsMembersList

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

//        checkBox?.setOnCheckedChangeListener { _, isChecked ->
//            val adapter = binding.outRecyclerview.adapter as? OutRecyclerViewAdapter
////            adapter?.setShowNonLeaveMembersOnly(isChecked)
//            adapter?.setShowNonLeaveMembersOnly(isChecked)
//        }
        checkBox?.setOnCheckedChangeListener { _, isChecked ->
            val adapter = binding.outRecyclerview.adapter as? OutRecyclerViewAdapter
            adapter?.setShowNonLeaveMembersOnly(isChecked)

        }


        // Create ViewModel instance
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel_GroupsMembersList::class.java)
//        viewModel = ViewModelProvider(this).get(GroupsMembersList::class.java)

        // Observe changes in LiveData
        sharedViewModel.groupsMembersListLiveData.observe(viewLifecycleOwner) { groupList ->
            if (groupList.isNotEmpty()) {
                val itemList = mutableListOf<RecyclerOutViewModel>()

                groupList.forEach { groupDetail ->
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
                        Log.d("groupId", "id : ${groupDetail.groupId}")
                        innerItemList.add(recyclerInViewModel)
                    }

                    val recyclerOutViewModel = RecyclerOutViewModel(
                        title = groupDetail.groupName ?: "",
                        innerItemList,
                        groupId = groupDetail.groupId ?: 0,
                        viewgroup = false
                    )

                    itemList.add(recyclerOutViewModel)
                }

                // itemList에서 "default" 그룹을 찾아서 제거하고 다시 맨 뒤로 추가
                val defaultGroup = itemList.firstOrNull { it.title == "default" }

                if (defaultGroup != null) {
                    itemList.remove(defaultGroup)
                    defaultGroup.title = "친구 목록"
                    defaultGroup.viewgroup = true
                    itemList.add(defaultGroup)
                }

                // Set up RecyclerView Adapter
                val adapter = OutRecyclerViewAdapter(requireContext(), itemList, sharedViewModel)
                binding.outRecyclerview.adapter = adapter
                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())


            }
            else {
                // Handle empty or null data
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList, sharedViewModel)
                binding.outRecyclerview.adapter = adapter
                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            }
        }

        // Call function to fetch data
//        val intraId = 6 // Replace this with your memberId value
        sharedViewModel.getGroupMemberList(intraid, token)
    }
}
