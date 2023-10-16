package com.example.where42android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ListViewAdapter(val context: Context, val profileList: ArrayList<profile_list>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val Photo = view.findViewById<ImageView>(R.id.Photo)
        val intraId = view.findViewById<TextView>(R.id.intraid)
        val comment = view.findViewById<TextView>(R.id.Comment)
        val location = view.findViewById<TextView>(R.id.location)

        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val profile = profileList[position]
        val resourceId = context.resources.getIdentifier(profile.photo, "drawable", context.packageName)
        Photo.setImageResource(resourceId)
        intraId.text = profile.intraId
        comment.text = profile.comment
        location.text = profile.location

        return view
    }

    override fun getItem(position: Int): Any {
        return profileList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return profileList.size
    }
}