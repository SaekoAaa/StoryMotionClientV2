package com.example.storyvision_client.data.remote

object CookieUtils {

    fun extractRefreshToken(setCookie: String?): String? {
        if (setCookie.isNullOrBlank()) return null
        return setCookie
            .split(";")
            .map { it.trim() }
            .firstOrNull { it.startsWith("refresh=") }
            ?.substringAfter("refresh=")
    }
}