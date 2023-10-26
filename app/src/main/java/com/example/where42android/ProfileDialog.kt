package com.example.where42android

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

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

    fun showGroupDialog(name: String) {
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
            dialog.dismiss()
        }
        dialog.show()
    }
}
