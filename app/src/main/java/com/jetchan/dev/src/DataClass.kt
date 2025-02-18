package com.jetchan.dev.src

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id") val id: Int = 0,
    @SerializedName("username") val name: String = "",
    @SerializedName("role") val roleName: String = ""
)

data class Task(
    @SerializedName("task_id") val id: Int = -1,
    @SerializedName("task_name") val name: String = "",
    @SerializedName("publisher_id") val publisherId: Int = -1,
    @SerializedName("receiver_id") val receiverId: Int = -1,
    @SerializedName("task_state") val state: String = "",
    @SerializedName("completion_time") val completionTime: String = "",
    @SerializedName("c_id") val orgId: Int = -1,
    @SerializedName("task_desc") val description: String = "",
    @SerializedName("publish_time") val publishTime: String = "",
    @SerializedName("time_limit") val timeLimit: Int = -1
)

data class Organization(
    val id: Int,
    val name: String,
    val type: String,
    val creatorId: Int,
    val createTime: String,
    val inviteCode: String
)

data class AuthBody(
    val username: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("access_token") val token: String,
    @SerializedName("expiration") val expiration: Long,
    @SerializedName("user_id") val id: Int,
    @SerializedName("username") val name: String,
    @SerializedName("role_name") val roleName: String = ""
)

data class AuthToken(
    val token: String,
    val expiration: Long
)

data class OrganizationBaseInfo(
    @SerializedName("c_id") val id: Int = -1,
    @SerializedName("c_name") val name: String = "",
    @SerializedName("c_type") val type: String = "",
    @SerializedName("invite_code") val inviteCode: String = ""
)

data class GetOrgListResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<OrganizationBaseInfo>
)

data class JoinOrgResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: OrganizationBaseInfo
)

data class GetUserTaskListResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<Task>
)