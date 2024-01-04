package com.example.where42android.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.databinding.HolderRecyclerviewOutGroupBinding
import com.example.where42android.dialog.GroupDialog
import com.example.where42android.model.RecyclerInViewModel
import com.example.where42android.model.RecyclerOutViewModel


//context: 어댑터를 생성할 때 전달되는 컨텍스트(액티비티, 프래그먼트 등)
//itemList: RecyclerOutViewModel 객체의 MutableList로, RecyclerView에 표시될 데이터 목록을 저장합니다.
class OutRecyclerViewAdapter (val context: Context, val itemList: MutableList<RecyclerOutViewModel>): RecyclerView.Adapter<OutRecyclerViewAdapter.Holder>() {

    // 체크박스 상태를 저장하기 위한 변수
    private var showNonLeaveMembersOnly = false

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

        //group 편집 버튼 사용 -> ShowgroupDialog로 이동
        holder.binding.groupEdit.setOnClickListener {
            val groupDialog = GroupDialog(holder.binding.root.context)
            groupDialog.showGroupDialog(item.title, item.groupId) { success ->
                if (success) {
                    // 데이터 업데이트 및 RecyclerView 갱신

                    updateData() // 데이터 업데이트 메서드 호출
                }
            }
        }


        //CheckBox 출근한 친구만 보기
        val filteredInnerList: MutableList<RecyclerInViewModel> = if (showNonLeaveMembersOnly) {
            item.innerList.filter { it.location != "퇴근" }.toMutableList() // "퇴근"이 아닌 위치만 필터링
        } else {
            item.innerList.toMutableList() // 필터링하지 않고 전체 리스트 표시
        }

        holder.bind(item.copy(innerList = filteredInnerList))
    }

    //RecyclerView에 표시될 총 항목 수를 반환합니다.
    override fun getItemCount(): Int {
        return itemList.size
    }

    //RecyclerView.ViewHolder를 확장한 내부 클래스로, 각 항목의 뷰를 보유합니다.
    //bind() 메서드는 데이터를 ViewHolder에 바인딩합니다.
    //innerRecyclerview에 대한 adapter와 layoutManager를 설정하여 내부 RecyclerView를 초기화하고 연결합니다.
    inner class Holder(var binding: HolderRecyclerviewOutGroupBinding) : RecyclerView.ViewHolder(binding.root) {


        //밑 부분이 토글 버튼 눌렀을 때 데이터 보이냐 안 보이냐!!
        private var isInnerRecyclerViewVisible = true // 내부 RecyclerView 가시성 상태를 기록하는 변수
        init {
            binding.groupToggle.setOnClickListener {
                isInnerRecyclerViewVisible = !isInnerRecyclerViewVisible // 가시성 상태 변경

                if (isInnerRecyclerViewVisible) {
                    binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
                    binding.viewLine.visibility = View.VISIBLE
                } else {
                    binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
                    binding.viewLine.visibility = View.GONE
                }
            }
        }

        fun bind(item: RecyclerOutViewModel) {
            binding.model = item

            //내부 RecyclerView에 대한 어댑터를 설정합니다.
            //InRecyclerViewAdapter는 내부 RecyclerView에 표시될 데이터를 관리합니다. item.innerList는 외부 RecyclerView의 각 항목에 대한 내부 RecyclerView의 데이터 목록입니다.

            if (isInnerRecyclerViewVisible) {
                binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
                binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, item.innerList)
                binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)
            } else {
                binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
            }
        }


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