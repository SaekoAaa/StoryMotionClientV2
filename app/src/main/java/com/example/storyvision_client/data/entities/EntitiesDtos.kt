// data/entities/EntitiesDtos.kt
package com.example.storyvision_client.data.entities
data class CharacterDto(
    val id: String,
    val name: String,
    val description: String?,
    val created_at: String
)

data class EventDto(
    val id: String,
    val name: String,
    val location: String?,
    val description: String?,
    val timestamp: String,
    val created_at: String
)

data class RelationDto(
    val id: String,
    val name: String,
    val relation_type: String,
    val description: String?,
    val created_at: String
)

data class ConnectionDto(
    val id: String,
    val from_entity_id: String,
    val to_entity_id: String,
    val relation_id: String,
    val relation_type: String,
    val from_name: String,
    val to_name: String,
    val from_type: String, // "Character" или "Event"
    val to_type: String,   // "Character" или "Event"
    val created_at: String
)
data class ConnectionsResponse(
    val items: List<ConnectionDto>,
    val page: Int,
    val per_page: Int,
    val total: Int,
    val has_more: Boolean
)
