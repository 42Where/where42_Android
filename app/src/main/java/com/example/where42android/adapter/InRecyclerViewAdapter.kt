package com.example.where42android.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.where42android.R
import com.example.where42android.databinding.HolderRecyclerviewInMeberListBinding
import com.example.where42android.fragment.loadImage
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
            //            Glide.with(binding.root.context).load(item.emoji).into(binding.emoji)
//            Glide.with(binding.root.context)
//                .load(item.emoji) // item.emoji에 이미지 URL이 있어야 합니다.
//                .placeholder(R.drawable.placeholder) // 로딩 중에 표시할 이미지
//                .error(R.drawable.placeholder) // 이미지 로드 실패 시 표시할 이미지
//                .into(binding.emoji) // 이미지를 표시할 ImageView입니다.
            binding.emoji.setImageResource(R.drawable.placeholder) // placeholder는 원하는 기본 이미지 리소스로 변경
//            binding.imageBackground.setBackgroundResource(R.color.black)

            // 백그라운드 스레드에서 이미지 로딩 실행
            Thread {
                val bitmap = loadImage(item.emoji)

                // UI 업데이트는 메인(UI) 스레드에서 해야 합니다.
                binding.root.post {
                    // 로드된 이미지가 null이 아니면 이미지뷰에 설정
                    bitmap?.let {
                        binding.emoji.setImageBitmap(it)
                    }
                }
            }.start()
            binding.model = item

        }
    }

}