import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.where42android.model.RecyclerOutViewModel

class MainFragmentViewModel : ViewModel() {
    // MutableLiveData로 감싼 emptyItemList LiveData 생성
    private val _emptyItemList = MutableLiveData<MutableList<RecyclerOutViewModel>>()

    // 외부에서 접근하는 LiveData
    val emptyItemList: LiveData<MutableList<RecyclerOutViewModel>>
        get() = _emptyItemList

    // emptyItemList을 업데이트하는 메서드
    fun updateEmptyItemList(newList: MutableList<RecyclerOutViewModel>) {
        _emptyItemList.value = newList
    }
}
