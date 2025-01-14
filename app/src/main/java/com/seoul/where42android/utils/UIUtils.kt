//package com.seoul.where42android.utils
//
//import SharedViewModel_Profile
//import android.app.Dialog
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.util.Log
//import android.view.Gravity
//import android.widget.Button
//import android.widget.ImageButton
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.res.ResourcesCompat
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.ViewModelStoreOwner
//import androidx.lifecycle.lifecycleScope
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.request.RequestOptions
//import com.seoul.where42android.Base_url_api_Retrofit.Member
//import com.seoul.where42android.R
//import com.seoul.where42android.databinding.ActivityMainPageBinding
//import com.seoul.where42android.fragment.MainFragment
//import com.seoul.where42android.utils.ApiUtils
//import com.seoul.where42android.utils.TokenManager
//import de.hdodenhof.circleimageview.CircleImageView
//import kotlinx.coroutines.launch
//
//object UIUtils {
//    fun updateProfileUI(activity: AppCompatActivity, member: Member) {
//        val mainImage = activity.findViewById<CircleImageView>(R.id.profile_photo)
//        val imageUrl = member.image
//        Glide.with(activity)
//            .load(imageUrl)
//            .apply(RequestOptions().circleCrop())
//            .error(R.drawable.nointraimage)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .into(mainImage)
//
//        val intraIdTextView = activity.findViewById<TextView>(R.id.intraId)
//        intraIdTextView.text = member.intraName
//
//        val commentView = activity.findViewById<TextView>(R.id.comment_view)
//        commentView.text = member.comment
//
//        val locationView = activity.findViewById<TextView>(R.id.locationInfo)
//        locationView.text = member.location
//
//        if (member.location == "퇴근") {
//            locationView.setBackgroundResource(R.drawable.location_outcluster)
//            val strokeColor = Color.parseColor("#132743")
//            locationView.setTextColor(strokeColor)
//        }
//        locationView.setPadding(20, 0, 20, 0)
//    }
//}
