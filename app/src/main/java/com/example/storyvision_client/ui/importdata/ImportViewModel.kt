package com.example.storyvision_client.ui.importdata

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyvision_client.data.AuthResult
import com.example.storyvision_client.data.importdata.ImportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ImportViewModel(
    private val repo: ImportRepository,
    private val projectId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(ImportState())
    val state: StateFlow<ImportState> = _state

    fun importJson(file: File, onUnauthorized: () -> Unit) {
        Log.d("IMPORT_VIEWMODEL", "importJson CALLED with file=${file.name}")
        _state.value = _state.value.copy(isLoading = true, error = null, success = null)
        viewModelScope.launch {
            when (val result = repo.importJson(projectId, file)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = "Файл импортирован",
                        error = null
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                        success = null
                    )
                    if (result.isUnauthorized) onUnauthorized()
                }
            }
        }
    }

    fun clearStatus() {
        _state.value = ImportState()
    }
}

data class ImportState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)
