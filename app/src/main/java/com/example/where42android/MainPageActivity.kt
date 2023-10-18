package com.example.where42android

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainPageActivity : AppCompatActivity() {

    var profileList = arrayListOf<profile_list>(
        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
        profile_list("Jooypark","beautiful", "개포 c2r5s6", "profile_photo_example" ),
        profile_list("jaju","graphics master", "개포 c2r5s6", "profile_photo_example"),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val otherLayout = LayoutInflater.from(this).inflate(R.layout.activity_list_view, null)
        val mainListView: ListView = otherLayout.findViewById(R.id.peopleListView) as ListView
        val ListAdapter = ListViewAdapter(this, profileList)
        mainListView.setAdapter(ListAdapter)
    }
}