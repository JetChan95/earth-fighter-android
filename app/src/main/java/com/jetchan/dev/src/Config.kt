package com.jetchan.dev.src

// 定义任务状态常量
object TaskStatus {
    const val PENDING = "0"
    const val IN_PROGRESS = "1"
    const val COMPLETED = "2"
    const val EXPIRED = "3"
    const val OVERDUE_COMPLETED = "4"
    const val FAILED = "5"
    const val TO_BE_CONFIRMED = "6"
    const val ABANDONED = "7"
    const val DEFAULT = "default"
}

object Server {
//    const val BASE_URL = "https://api.earthfighter.site/"
//    const val BASE_URL = "http://192.168.1.130:5000"
    const val BASE_URL = "http://1.14.15.196:5001"
}