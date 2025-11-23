package com.example.storyvision_client.data.remote



// refresh токена
data class RefreshRequest(
    val refresh_token: String
)

data class RefreshResponse(
    val access_token: String
)
// data/remote/ProjectDtos.kt
data class CreateProjectRequest(
    val project_name: String,
    val description: String
)

data class ProjectDto(
    val id: Long,
    val name: String,
    val valid_name: String,
    val owner_id: Long,
    val owner_name: String? = null,
    val description: String
)
