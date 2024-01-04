package com.example.where42android.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.where42android.Base_url_api_Retrofit.GroupChangeName
import com.example.where42android.Base_url_api_Retrofit.GroupDelete
import com.example.where42android.Base_url_api_Retrofit.GroupDeleteResponse
import com.example.where42android.Base_url_api_Retrofit.GroupNameRequest
import com.example.where42android.Base_url_api_Retrofit.GroupNameResponse
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.R
import com.example.where42android.main.MainDeleteGroupDetailList
import com.example.where42android.profile_list
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileDialog (private val context: Context) {
    private val dialog = Dialog(context)

    fun showProfileDialog(profile: profile_list) {
        dialog.setContentView(R.layout.profile_popup)

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val info_intradId = dialog.findViewById<TextView>(R.id.intraID)
        // 다른 TextView들도 위와 같이 가져오세요.

        info_intradId.text = profile.intraId

        val btnDelete = dialog.findViewById<Button>(R.id.Delete)

        btnDelete.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}

class GroupDialog (private val context: Context) {
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
            val editdialog = Dialog(context)
            editdialog.setContentView(R.layout.activity_edittext_popup)

            // 레이아웃 내의 뷰들을 가져옴
            val title = editdialog.findViewById<TextView>(R.id.title)
            title.text = "그룹 이름을 변경해주세요"

            val input = editdialog.findViewById<EditText>(R.id.input)
            val cancel = editdialog.findViewById<Button>(R.id.cancel)
            val submit = editdialog.findViewById<Button>(R.id.submit)

            // Dialog 크기 설정
//            editdialog.window?.setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
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
//                //input == changegroupname,  groupNameChange = groupId.toInt()
                    val retrofitAPI =
                        RetrofitConnection.getInstance().create(GroupChangeName::class.java)

                    val groupChangedata = GroupNameRequest(groupId.toInt(), groupName)
                    val call = retrofitAPI.groupChangeName(groupChangedata)

                    call.enqueue(object : Callback<GroupNameResponse> {
                        override fun onResponse(
                            call: Call<GroupNameResponse>,
                            response: Response<GroupNameResponse>
                        ) {
                            if (response.isSuccessful) {
                                val deletedGroup = response.body()
                                callback(true) // 삭제 성공 시 true 전달
                                // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
                            } else {
                                // API 호출에 실패한 경우
                                Log.e(
                                    "DELETE_ERROR",
                                    "Failed to delete group. Error code: ${response.code()}"
                                )
                                // 실패 처리 로직을 수행하세요.
                                callback(false) // 삭제 실패 시 false 전달
                            }
                        }

                        override fun onFailure(call: Call<GroupNameResponse>, t: Throwable) {
                            // 네트워크 오류 등의 이유로 API 호출이 실패한 경우
                            Log.e("DELETE_ERROR", "Network error occurred. Message: ${t.message}")
                            // 실패 처리 로직을 수행하세요.
                            callback(false) // 삭제 실패 시 false 전달
                        }
                    })
                }
                else
                {

                }

//                dialog.dismiss()
            }

//            dialog.dismiss() // 기존 GroupDialog 닫기
//            val retrofitAPI = RetrofitConnection.getInstance().create(GroupChangeName::class.java)
//            val groupNameChange = groupId.toInt()
//            val call = retrofitAPI.groupChangeName(groupIdToDelete)
//
            dialog.dismiss()
            editdialog.show()
        }

        //그룹 수정하기
        btnEdit.setOnClickListener {
            //넘겨줄 거 GroupId, GroupName
            val intent = Intent(context, MainDeleteGroupDetailList::class.java) // YourNextActivity에는 이동하길 원하는 액티비티를 명시합니다.
            intent.putExtra("GROUP_ID", groupId) // groupId는 int 형태로 가정
            intent.putExtra("GROUP_NAME", name) // name은 String 형태로 가정
            context.startActivity(intent) // 액티비티 전환
//            finish() // 현재 액티비티 종료 (선택사항)
            dialog.dismiss()
        }

        //그룹 삭제하기
        btnDelete.setOnClickListener {
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
                val retrofitAPI = RetrofitConnection.getInstance().create(GroupDelete::class.java)
                val groupIdToDelete = groupId.toInt() // 여기에 삭제하려는 groupId를 설정하세요.
                val call = retrofitAPI.deleteGroup(groupIdToDelete)

                call.enqueue(object : Callback<GroupDeleteResponse> {
                    override fun onResponse(
                        call: Call<GroupDeleteResponse>,
                        response: Response<GroupDeleteResponse>
                    ) {
                        if (response.isSuccessful) {
                            val deletedGroup = response.body()
                            callback(true) // 삭제 성공 시 true 전달
                            // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
                        } else {
                            // API 호출에 실패한 경우
                            Log.e("DELETE_ERROR", "Failed to delete group. Error code: ${response.code()}")
                            // 실패 처리 로직을 수행하세요.
                            callback(false) // 삭제 실패 시 false 전달
                        }
                    }

                    override fun onFailure(call: Call<GroupDeleteResponse>, t: Throwable) {
                        // 네트워크 오류 등의 이유로 API 호출이 실패한 경우
                        Log.e("DELETE_ERROR", "Network error occurred. Message: ${t.message}")
                        // 실패 처리 로직을 수행하세요.
                        callback(false) // 삭제 실패 시 false 전달
                    }
                })
                deletegroup.dismiss()
            }
            deletegroup.show()
        }
        dialog.show()
    }
}
