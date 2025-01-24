package com.jetchan.dev.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jetchan.dev.src.AuthToken
import com.jetchan.dev.src.User
import timber.log.Timber

object SharedPreferencesUtil {
    private const val LOGIN_PREF_NAME = "LoginPrefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"


    fun saveLoginStatus(context: Context, isLoggedIn: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun getLoginStatus(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveUserInfo(context: Context, user: User) {
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val userJson = gson.toJson(user)
        Timber.d(String.format("save user info: %s", userJson))
        editor.putString("user", userJson)
        editor.apply()
    }

    fun getUserInfo(context: Context): User? {
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE)
        val userJson = prefs.getString("user", null)
        if (userJson != null) {
            val gson = Gson()
            // 将 JSON 字符串反序列化为对象
            return gson.fromJson(userJson, User::class.java)
        }
        return null
    }

    fun saveAuthToken(context: Context, token: AuthToken) {
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val tokenJson = Gson().toJson(token)
        editor.putString("authToken", tokenJson)
        editor.apply()
        Timber.d("save authToken $tokenJson")
    }

    fun getAuthToken(context: Context): AuthToken? {
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE)
        val tokenJson = prefs.getString("authToken", null)
        Timber.d("---CCC---$tokenJson")
        var token: AuthToken? = null
        if (tokenJson != null) {
            token = Gson().fromJson(tokenJson, AuthToken::class.java)
        }
        return token
    }
}