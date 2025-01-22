package com.seoul.where42android.main.v3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seoul.where42android.Base_url_api_Retrofit.AnnouncementApi
import com.seoul.where42android.Base_url_api_Retrofit.AnnouncementReponse
import com.seoul.where42android.Base_url_api_Retrofit.RetrofitConnection
import com.seoul.where42android.R
import com.seoul.where42android.adapter.AnnouncementAdapter
import com.seoul.where42android.dialog.AnnouncementBottomSheet
import com.seoul.where42android.utils.ApiUtils
import kotlinx.coroutines.launch

class MainAnnouncement : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnnouncementAdapter
    private val announcements = mutableListOf<AnnouncementReponse>() // 공지 데이터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)

        recyclerView = findViewById(R.id.recycler_view)
        adapter = AnnouncementAdapter(announcements) { announcement ->
            val bottomSheet = AnnouncementBottomSheet(
                title = announcement.title,
                description = announcement.content,
                createdDate = announcement.createAt,
                updatedDate = announcement.updateAt
            )
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        // 공지 데이터 생성
        generateDummyData(0, 10)
    }

    // 더미 데이터 생성
    private fun generateDummyData(page: Int, size: Int) {
//        announcements.add(AnnouncementReponse("기능 추가", "2025-01-07"))
//        announcements.add(AnnouncementReponse("UI/UX 개선", "2024-12-13"))
//        announcements.add(AnnouncementReponse("기능 추가", "2024-12-10"))
        lifecycleScope.launch {
            try {

                val response = ApiUtils.performApiRequest(this@MainAnnouncement) { accessToken ->
                    val retrofitAPI = RetrofitConnection.getInstance(accessToken).create(
                        AnnouncementApi::class.java)
                    retrofitAPI.fetchAnnouncements(page, size)
                }
                if (response != null) {
                    val responseData = response.body()
                    responseData?.let {
                        adapter.updateData(it.announcements) // 어댑터에 데이터 전달
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}")
            }
        }
    }
}