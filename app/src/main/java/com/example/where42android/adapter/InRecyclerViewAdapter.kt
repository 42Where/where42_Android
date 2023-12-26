package com.example.where42android.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.databinding.HolderRecyclerviewInMeberListBinding
import com.example.where42android.model.RecyclerInViewModel

class InRecyclerViewAdapter(context: Context, val itemList: MutableList<RecyclerInViewModel>): RecyclerView.Adapter<InRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderRecyclerviewInMeberListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderRecyclerviewInMeberListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecyclerInViewModel) {
            binding.model = item
        }
    }

}