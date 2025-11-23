package com.example.storyvision_client.data.importdata

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImportApi {
    @Multipart
    @POST("/v1/entities/{project_id}/import")
    suspend fun importJsonFile(
        @Header("Authorization") authHeader: String,
        @Path("project_id") projectId: Long,
        @Part file: MultipartBody.Part
    ): Response<Unit>
}
