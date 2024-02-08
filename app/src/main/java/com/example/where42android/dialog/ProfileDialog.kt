package com.example.where42android.dialog

import SharedViewModel_GroupsMembersList
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
import com.example.where42android.Base_url_api_Retrofit.GroupChangeName
import com.example.where42android.Base_url_api_Retrofit.GroupNameRequest
import com.example.where42android.Base_url_api_Retrofit.GroupNameResponse
import com.example.where42android.Base_url_api_Retrofit.JoinAPI
import com.example.where42android.Base_url_api_Retrofit.JoinResponse
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.R
import com.example.where42android.UserSettings
import com.example.where42android.main.MainAddGroupDetailList
import com.example.where42android.main.MainDeleteGroupDetailList
import com.example.where42android.profile_list
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProfileDialog (private val context: Context) {
    private val dialog = Dialog(context)

    fun showProfileDialog(profile: profile_list) {
        dialog.setContentView(R.layout.activity_profile_popup)

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val info_intradId = dialog.findViewById<TextView>(R.id.intra_id)
        // 다른 TextView들도 위와 같이 가져오세요.

        info_intradId.text = profile.intraId

        val btnDelete = dialog.findViewById<Button>(R.id.Delete)

        btnDelete.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}



class AgreeDialog (private val context: Context) {
    private val agreedialog = Dialog(context)

    fun saveAgreementToSharedPreferences(context: Context, agreement: String?) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
//        editor.putString("AuthToken", token)
        editor.putString("agreement", agreement)
        editor.apply()
    }


    fun showAgreeDialog(token:String, intraId: String?, agreement:String?, context: Context, callback: (Boolean) -> Unit)
    {
        agreedialog.setContentView(R.layout.activity_profile_agree)
        agreedialog.setCanceledOnTouchOutside(true)
        agreedialog.setCancelable(true)

        agreedialog.window?.setGravity(Gravity.CENTER)
        agreedialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnCancel = agreedialog.findViewById<TextView>(R.id.cancel)
        val btnSubmit = agreedialog.findViewById<Button>(R.id.submit)
        btnSubmit.setOnClickListener {

            val intraIdtoInt = intraId?.toInt()?: -1

            //Join api 호출해야됨
            Log.d("token_check", "api join token : ${token}")
            val retrofitAPI = RetrofitConnection.getInstance(token).create(JoinAPI::class.java)

//            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = retrofitAPI.join(intraIdtoInt)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Log.e("join_api", "SUC")
                                saveAgreementToSharedPreferences(context, "true")
                                callback(true)
                            }
                            else {
                                Log.e("join_api", "fail1")
                                when (response.code()){
                                    400 -> {
                                        Log.e("join_api", "fail1")
                                        callback(false)
                                    }
                                    401 -> {
                                        Log.e("join_api", "fail2")
                                        callback(false)
                                    }
                                }
                            }
                        }


                    } catch (e:IOException) {
                        Log.e ("join_check", "message : ${e.message}")
                        callback(false)
                    }

                }
//            }
//            val call = retrofitAPI.join(intraId?.toInt() ?: -1)
//            call.enqueue(object : retrofit2.Callback<JoinResponse> {
//                override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
//                    if (response.isSuccessful) {
//                        val joinResponse = response.body()
//                        // joinResponse를 이용하여 응답 처리
//                        Log.e("joinResponse", "joinResponse : ${joinResponse}" )
//                        callback(true)
//                    } else {
//                        // 서버 응답이 실패인 경우
//                        Log.e("joinResponse", "joinResponse f: ${response}")
//                        callback(true)
//                    }
//                }
//
//                override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
//                    Log.e("joinResponse", "joinResponse fa : ${t.message}")
//                    // 네트워크 실패 또는 예외 발생
//                }
//            })


            agreedialog.dismiss()

        }

        btnCancel.setOnClickListener {

            agreedialog.dismiss()
            callback(false)
        }
        agreedialog.show()
    }
}



class GroupDialog (private val context: Context, val viewModel: SharedViewModel_GroupsMembersList) {
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
                        viewModel.editGroupName(groupName, groupId.toInt())
                        editdialog.dismiss()
                        dialog.dismiss()

                    } else {

                    }

                }
                editdialog.show()
            }

//            dialog.dismiss() // 기존 GroupDialog 닫기
//            val retrofitAPI = RetrofitConnection.getInstance().create(GroupChangeName::class.java)
//            val groupNameChange = groupId.toInt()
//            val call = retrofitAPI.groupChangeName(groupIdToDelete)
//
//            dialog.dismiss()

        }

        //멤버 수정하기
        btnEdit.setOnClickListener {
//            dialog.dismiss()
//            val intent = Intent(context, MainDeleteGroupDetailList::class.java) // YourNextActivity에는 이동하길 원하는 액티비티를 명시합니다.
//            intent.putExtra("GROUP_ID", groupId) // groupId는 int 형태로 가정
//            intent.putExtra("GROUP_NAME", name) // name은 String 형태로 가정
//            context.startActivity(intent) // 액티비티 전환
//            finish() // 현재 액티비티 종료 (선택사항)



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
                    viewModel.deleteGroup(groupId.toInt())
//                val retrofitAPI = RetrofitConnection.getInstance().create(GroupDelete::class.java)
//                val groupIdToDelete = groupId.toInt() // 여기에 삭제하려는 groupId를 설정하세요.
//                val call = retrofitAPI.deleteGroup(groupIdToDelete)
//
//                call.enqueue(object : Callback<GroupDeleteResponse> {
//                    override fun onResponse(
//                        call: Call<GroupDeleteResponse>,
//                        response: Response<GroupDeleteResponse>
//                    ) {
//                        if (response.isSuccessful) {
//                            val deletedGroup = response.body()
////                            callback(true) // 삭제 성공 시 true 전달
//                            // 성공적으로 삭제되었으므로 적절한 처리를 수행합니다.
//
//                        } else {
//                            // API 호출에 실패한 경우
//                            Log.e("DELETE_ERROR", "Failed to delete group. Error code: ${response.code()}")
//                            // 실패 처리 로직을 수행하세요.
////                            callback(false) // 삭제 실패 시 false 전달
//                        }
//                    }
//
//                    override fun onFailure(call: Call<GroupDeleteResponse>, t: Throwable) {
//                        // 네트워크 오류 등의 이유로 API 호출이 실패한 경우
//                        Log.e("DELETE_ERROR", "Network error occurred. Message: ${t.message}")
//                        // 실패 처리 로직을 수행하세요.
////                        callback(false) // 삭제 실패 시 false 전달
//                    }
//                })
                    deletegroup.dismiss()
                    dialog.dismiss()
                }
                deletegroup.show()
            }

        }
        dialog.show()
    }



}
