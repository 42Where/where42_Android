package com.example.where42android.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.URL

// 백그라운드 스레드에서 실행해야 합니다. UI 스레드에서는 사용하지 마세요.
fun loadImage(urlString: String): Bitmap? {
    return try {
        val url = URL(urlString)
        val connection = url.openConnection()
        connection.connect()
        val input = connection.getInputStream()
        BitmapFactory.decodeStream(input)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}