package com.example.where42android.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.where42android.Base_url_api_Retrofit.MemberAll
import com.example.where42android.R
import de.hdodenhof.circleimageview.CircleImageView

class RecyclerViewAdapter_MemberAll(
    private val context: Context,
    private val dataList: List<MemberAll.MemberAllItem>,
    private val isEditable: Boolean
) : RecyclerView.Adapter<RecyclerViewAdapter_MemberAll.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_view_detail_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: CircleImageView = itemView.findViewById(R.id.profile_photo)
        private val textViewGrade: TextView = itemView.findViewById(R.id.intra_id)
        private val textViewLocation: TextView = itemView.findViewById(R.id.Comment)
        private val textViewComment: TextView = itemView.findViewById(R.id.location_info)

        fun bind(member: MemberAll.MemberAllItem) {
            Glide.with(context)
                .load(member.image) // Assuming 'member.image' is the URL or path to the image
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.placeholder) // Error image if Glide fails to load
                .into(textViewName)

            textViewGrade.text = member.intraName
            textViewLocation.text = member.comment
            textViewComment.text = member.location ?: "No comment available"
        }
    }
}