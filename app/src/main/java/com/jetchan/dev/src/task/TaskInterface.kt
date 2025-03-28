package com.jetchan.dev.src.task

interface TaskCallback {
    fun onResult(success: Boolean, message: String)
}