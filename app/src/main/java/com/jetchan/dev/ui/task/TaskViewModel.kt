package com.jetchan.dev.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "任务界面"
    }
    val text: LiveData<String> = _text
}