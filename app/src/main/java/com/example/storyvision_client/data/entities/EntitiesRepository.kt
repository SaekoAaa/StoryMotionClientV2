package com.example.storyvision_client.data.entities

import com.example.storyvision_client.data.AuthRepository
import com.example.storyvision_client.data.AuthResult

// data/entities/EntitiesRepository.kt
class EntitiesRepository(
    private val api: EntitiesApi,
    private val authRepo: AuthRepository
) {
    suspend fun getCharacters(projectId: Long, page: Int, perPage: Int): AuthResult<CharactersResponse> {
        return authRepo.authorizedCall { token ->
            api.getCharacters(token, projectId, page, perPage)
        }
    }
    suspend fun getEvents(projectId: Long, page: Int, perPage: Int): AuthResult<EventsResponse> {
        return authRepo.authorizedCall { token ->
            api.getEvents(token, projectId, page, perPage)
        }
    }
    suspend fun getRelations(projectId: Long, page: Int, perPage: Int): AuthResult<RelationsResponse> {
        return authRepo.authorizedCall { token ->
            api.getRelations(token, projectId, page, perPage)
        }
    }
    suspend fun createCharacter(projectId: Long, req: CreateCharacterRequest): AuthResult<CharacterDto> =
        authRepo.authorizedCall { token -> api.createCharacter(token, projectId, req) }
    suspend fun createEvent(projectId: Long, req: CreateEventRequest): AuthResult<EventDto> =
        authRepo.authorizedCall { token -> api.createEvent(token, projectId, req) }
    suspend fun createRelation(projectId: Long, req: CreateRelationRequest): AuthResult<RelationDto> =
        authRepo.authorizedCall { token -> api.createRelation(token, projectId, req) }
    suspend fun getConnections(projectId: Long, entityId: String, page: Int, perPage: Int): AuthResult<ConnectionsResponse> =
        authRepo.authorizedCall { token ->
            api.getConnections(token, projectId, page, perPage, entityId)
        }
}
