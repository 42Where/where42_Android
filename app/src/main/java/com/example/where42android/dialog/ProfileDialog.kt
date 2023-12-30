package com.example.where42android.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.where42android.Base_url_api_Retrofit.GroupDelete
import com.example.where42android.Base_url_api_Retrofit.GroupDeleteResponse
import com.example.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.example.where42android.R
import com.example.where42android.fragment.MainFragment
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
        dialog.setContentView(R.layout.group_popup)

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

        btnEditName.setOnClickListener {
            dialog.dismiss()
        }
        btnEdit.setOnClickListener {
            dialog.dismiss()
        }
        btnDelete.setOnClickListener {
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

            dialog.dismiss()

        }
        dialog.show()
    }
}
