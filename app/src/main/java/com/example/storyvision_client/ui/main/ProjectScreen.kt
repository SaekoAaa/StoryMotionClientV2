package com.example.storyvision_client.ui.main


import ImportPanel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyvision_client.data.entities.EntitiesRepository
import com.example.storyvision_client.data.importdata.ImportRepository
import com.example.storyvision_client.ui.importdata.ImportViewModel
import com.example.storyvision_client.ui.importdata.ImportViewModelFactory
import com.example.storyvision_client.ui.library.LibraryPanel
import com.example.storyvision_client.ui.library.LibraryViewModel
import com.example.storyvision_client.ui.library.LibraryViewModelFactory

enum class MainTab(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    LIBRARY("Библиотека", Icons.Filled.Home),
    NOTES("Заметки", Icons.Filled.Favorite),
    IMPORT("Импорт проекта", Icons.Filled.Settings)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    projectId: Long,
    projectName: String,
    entitiesRepo: EntitiesRepository,
    importRepo: ImportRepository,
    onOpenAccount: () -> Unit,
    onUnauthorized: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.IMPORT) }

    // создаём LibraryViewModel через фабрику, чтобы был правильный projectId
    val libraryViewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModelFactory(entitiesRepo, projectId)
    )
// Создать ImportRepository
    val importViewModel: ImportViewModel = viewModel(
        factory = ImportViewModelFactory(importRepo, projectId)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("${selectedTab.title} — $projectName")
                },
                actions = {
                    IconButton(onClick = onOpenAccount) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Аккаунт"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                MainTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                MainTab.LIBRARY -> LibraryPanel(
                    viewModel = libraryViewModel,
                    onUnauthorized = onUnauthorized,
                    projectId = projectId,
                    entitiesRepo = entitiesRepo
                )
                MainTab.NOTES -> Text("Notes: $projectId")
                MainTab.IMPORT -> ImportPanel(
                    viewModel = importViewModel,
                    onUnauthorized = onUnauthorized
                )
            }
        }
    }
}
