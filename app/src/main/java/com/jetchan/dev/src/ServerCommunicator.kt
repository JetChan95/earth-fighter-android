package com.jetchan.dev.src

import android.content.Context
import android.content.SharedPreferences
import com.jetchan.dev.utils.SharedPreferencesUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Callback
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.lang.reflect.Method

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class NeedToken

interface ApiService {
    @POST("users/create")
    fun registry(@Body body: AuthBody): retrofit2.Call<User>

    @POST("users/login")
    fun login(@Body body: AuthBody): retrofit2.Call<AuthResponse>

    @GET("/users/{u_id}/info")
    @NeedToken
    fun getUserInfoById(@Path("u_id")path: Int): retrofit2.Call<User>

    @POST("/organizations/create")
    @NeedToken
    fun createOrganization(@Body body: OrganizationBaseInfo): retrofit2.Call<OrganizationBaseInfo>

    @GET("/users/organizations")
    @NeedToken
    fun getOrganizationList(): retrofit2.Call<GetOrgListResponse>

    @PUT("/organizations/{c_id}/join")
    @NeedToken
    fun joinOrganization(@Path("c_id")path: Int, @Body body: OrganizationBaseInfo): retrofit2.Call<JoinOrgResponse>

    @GET("/users/tasks")
    @NeedToken
    fun getUserTaskList(): retrofit2.Call<GetUserTaskListResponse>

    @GET("/organizations/{c_id}/tasks")
    @NeedToken
    fun getOrgTaskList(@Path("c_id")path: Int): retrofit2.Call<GetOrgTaskListResponse>

    @PUT("/tasks/publish")
    @NeedToken
    fun publishTask(@Body body: Task): retrofit2.Call<TaskResponse>

    @PUT("/tasks/{task_id}/accept")
    @NeedToken
    fun acceptTask(@Path("task_id") taskId: Int): retrofit2.Call<TaskResponse>

    @PUT("/tasks/{task_id}/submit")
    @NeedToken
    fun submitTask(@Path("task_id") taskId: Int): retrofit2.Call<TaskResponse>

    @PUT("/tasks/{task_id}/confirm")
    @NeedToken
    fun confirmTask(@Path("task_id") taskId: Int):retrofit2.Call<TaskResponse>

    @PUT("/tasks/{task_id}/abandon")
    @NeedToken
    fun abandonTask(@Path("task_id") taskId: Int):retrofit2.Call<TaskResponse>

    @DELETE("/tasks/{task_id}/delete")
    @NeedToken
    fun deleteTask(@Path("task_id") taskId: Int): retrofit2.Call<TaskResponse>
}

class TokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val invocation = originalRequest.tag(Invocation::class.java)
        if (invocation != null) {
            val method = invocation.method()
            if (method.isAnnotationPresent(NeedToken::class.java)) {
                // 从 SharedPreferences 中获取 Token
                val token = SharedPreferencesUtil.getAuthToken(context)
                if (token != null) {
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer ${token.token}")
                        .build()
                    return chain.proceed(newRequest)
                }
            }
        }
        return chain.proceed(originalRequest)
    }

    private fun Invocation.method(): Method {
        return javaClass.declaredMethods.first { it.name == "method" }.invoke(this) as Method
    }
}

object RetrofitClient {
    private const val BASE_URL = Server.BASE_URL

    fun getInstance(context: Context): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}

// 懒汉式单例模式的 ServerCommunicator 类
class ServerCommunicator private constructor(context: Context) {
    private val apiService = RetrofitClient.getInstance(context)

    companion object {
        @Volatile
        private var instance: ServerCommunicator? = null

        fun getInstance(context: Context): ServerCommunicator {
            return instance ?: synchronized(this) {
                instance ?: ServerCommunicator(context).also { instance = it }
            }
        }
    }

    fun registry(body: AuthBody, callback: Callback<User>) {
        val call = apiService.registry(body)
        call.enqueue(callback)
    }

    fun login(body: AuthBody, callback: Callback<AuthResponse>) {
        val call = apiService.login(body)
        call.enqueue(callback)
    }

    fun getUserInfoById(path: Int, callback: Callback<User>) {
        val call = apiService.getUserInfoById(path)
        call.enqueue(callback)
    }

    fun createOrganization(body: OrganizationBaseInfo, callback: Callback<OrganizationBaseInfo>) {
        val call = apiService.createOrganization(body)
        call.enqueue(callback)
    }

    fun getOrganizationList(callback: Callback<GetOrgListResponse>) {
        val call = apiService.getOrganizationList()
        call.enqueue(callback)
    }

    fun joinOrganization(path: Int, body: OrganizationBaseInfo, callback: Callback<JoinOrgResponse>) {
        val call = apiService.joinOrganization(path, body)
        call.enqueue(callback)
    }

    fun getUserTaskList(callback: Callback<GetUserTaskListResponse>) {
        val call = apiService.getUserTaskList()
        call.enqueue(callback)
    }

    fun getOrgTaskList(path: Int, callback: Callback<GetOrgTaskListResponse>) {
        val call = apiService.getOrgTaskList(path)
        call.enqueue(callback)
    }

    fun publishTask(body: Task, callback: Callback<TaskResponse>) {
        val call = apiService.publishTask(body)
        call.enqueue(callback)
    }

    fun acceptTask(taskId: Int, callback: Callback<TaskResponse>) {
        val call = apiService.acceptTask(taskId)
        call.enqueue(callback)
    }

    fun submitTask(taskId: Int, callback: Callback<TaskResponse>) {
        val call = apiService.submitTask(taskId)
        call.enqueue(callback)
    }

    fun confirmTask(taskId: Int, callback: Callback<TaskResponse>) {
        val call = apiService.confirmTask(taskId)
        call.enqueue(callback)
    }

    fun deleteTask(taskId: Int, callback: Callback<TaskResponse>) {
        val call = apiService.deleteTask(taskId)
        call.enqueue(callback)
    }

    fun abandonTask(taskId: Int, callback: Callback<TaskResponse>) {
        val call = apiService.abandonTask(taskId)
        call.enqueue(callback)
    }
}