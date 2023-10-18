package com.example.where42android

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener as OnQueryTextListener1


//class SearchPage : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search_page)
//
//        val items = arrayOf("Hello, Android!", "This is a simple example.", "ListView is awesome!")
//
//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
//        val listView = findViewById<ListView>(R.id.listvia)
//        var searchView: SearchView = findViewById<SearchView>(R.id.searchViewa)
//
//        listView.adapter = adapter
////
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (items.contains(query)) {
//                    adapter.filter.filter(query)
//                } else {
//                    Toast.makeText(this@SearchPage, "No Match Found", Toast.LENGTH_SHORT).show()
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.filter.filter(newText)
//                return false
//            }
//        })
//    }
//}

//class SearchPage : AppCompatActivity() {
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search_page)
//
//        val items = arrayOf("Hello, Android!", "This is a simple example.", "ListView is awesome!")
//
//
//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
//        val listView = findViewById<ListView>(R.id.listvia)
//        val searchView = findViewById<SearchView>(R.id.searchViewa)
//        listView.adapter = adapter
//
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if(items.contains(query)){
//                    adapter.filter.filter(query)
//                }else{
//                    Toast.makeText(this@SearchPage,"No Match Found", Toast.LENGTH_SHORT).show()
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.filter.filter(newText)
//                return false
//            }
//
//        })
//
//
//    }
//}

class SearchPage : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_page)

        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listView)

        // 리스트뷰에 표시할 데이터
        val data = listOf("Apple", "Banana", "Cherry", "Date", "Fig", "Grape")

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        listView.adapter = adapter

        // 검색 기능 구현
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }
}
