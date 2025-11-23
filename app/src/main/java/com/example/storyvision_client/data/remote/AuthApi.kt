package com.example.storyvision_client.data.remote


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @GET("/healthcheck")
    suspend fun healthCheck(): Response<Unit>

    @POST("/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @GET("/v1/auth/me")
    suspend fun me(
        @Header("Authorization") authHeader: String
    ): Response<MeResponse>

    @POST("/v1/auth/logout")
    suspend fun logout(
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    @POST("/v1/auth/refresh")
    suspend fun refreshWithCookie(
        @Header("Cookie") cookie: String
    ): Response<RefreshResponse>
    @GET("/v1/projects")
    suspend fun getProjects(
        @Header("Authorization") authHeader: String
    ): Response<List<ProjectDto>>
    // data/remote/AuthApi.kt
    @POST("/v1/projects")
    suspend fun createProject(
        @Header("Authorization") authHeader: String,
        @Body request: CreateProjectRequest
    ): Response<ProjectDto>

}