import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seoul.where42android.R
import com.seoul.where42android.databinding.ActivityCreateAddFriendBinding
import com.seoul.where42android.main.friendListObject
import com.seoul.where42android.main.intraNameObject
import com.seoul.where42android.model.SearchRecyclerInViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchRecyclerViewAdapter(private val context: Context,
    val itemList : MutableList<SearchRecyclerInViewModel>)
    : RecyclerView.Adapter<SearchRecyclerViewAdapter.OutViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutViewHolder
    {
        val binding = ActivityCreateAddFriendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OutViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OutViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    // 체크박스 클릭 이벤트 리스너
    private var checkBoxClickListener: ((Boolean, Int, Int) -> Unit)? = null

    fun setOnCheckBoxClickListener(listener: (Boolean, Int, Int) -> Unit) {
        checkBoxClickListener = listener
    }

    inner class OutViewHolder(
        var binding: ActivityCreateAddFriendBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: SearchRecyclerInViewModel) {
//            GlobalScope.launch(Dispatchers.IO) {
//                withContext(Dispatchers.Main) {
                    Glide.with(binding.root.context)
                        .load(item.emoji)
                        .placeholder(R.drawable.placeholder)
//                        .error(R.drawable.placeholder)
                        .error(R.drawable.nointraimage)
                        .skipMemoryCache(true)
                        .into(binding.profilePhoto)
                    val color = Color.parseColor("#132743")
                    binding.intraId.text = item.intra_name
//                    binding.Comment.text = item.comment
//                    binding.locationInfo.text = item.location
//                }
//            }

            if (friendListObject.searchItem(item.intra_id) != null)
            {
                binding.checkBox.visibility = View.GONE
            }



            val intraNameObject = intraNameObject.getcheckFriendList()
//            Log.d("intraNameObject", "intraNameObject : ${intraNameObject}")
            var checkName = false
            for (i in intraNameObject) {
                if (i == item.intra_id)
                {
                    checkName = true
                    binding.checkBox.isChecked = true

//                    Log.d("intraNameObject", "i : ${i}, item.intra_id : ${item.intra_name}")
                    break
                }
            }

            binding.checkBox.setOnClickListener {
                val checked = binding.checkBox.isChecked
                checkBoxClickListener?.invoke(checked, adapterPosition, item.intra_id)
            }

            binding.model = item
        }
    }
}
