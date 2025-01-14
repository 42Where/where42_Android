package com.seoul.where42android.dialog

import com.seoul.where42android.ViewModel.SharedViewModelGroupsMembers
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.seoul.where42android.R
import com.seoul.where42android.main.UserSettings
import com.seoul.where42android.main.MainAddGroupDetailList
import com.seoul.where42android.main.MainDeleteGroupDetailList

class GroupDialog (private val context: Context, val viewModel: SharedViewModelGroupsMembers) {
    private val usersetting = UserSettings.getInstance()
    private val dialog = Dialog(context)

    fun showGroupDialog(name: String, groupId:Number, callback: (Boolean) -> Unit) {
        dialog.setContentView(R.layout.activity_editgroup_popup)

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val groupName = dialog.findViewById<TextView>(R.id.GroupName)
        // 다른 TextView들도 위와 같이 가져오세요.

        groupName.text = name

        val btnEditName = dialog.findViewById<Button>(R.id.EditName)
        val btnEdit = dialog.findViewById<Button>(R.id.Edit)
        val btnDelete = dialog.findViewById<Button>(R.id.Delete)


        //그룹 이름 수정하기
        btnEditName.setOnClickListener {
            //default 그룹은 그룹 이름 변경이 안 되도록 변경해야함.

            if (usersetting.defaultGroup == groupId)
            {
                val noEditDefaultDialog = Dialog(context)
                noEditDefaultDialog.setContentView(R.layout.activity_editstatus_popup)

                val cancel = noEditDefaultDialog.findViewById<Button>(R.id.cancel)
                cancel.visibility = View.GONE

                val title = noEditDefaultDialog.findViewById<TextView>(R.id.title)
                title.text = "친구 그룹은 이름을 바꿀 수 없습니다."


                noEditDefaultDialog.window?.setGravity(Gravity.CENTER)
                noEditDefaultDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val submit = noEditDefaultDialog.findViewById<Button>(R.id.submit)
                submit.setOnClickListener {
                    noEditDefaultDialog.dismiss()
                }
                noEditDefaultDialog.show()
            }
            else {
                val editdialog = Dialog(context)
                editdialog.setContentView(R.layout.activity_edittext_popup)

                // 레이아웃 내의 뷰들을 가져옴
                val title = editdialog.findViewById<TextView>(R.id.title)
                title.text = "그룹 이름 변경"

                val input = editdialog.findViewById<EditText>(R.id.input)
                input.hint = "그룹 이름을 변경을 변경해주세요."
                val cancel = editdialog.findViewById<Button>(R.id.cancel)
                val submit = editdialog.findViewById<Button>(R.id.submit)

                editdialog.window?.setGravity(Gravity.CENTER)
                editdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // 취소 버튼 클릭 시 다이얼로그 닫기
                cancel.setOnClickListener {
//                dialog.dismiss()
                    editdialog.dismiss()
                    dialog.show()
                }

                // 확인 버튼 클릭 시 원하는 작업 수행 후 다이얼로그 닫기
                //여기가 확인 버튼 누르면 닉네임 변경
                submit.setOnClickListener {
                    //스트링 공백 처리를 trim으로 함
                    val groupName = input.text.toString().trim()

                    if (groupName.isNotEmpty()) {
                        Log.d("here2", "here2")
                        viewModel.editGroupName(groupName, groupId.toInt(), context)
                        editdialog.dismiss()
                        dialog.dismiss()
                    }
                }
                editdialog.show()
            }

        }

        //멤버 수정하기
        btnEdit.setOnClickListener {
            //피그마에서 추가된 멤버 추가하기, 멤버 수정하기 부분을 선택할 수 있는 창을 만들어야함.
            val editMember = Dialog(context)
            editMember.setContentView(R.layout.activity_editgroupmember_popup)

            editMember.setCanceledOnTouchOutside(true)
            editMember.setCancelable(true)
            editMember.window?.setGravity(Gravity.CENTER)
            editMember.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val btnEditPlus = editMember.findViewById<Button>(R.id.member_plus)
            val btnEditDelete = editMember.findViewById<Button>(R.id.member_delete)


            //그룹 멤버 추가하기
            btnEditPlus.setOnClickListener {
                editMember.dismiss()
                val intent = Intent(context, MainAddGroupDetailList::class.java) // YourNextActivity에는 이동하길 원하는 액티비티를 명시합니다.
                intent.putExtra("GROUP_ID", groupId) // groupId는 int 형태로 가정
                intent.putExtra("GROUP_NAME", name) // name은 String 형태로 가정
                context.startActivity(intent) // 액티비티 전환
                dialog.dismiss()
            }

            btnEditDelete.setOnClickListener {
                //밑 부분은 멤버 삭제하는 코드
                //넘겨줄 거 GroupId, GroupName
                editMember.dismiss()
                val intent = Intent(context, MainDeleteGroupDetailList::class.java) // YourNextActivity에는 이동하길 원하는 액티비티를 명시합니다.
                intent.putExtra("GROUP_ID", groupId) // groupId는 int 형태로 가정
                intent.putExtra("GROUP_NAME", name) // name은 String 형태로 가정
                context.startActivity(intent) // 액티비티 전환
//            finish() // 현재 액티비티 종료 (선택사항)
                dialog.dismiss()
            }
            editMember.show()
        }

        //3. 그룹 삭제하기
        btnDelete.setOnClickListener {

            if (usersetting.defaultGroup == groupId)
            {
                val noEditDefaultDialog = Dialog(context)
                noEditDefaultDialog.setContentView(R.layout.activity_editstatus_popup)


                val cancel = noEditDefaultDialog.findViewById<Button>(R.id.cancel)
                cancel.visibility = View.GONE

                val title = noEditDefaultDialog.findViewById<TextView>(R.id.title)
                title.text = "친구 그룹은 삭제할 수 없습니다."

                noEditDefaultDialog.window?.setGravity(Gravity.CENTER)
                noEditDefaultDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val submit = noEditDefaultDialog.findViewById<Button>(R.id.submit)
                submit.setOnClickListener {
                    noEditDefaultDialog.dismiss()
                }
                noEditDefaultDialog.show()
            }
            else
            {
                val deletegroup = Dialog(context)
                deletegroup.setContentView(R.layout.activity_editstatus_popup)

                deletegroup.setCanceledOnTouchOutside(true)
                deletegroup.setCancelable(true)
                deletegroup.window?.setGravity(Gravity.CENTER)
                deletegroup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val title = deletegroup.findViewById<TextView>(R.id.title)
                title.text = "그룹을 정말 삭제하시겠습니까?"

                val btnCancel = deletegroup.findViewById<Button>(R.id.cancel)
                val btnSubmit = deletegroup.findViewById<Button>(R.id.submit)

                btnCancel.setOnClickListener {
                    deletegroup.dismiss()
                }

                btnSubmit.setOnClickListener {
                    viewModel.deleteGroup(groupId.toInt(), context)
                    deletegroup.dismiss()
                    dialog.dismiss()
                }
                deletegroup.show()
            }

        }
        dialog.show()
    }



}
