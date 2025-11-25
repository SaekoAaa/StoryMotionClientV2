package com.example.storyvision_client.data

import com.example.storyvision_client.data.local.TokenStorage
import com.example.storyvision_client.data.remote.AuthApi
import com.example.storyvision_client.data.remote.AuthResponse
import com.example.storyvision_client.data.remote.CookieUtils
import com.example.storyvision_client.data.remote.CreateProjectRequest
import com.example.storyvision_client.data.remote.ErrorResponse
import com.example.storyvision_client.data.remote.LoginRequest
import com.example.storyvision_client.data.remote.MeResponse
import com.example.storyvision_client.data.remote.ProjectDto
import com.example.storyvision_client.data.remote.RefreshRequest
import com.example.storyvision_client.data.remote.RegisterRequest
import com.google.gson.Gson
import retrofit2.Response

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(
        val message: String,
        val isUnauthorized: Boolean = false
    ) : AuthResult<Nothing>()
}

class AuthRepository(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
    private val gson: Gson = Gson()
) {

    // --- НЕ МЕНЯЛОСЬ: healthCheck, login, register (как раньше) ---

    suspend fun healthCheck(): AuthResult<Unit> {
        return try {
            val response = api.healthCheck()
            if (response.isSuccessful) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error("Healthcheck failed: ${response.code()}")
            }
        } catch (e: Exception) {
            AuthResult.Error("Healthcheck error: ${e.message}")
        }
    }

    suspend fun login(email: String, password: String): AuthResult<Unit> {
        return authCall(
            call = { api.login(LoginRequest(email, password)) },
            actionName = "Login"
        )
    }

    suspend fun register(email: String, password: String): AuthResult<Unit> {
        return authCall(
            call = { api.register(RegisterRequest(email, password)) },
            actionName = "Register"
        )
    }

    private suspend fun authCall(
        call: suspend () -> Response<AuthResponse>,
        actionName: String
    ): AuthResult<Unit> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return AuthResult.Error("$actionName: empty body")

                val setCookie = response.headers()["Set-Cookie"]
                val refreshToken = CookieUtils.extractRefreshToken(setCookie)
                    ?: return AuthResult.Error("$actionName: refresh token not found in Set-Cookie")

                val accessToken = body.access_token
                tokenStorage.saveTokens(accessToken, refreshToken)

                AuthResult.Success(Unit)
            } else {
                val errorMsg = parseError(response)
                AuthResult.Error("$actionName error: $errorMsg")
            }
        } catch (e: Exception) {
            AuthResult.Error("$actionName exception: ${e.message}")
        }
    }

    // --- ОБЩИЙ helper для любых запросов с access_token + refresh на 401 ---

    suspend fun <T> authorizedCall(
        call: suspend (String) -> Response<T>
    ): AuthResult<T> {
        val storedAccess = tokenStorage.getAccessToken()
            ?: return AuthResult.Error("No access token", isUnauthorized = true)

        var token = storedAccess
        var triedRefresh = false

        while (true) {
            val response = try {
                call("Bearer $token")
            } catch (e: Exception) {
                return AuthResult.Error("Network error: ${e.message}")
            }

            if (response.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                val body = response.body()
                    ?: return AuthResult.Success(Unit as T)
                return AuthResult.Success(body)
            }

            val code = response.code()

            if (code == 401 && !triedRefresh) {
                triedRefresh = true
                when (val refreshResult = refreshAccessToken()) {
                    is AuthResult.Success -> {
                        token = refreshResult.data
                        continue    // пробуем ещё раз с новым токеном
                    }
                    is AuthResult.Error -> {
                        // refresh не удался — считаем пользователя неавторизованным
                        return AuthResult.Error(
                            message = refreshResult.message,
                            isUnauthorized = true
                        )
                    }
                }
            } else {
                val msg = parseError(response)
                val isUnauthorized = code == 401
                if (isUnauthorized) {
                    tokenStorage.clearTokens()
                }
                return AuthResult.Error(msg, isUnauthorized = isUnauthorized)
            }
        }
    }


    // refresh по refresh_token
    private suspend fun refreshAccessToken(): AuthResult<String> {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: return AuthResult.Error("No refresh token", isUnauthorized = true)

        try {
            // передаем токен через Cookie заголовок
            val cookieHeader = "refresh=$refreshToken"
            val response = api.refreshWithCookie(cookieHeader)
            // дальше обработка аналогична твоей
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return AuthResult.Error("Refresh: empty body", isUnauthorized = true)
                tokenStorage.saveTokens(body.access_token, refreshToken)
                return AuthResult.Success(body.access_token)
            } else {
                tokenStorage.clearTokens()
                val msg = parseError(response)
                return AuthResult.Error(msg, isUnauthorized = true)
            }
        } catch (e: Exception) {
            tokenStorage.clearTokens()
            return AuthResult.Error("Refresh exception: ${e.message}", isUnauthorized = true)
        }
    }



    private fun parseError(response: Response<*>): String {
        val errorBody = response.errorBody()?.string() ?: return "HTTP ${response.code()}"
        return try {
            val err = gson.fromJson(errorBody, ErrorResponse::class.java)
            err.message ?: err.error ?: "HTTP ${response.code()}"
        } catch (e: Exception) {
            "HTTP ${response.code()}"
        }
    }

    // --- ТЕПЕРЬ getCurrentUser, logout и getProjects используют authorizedCall ---

    suspend fun getCurrentUser(): AuthResult<MeResponse> {
        return authorizedCall { token -> api.me(token) }
    }

    suspend fun logout(): AuthResult<Unit> {
        val result = authorizedCall { token -> api.logout(token) }
        tokenStorage.clearTokens()
        return result
    }

    suspend fun getProjects(): AuthResult<List<ProjectDto>> {
        return authorizedCall { token -> api.getProjects(token) }
    }
    // data/AuthRepository.kt
    suspend fun createProject(
        name: String,
        description: String
    ): AuthResult<ProjectDto> {
        return authorizedCall { token ->
            api.createProject(token, CreateProjectRequest(name, description))
        }
    }

}
