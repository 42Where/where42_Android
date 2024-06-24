package com.seoul.where42android.adapter


import SharedViewModel_GroupsMembersList
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seoul.where42android.R
import com.seoul.where42android.databinding.HolderRecyclerviewInMeberListBinding
import com.seoul.where42android.main.MainPageActivity
import com.seoul.where42android.model.RecyclerInViewModel

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

        fun bind(item: RecyclerInViewModel) {
            with(binding) {
                optionEdit.setOnClickListener {
                    showEditDialog(root.context, item)
                }

                if (item.location == "퇴근") {
                    location.setBackgroundResource(R.drawable.location_outcluster)
                    val strokeColor = Color.parseColor("#132743")
                    location.setTextColor(strokeColor)
                }

                    binding.root.post {
                        Glide.with(binding.root.context)
                            .load(item.emoji)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.nointraimage)
                            .skipMemoryCache(true)
                            .into(binding.emoji)

                        if (item.location == "퇴근")
                        {
                            binding.emoji.borderWidth  = 0
                        }

                    }

                location.post {
                    val leftPadding = 20
                    val rightPadding = 20
                    location.setPadding(leftPadding, 0, rightPadding, 0)
                    adjustBackgroundSizeWithPadding(location, leftPadding, rightPadding)
                }
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
//            .error(R.drawable.placeholder) // 이미지 로드 실패 시 표시할 이미지
            .error(R.drawable.nointraimage)
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
                val sharedViewModel = ViewModelProvider(context as MainPageActivity).get(
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


