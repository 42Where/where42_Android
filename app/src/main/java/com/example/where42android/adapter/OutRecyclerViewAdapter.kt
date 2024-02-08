package com.example.where42android.adapter

import SharedViewModel_GroupsMembersList
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.LiveData.GroupsMembersList
import com.example.where42android.databinding.HolderRecyclerviewOutGroupBinding
import com.example.where42android.dialog.GroupDialog
import com.example.where42android.model.RecyclerInViewModel
import com.example.where42android.model.RecyclerOutViewModel


//context: 어댑터를 생성할 때 전달되는 컨텍스트(액티비티, 프래그먼트 등)
//itemList: RecyclerOutViewModel 객체의 MutableList로, RecyclerView에 표시될 데이터 목록을 저장합니다.
class OutRecyclerViewAdapter (
    val context: Context,
    val itemList: MutableList<RecyclerOutViewModel>,
    val viewModel : SharedViewModel_GroupsMembersList,

): RecyclerView.Adapter<OutRecyclerViewAdapter.Holder>() {

    // 체크박스 상태를 저장하기 위한 변수
    private var showNonLeaveMembersOnly = false
    // 각 그룹에 대한 필터링된 데이터를 저장하는 맵
    private val filteredGroups: MutableMap<Int, MutableList<RecyclerInViewModel>> = mutableMapOf()


    // 체크박스 상태를 설정하는 메서드
    fun setShowNonLeaveMembersOnly(showOnly: Boolean) {
        showNonLeaveMembersOnly = showOnly

        notifyDataSetChanged() // 데이터가 변경되었음을 어댑터에 알려 갱신

    }

    //RecyclerView에 표시될 각 항목의 ViewHolder를 생성합니다.
    //HolderRecyclerviewOutGroupBinding을 사용하여 ViewHolder를 생성하고 반환합니다.
    //inflate?
    //inflate() 메서드를 사용하여 XML 레이아웃 파일을 인플레이트(부풀리다)하여 뷰를 생성합니다.
    //LayoutInflater.from(parent.context)를 사용하여 부모 컨텍스트로부터 LayoutInflater를 가져옵니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderRecyclerviewOutGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }


    //RecyclerView의 각 항목에 데이터를 바인딩합니다.
    //itemList에서 특정 위치(position)에 있는 데이터를 가져와 해당 ViewHolder에 연결합니다.
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemList[position]
        holder.bind(item)


        // CheckBox 상태에 따라 필터링된 데이터를 업데이트합니다.
//        updateFilteredData()

        //group 편집 버튼 사용 -> ShowgroupDialog로 이동
        holder.binding.groupEdit.setOnClickListener {
            val groupDialog = GroupDialog(holder.binding.root.context , viewModel)
            groupDialog.showGroupDialog(item.title, item.groupId) { success ->
//                if (success) {
//                    // 데이터 업데이트 및 RecyclerView 갱신
//
//                    updateData() // 데이터 업데이트 메서드 호출
//                }
            }
        }

//        //CheckBox 출근한 친구만 보기
//        val filteredInnerList: MutableList<RecyclerInViewModel> =
//            if (showNonLeaveMembersOnly ) {
//            item.innerList.filter { it.location != "퇴근" }.toMutableList() // "퇴근"이 아닌 위치만 필터링
//        } else {
//            item.innerList.toMutableList() // 필터링하지 않고 전체 리스트 표시
//
//        }
//
//        holder.bind(item.copy(innerList = filteredInnerList))
        // CheckBox 상태에 따라 필터링된 데이터를 업데이트합니다.
        if (showNonLeaveMembersOnly != holder.showNonLeaveMembersOnly) {
            holder.showNonLeaveMembersOnly = showNonLeaveMembersOnly
            holder.updateFilteredData()
        }
    }

    //RecyclerView에 표시될 총 항목 수를 반환합니다.
    override fun getItemCount(): Int {
        return itemList.size
    }

    //RecyclerView.ViewHolder를 확장한 내부 클래스로, 각 항목의 뷰를 보유합니다.
    //bind() 메서드는 데이터를 ViewHolder에 바인딩합니다.
    //innerRecyclerview에 대한 adapter와 layoutManager를 설정하여 내부 RecyclerView를 초기화하고 연결합니다.
    inner class Holder(var binding: HolderRecyclerviewOutGroupBinding) : RecyclerView.ViewHolder(binding.root) {

        var showNonLeaveMembersOnly: Boolean = false // 체크박스 상태를 저장하기 위한 변수

        // 필터링된 데이터를 업데이트하고 내부 RecyclerView를 다시 설정하는 메서드
        fun updateFilteredData() {
            val item = binding.model ?: return // 현재 ViewHolder에 연결된 데이터 가져오기
            val filteredInnerList: MutableList<RecyclerInViewModel> =
                if (showNonLeaveMembersOnly) {
                    item.innerList.filter { it.location != "퇴근" }.toMutableList() // "퇴근"이 아닌 위치만 필터링
                } else {
                    item.innerList.toMutableList() // 필터링하지 않고 전체 리스트 표시
                }

            // 필터링된 데이터로 내부 RecyclerView를 설정
            binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, filteredInnerList)
            binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)


        }



        //밑 부분이 토글 버튼 눌렀을 때 데이터 보이냐 안 보이냐!!
        private var isInnerRecyclerViewVisible = false // 내부 RecyclerView 가시성 상태를 기록하는 변수

        init {
            binding.groupToggle.setOnClickListener {
                Log.d("checkfriend3", "itemName : ${binding.model?.title}, item_viewgroup : ${binding.model?.viewgroup}")
                Log.d("check_here", "isInnerRecyclerViewVisible before : ${isInnerRecyclerViewVisible}")

                binding.model?.viewgroup = !(binding.model?.viewgroup ?: false) // 가시성 상태 변경
                Log.d("checkfriend3", "itemName after : ${binding.model?.title}, item_viewgroup : ${binding.model?.viewgroup}")
                Log.d("check_here", "isInnerRecyclerViewVisible after : ${isInnerRecyclerViewVisible}")
                if (binding.model?.viewgroup == true) {
                    binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
                    binding.viewLine.visibility = View.GONE
                } else {
                    binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
                    binding.viewLine.visibility = View.GONE
                }
            }
        }

