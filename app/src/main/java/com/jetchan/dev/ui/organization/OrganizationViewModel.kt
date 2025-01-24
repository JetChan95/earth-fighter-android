package com.jetchan.dev.ui.organization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrganizationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "组织界面"
    }
    val text: LiveData<String> = _text
}