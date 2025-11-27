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
    val hasMore: Boolean = false,

    val allCharacters: List<CharacterDto> = emptyList(),
    val allEvents: List<EventDto> = emptyList(),
    val allRelations: List<RelationDto> = emptyList(),


    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NAME_ASC
)
enum class SortOption(val label: String) {
    NAME_ASC("По имени (А–Я)"),
    NAME_DESC("По имени (Я–А)")
}
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
                            allCharacters = response.data.characters,
                            allEvents = emptyList(),
                            allRelations = emptyList(),
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
                            allCharacters = emptyList(),
                            allEvents = response.data.items,
                            allRelations = emptyList(),
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
                            allCharacters = emptyList(),
                            allEvents = emptyList(),
                            allRelations = response.data.items,
                            page = page,
                            hasMore = response.data.has_more,
                            error = null
                        )
                        is AuthResult.Error -> processError(response, onUnauthorized)
                    }
                }
            }
            applyFilterAndSort()
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
    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilterAndSort()
    }

    fun onSortOptionChange(option: SortOption) {
        _state.value = _state.value.copy(sortOption = option)
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        val s = _state.value
        val query = s.searchQuery.trim().lowercase()

        fun String?.containsQuery(): Boolean =
            query.isNotBlank() && this?.lowercase()?.contains(query) == true

        when (s.selectedType) {
            EntityType.CHARACTERS -> {
                val base = s.allCharacters
                val filtered = if (query.isBlank()) {
                    base
                } else {
                    base.filter { ch ->
                        ch.name.containsQuery() ||
                                ch.description.containsQuery()
                    }
                }
                val sorted = when (s.sortOption) {
                    SortOption.NAME_ASC  -> filtered.sortedBy { it.name.lowercase() }
                    SortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
                }
                _state.value = s.copy(characters = sorted)
            }

            EntityType.EVENTS -> {
                val base = s.allEvents
                val filtered = if (query.isBlank()) {
                    base
                } else {
                    base.filter { ev ->
                        ev.name.containsQuery() ||
                                ev.location.containsQuery() ||
                                ev.description.containsQuery()
                    }
                }
                val sorted = when (s.sortOption) {
                    SortOption.NAME_ASC  -> filtered.sortedBy { it.name.lowercase() }
                    SortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
                }
                _state.value = s.copy(events = sorted)
            }

            EntityType.RELATIONS -> {
                val base = s.allRelations
                val filtered = if (query.isBlank()) {
                    base
                } else {
                    base.filter { rel ->
                        rel.name.containsQuery() ||
                                rel.relation_type.containsQuery() ||
                                rel.description.containsQuery()
                    }
                }
                val sorted = when (s.sortOption) {
                    SortOption.NAME_ASC  -> filtered.sortedBy { it.name.lowercase() }
                    SortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
                }
                _state.value = s.copy(relations = sorted)
            }
        }
    }

}
