package com.jetchan.dev.src

import com.google.gson.annotations.SerializedName
import java.util.Date

data class User(@SerializedName("user_id") val id: Int = 0,
                @SerializedName("username") val name: String = "",
                @SerializedName("role") val roleName: String = "")
data class Task(val id: Int, val name: String, val publisherId: Int, val receiverId: String, val state: Int, val completionTime: String, val orgId: Int, val description: String)
data class Organization(val id: Int, val name: String, val type: String, val creatorId: Int, val createTime: String, val inviteCode: String)
data class AuthBody(val username: String, val password: String)
data class AuthResponse(@SerializedName("access_token") val token: String,
                        @SerializedName("expiration") val expiration: Long,
                        @SerializedName("user_id") val id: Int,
                        @SerializedName("username") val name: String,
                        @SerializedName("role_name") val roleName: String = "")
data class AuthToken(val token: String, val expiration: Long)
