package com.jetchan.dev

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.jetchan.dev.databinding.ActivityMainBinding
import com.jetchan.dev.src.AuthToken
import com.jetchan.dev.src.MyAdapter
import com.jetchan.dev.src.ServerCommunicator
import com.jetchan.dev.src.User
import com.jetchan.dev.utils.SharedPreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

//        binding.appBarMain.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
//        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_organization, R.id.nav_task
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val headerView = navView.getHeaderView(0)
        val tvUserName = headerView.findViewById<TextView>(R.id.tv_username)
        val user: User? = SharedPreferencesUtil.getUserInfo(this)
        if (tvUserName != null && user != null) {
            tvUserName.text = user.name
        }

        if (user != null) {
            ServerCommunicator.getInstance(this).getUserInfoById(user.id, object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val user: User? = response.body()
                    if (user != null) {
                        Timber.d(Gson().toJson(user))
                    }
                    Timber.d("msg: ${response.message()}")
                    Timber.d("code: ${response.code()}")
                    Timber.d("body: ${response.body()}")
                    Timber.d("raw: ${response.raw()}")
                    Timber.d("errorbody: ${response.errorBody()}")

                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Timber.d("error", t)
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // 在这里添加注销的逻辑
                SharedPreferencesUtil.saveAuthToken(this, AuthToken("", 0))
                Toast.makeText(this, "已注销", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // 关闭登录界面，防止用户按返回键回到登录界面
                true
            }
            R.id.action_settings -> {
                // 处理设置菜单项的点击事件
                Toast.makeText(this, "设置操作", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}