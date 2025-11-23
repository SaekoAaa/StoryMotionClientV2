// data/entities/EntitiesApi.kt
package com.example.storyvision_client.data.entities

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.Path


interface EntitiesApi {
    @GET("/v1/entities/{project_id}/characters")
    suspend fun getCharacters(
        @Header("Authorization") authHeader: String,
        @Path("project_id") projectId: Long,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<CharactersResponse>


    @GET("/v1/entities/{project_id}/events")
    suspend fun getEvents(
        @Header("Authorization") authHeader: String,
        @Path("project_id") projectId: Long,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<EventsResponse>

    @GET("/v1/entities/{project_id}/relations")
    suspend fun getRelations(
        @Header("Authorization") authHeader: String,
        @Path("project_id") projectId: Long,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<RelationsResponse>
    @POST("/v1/entities/{project_id}/characters")
    suspend fun createCharacter(
        @Header("Authorization") authHeader: String,
        @Path("project_id") projectId: Long,
        @Body request: CreateCharacterRequest
    ): Response<CharacterDto>

    @POST("/v1/entities/{project_id}/events")
    suspend fun createEvent(
        @Header("Authorization") authHeader: String,
        @Path("project_id") projectId: Long,
        @Body request: CreateEventRequest
    ): Response<EventDto>

    @POST("/v1/entities/{project_id}/relations")
    suspend fun createRelation(
        @Header("Authorization") authHeader: String,
        @Path("project_id") projectId: Long,
        @Body request: CreateRelationRequest
    ): Response<RelationDto>

    @GET("/v1/entities/{project_id}/connections")
    suspend fun getConnections(
        @Header("Authorization") authHeader: String,
        @Path("project_id") projectId: Long,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("entity_id") entityId: String
    ): Response<ConnectionsResponse>

}

// Ответы
data class CharactersResponse(
    val characters: List<CharacterDto>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val has_more: Boolean
)

data class EventsResponse(
    val items: List<EventDto>,
    val total: Int,
    val page: Int,
    val per_page: Int,
    val has_more: Boolean
)

data class RelationsResponse(
    val items: List<RelationDto>,
    val total: Int,
    val page: Int,
    val per_page: Int,
    val has_more: Boolean
)

data class CreateCharacterRequest(
    val name: String,
    val description: String?,
    val attributes: Map<String, Any>? = null
)
data class CreateEventRequest(
    val name: String,
    val location: String?,
    val description: String?,
    val timestamp: String?,
    val attributes: Map<String, Any>? = null
)
data class CreateRelationRequest(
    val name: String,
    val relation_type: String?,
    val description: String?,
    val attributes: Map<String, Any>? = null
)