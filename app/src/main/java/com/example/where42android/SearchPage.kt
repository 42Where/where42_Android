package com.example.where42android

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.where42android.main.MainPageActivity

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

        /* 홈버튼 눌렀을 때 MainPageActivity로 가기*/
        val homeButton: ImageButton = this.findViewById(R.id.home_button)

        homeButton.setOnClickListener {
            try {
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "작업을 수행하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        /* 검색할 떄의 기능 구현*/
        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listView)


//
//        val listView: ListView = findViewById(R.id.listView) // 리스트뷰의 ID를 적절하게 변경해야 합니다.
//        val adapter = ArrayAdapter<Member>(this, android.R.layout.simple_list_item_1)
//        listView.adapter = adapter
//
//        getMemberService.getMembers().enqueue(object : Callback<List<Member>> {
//            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
//                if (response.isSuccessful) {
//                    val members = response.body() ?: return
//                    adapter.clear()
//                    adapter.addAll(members)
//                } else {
//
//                    // 서버에서 에러 응답을 받았을 때의 처리를 작성합니다.
//                }
//            }
//
//            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
//                // 네트워크 요청이 실패했을 때의 처리를 작성합니다.
//            }
//        })
//
//        listView.setOnItemClickListener { parent, view, position, id ->
//            val selectedMember = adapter.getItem(position)
//            // selectedMember를 사용하여 클릭 이벤트를 처리합니다.
//            // 예를 들어, 새 액티비티를 시작하거나 토스트 메시지를 표시할 수 있습니다.
//        }

//         리스트뷰에 표시할 데이터
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
