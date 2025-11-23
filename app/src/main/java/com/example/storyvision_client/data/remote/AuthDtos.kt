package com.example.storyvision_client.data.remote

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val id: Long,
    val email: String,
    val access_token: String
)

data class ErrorResponse(
    val error: String?,
    val message: String?
)
data class MeResponse(
    val email: String,
    val role: String
)