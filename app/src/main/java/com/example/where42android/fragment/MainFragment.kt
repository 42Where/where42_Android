package com.example.where42android.fragment


import SharedViewModel_GroupsMembersList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.where42android.adapter.OutRecyclerViewAdapter
//import com.example.where42android.databinding.FragmentActivityMainPageBinding
//import com.example.where42android.group_api.GroupViewModel
//import com.example.where42android.group_api.GroupViewModelFactory
import com.example.where42android.model.RecyclerInViewModel
import com.example.where42android.model.RecyclerOutViewModel
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.Base_url_api_Retrofit.groups_memberlist
import retrofit2.Call
import com.example.where42android.Base_url_api_Retrofit.GroupMemberListService
import com.example.where42android.LiveData.GroupsMembersList
import com.example.where42android.main.MainPageActivity
import com.example.where42android.R
import com.example.where42android.databinding.ActivityMainPageFragmentBinding
import retrofit2.Callback


import retrofit2.Response


class MainFragment : Fragment() {

    private lateinit var binding: ActivityMainPageFragmentBinding

    private val retrofitAPI = RetrofitConnection.getInstance("awd").create(GroupMemberListService::class.java)
    private val emptyItemList = mutableListOf<RecyclerOutViewModel>()

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
                            intra_id = intraId.intraName ?: ""
                        )

                        innerItemList.add(recyclerInViewModel)
                    }

                    val recyclerOutViewModel = RecyclerOutViewModel(
                        title = groupDetail.groupName ?: "",
                        innerItemList,
                        groupId = groupDetail.groupId ?: 0
                    )

                    itemList.add(recyclerOutViewModel)
                }

                // Set up RecyclerView Adapter
                val adapter = OutRecyclerViewAdapter(requireContext(), itemList, sharedViewModel)
                binding.outRecyclerview.adapter = adapter
                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())


            } else {
                // Handle empty or null data
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList, sharedViewModel)
                binding.outRecyclerview.adapter = adapter
                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            }
        }

        // Call function to fetch data
        val intraId = 6 // Replace this with your memberId value
        sharedViewModel.getGroupMemberList(intraId)
    }
}

//
//class MainFragment : Fragment() {
//
//    private lateinit var binding: ActivityMainPageFragmentBinding
//    private lateinit var binding2: MainPageActivity
//    private val retrofitAPI = RetrofitConnection.getInstance().create(GroupMemberListService::class.java)
//    private lateinit var viewModel: MainFragmentViewModel
//
//    val emptyItemList = mutableListOf<RecyclerOutViewModel>()
////    private lateinit var progressBar: ProgressBar
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = ActivityMainPageFragmentBinding.inflate(inflater, container, false)
////        progressBar = binding.myProgressBar // Progress bar 초기화
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////        progressBar = binding.myProgressBar
//        setUpRecyclerView()
//
//        //CheckBox 출근한 친구만 보기
//        // CheckBox 상태 변경 감지 및 Adapter 업데이트
//        val checkBox = activity?.findViewById<CheckBox>(R.id.checkBox)
////        val checkBox :CheckBox = binding2.che
////        val newGroupButton: Button = binding.newGroupButton // 레이아웃 바인딩 객체에서 버튼 가져오기
//        checkBox?.setOnCheckedChangeListener { buttonView, isChecked ->
//            val adapter = binding.outRecyclerview.adapter as? OutRecyclerViewAdapter
//            adapter?.setShowNonLeaveMembersOnly(isChecked)
//        }
//    }
//
//     fun setUpRecyclerView() {
////        showLoading()
//        // Example memberId
//        val intraId = 6 // Replace this with your memberId value
//        retrofitAPI.getGroupMemberList(intraId).enqueue(object :
//            Callback<List<groups_memberlist.groups_memberlistItem>> {
//            override fun onResponse(
//                call: Call<List<groups_memberlist.groups_memberlistItem>>,
//                response: Response<List<groups_memberlist.groups_memberlistItem>>
//            ) {
//
//                if (response.isSuccessful) {
//                    val groupList = response.body()
//
//                    val itemList = mutableListOf<RecyclerOutViewModel>()
//
//                    groupList?.forEach { groupDetail ->
//                        val innerItemList = mutableListOf<RecyclerInViewModel>()
//
//                        groupDetail.members.forEach { intraId ->
//                            val recyclerInViewModel = RecyclerInViewModel(
//                                emoji = intraId.image ?: "",
//                                location = intraId.location ?: "",
//                                comment = intraId.comment ?: "",
//                                intra_id = intraId.memberIntraName ?: ""
//                            )
//
//                            innerItemList.add(recyclerInViewModel)
//                        }
//
//                        val recyclerOutViewModel = RecyclerOutViewModel(
//                            title = groupDetail.groupName ?: "",
//                            innerItemList,
//                            //그룹을 삭제하기 위해 필요한 groupId
//                            groupId = groupDetail.groupId ?: 0 // Assuming groupId is of type Number or In
//                        )
//
//                        itemList.add(recyclerOutViewModel)
//                    }
//
//                    // Set up RecyclerView Adapter
//                    val adapter = OutRecyclerViewAdapter(requireContext(), itemList)
//                    binding.outRecyclerview.adapter = adapter
//                    binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
//
//                    // 데이터 로딩이 완료되면 ProgressBar를 숨깁니다.
//
////                    hideLoading()
//                } else {
//                    // Handle API call error
//                    val errorMessage = "API call failed with code: ${response.code()}"
//                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
////                    val emptyItemList = mutableListOf<RecyclerOutViewModel>()
//                    val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList)
//                    binding.outRecyclerview.adapter = adapter
//                    binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
////                    hideLoading()
//                }
//            }
//
//            override fun onFailure(call: Call<List<groups_memberlist.groups_memberlistItem>>, t: Throwable) {
//                val errorMessage = "Network request failed. Please check your internet connection."
//                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
//
//                // 또는 실패한 이유를 로그에 출력하여 개발 중인 경우 디버깅에 활용할 수 있습니다.
//                Log.e("API_FAILURE", "API_FAILURE", t)
//
//                // RecyclerView를 초기화하고 빈 상태로 설정
//                val emptyItemList = mutableListOf<RecyclerOutViewModel>()
//                val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList)
//                binding.outRecyclerview.adapter = adapter
//                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
////                hideLoading()
//            }
//        })
//    }
//
////    override fun onDestroyView() {
////        super.onDestroyView()
////        // Clear the binding
////        binding = null
////    }
//}
