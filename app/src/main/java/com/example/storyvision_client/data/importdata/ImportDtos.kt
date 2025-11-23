package com.example.storyvision_client.data.importdata

data class ImportResponse(
    val status: String? = null, // по спецификации API, если есть
    val message: String? = null // опционально
)
