package com.example.where42android.adapter


import SharedViewModel_GroupsMembersList
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.where42android.R
import com.example.where42android.databinding.HolderRecyclerviewInMeberListBinding
import com.example.where42android.fragment.loadImage
import com.example.where42android.main.MainPageActivity
import com.example.where42android.model.RecyclerInViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
import java.net.URL


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
        holder.bind(itemList[position])
    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderRecyclerviewInMeberListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context: Context = binding.root.context //ViewHolder 내부에서 context를 가져옴
        fun bind(item: RecyclerInViewModel) {
            with(binding) {
                optionEdit.setOnClickListener {
                    showEditDialog(root.context, item)
                }
                if (item.location != "퇴근") {
                    // 배경을 설정
                    val color = Color.parseColor("#132743")
                    val gradientDrawable = GradientDrawable()
                    gradientDrawable.setColor(color)
                    gradientDrawable.cornerRadius = 40f // radius 값 설정 (단위는 pixel)
                    location.background = gradientDrawable
//                    location.setTextColor(color)
                } else {
                    // 배경을 설정
                    val color = Color.parseColor("#132743")
                    val gradientDrawable = GradientDrawable()
                    gradientDrawable.cornerRadius = 40f // radius 값 설정 (단위는 pixel)
                    val strokeWidth = 2 // 테두리의 두께 설정
                    val strokeColor = Color.parseColor("#132743") // 테두리의 색상 설정
                    gradientDrawable.setStroke(strokeWidth, strokeColor)
                    location.background = gradientDrawable
                    location.setTextColor(color)
                }

                    binding.root.post {
                        Glide.with(binding.root.context)
                            .load(item.emoji)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .skipMemoryCache(true)
                            .into(binding.emoji)
                    }
                // 이미지 로드
//                Glide.with(root.context)
//                    .load(item.emoji)
//                    .placeholder(R.drawable.placeholder)
//                    .error(R.drawable.placeholder)
//                    .skipMemoryCache(true)
//                    .into(emoji)

                // TextView의 내용이 변경될 때마다 배경의 크기를 조절
                location.post {
                    val leftPadding = 20 // 왼쪽 여백 값
                    val rightPadding = 20 // 오른쪽 여백 값
                    location.setPadding(leftPadding, 0, rightPadding, 0)
                    adjustBackgroundSizeWithPadding(location, leftPadding, rightPadding)
                }
//                GlobalScope.launch(Dispatchers.IO) {
//                    withContext(Dispatchers.Main) {
//                            if (binding.location.text != "퇴근")
//                            {
//                                // UI 변경 작업
//                                val leftPadding = 20 // 왼쪽 여백 값
//                                val rightPadding = 20 // 오른쪽 여백 값
//                                binding.location.setPadding(leftPadding, 0, rightPadding, 0)
//                                adjustBackgroundSizeWithPadding(
//                                    binding.location,
//                                    leftPadding,
//                                    rightPadding
//                                )
//
//                                val color = Color.parseColor("#132743")
//                                val gradientDrawable = GradientDrawable()
//                                gradientDrawable.setColor(color)
//                                gradientDrawable.cornerRadius = 40f // radius 값 설정 (단위는 pixel)
//                                // 배경을 설정
//                                binding.location.background = gradientDrawable
//                            }
//                            else
//                            {
//                                // UI 변경 작업
//                                val leftPadding = 20 // 왼쪽 여백 값
//                                val rightPadding = 20 // 오른쪽 여백 값
//                                binding.location.setPadding(leftPadding, 0, rightPadding, 0)
//                                adjustBackgroundSizeWithPadding(
//                                    binding.location,
//                                    leftPadding,
//                                    rightPadding
//                                )
//                                val color = Color.parseColor("#132743")
//                                val gradientDrawable = GradientDrawable()
//                                gradientDrawable.cornerRadius = 40f // radius 값 설정 (단위는 pixel)
//                                val strokeWidth = 2 // 테두리의 두께 설정
//                                val strokeColor = Color.parseColor("#132743") // 테두리의 색상 설정
//                                gradientDrawable.setStroke(strokeWidth, strokeColor)
//
//                                // 배경을 설정
//                                binding.location.background = gradientDrawable
//                                binding.location.setTextColor(color)
//                            }
//                        }
//                    }
//                    binding.root.post{
//                        Glide.with(binding.root.context)
//                            .load(item.emoji)
//                            .placeholder(R.drawable.placeholder)
//                            .error(R.drawable.placeholder)
//                            .skipMemoryCache(true)
//                            .into(binding.emoji)
//                        val color = Color.parseColor("#132743")
//
//                    }
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


        val emojiImageView =
            dialog.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.emoji)
        val intraIdTextView = dialog.findViewById<TextView>(R.id.intra_id)
        val commentTextView = dialog.findViewById<TextView>(R.id.text_view)
        val locationTextView = dialog.findViewById<TextView>(R.id.location)

        // 아이템의 값을 가져와서 TextView와 CircleImageView에 설정
        Glide.with(context)
            .load(item.emoji) // item.emoji에는 이미지 URL이나 경로가 있어야 합니다.
            .placeholder(R.drawable.placeholder) // 로딩 중에 표시할 이미지
            .error(R.drawable.placeholder) // 이미지 로드 실패 시 표시할 이미지
            .into(emojiImageView) // CircleImageView에 이미지를 설정합니다.

        intraIdTextView.text = item.intra_name
        commentTextView.text = item.comment
        locationTextView.text = item.location
        val leftPadding = 20 // 왼쪽 여백 값
        val rightPadding = 20 // 오른쪽 여백 값
        locationTextView.setPadding(leftPadding, 0, rightPadding, 0)
        adjustBackgroundSizeWithPadding(locationTextView, leftPadding, rightPadding)

        val deleteFriend = dialog.findViewById<Button>(R.id.Delete)

        //친구 삭제 버튼을 누르고 난 후
        deleteFriend.setOnClickListener {

            // 삭제 버튼을 눌렀을 때 동작 정의
            // 예를 들어, 다이얼로그를 닫거나 삭제 작업을 수행할 수 있습니다.
            val deletefrienddialog = Dialog(context)
            deletefrienddialog.setContentView(R.layout.activity_editstatus_popup)

            deletefrienddialog.setCanceledOnTouchOutside(true)
            deletefrienddialog.setCancelable(true)
            deletefrienddialog.window?.setGravity(Gravity.CENTER)
            deletefrienddialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title = deletefrienddialog.findViewById<TextView>(R.id.title)
            title.text = "정말 친구를 삭제하시겠습니까?"
            val cancel = deletefrienddialog.findViewById<Button>(R.id.cancel)
            val submit = deletefrienddialog.findViewById<Button>(R.id.submit)

            submit.setOnClickListener {
                //여기가 친구 삭제하기 버튼 수락
                var sharedViewModel = ViewModelProvider(context as MainPageActivity).get(
                    SharedViewModel_GroupsMembersList::class.java
                )

                val members = mutableListOf<Int>()
                members.add(item.intra_id)
                sharedViewModel.deleteFriendGroup(item.included_group, members)

                deletefrienddialog.dismiss()
            }
            cancel.setOnClickListener {
                deletefrienddialog.dismiss()
            }
            dialog.dismiss() // 다이얼로그를 닫을 수 있도록 dismiss() 호출
            deletefrienddialog.show()
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


