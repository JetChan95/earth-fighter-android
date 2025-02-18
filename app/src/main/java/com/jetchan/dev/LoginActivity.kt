package com.jetchan.dev

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.gson.Gson
import com.jetchan.dev.src.AuthBody
import com.jetchan.dev.src.AuthResponse
import com.jetchan.dev.src.AuthToken
import com.jetchan.dev.src.ServerCommunicator
import com.jetchan.dev.src.User
import com.jetchan.dev.utils.SharedPreferencesUtil
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import com.jetchan.dev.utils.TimeUtils
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class LoginActivity : AppCompatActivity() {

    private var loggingCount = AtomicInteger(0)
    private var registeringCount = AtomicInteger(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 初始化启动屏
        val splashScreen = installSplashScreen()

        // 设置一个等待标志，用于控制启动屏的显示
        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        // 做一些初始化操作
        // 取消启动屏保持状态
        keepSplashOnScreen = false

        // 检查登录状态
        if (isLogin()) {
            // 已登录，跳转到主页
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)

//      登录按钮点击事件
        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener {
            if (loggingCount.incrementAndGet() > 1) {
                Timber.d("禁止并发 loggingCount:${loggingCount.get()}")
                loggingCount.decrementAndGet()
                return@setOnClickListener
            }
            Timber.d("loggingCount:${loggingCount.get()}")
            // 这里可以添加实际的登录验证逻辑，比如检查用户名和密码
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username != "" && password != "") {
                // 异步注册，异步结果处理时处理原子变量loggingCount
                login(AuthBody(username, password))
            } else {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
                loggingCount.decrementAndGet()
            }
        }

//      注册按钮点击事件
        val btnRegistry = findViewById<Button>(R.id.btn_registry)
        btnRegistry.setOnClickListener {
            if (registeringCount.incrementAndGet() > 1) {
                registeringCount.decrementAndGet()
                return@setOnClickListener
            }
            // 这里可以添加实际的登录验证逻辑，比如检查用户名和密码
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username != "" && password != "") {
                // 异步注册，异步结果处理时处理原子变量registeringCount
                registry(AuthBody(username, password))
            } else {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
                registeringCount.decrementAndGet()
            }
        }
    }

/************************************** 登录 ***************************************/
    private fun login(body: AuthBody) {
        ServerCommunicator.getInstance(this).login(body, object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val authResponse: AuthResponse? = response.body()
                    val gson = Gson()
                    Timber.d("response: $response, auth ${gson.toJson(authResponse)}")
                    onLoginSuccessful(authResponse)
                } else {
                    Timber.d("Request failed: ${response.code()}")
                    onLoginResponseFail(response.code())
                }
                loggingCount.decrementAndGet()
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Timber.d("Network error: ${t.message}")
                onLoginError()
                loggingCount.decrementAndGet()
            }
        })
    }

    private fun onLoginSuccessful(authResponse: AuthResponse?) {
        if (authResponse == null) {
            val message = "登录异常"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            return
        }
        val user: User = User(authResponse.id, authResponse.name, authResponse.roleName)
        val token: AuthToken = AuthToken(authResponse.token, authResponse.expiration)
        SharedPreferencesUtil.saveUserInfo(this, user)
        SharedPreferencesUtil.saveAuthToken(this, token)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 关闭登录界面，防止用户按返回键回到登录界面
    }

    private fun onLoginResponseFail(code: Int){
        var message: String = "Error"
        when (code) {
            401 -> message = "无效的用户名或密码"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onLoginError() {
        // 显示注册失败信息
        Toast.makeText(this, "登录失败，请稍后重试", Toast.LENGTH_SHORT).show()
    }
/************************************** 登录 ***************************************/

/************************************** 注册 ***************************************/
    private fun registry(body: AuthBody) {
        ServerCommunicator.getInstance(this).registry(body, object : Callback<User> {
            override fun onResponse(call: retrofit2.Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user: User? = response.body()
                    if (user != null){
                        Timber.d("User: ${Gson().toJson(user)}")
                        onRegistrySuccessful(user.id)
                    }
                } else {
                    onRegistryResponseFail(response.code())
                    Timber.d("Request failed: ${response.code()}")
                }
                registeringCount.decrementAndGet()
            }

            override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                onRegistrationError()
                Timber.d("Network error: ${t.message}")
                registeringCount.decrementAndGet()
            }
        })
    }

    private fun onRegistrySuccessful(userId: Int) {
        // 这里可以更新 LoginActivity 的 UI，例如显示注册成功信息
        val message = "注册成功，用户 ID 为: $userId"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onRegistryResponseFail(code: Int) {
        // 显示注册失败信息
        var message: String = ""
        when (code) {
            400 -> message = "用户名已存在"
            500 -> message = "系统繁忙"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onRegistrationError() {
        // 显示注册失败信息
        Toast.makeText(this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show()
    }
/************************************** 注册 ***************************************/

    private fun isLogin(): Boolean {
        val authToken: AuthToken = SharedPreferencesUtil.getAuthToken(this) ?: return false
        Timber.d("exp${authToken.expiration}\ncur${System.currentTimeMillis()/1000}\n${authToken.expiration - System.currentTimeMillis()/1000}")

        if (authToken.token == "") {
                return false
            }

        if (TimeUtils.isExpired(authToken.expiration)) {
                return false
            }
        return true
    }
}