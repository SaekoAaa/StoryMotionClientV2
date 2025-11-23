package com.example.storyvision_client.data.importdata


import android.util.Log
import com.example.storyvision_client.data.AuthRepository
import com.example.storyvision_client.data.AuthResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ImportRepository(
    private val api: ImportApi,
    private val authRepo: AuthRepository
) {
    suspend fun importJson(
        projectId: Long,
        file: File
    ): AuthResult<Unit> {
        val requestFile = file
            .asRequestBody("application/json".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestFile
        )
        return authRepo.authorizedCall { token ->
            Log.d("IMPORT", "Token = $token")
            api.importJsonFile(token, projectId, body)
        }
    }
}
