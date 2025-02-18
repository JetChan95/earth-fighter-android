import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jetchan.dev.src.GetUserTaskListResponse
import com.jetchan.dev.src.ServerCommunicator
import com.jetchan.dev.src.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TaskViewModel : ViewModel() {
    private val _taskList = MutableLiveData<ArrayList<Task>>()
    val taskList: LiveData<ArrayList<Task>> = _taskList

    suspend fun loadTasks(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            ServerCommunicator.getInstance(context).getUserTaskList(object :
                Callback<GetUserTaskListResponse> {
                override fun onResponse(call: Call<GetUserTaskListResponse>, response: Response<GetUserTaskListResponse>) {
                    val body: GetUserTaskListResponse? = response.body()
                    if (body != null) {
                        Timber.d(Gson().toJson(body))
                        _taskList.postValue(body.data)
                    }
                }

                override fun onFailure(call: Call<GetUserTaskListResponse>, t: Throwable) {
                    Timber.d("error", t)
                }
            })
        }
    }
}