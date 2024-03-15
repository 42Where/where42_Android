package com.seoul.where42android.adapter


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seoul.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import com.seoul.where42android.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecyclerViewAdapter_defaultList(
    private val context: Context,
    private val dataList: List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>,
    private val isEditable: Boolean
) : RecyclerView.Adapter<RecyclerViewAdapter_defaultList.ViewHolder>() {

    //checkBox
    // 각 항목의 체크 여부를 저장하기 위한 리스트
    val checkedItems = mutableListOf<friendGroup_default_memberlist.friendGroup_default_memberlistItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_create_group_list_view_detail_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)

        // checkbox 항목이 클릭되었을 때의 처리
        holder.itemView.setOnClickListener {
            // 체크된 항목이면 리스트에서 제거하고, 그렇지 않으면 추가합니다.
            if (currentItem in checkedItems) {
                checkedItems.remove(currentItem)
            } else {
                checkedItems.add(currentItem)
            }
            // TODO: 체크된 항목 상태에 따라 UI 업데이트 로직 추가

            // 변경된 상태를 알리고 UI를 업데이트할 수 있도록 notifyDataSetChanged() 등을 호출합니다.
//            notifyDataSetChanged()
        }
        // TODO: 기존의 onBindViewHolder() 코드 작성은 여기에 해당합니다.
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: CircleImageView = itemView.findViewById(R.id.profile_photo)
        private val textViewGrade: TextView = itemView.findViewById(R.id.intra_id)
        private val textViewLocation: TextView = itemView.findViewById(R.id.location_info)
        private val textViewComment: TextView =  itemView.findViewById(R.id.Comment)


        fun bind(member: friendGroup_default_memberlist.friendGroup_default_memberlistItem)
        {
            Glide.with(context)
                .load(member.image) // Assuming 'member.image' is the URL or path to the image
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
//                .error(R.drawable.placeholder) // Error image if Glide fails to load
                .error(R.drawable.nointraimage)
                .into(textViewName)

//            textViewGrade.text = member.memberIntraName
            textViewGrade.text = member.intraName
            textViewComment.text = member.comment
            textViewLocation.text = member.location ?: "No comment available"
            val leftPadding = 20 // 왼쪽 여백 값
            val rightPadding = 20 // 오른쪽 여백 값
            GlobalScope.launch(Dispatchers.Main) {
                if (textViewLocation.text != "퇴근") {
                    val leftPadding = 20 // 왼쪽 여백 값
                    val rightPadding = 20 // 오른쪽 여백 값
                    textViewLocation.setPadding(leftPadding, 0, rightPadding, 0)
                    adjustBackgroundSizeWithPadding(textViewLocation, leftPadding, rightPadding)
                    val color = Color.parseColor("#132743")
//                        binding.location.setBackgroundColor(color)

                    val gradientDrawable = GradientDrawable()
                    gradientDrawable.setColor(color)
                    gradientDrawable.cornerRadius = 40f // radius 값 설정 (단위는 pixel)
                    textViewLocation.background = gradientDrawable
                }
                else
                {
                    // UI 변경 작업
                    textViewLocation.setPadding(leftPadding, 0, rightPadding, 0)
                    adjustBackgroundSizeWithPadding(textViewLocation, leftPadding, rightPadding)
                    adjustBackgroundSizeWithPadding(
                        textViewLocation,
                        leftPadding,
                        rightPadding
                    )
                    val color = Color.parseColor("#132743")
                    val gradientDrawable = GradientDrawable()
                    gradientDrawable.cornerRadius = 40f // radius 값 설정 (단위는 pixel)
                    val strokeWidth = 2 // 테두리의 두께 설정
                    val strokeColor = Color.parseColor("#132743") // 테두리의 색상 설정
                    gradientDrawable.setStroke(strokeWidth, strokeColor)
                    // 배경을 설정
                    textViewLocation.background = gradientDrawable
                    textViewLocation.setTextColor(color)
                }
            }


            val checkBox: CheckBox = itemView.findViewById(R.id.checkBox) // 체크박스 ID에 맞게 수정
            checkBox.isChecked = member in checkedItems

            // 체크박스 클릭 이벤트 처리
            checkBox.setOnClickListener {
                // 체크된 항목이면 리스트에서 제거하고, 그렇지 않으면 추가합니다.
                if (member in checkedItems) {
                    checkedItems.remove(member)
                } else {
                    checkedItems.add(member)
                }
                // 변경된 상태를 알리고 UI를 업데이트할 수 있도록 notifyDataSetChanged() 등을 호출합니다.
//                notifyDataSetChanged()
            }
        }
    }
}