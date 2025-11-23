package com.example.storyvision_client.ui.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyvision_client.data.AuthResult
import com.example.storyvision_client.data.entities.ConnectionDto
import com.example.storyvision_client.data.entities.EntitiesRepository
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(
    private val repository: EntitiesRepository,
    private val projectId: Long
) : ViewModel() {

    var connections by mutableStateOf<List<ConnectionDto>>(emptyList())
        private set
    var error by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)

    fun loadConnections(characterId: String, onUnauthorized: () -> Unit) {
        loading = true
        error = null
        viewModelScope.launch {
            when (val res = repository.getConnections(projectId, characterId, page = 1, perPage = 10)) {
                is AuthResult.Success -> {
                    connections = res.data.items
                    loading = false
                }
                is AuthResult.Error -> {
                    loading = false
                    if (res.isUnauthorized) onUnauthorized()
                    else error = res.message
                }
            }
        }
    }
}
