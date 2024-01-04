package com.example.where42android.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.Base_url_api_Retrofit.friendGroup_default_memberlist
import com.example.where42android.R

class GroupMemberAdapter(private val liveData: LiveData<List<friendGroup_default_memberlist.friendGroup_default_memberlistItem>>)  :
    ListAdapter<friendGroup_default_memberlist.friendGroup_default_memberlistItem, GroupMemberAdapter.GroupMemberViewHolder>(DiffCallback()) {

    init {
        liveData.observeForever {
            submitList(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_create_group_list_view_detail_checkbox, parent, false)
        return GroupMemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupMemberViewHolder, position: Int) {
        val currentItem = getItem(position)
        Log.d("currentItem", "${currentItem}")
        holder.bind(currentItem)
    }

    class GroupMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val memberName: TextView = itemView.findViewById(R.id.intra_id)
        private val membercomment: TextView = itemView.findViewById(R.id.Comment)
        private val memberlocation: TextView = itemView.findViewById(R.id.location_info)

        fun bind(member: friendGroup_default_memberlist.friendGroup_default_memberlistItem) {
            memberName.text = member.memberIntraName
            membercomment.text = member.comment
            memberlocation.text = member.location
            // 기타 필요한 데이터를 ViewHolder의 View에 연결하세요.
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<friendGroup_default_memberlist.friendGroup_default_memberlistItem>() {
        override fun areItemsTheSame(oldItem: friendGroup_default_memberlist.friendGroup_default_memberlistItem, newItem: friendGroup_default_memberlist.friendGroup_default_memberlistItem): Boolean {
            return oldItem.intraId == newItem.intraId // 예시로 사용된 ID와 비교하여 같은 아이템인지 확인합니다.
        }

        override fun areContentsTheSame(oldItem: friendGroup_default_memberlist.friendGroup_default_memberlistItem, newItem: friendGroup_default_memberlist.friendGroup_default_memberlistItem): Boolean {
            return oldItem == newItem // 내용이 같은지 확인합니다.
        }
    }
}
