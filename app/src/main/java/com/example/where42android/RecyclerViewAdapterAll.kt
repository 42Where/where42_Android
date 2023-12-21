package com.example.where42android


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapterAll(private val context: Context) :
    RecyclerView.Adapter<RecyclerViewAdapterAll.ViewHolder>() {

    private val dummyDataList = mutableListOf<String>()

    init {
        for (i in 1..10) {
            if (i != 10)
                dummyDataList.add("Group $i")
            else
                dummyDataList.add("friendList")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_group_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupName = dummyDataList[position]
        holder.groupName.text = groupName
        // 데이터를 ViewHolder의 View에 바인딩하는 로직이 필요합니다.
        // 예: holder.groupName.text = groupName
    }

    override fun getItemCount(): Int {
        return dummyDataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.group_name_text_view)
    }
}


//class RecyclerViewAdapterAll(private val context: Context) :
//    RecyclerView.Adapter<RecyclerViewAdapterAll.ViewHolder>() {
//
//    private val dummyDataList = mutableListOf<String>()
//
//    init {
//        // 10개의 더미 데이터 추가
//        for (i in 1..10) {
//            dummyDataList.add("Group $i")
//        }
//    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.recycler_group_list, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val groupName = dummyDataList[position]
//        holder.groupName.text = groupName
//        // 편집 버튼 클릭 리스너 설정
////        holder.editButton.setOnClickListener {
////            // 편집 버튼 클릭 이벤트 처리
////            // 원하는 동작 수행
////        }
////
////        // 토글 버튼 클릭 리스너 설정
////        holder.toggleButton.setOnClickListener {
////            // 토글 버튼 클릭 이벤트 처리
////            // 원하는 동작 수행
////        }
//    }
//
//    override fun getItemCount(): Int {
//        return dummyDataList.size
//    }
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val groupName: TextView = itemView.findViewById(R.id.group_name_text_view)
////        val editButton: ImageButton = itemView.findViewById(R.id.group_edit)
////        val toggleButton: ImageButton = itemView.findViewById(R.id.group_toggle)
//    }
//}



//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//class RecyclerViewAdapterAll (val context: Context, private var profileList: ArrayList<profile_list>, val filterChecked: Boolean) :
//    RecyclerView.Adapter<RecyclerViewAdapterAll.ProfileViewHolder>() {
//
//
//    fun updateList(newList: ArrayList<profile_list>) {
//        profileList = newList
//        notifyDataSetChanged()
//    }
//    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val profilePhoto: ImageView = itemView.findViewById(R.id.profile_photo)
//        val intraId: TextView = itemView.findViewById(R.id.intra_id)
//        val comment: TextView = itemView.findViewById(R.id.Comment)
//        val locationInfo: TextView = itemView.findViewById(R.id.location_info)
//        val seeMoreButton: ImageButton = itemView.findViewById(R.id.see_more)
//
//        init {
//            seeMoreButton.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    val profile: profile_list
//                    if (filterChecked) {
//                        val filteredList = profileList.filter { it.location != "퇴근" }
//                        profile = filteredList[position]
//                    } else {
//                        profile = profileList[position]
//                    }
//                    val profileDialog = ProfileDialog(context)
//                    profileDialog.showProfileDialog(profile)
//                }
//            }
//        }
//    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.list_view_detail, parent, false)
//        return ProfileViewHolder(view)
//    }
//    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
//        if (filterChecked) {
//            // filterChecked가 true일 때만 필터링을 적용합니다.
//            // '퇴근'인 요소를 건너뜁니다.
//            val filteredList = profileList.filter { it.location != "퇴근" }
//            val profile = filteredList[position]
//            val resourceId = context.resources.getIdentifier(profile.photo, "drawable", context.packageName)
//            holder.profilePhoto.setImageResource(resourceId)
//            holder.intraId.text = profile.intraId
//            holder.comment.text = profile.comment
//            holder.locationInfo.text = profile.location
//            // 여기서는 필터링된 목록을 사용합니다.
//        } else {
//            // filterChecked가 false이면 필터링하지 않고 원래 목록을 사용합니다.
//            val profile = profileList[position]
//            val resourceId = context.resources.getIdentifier(profile.photo, "drawable", context.packageName)
//            holder.profilePhoto.setImageResource(resourceId)
//            holder.intraId.text = profile.intraId
//            holder.comment.text = profile.comment
//            holder.locationInfo.text = profile.location
//        }
//    }
//    override fun getItemCount(): Int {
//        return if (filterChecked) {
//            profileList.filter { it.location != "퇴근" }.size
//        } else {
//            profileList.size
//        }
//    }
//
//}