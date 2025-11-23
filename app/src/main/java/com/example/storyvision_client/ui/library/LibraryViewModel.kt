package com.example.storyvision_client.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyvision_client.data.AuthResult
import com.example.storyvision_client.data.entities.CharacterDto
import com.example.storyvision_client.data.entities.CreateCharacterRequest
import com.example.storyvision_client.data.entities.CreateEventRequest
import com.example.storyvision_client.data.entities.CreateRelationRequest
import com.example.storyvision_client.data.entities.EntitiesRepository
import com.example.storyvision_client.data.entities.EventDto
import com.example.storyvision_client.data.entities.RelationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ui/library/LibraryViewModel.kt
enum class EntityType(val displayName: String) {
    CHARACTERS("Персонажи"),
    EVENTS("События"),
    RELATIONS("Типы связей")
}

data class LibraryUiState(
    val selectedType: EntityType = EntityType.CHARACTERS,
    val isLoading: Boolean = false,
    val error: String? = null,
    val characters: List<CharacterDto> = emptyList(),
    val events: List<EventDto> = emptyList(),
    val relations: List<RelationDto> = emptyList(),
    val page: Int = 1,
    val hasMore: Boolean = false
)

class LibraryViewModel(
    private val repository: EntitiesRepository,
    private val projectId: Long
) : ViewModel() {
    private val _state = MutableStateFlow(LibraryUiState())
    val state: StateFlow<LibraryUiState> = _state

    fun load(type: EntityType, page: Int = 1, perPage: Int = 10, onUnauthorized: () -> Unit) {
        _state.value = _state.value.copy(selectedType = type, isLoading = true, error = null)
        viewModelScope.launch {
            when (type) {
                EntityType.CHARACTERS -> {
                    when (val response = repository.getCharacters(projectId, page, perPage)) {
                        is AuthResult.Success -> _state.value = _state.value.copy(
                            isLoading = false,
                            characters = response.data.characters,
                            events = emptyList(),
                            relations = emptyList(),
                            page = page,
                            hasMore = response.data.has_more,
                            error = null
                        )
                        is AuthResult.Error -> processError(response, onUnauthorized)
                    }
                }
                EntityType.EVENTS -> {
                    when (val response = repository.getEvents(projectId, page, perPage)) {
                        is AuthResult.Success -> _state.value = _state.value.copy(
                            isLoading = false,
                            characters = emptyList(),
                            events = response.data.items,
                            relations = emptyList(),
                            page = page,
                            hasMore = response.data.has_more,
                            error = null
                        )
                        is AuthResult.Error -> processError(response, onUnauthorized)
                    }
                }
                EntityType.RELATIONS -> {
                    when (val response = repository.getRelations(projectId, page, perPage)) {
                        is AuthResult.Success -> _state.value = _state.value.copy(
                            isLoading = false,
                            characters = emptyList(),
                            events = emptyList(),
                            relations = response.data.items,
                            page = page,
                            hasMore = response.data.has_more,
                            error = null
                        )
                        is AuthResult.Error -> processError(response, onUnauthorized)
                    }
                }
            }
        }
    }

    private fun processError(error: AuthResult.Error, onUnauthorized: () -> Unit) {
        if (error.isUnauthorized) onUnauthorized()
        else _state.value = _state.value.copy(isLoading = false, error = error.message)
    }

    fun createCharacter(
        name: String,
        description: String?,
        attributes: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onUnauthorized: () -> Unit
    ) {
        viewModelScope.launch {
            val req = CreateCharacterRequest(
                name,
                description,
                if (attributes.isEmpty()) null else attributes
            )
            when (val result = repository.createCharacter(projectId, req)) {
                is AuthResult.Success -> {
                    load(EntityType.CHARACTERS, page = 1, onUnauthorized = onUnauthorized)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    if (result.isUnauthorized) onUnauthorized()
                    else onError(result.message.orEmpty())
                }
            }
        }
    }
    fun createEvent(
        name: String,
        location: String?,
        description: String?,
        timestamp: String?,
        attributes: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onUnauthorized: () -> Unit
    ) {
        viewModelScope.launch {
            val req = CreateEventRequest(
                name,
                location,
                description,
                timestamp,
                if (attributes.isEmpty()) null else attributes
            )
            when (val result = repository.createEvent(projectId, req)) {
                is AuthResult.Success -> {
                    load(EntityType.EVENTS, page = 1, onUnauthorized = onUnauthorized)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    if (result.isUnauthorized) onUnauthorized()
                    else onError(result.message.orEmpty())
                }
            }
        }
    }
    fun createRelation(
        name: String,
        type: String?,
        description: String?,
        attributes: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onUnauthorized: () -> Unit
    ) {
        viewModelScope.launch {
            val req = CreateRelationRequest(
                name,
                type,
                description,
                if (attributes.isEmpty()) null else attributes
            )
            when (val result = repository.createRelation(projectId, req)) {
                is AuthResult.Success -> {
                    load(EntityType.RELATIONS, page = 1, onUnauthorized = onUnauthorized)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    if (result.isUnauthorized) onUnauthorized()
                    else onError(result.message.orEmpty())
                }
            }
        }
    }
}
