package com.example.storyvision_client.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyvision_client.data.entities.EntitiesRepository


class CharacterDetailsViewModelFactory(
    private val repository: EntitiesRepository,
    private val projectId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterDetailsViewModel(repository, projectId) as T
    }
}