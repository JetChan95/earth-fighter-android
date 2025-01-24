package com.jetchan.dev.utils

object TimeUtils {
    private const val MILLS_TO_SECONDS = 1000;
    fun isExpired(expiration: Long): Boolean {
        val currentTimestamp = System.currentTimeMillis() / MILLS_TO_SECONDS
        return currentTimestamp > expiration
    }
}