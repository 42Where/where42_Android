package com.seoul.where42android.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
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
import com.seoul.where42android.main.friendCheckedList

class RecyclerViewCreatGroupActivity(
    private val context: Context,
    private val dataList: List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>,
    private val isEditable: Boolean
) : RecyclerView.Adapter<RecyclerViewCreatGroupActivity.ViewHolder>() {
    //checkBox
    // 각 항목의 체크 여부를 저장하기 위한 리스트

    private var checkBoxClickListener: ((Boolean, Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_create_group_list_view_detail_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // 클릭 리스너 설정 메서드
    fun setOnCheckBoxClickListener(listener: (Boolean, Int) -> Unit) {
        checkBoxClickListener = listener
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: CircleImageView = itemView.findViewById(R.id.profile_photo)
        private val textViewGrade: TextView = itemView.findViewById(R.id.intra_id)
        private val textViewLocation: TextView = itemView.findViewById(R.id.location_info)
        private val textViewComment: TextView =  itemView.findViewById(R.id.Comment)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

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

            textViewLocation.text = member.location
//            Log.d("location_Here", "text :  ${textViewLocation.text}")
            if (textViewLocation.text == "퇴근") {

                textViewLocation.setBackgroundResource(R.drawable.location_outcluster)
                val strokeColor = Color.parseColor("#132743")
//                    binding.locationInfo.setPadding(20, 0, 20, 0)
                textViewLocation.setTextColor(strokeColor)


                textViewName.borderWidth = 0

            }
            textViewLocation.setPadding(20, 0, 20, 0)



//            checkBox.isChecked = member in checkedItems
            checkBox.isChecked = friendCheckedList.searchfriendChecked(member.intraId)
            // 체크박스 클릭 이벤트 처리
            checkBox.setOnClickListener {
                if (!checkBox.isChecked) {
//                    checkedItems.remove(member)
                    friendCheckedList.removeItem(member.intraId)
                } else {
//                    checkedItems.add(member)
                    friendCheckedList.addItem(member.intraId)
                }
                checkBoxClickListener?.invoke(checkBox.isChecked, adapterPosition)
            }
        }
    }
}