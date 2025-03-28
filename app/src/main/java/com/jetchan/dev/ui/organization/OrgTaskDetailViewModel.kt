package com.jetchan.dev.ui.organization

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

class OrgTaskDetailViewModel : ViewModel() {
    private val _todoList = MutableLiveData<MutableList<Pair<String, Boolean>>>()
    val todoList: LiveData<MutableList<Pair<String, Boolean>>> = _todoList

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    private val _taskDesc = MutableLiveData<String>()
    val taskDesc: LiveData<String> = _taskDesc

    fun setTask(task: Task) {
        _task.value = task
        val desc = Gson().fromJson(task.description, TaskDescription::class.java)
        _taskDesc.value = desc.description
        _todoList.postValue(desc.todoList)
        Timber.d(Gson().toJson(desc.todoList))
    }

    fun acceptTask(context: Context, taskId: Int, callback: TaskCallback) {
        ServerCommunicator.getInstance(context).acceptTask(taskId, object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                val body: TaskResponse? = response.body()
                if (body == null) {
                    callback.onResult(false, "任务接受失败")
                    return
                }
                Timber.d("Accepted task with ID: $taskId")
                callback.onResult(true, "任务接受成功")
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Timber.d("error", t)
                callback.onResult(false, "任务接受失败:${t.message}")
            }
        })
    }
    fun submitTask(context: Context, taskId: Int, callback: TaskCallback) {
        ServerCommunicator.getInstance(context).submitTask(taskId, object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                val body: TaskResponse? = response.body()
                if (body == null) {
                    callback.onResult(false, "任务提交失败")
                    return
                }
                Timber.d("Submitted task with ID: $taskId")
                callback.onResult(true, "任务提交成功")
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Timber.d("error", t)
                callback.onResult(false, "任务提交失败:${t.message}")
            }
        })
    }

    fun confirmTask(context: Context, taskId: Int, callback: TaskCallback) {
        ServerCommunicator.getInstance(context).confirmTask(taskId, object :
            Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                val body: TaskResponse? = response.body()
                if (body == null) {
                    callback.onResult(false, "任务确认失败")
                    return
                }
                Timber.d("Confirmed task with ID: $taskId")
                callback.onResult(true, "任务确认成功")
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Timber.d("error", t)
                callback.onResult(false, "任务确认失败:${t.message}")
            }
        })
    }

    fun deleteTask(context: Context, taskId: Int, callback: TaskCallback) {
        ServerCommunicator.getInstance(context).deleteTask(taskId, object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                val body: TaskResponse? = response.body()
                if (body == null) {
                    callback.onResult(false, "任务删除失败")
                    return
                }
                Timber.d("Deleted task with ID: $taskId")
                callback.onResult(true, "任务删除成功")
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Timber.d("error", t)
                callback.onResult(false, "任务删除失败:${t.message}")
            }
        })
    }

    fun abandonTask(context: Context, taskId: Int, callback: TaskCallback) {
        ServerCommunicator.getInstance(context).abandonTask(taskId, object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                val body: TaskResponse? = response.body()
                if (body == null) {
                    callback.onResult(false, "任务放弃失败")
                    return
                }
                Timber.d("Abandon task with ID: $taskId")
                callback.onResult(true, "任务放弃成功")
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Timber.d("error", t)
                callback.onResult(false, "任务放弃失败:${t.message}")
            }
        })
    }
}