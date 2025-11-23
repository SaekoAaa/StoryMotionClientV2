package com.example.storyvision_client.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyvision_client.data.AuthRepository
import com.example.storyvision_client.data.AuthResult
import com.example.storyvision_client.data.remote.MeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AccountUiState(
    val email: String = "",
    val role: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

class AccountViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AccountUiState())
    val state: StateFlow<AccountUiState> = _state

    fun loadAccount(onUnauthorized: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getCurrentUser()) {
                is AuthResult.Success -> {
                    val data: MeResponse = result.data
                    _state.value = AccountUiState(
                        email = data.email,
                        role = mapRole(data.role),
                        isLoading = false
                    )
                }
                is AuthResult.Error -> {
                    if (result.isUnauthorized) {
                        onUnauthorized()
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            when (val result = repository.logout()) {
                is AuthResult.Success -> {
                    onLoggedOut()
                }
                is AuthResult.Error -> {
                    if (result.isUnauthorized) {
                        onLoggedOut()
                    } else {
                        _state.value = _state.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun mapRole(role: String): String {
        return when (role.lowercase()) {
            "admin" -> "Администратор"
            "user" -> "Пользователь"
            else -> "Пользователь"
        }
    }
}
