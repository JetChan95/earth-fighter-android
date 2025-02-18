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
    private const val BASE_URL = "http://192.168.1.130:5000/" // 替换为实际的服务器 API 基础 URL

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
}