//package com.seoul.where42android.adapter
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.seoul.where42android.R
//import com.seoul.where42android.databinding.CompassRowitemBinding
//import com.seoul.where42android.databinding.CompassSetitemBinding
//import com.seoul.where42android.main.v3.Seat
//import com.seoul.where42android.main.v3.SeatRow
//
//class SeatAdapter(private val rows: List<SeatRow>) :
//    RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {
//
//    inner class SeatViewHolder(val binding: CompassRowitemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(row: SeatRow) {
//            binding.rowLabel.text = row.rowLabel
//
//            // 내부 RecyclerView 설정
//            binding.innerRecyclerView.apply {
//                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
//                setHasFixedSize(true)
//                isNestedScrollingEnabled = false // 중첩 스크롤 비활성화
//            }
//
//            // 각 row 내부의 좌석들 표시
//            val seatAdapter = InnerSeatAdapter(row.seats)
//            binding.innerRecyclerView.adapter = seatAdapter
//            seatAdapter.notifyDataSetChanged()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
//        val binding = CompassRowitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return SeatViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
//        holder.bind(rows[position])
//        Log.d("SeatAdapter", "Binding row: ${rows[position]}")
//    }
//
//
//    override fun getItemCount() = rows.size
//}
//
//class InnerSeatAdapter(private val seats: List<Seat>) :
//    RecyclerView.Adapter<InnerSeatAdapter.InnerSeatViewHolder>() {
//
//    inner class InnerSeatViewHolder(val binding: CompassSetitemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(seat: Seat) {
//            // 좌석 번호와 이미지를 설정
//            binding.seatNumber.text = seat.seatNumber.toString()
//
//            if (seat.occupantImage != null) {
//                Glide.with(binding.root.context)
//                    .load(seat.occupantImage)
//                    .placeholder(R.drawable.ic_action_computer)
//                    .into(binding.seatImage)
//            } else {
//                binding.seatImage.setImageResource(R.drawable.ic_action_computer)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerSeatViewHolder {
//        val binding = CompassSetitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return InnerSeatViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: InnerSeatViewHolder, position: Int) {
//        holder.bind(seats[position])
//        Log.d("SeatAdapter", "In Binding row: ${seats[position]}")
//    }
//
//    override fun getItemCount() = seats.size
//}
