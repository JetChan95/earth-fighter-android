import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jetchan.dev.src.GetOrgTaskListResponse
import com.jetchan.dev.src.GetUserTaskListResponse
import com.jetchan.dev.src.ServerCommunicator
import com.jetchan.dev.src.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class OrganizationDetailViewModel : ViewModel() {
    private val _orgId = MutableLiveData<Int>()
    val orgId: LiveData<Int> = _orgId

    private val _orgName = MutableLiveData<String>()
    val orgName: LiveData<String> = _orgName

    private val _orgType = MutableLiveData<String>()
    val orgType :LiveData<String> = _orgType

    private val _inviteCode = MutableLiveData<String>()
    val inviteCode :LiveData<String> = _inviteCode

    private val _taskList = MutableLiveData<ArrayList<Task>>()
    val taskList: LiveData<ArrayList<Task>> = _taskList

    suspend fun loadTasks(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            var path: Int = -1
            _orgId.value?.let { path = it }
            ServerCommunicator.getInstance(context).getOrgTaskList(path, object :
                Callback<GetOrgTaskListResponse> {
                override fun onResponse(call: Call<GetOrgTaskListResponse>, response: Response<GetOrgTaskListResponse>) {
                    val body: GetOrgTaskListResponse? = response.body()
                    if (body != null) {
                        Timber.d(Gson().toJson(body))
                        _taskList.postValue(body.data)
                    }
                }

                override fun onFailure(call: Call<GetOrgTaskListResponse>, t: Throwable) {
                    Timber.d("error", t)
                }
            })
        }
    }

    fun setOrganizationData(id: Int = -1,
                            name: String = "--",
                            type: String = "--",
                            inviteCode: String = "--") {
        _orgId.value = id
        _orgName.value = name
        _orgType.value = type
        _inviteCode.value = inviteCode
    }
}