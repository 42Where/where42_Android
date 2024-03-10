package com.seoul.where42android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter_new_group(val context: Context, private var profileList: ArrayList<profile_list>, val filterChecked: Boolean) :
    RecyclerView.Adapter<RecyclerViewAdapter_new_group.GroupViewHolder>() {

    fun updateList(newList: ArrayList<profile_list>) {
        profileList = newList
        notifyDataSetChanged()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhoto: ImageView = itemView.findViewById(R.id.profile_photo)
        val intraId: TextView = itemView.findViewById(R.id.intra_id)
        val comment: TextView = itemView.findViewById(R.id.Comment)
        val locationInfo: TextView = itemView.findViewById(R.id.location_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_create_group_list_view_detail_checkbox, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        if (filterChecked) {
            // filterChecked가 true일 때만 필터링을 적용합니다.
            // 특정 조건에 따라 그룹 목록을 필터링할 수 있습니다.
            // 이 코드를 원하는 방식으로 수정하세요.
            val filteredList = profileList.filter { it.location != "퇴근" }
            val profile = filteredList[position]
            val resourceId = context.resources.getIdentifier(profile.photo, "drawable", context.packageName)
            holder.profilePhoto.setImageResource(resourceId)
            holder.intraId.text = profile.intraId
            holder.comment.text = profile.comment
            holder.locationInfo.text = profile.location
            // 여기서는 필터링된 목록을 사용합니다.
        } else {
            // filterChecked가 false이면 필터링하지 않고 원래 목록을 사용합니다.
            val profile = profileList[position]
            val resourceId = context.resources.getIdentifier(profile.photo, "drawable", context.packageName)
            holder.profilePhoto.setImageResource(resourceId)
            holder.intraId.text = profile.intraId
            holder.comment.text = profile.comment
            holder.locationInfo.text = profile.location
        }
    }

    override fun getItemCount(): Int {
        return if (filterChecked) {
            profileList.filter {it.location != "퇴근"}.size
        } else {
            profileList.size
        }
    }
}
