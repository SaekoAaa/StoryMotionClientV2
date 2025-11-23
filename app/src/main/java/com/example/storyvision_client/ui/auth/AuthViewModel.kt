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
    val error: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    init {
        // Проверяем доступность API
        viewModelScope.launch {
            repository.healthCheck()
            // Можно обработать результат, если нужно
        }
    }

    fun onEmailChange(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail, error = null)
    }

    fun onPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword, error = null)
    }

    fun login(onSuccess: () -> Unit) {
        authAction(onSuccess) {
            repository.login(state.value.email, state.value.password)
        }
    }

    fun register(onSuccess: () -> Unit) {
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
                        error = result.message
                    )
                }
            }
        }
    }
}
