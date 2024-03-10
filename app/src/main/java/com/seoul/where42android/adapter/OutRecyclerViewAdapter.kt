package com.seoul.where42android.adapter

import SharedViewModel_GroupsMembersList
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seoul.where42android.R
import com.seoul.where42android.databinding.HolderRecyclerviewOutGroupBinding
import com.seoul.where42android.dialog.GroupDialog
import com.seoul.where42android.model.RecyclerOutViewModel

object ToggleStatManager {
    val togglestat: MutableMap<String, Boolean> = mutableMapOf()

    fun setToggleStat(key: String, value: Boolean) {
        togglestat[key] = value
    }

    fun getToggleStat(key: String): Boolean? {
        return togglestat[key]
    }

    fun clearToggleStat() {
        togglestat.clear()
    }
}

//context: 어댑터를 생성할 때 전달되는 컨텍스트(액티비티, 프래그먼트 등)
//itemList: RecyclerOutViewModel 객체의 MutableList로, RecyclerView에 표시될 데이터 목록을 저장합니다.
class OutRecyclerViewAdapter (
    val context: Context,
    var itemList: MutableList<RecyclerOutViewModel>,
    val viewModel : SharedViewModel_GroupsMembersList,
    ): RecyclerView.Adapter<OutRecyclerViewAdapter.Holder>() {

    // 체크박스 상태를 저장하기 위한 변수
    private var showNonLeaveMembersOnly = false
    private var copyItemList: MutableList<RecyclerOutViewModel> = mutableListOf()
    private var togglestat: MutableMap<String, Boolean> = mutableMapOf()

    init {
        // itemList의 복사본을 생성하여 copyItemList에 저장
        copyItemList.addAll(itemList)
    }
    // 체크박스 상태를 설정하는 메서드
//    fun setShowNonLeaveMembersOnly(showOnly: Boolean) {
//        showNonLeaveMembersOnly = showOnly
//        notifyDataSetChanged() // 데이터가 변경되었음을 어댑터에 알려 갱신
//    }
    fun setShowNonLeaveMembersOnly(showOnly: Boolean) {
        showNonLeaveMembersOnly = showOnly
        val filteredList = if (showOnly) {
            itemList.map { group ->
                group.copy(innerList = group.innerList.filter { it.location != "퇴근" }.toMutableList())
            }
        } else {
            // 필터링하지 않고 전체 리스트 유지
            copyItemList
        }
        updateData(filteredList)
    }

    fun updateData(newList: List<RecyclerOutViewModel>) {
        val diffCallback = MyDiffUtil(itemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        itemList = mutableListOf()
        itemList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
//    fun updateData(newList: List<RecyclerOutViewModel>) {
//        val diffCallback = MyDiffUtil(itemList, newList)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        itemList.clear()
//        itemList.addAll(newList)
//        diffResult.dispatchUpdatesTo(this)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderRecyclerviewOutGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
        //group 편집 버튼 사용 -> ShowgroupDialog로 이동
        holder.binding.groupEdit.setOnClickListener {
            val groupDialog = GroupDialog(holder.binding.root.context , viewModel)
            groupDialog.showGroupDialog(item.title, item.groupId) { success ->
            }
        }
//        // CheckBox 상태에 따라 필터링된 데이터를 업데이트합니다.
//        if (showNonLeaveMembersOnly != holder.showNonLeaveMembersOnly) {
//            holder.showNonLeaveMembersOnly = showNonLeaveMembersOnly
//            holder.updateFilteredData()
//        }
    }

    //RecyclerView에 표시될 총 항목 수를 반환합니다.
    override fun getItemCount(): Int {
        return itemList.size
    }
    //RecyclerView.ViewHolder를 확장한 내부 클래스로, 각 항목의 뷰를 보유합니다.
    //bind() 메서드는 데이터를 ViewHolder에 바인딩합니다.
    //innerRecyclerview에 대한 adapter와 layoutManager를 설정하여 내부 RecyclerView를 초기화하고 연결합니다.
    inner class Holder(var binding: HolderRecyclerviewOutGroupBinding) : RecyclerView.ViewHolder(binding.root) {
//        var showNonLeaveMembersOnly: Boolean = false // 체크박스 상태를 저장하기 위한 변수
        // 필터링된 데이터를 업데이트하고 내부 RecyclerView를 다시 설정하는 메서드
//        fun updateFilteredData() {
//            val item = binding.model ?: return // 현재 ViewHolder에 연결된 데이터 가져오기
//            val filteredInnerList: MutableList<RecyclerInViewModel> =
//                if (showNonLeaveMembersOnly) {
//                    item.innerList.filter { it.location != "퇴근" }.toMutableList() // "퇴근"이 아닌 위치만 필터링
//                } else {
//                    item.innerList.toMutableList() // 필터링하지 않고 전체 리스트 표시
//                }
//            // 필터링된 데이터로 내부 RecyclerView를 설정
//            binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, filteredInnerList)
//            binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)
//        }
        init {
            binding.groupToggle.setOnClickListener {
                binding.model?.viewgroup = !(binding.model?.viewgroup ?: false) // 가시성 상태 변경
//                Log.d("innerrecyclerview", "inner : ${binding.innerRecyclerview}")
                if (binding.model?.viewgroup == true)
                {
//                    Log.d("groupDetail", "OutRecyclerViewAdapter : ${binding.model?.title}")
                    if (binding.model?.innerList?.isEmpty() == false) {
//                        Log.d("innerrecyclerview", "inner here : ${binding.model?.innerList}")
                        binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
                        binding.viewLine.visibility = View.GONE


//                        val string = binding.model?.title.toString()
//                        togglestat[string] = true
                        ToggleStatManager.setToggleStat(binding.model?.title.toString(), true)

//                        Log.d("innerrecyclerview", "GroupName : ${binding.model?.title}, togglestat[string] : ${togglestat[string]}")
                        //ViewModel 값 연결해주기
//                        binding.model?.title?.let { it1 -> viewModel.groupToggleChange(it1,
//                            binding.model?.viewgroup!!
//                        ) }

                    } else {
//                        Log.d("innerrecyclerview", "inner : ${null}")
                        // Dialog 객체를 생성합니다.
                        val dialog = Dialog(context)

                        // 다이얼로그 레이아웃을 설정합니다.
                        dialog.setContentView(R.layout.activity_editstatus_popup)
                        dialog.setCanceledOnTouchOutside(true)
                        dialog.setCancelable(true)
                        dialog.window?.setGravity(Gravity.CENTER)
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        // 다이얼로그 안의 버튼 등의 View를 찾아서 이벤트를 처리할 수 있습니다.
                        val textView = dialog.findViewById<TextView>(R.id.title)
                        textView.text = "그룹에 친구를 먼저 추가해주세요."

                        val cancelButton = dialog.findViewById<Button>(R.id.cancel)
                        cancelButton.visibility = View.GONE
                        val submitButton = dialog.findViewById<Button>(R.id.submit)


                        // 확인 버튼 클릭 시 원하는 동작을 수행합니다.
                        submitButton.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                } else {
//                    val string = binding.model?.title.toString()
//                    togglestat[string] = false
                    ToggleStatManager.setToggleStat(binding.model?.title.toString(), false)

//                    togglestat[binding.model.title] = false
                    binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
                    binding.viewLine.visibility = View.GONE
                }
            }
        }
        fun bind(item: RecyclerOutViewModel) {
            binding.model = item
//            Log.d("DiffUtil", "here3")

//            Log.d("boolean_check", " showNonLeaveMembersOnly : ${showNonLeaveMembersOnly}")

//            togglestat = MutableMap(binding.model.title, binding.model.viewgroup)
//            Log.d("innerrecyclerview", "togglestat : ${togglestat}")
//            val toggle = togglestat[item.title]
            val toggle = ToggleStatManager.getToggleStat(item.title)
//            Log.d("innerrecyclerview", "togglestat item.title : ${item.title}, toggle : ${toggle}")
            if (toggle == true)
            {
                item.viewgroup = true
            }

            if (item.title != "친구 목록" && item.viewgroup == false)
            {
//                Log.d("innerrecyclerview", "if 초기화 item.title : ${item.title}, item.viewgroup : ${item.viewgroup}")
//                Log.d("Togglestat Entry", "Togglestat Entry : ${item.title}")
//                togglestat[item.title] = item.viewgroup
                ToggleStatManager.setToggleStat(item.title, item.viewgroup)

            }
            else
            {
                //here
//                togglestat.forEach { (key, value) ->
////                    Log.d("Togglestat Entry", "Key: $key, Value: $value")
//                    if (item.title == key)
//                    {
//                        item.viewgroup = value
//                        Log.d("innerrecyclerview", "else 초기화 item.title : ${item.title}, item.viewgroup : ${item.viewgroup}")
//                    }
//                }
                // ToggleStatManager의 togglestat 맵을 순회합니다.
                for ((key, value) in ToggleStatManager.togglestat) {
                    // 각 항목에 대해 키와 값에 접근할 수 있습니다.
                    if (item.title == key)
                    {
                        item.viewgroup = value
                    }
                }


            }



            if (item.viewgroup)
            {
                binding.innerRecyclerview.visibility = View.VISIBLE // 내부 RecyclerView 보이기
            } else {
                binding.innerRecyclerview.visibility = View.GONE // 내부 RecyclerView 숨기기
            }
            binding.innerRecyclerview.adapter = InRecyclerViewAdapter(context, item.innerList)
            binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)
        }
    }

    class MyDiffUtil(
        private val oldList: List<RecyclerOutViewModel>,
        private val newList: List<RecyclerOutViewModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }
        override fun getNewListSize(): Int {
            return newList.size
        }
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].groupId == newList[newItemPosition].groupId
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}