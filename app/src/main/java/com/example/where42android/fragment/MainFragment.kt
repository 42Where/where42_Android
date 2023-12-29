package com.example.where42android.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.where42android.ApiObject
import com.example.where42android.adapter.OutRecyclerViewAdapter
import com.example.where42android.databinding.FragmentActivityMainPageBinding
//import com.example.where42android.group_api.GroupViewModel
//import com.example.where42android.group_api.GroupViewModelFactory
import com.example.where42android.model.RecyclerInViewModel
import com.example.where42android.model.RecyclerOutViewModel
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.where42android.retrofit.RetrofitConnection
import com.example.where42android.retrofit.groups_memberlist
import retrofit2.Call
import com.example.where42android.retrofit.GroupMemberListService
import retrofit2.Callback


import retrofit2.Response


class MainFragment : Fragment() {

    private lateinit var binding: FragmentActivityMainPageBinding
    private val retrofitAPI = RetrofitConnection.getInstance().create(GroupMemberListService::class.java)
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivityMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = binding.myProgressBar
        setUpRecyclerView()
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE // 프로그레스바 보이기
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE // 프로그레스바 숨기기
    }

    private fun setUpRecyclerView() {
        showLoading()
        // Example memberId
        val intraId = 6 // Replace this with your memberId value
        retrofitAPI.getGroupMemberList(intraId).enqueue(object :
            Callback<List<groups_memberlist.groups_memberlistItem>> {
            override fun onResponse(
                call: Call<List<groups_memberlist.groups_memberlistItem>>,
                response: Response<List<groups_memberlist.groups_memberlistItem>>
            ) {
                hideLoading()
                if (response.isSuccessful) {
                    val groupList = response.body()

                    val itemList = mutableListOf<RecyclerOutViewModel>()

                    groupList?.forEach { groupDetail ->
                        val innerItemList = mutableListOf<RecyclerInViewModel>()

                        groupDetail.members.forEach { intraId ->
                            val recyclerInViewModel = RecyclerInViewModel(
                                emoji = intraId.image ?: "",
                                location = intraId.location ?: "",
                                comment = intraId.comment ?: "",
                                intra_id = intraId.memberIntraName ?: ""
                            )

                            innerItemList.add(recyclerInViewModel)
                        }

                        val recyclerOutViewModel = RecyclerOutViewModel(
                            title = groupDetail.groupName ?: "",
                            innerItemList
                        )

                        itemList.add(recyclerOutViewModel)
                    }

                    // Set up RecyclerView Adapter
                    val adapter = OutRecyclerViewAdapter(requireContext(), itemList)
                    binding.outRecyclerview.adapter = adapter
                    binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())

                    // 데이터 로딩이 완료되면 ProgressBar를 숨깁니다.

                } else {
                    // Handle API call error
                    val errorMessage = "API call failed with code: ${response.code()}"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    val emptyItemList = mutableListOf<RecyclerOutViewModel>()
                    val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList)
                    binding.outRecyclerview.adapter = adapter
                    binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
                }
            }

            override fun onFailure(call: Call<List<groups_memberlist.groups_memberlistItem>>, t: Throwable) {
                val errorMessage = "Network request failed. Please check your internet connection."
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                // 또는 실패한 이유를 로그에 출력하여 개발 중인 경우 디버깅에 활용할 수 있습니다.
                Log.e("API_FAILURE", "API_FAILURE", t)

                // RecyclerView를 초기화하고 빈 상태로 설정
                val emptyItemList = mutableListOf<RecyclerOutViewModel>()
                val adapter = OutRecyclerViewAdapter(requireContext(), emptyItemList)
                binding.outRecyclerview.adapter = adapter
                binding.outRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            }
        })
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        // Clear the binding
//        binding = null
//    }
}
