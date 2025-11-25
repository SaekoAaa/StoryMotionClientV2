package com.example.storyvision_client.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyvision_client.data.AuthRepository
import com.example.storyvision_client.data.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    init {
        viewModelScope.launch {
            repository.healthCheck()
        }
    }

    fun onEmailChange(newEmail: String) {
        _state.value = _state.value.copy(
            email = newEmail,
            emailError = null,
            error = null
        )
    }

    fun onPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(
            password = newPassword,
            passwordError = null,
            error = null
        )
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email не может быть пустым"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Некорректный формат email"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Пароль не может быть пустым"
            password.length < 8 -> "Пароль должен содержать минимум 8 символов"
            password.length > 30 -> "Пароль не может быть длиннее 30 символов"
            else -> null
        }
    }

    private fun validateForm(): Boolean {
        val emailError = validateEmail(_state.value.email)
        val passwordError = validatePassword(_state.value.password)

        _state.value = _state.value.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    fun login(onSuccess: () -> Unit) {
        if (!validateForm()) return
        authAction(onSuccess) {
            repository.login(state.value.email, state.value.password)
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (!validateForm()) return
        authAction(onSuccess) {
            repository.register(state.value.email, state.value.password)
        }
    }

    private fun authAction(
        onSuccess: () -> Unit,
        action: suspend () -> AuthResult<Unit>
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = action()) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = parseErrorMessage(result.message)
                    )
                }
            }
        }
    }

    private fun parseErrorMessage(message: String?): String {
        return when {
            message == null -> "Неизвестная ошибка"
            message.contains("EMAIL_ALREADY_EXISTS", ignoreCase = true) ->
                "Этот email уже зарегистрирован"
            message.contains("INVALID_CREDENTIALS", ignoreCase = true) ->
                "Неверный email или пароль"
            message.contains("Network error", ignoreCase = true) ->
                "Ошибка сети. Проверьте подключение"
            message.contains("Validation error", ignoreCase = true) ->
                "Проверьте правильность введенных данных"
            else -> message
        }
    }
}