//        init {


//            binding.groupToggle.setOnClickListener {
//
//                Log.d("check_here", "isInnerRecyclerViewVisible before : ${isInnerRecyclerViewVisible}")
//                isInnerRecyclerViewVisible = !isInnerRecyclerViewVisible // 가시성 상태 변경
//
//                Log.d("check_here", "isInnerRecyclerViewVisible after : ${isInnerRecyclerViewVisible}")
//                if (isInnerRecyclerViewVisible) {
//                    binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
//                    binding.viewLine.visibility = View.GONE
//                } else {
//                    binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
//                    binding.viewLine.visibility = View.GONE
//                }
//            }
//        }

        fun bind(item: RecyclerOutViewModel) {
            binding.model = item


            //내부 RecyclerView에 대한 어댑터를 설정합니다.
            //InRecyclerViewAdapter는 내부 RecyclerView에 표시될 데이터를 관리합니다. item.innerList는 외부 RecyclerView의 각 항목에 대한 내부 RecyclerView의 데이터 목록입니다.

            Log.d("checkfriend3", "itemName : ${item.title}, item_viewgroup : ${item.viewgroup}")

//            binding.groupToggle.setOnClickListener {
//                Log.d("checkfriend3", "itemName : ${item.title}, item_viewgroup : ${item.viewgroup}")
//                Log.d("check_here", "isInnerRecyclerViewVisible before : ${isInnerRecyclerViewVisible}")
//
//                item.viewgroup = !item.viewgroup // 가시성 상태 변경
//                Log.d("checkfriend3", "itemName after : ${item.title}, item_viewgroup : ${item.viewgroup}")
//                Log.d("check_here", "isInnerRecyclerViewVisible after : ${isInnerRecyclerViewVisible}")
//                if (item.viewgroup) {
//                    binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
//                    binding.viewLine.visibility = View.GONE
//                } else {
//                    binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
//                    binding.viewLine.visibility = View.GONE
//                }

//                isInnerRecyclerViewVisible = !isInnerRecyclerViewVisible // 가시성 상태 변경
//
//                Log.d("check_here", "isInnerRecyclerViewVisible after : ${isInnerRecyclerViewVisible}")
//                if (isInnerRecyclerViewVisible) {
//                    binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
//                    binding.viewLine.visibility = View.GONE
//                } else {
//                    binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
//                    binding.viewLine.visibility = View.GONE
//                }
//            }


            if (item.viewgroup)
            {
                binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
//                val filteredInnerList: MutableList<RecyclerInViewModel> =
//                    if (showNonLeaveMembersOnly) {
//                        item.innerList.filter { it.location != "퇴근" }.toMutableList() // "퇴근"이 아닌 위치만 필터링
//                    } else {
//                        item.innerList.toMutableList() // 필터링하지 않고 전체 리스트 표시
//                    }
//                binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, filteredInnerList)
//                binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)

            } else {
                binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기

//                binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, item.innerList)
//                binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)
            }

            binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, item.innerList)
            binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)


