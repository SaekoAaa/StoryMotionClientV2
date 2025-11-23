package com.example.storyvision_client.ui.importdata
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyvision_client.data.importdata.ImportRepository
import com.example.storyvision_client.ui.importdata.ImportViewModel

class ImportViewModelFactory(
    private val repo: ImportRepository,
    private val projectId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImportViewModel::class.java)) {
            return ImportViewModel(repo, projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
