package com.seoul.where42android.main.v3

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import android.widget.GridLayout
import android.widget.TextView
import com.seoul.where42android.R


class MainVisualization : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualization)

        // XML에서 정의된 차트를 참조
        val chartC1 = findViewById<PieChart>(R.id.chart_c1)
        val chartC2 = findViewById<PieChart>(R.id.chart_c2)
        val chartC3 = findViewById<PieChart>(R.id.chart_c5)
        val chartC4 = findViewById<PieChart>(R.id.chart_c6)
        val chartC5 = findViewById<PieChart>(R.id.chart_cx1)
        val chartC6 = findViewById<PieChart>(R.id.chart_cx2)

        // 데이터 설정
        val usageData = listOf(70f, 65f, 80f, 50f, 75f, 60f)
        val charts = listOf(chartC1, chartC2, chartC3, chartC4, chartC5, chartC6)

        // 각 차트에 데이터 적용
        for (i in charts.indices) {
            setupPieChart(charts[i], usageData[i])
        }
    }

    private fun setupPieChart(pieChart: PieChart, usage: Float) {
        val entries = listOf(
            PieEntry(usage, "사용 중"),
            PieEntry(100 - usage, "여유 공간")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.GREEN, Color.DKGRAY)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        pieChart.data = PieData(dataSet)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 50f
        pieChart.setTransparentCircleRadius(55f)
        pieChart.invalidate() // 차트 갱신
    }
}

