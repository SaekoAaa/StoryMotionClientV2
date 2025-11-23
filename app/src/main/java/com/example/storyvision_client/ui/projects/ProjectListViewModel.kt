package com.example.storyvision_client.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyvision_client.data.AuthRepository
import com.example.storyvision_client.data.AuthResult
import com.example.storyvision_client.data.remote.ProjectDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProjectListUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val projects: List<ProjectDto> = emptyList()
)

class ProjectListViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProjectListUiState())
    val state: StateFlow<ProjectListUiState> = _state

    fun loadProjects(onUnauthorized: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getProjects()) {
                is AuthResult.Success -> {
                    _state.value = ProjectListUiState(
                        isLoading = false,
                        projects = result.data
                    )
                }
                is AuthResult.Error -> {
                    if (result.isUnauthorized) {
                        // просто вызываем коллбек, дальше UI разрулит навигацию
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
    fun createProject(
        name: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onUnauthorized: () -> Unit
    ) {
        viewModelScope.launch {
            when (val result = repository.createProject(name, description)) {
                is AuthResult.Success -> {
                    loadProjects(onUnauthorized) // обновить список
                    onSuccess()
                }
                is AuthResult.Error -> {
                    if (result.isUnauthorized) onUnauthorized()
                    else onError(result.message)
                }
            }
        }
    }

}
