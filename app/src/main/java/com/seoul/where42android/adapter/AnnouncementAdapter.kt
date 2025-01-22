package com.seoul.where42android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seoul.where42android.Base_url_api_Retrofit.AnnouncementReponse
import com.seoul.where42android.R

class AnnouncementAdapter(
    private val announcements: MutableList<AnnouncementReponse>,
    private val onItemClick: (AnnouncementReponse) -> Unit
) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    inner class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.text_title)
        val date: TextView = itemView.findViewById(R.id.text_date)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(announcements[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.title.text = announcement.title
        holder.date.text = announcement.createAt
    }

    override fun getItemCount() = announcements.size

    // 데이터 갱신 메서드
    fun updateData(newAnnouncements: List<AnnouncementReponse>) {
        announcements.clear() // 기존 데이터 삭제
        announcements.addAll(newAnnouncements) // 새로운 데이터 추가
        notifyDataSetChanged() // RecyclerView 갱신
    }
}

