package com.seoul.where42android.fragment

import SearchRecyclerViewAdapter
import android.content.Context

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seoul.where42android.ViewModel.SharedViewModelSearch
import com.seoul.where42android.databinding.ActivityMainSearchFragmentBinding
import com.seoul.where42android.main.MainSearchPage
import com.seoul.where42android.model.SearchRecyclerInViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.*



class MainSearchFragment() : Fragment() {
    private lateinit var binding: ActivityMainSearchFragmentBinding
    private lateinit var adapter: SearchRecyclerViewAdapter
    private var intraName: String = ""
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            intraName = it.getString("NAME").toString()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    companion object {
        fun newInstance(intraName: String, t1: Long = 0L): MainSearchFragment {
            val fragment = MainSearchFragment()
            val args = Bundle()
            args.putString("NAME", intraName)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityMainSearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ViewModel 초기화
        val SharedViewModelSearch = ViewModelProvider(this).get(SharedViewModelSearch::class.java)
        // Observer 등록
        SharedViewModelSearch.searchListLiveData.observe(viewLifecycleOwner) { searchList ->

            if (searchList != null) {
                if (searchList.isNotEmpty()) {
                    val itemList = mutableListOf<SearchRecyclerInViewModel>()
                    CoroutineScope(Dispatchers.IO).launch {
                        searchList.forEach { searchDetail ->
                            val searchItem = SearchRecyclerInViewModel(
                                emoji = searchDetail.image ?: "",
                                intra_name = searchDetail.intraName,
                                intra_id = searchDetail.intraId
                            )
                            itemList.add(searchItem)
                        }
                        withContext(Dispatchers.Main)
                        {
                            binding.searchview.layoutManager = LinearLayoutManager(requireContext())
                            adapter = SearchRecyclerViewAdapter(requireContext(), itemList)
                            binding.searchview.adapter = adapter
                            binding.progressBar.visibility = View.GONE
                            binding.searchview.visibility = View.VISIBLE
                            binding.noItemsTextView.visibility = View.GONE
                            adapter.setOnCheckBoxClickListener { checked, position,intraId ->
                                val activity = requireActivity() as? MainSearchPage
                                activity?.onCheckBoxClicked(checked, position, intraId)
                            }
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main)
                        {
                            val itemList = mutableListOf<SearchRecyclerInViewModel>()
                            binding.searchview.layoutManager = LinearLayoutManager(requireContext())
                            // 어댑터 초기화 및 설정
                            adapter = SearchRecyclerViewAdapter(requireContext(), itemList)
                            binding.searchview.adapter = adapter
                            binding.progressBar.visibility = View.GONE
                            binding.noItemsTextView.visibility = View.VISIBLE // TextView를 표시
                            binding.searchview.visibility = View.GONE

                        }
                    }

//                    Log.d("SaerchFragment", "SearchFragment here")
                }
            }
            else
            {
//                Log.d("SaerchFragment", "SearchFragment here")
            }
        }
        // 데이터 요청
        SharedViewModelSearch.getSearchMemberList(intraName, context)
    }
}
