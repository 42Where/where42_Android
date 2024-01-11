package com.example.where42android.adapter


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import com.bumptech.glide.request.transition.Transition

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.where42android.R
import com.example.where42android.databinding.HolderRecyclerviewInMeberListBinding
import com.example.where42android.fragment.loadImage
import com.example.where42android.model.RecyclerInViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*



class InRecyclerViewAdapter(
    context: Context,
    val itemList: MutableList<RecyclerInViewModel>,
   ): RecyclerView.Adapter<InRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderRecyclerviewInMeberListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderRecyclerviewInMeberListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context: Context = binding.root.context //ViewHolder 내부에서 context를 가져옴

        fun bind(item: RecyclerInViewModel) {

            binding.optionEdit.setOnClickListener {
                showEditDialog(binding.root.context, item)
            }

            val leftPadding = 20 // 왼쪽 여백 값
            val rightPadding = 20 // 오른쪽 여백 값


            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    // UI 변경 작업
                    binding.location.setPadding(leftPadding, 0, rightPadding, 0)
                    // 여기서는 코루틴 밖에서 UI 갱신 작업을 수행하므로 문제를 일으키지 않아야 합니다.
                }
                // 코루틴에서 추가 작업 수행
                adjustBackgroundSizeWithPadding(binding.location, leftPadding, rightPadding)
                binding.emoji.setImageResource(R.drawable.placeholder) // placeholder는 원하는 기본 이미지 리소스로 변경
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


                // 배경 변경 - 조건에 따라
                if (item.location == "퇴근") {
//                    binding.location.setBackgroundColor(Color.WHITE) // 퇴근이면 배경을 흰색으로 설정
                    val drawable = ContextCompat.getDrawable(context, R.drawable.location_sharpe_leave)

                    drawable?.let {
                        binding.location.background = it
                        val color = Color.parseColor("#132743")
                        binding.location.setTextColor(color)
                    }
                }

                //이게 훨씬 빠른데 속도가 너무 다르네,,,
//                Glide.with(binding.root.context)
//                    .load(item.emoji) // item.emoji에 이미지 URL이 있어야 합니다.
//                    .placeholder(R.drawable.placeholder) // 로딩 중에 표시할 이미지
//                    .error(R.drawable.placeholder) // 이미지 로드 실패 시 표시할 이미지
//                    .into(binding.emoji) // 이미지를 표시할 ImageView입니다.
                }

            binding.model = item
            }
        }
    }

    private fun showEditDialog(context: Context, item: RecyclerInViewModel) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.activity_profile_popup)

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val emojiImageView = dialog.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.emoji)
        val intraIdTextView = dialog.findViewById<TextView>(R.id.intra_id)
        val commentTextView = dialog.findViewById<TextView>(R.id.text_view)
        val locationTextView = dialog.findViewById<TextView>(R.id.location)

        // 아이템의 값을 가져와서 TextView와 CircleImageView에 설정
        Glide.with(context)
            .load(item.emoji) // item.emoji에는 이미지 URL이나 경로가 있어야 합니다.
            .placeholder(R.drawable.placeholder) // 로딩 중에 표시할 이미지
            .error(R.drawable.placeholder) // 이미지 로드 실패 시 표시할 이미지
            .into(emojiImageView) // CircleImageView에 이미지를 설정합니다.

        intraIdTextView.text = item.intra_id
        commentTextView.text = item.comment
        locationTextView.text = item.location

        val deleteFriend = dialog.findViewById<Button>(R.id.Delete)
        deleteFriend.setOnClickListener {
            // 삭제 버튼을 눌렀을 때 동작 정의
            // 예를 들어, 다이얼로그를 닫거나 삭제 작업을 수행할 수 있습니다.
            dialog.dismiss() // 다이얼로그를 닫을 수 있도록 dismiss() 호출
        }

        dialog.show() // 다이얼로그를 화면에 표시

    }

    // TextView의 내용이 변경될 때마다 배경의 크기를 조절하는 함수
    fun adjustBackgroundSizeWithPadding(textView: TextView, leftPadding: Int, rightPadding: Int) {
        textView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val layout = textView.layout
            if (layout != null) {
                val textWidth = layout.getLineWidth(0) + leftPadding + rightPadding
                val textHeight = textView.height
                val layoutParams = textView.layoutParams
                layoutParams.width = textWidth.toInt()
                layoutParams.height = textHeight
                textView.layoutParams = layoutParams
            }
        }
    }

