import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.jetchan.dev.src.ServerCommunicator
import com.jetchan.dev.src.Task
import com.jetchan.dev.src.TaskDescription
import com.jetchan.dev.src.TaskResponse
import com.jetchan.dev.src.task.TaskCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PublishTaskViewModel : ViewModel() {
    private val _taskName = MutableLiveData<String>()
    val taskName: LiveData<String> = _taskName

    private val _taskType = MutableLiveData<String>()
    val taskType: LiveData<String> = _taskType

    private val _taskDescription = MutableLiveData<String>()
    val taskDescription: LiveData<String> = _taskDescription

    private val _todoList = MutableLiveData<MutableList<Pair<String, Boolean>>>()
    val todoList: LiveData<MutableList<Pair<String, Boolean>>> = _todoList

    private val _orgId = MutableLiveData<Int>()
    val orgId: LiveData<Int> = _orgId

    fun setOrgId(orgId: Int) {
        _orgId.value = orgId
    }

    init {
        _todoList.value = mutableListOf()
    }

    fun setTaskName(name: String) {
        _taskName.value = name
    }

    fun setTaskType(type: String) {
        _taskType.value = type
    }

    fun setTaskDescription(description: String) {
        _taskDescription.value = description
    }

    fun addTodoItem(todo: Pair<String, Boolean>) {
        _todoList.value?.add(todo)
        _todoList.value = _todoList.value
    }

    fun publishTask(context: Context, callback: TaskCallback) {
        if (_taskName.value?.isBlank() == true || _taskDescription.value?.isBlank() == true) {
            callback.onResult(false, "任务名称和描述不能为空")
            return
        }

        val orgId = _orgId.value?:-1
        val name = _taskName.value?:""
        val type = _taskType.value?:""
        val taskDescription = _taskDescription.value?:""
        val todoList = _todoList.value?: arrayListOf()
        val description = Gson().toJson(TaskDescription(taskDescription, todoList))
        val task: Task = Task(orgId=orgId, name=name, description=description)
        ServerCommunicator.getInstance(context).publishTask(task, object: Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                val body: TaskResponse? = response.body()
                if (body == null) {
                    callback.onResult(false, "任务发布失败")
                    return
                }
                Timber.d(Gson().toJson(task))
                callback.onResult(true, "任务发布成功")
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Timber.d("error", t)
                callback.onResult(false, "任务发布失败:${t.message}")
            }
        })
    }
}