//            if (item.title == "친구 목록")
//            {
//                Log.e("fuck", "fuck here")
//                binding.innerRecyclerview.visibility =  View.VISIBLE
//                isInnerRecyclerViewVisible = true
//            }

//            if (isInnerRecyclerViewVisible) {
//                binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
//            } else {
//                binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
//            }
//
//
//            if (item.title == "친구 목록")
//            {
//                Log.e("fuck", "fuck here")
//                binding.innerRecyclerview.visibility =  View.VISIBLE
//                isInnerRecyclerViewVisible = true
//            }


//            binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, item.innerList)
//            binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)
            Log.d("checkfriend", "itemName : ${item.title}, item_isInnerRecyclerViewVisible : ${isInnerRecyclerViewVisible} , item_showNonLeaveMembersOnly : ${showNonLeaveMembersOnly}")

        }



//        fun bind(item: RecyclerOutViewModel) {
//            binding.model = item
//
//            // 내부 RecyclerView에 대한 어댑터를 설정합니다.
//            // InRecyclerViewAdapter는 내부 RecyclerView에 표시될 데이터를 관리합니다.
//            // item.innerList는 외부 RecyclerView의 각 항목에 대한 내부 RecyclerView의 데이터 목록입니다.
//            binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, item.innerList)
//            binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)
//
//            // 기본(default) 그룹이라면 내부 RecyclerView를 보이게 설정
//            if (item.title == "default") {
//                binding.innerRecyclerview.visibility =  View.VISIBLE
//            } else {
//                binding.innerRecyclerview.visibility = View.GONE // 기본(default) 그룹이 아니면 내부 RecyclerView를 숨김
//            }
//        }


//        fun bind(item: RecyclerOutViewModel) {
//            binding.model = item
//
//            //내부 RecyclerView에 대한 어댑터를 설정합니다.
//            //InRecyclerViewAdapter는 내부 RecyclerView에 표시될 데이터를 관리합니다. item.innerList는 외부 RecyclerView의 각 항목에 대한 내부 RecyclerView의 데이터 목록입니다.
//            binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, item.innerList)
//            binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)
//        }
    }

    //화면을 새로 그리는데,,, 괜찮겠지?
    fun updateData() {
//        notifyDataSetChanged()
        val intent = (context as Activity).intent
        context.finish() //현재 액티비티 종료 실시
        context.startActivity(intent) //현재 액티비티 재실행 실시


    }


}