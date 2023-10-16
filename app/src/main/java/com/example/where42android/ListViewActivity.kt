package com.example.where42android

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListViewActivity  : AppCompatActivity() {

    var profileList = arrayListOf<profile_list>(
        profile_list("Jaeyojun","handsome", "Gone", "jaeyojun_photo",),
        profile_list("Jooypark","beautiful", "Gone", "jooypark_photo" ),
        profile_list("jaju","graphics master", "Gone", "jaju_photo"),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        var mainListView : ListView = findViewById(R.id.peopleListView) as ListView
        val ExhibitionAdapter = ListViewAdapter(this, profileList)
        mainListView.setAdapter(ExhibitionAdapter)

        }
}