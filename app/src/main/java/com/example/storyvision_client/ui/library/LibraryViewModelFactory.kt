package com.example.storyvision_client.ui.library
// ui/library/LibraryViewModelFactory.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyvision_client.data.entities.EntitiesRepository

class LibraryViewModelFactory(
    private val entitiesRepo: EntitiesRepository,
    private val projectId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(entitiesRepo, projectId) as T
    }
}
