package com.example.where42android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CreateGroupActivity : AppCompatActivity() {
    var friendProfileList = arrayListOf<profile_list>(
        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
        profile_list("Jaeyojun","handsome", "퇴근", "profile_photo_example",),
        profile_list("Jaeyojun","handsome", "개포 c2r5s6", "profile_photo_example",),
        profile_list("Jooypark","beautiful", "개포 c2r5s6", "profile_photo_example" ),
        profile_list("jaju","graphics master", "퇴근", "profile_photo_example"),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val receivedIntent = intent
        if (receivedIntent != null && receivedIntent.hasExtra("userInputKey")) {
            val userInput = receivedIntent.getStringExtra("userInputKey")
            Toast.makeText(this, "$userInput", Toast.LENGTH_SHORT).show()
        }

        val friendRecyclerView: RecyclerView = findViewById<RecyclerView?>(R.id.friend_list)
        val friendRecyclerViewAdapter = RecyclerViewAdapter(this, friendProfileList, false)
        friendRecyclerView.layoutManager = LinearLayoutManager(this)
        friendRecyclerView.adapter = friendRecyclerViewAdapter

        val searchView: SearchView = findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = ArrayList(friendProfileList.filter { profile ->
                    profile.intraId.contains(newText.orEmpty(), ignoreCase = true)
                })
                friendRecyclerViewAdapter.updateList(filteredList)
                return true
            }
        })
    }
}