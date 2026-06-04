package com.seoul.where42android.main

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.seoul.where42android.R
import com.seoul.where42android.fragment.PieChartView
import com.seoul.where42android.main.v3.MainAnnouncement
import com.seoul.where42android.main.v3.MainCompass

class MainClusterStatActivity : AppCompatActivity() {

    data class ClusterInfo(val name: String, val used: Int, val total: Int)

    private val clusters = listOf(
        ClusterInfo("C1",  45, 63),
        ClusterInfo("C2",  60, 80),
        ClusterInfo("C5",  42, 63),
        ClusterInfo("C6",  55, 80),
        ClusterInfo("CX1", 20, 28),
        ClusterInfo("CX2", 40, 56),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cluster_stat)

        val pieHeight = (160 * resources.displayMetrics.density).toInt()

        listOf(R.id.pie_row_1, R.id.pie_row_2, R.id.pie_row_3)
            .zip(clusters.chunked(2))
            .forEach { (rowId, rowClusters) ->
                val row = findViewById<LinearLayout>(rowId)
                rowClusters.forEach { cluster ->
                    val pieView = PieChartView(this).apply {
                        used = cluster.used.toFloat()
                        total = cluster.total.toFloat()
                        label = cluster.name
                        subLabel = "(${cluster.used} / ${cluster.total})"
                    }
                    row.addView(pieView, LinearLayout.LayoutParams(0, pieHeight, 1f))
                }
            }

        val headerBinding = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        headerBinding.findViewById<ImageButton>(R.id.setting_button)?.setOnClickListener {
            startActivity(Intent(this, MainSettingPage::class.java))
        }
        headerBinding.findViewById<ImageButton>(R.id.compass_button)?.setOnClickListener {
            startActivity(Intent(this, MainCompass::class.java))
        }
        headerBinding.findViewById<ImageButton>(R.id.ann_button)?.setOnClickListener {
            startActivity(Intent(this, MainAnnouncement::class.java))
        }

        val footerBinding = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.footer)
        footerBinding.findViewById<ImageButton>(R.id.home_button)?.setOnClickListener {
            startActivity(Intent(this, MainPageActivity::class.java))
            finish()
        }
        footerBinding.findViewById<ImageButton>(R.id.search_button)?.setOnClickListener {
            startActivity(Intent(this, MainSearchPage::class.java))
        }
    }
}
