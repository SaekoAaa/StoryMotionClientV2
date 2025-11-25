package com.example.storyvision_client.ui.main


import ImportPanel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
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
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    LIBRARY(
        "Библиотека",
        Icons.AutoMirrored.Outlined.MenuBook,
        Icons.AutoMirrored.Default.MenuBook
    ),
    NOTES(
        "Заметки",
        Icons.Outlined.EditNote,
        Icons.Filled.EditNote
    ),
    IMPORT(
        "Импорт",
        Icons.Outlined.Upload,
        Icons.Filled.Upload
    )
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectScreen(
    projectId: Long,
    projectName: String,
    entitiesRepo: EntitiesRepository,
    importRepo: ImportRepository,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onOpenAccount: () -> Unit,
    onUnauthorized: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.LIBRARY) }

    val libraryViewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModelFactory(entitiesRepo, projectId)
    )
    val importViewModel: ImportViewModel = viewModel(
        factory = ImportViewModelFactory(importRepo, projectId)
    )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text(
                            text = projectName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = selectedTab.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        shape = MaterialShapes.Cookie6Sided.toShape(),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                actions = {
                    // Переключатель темы
                    IconButton(
                        onClick = { onThemeChange(!isDarkTheme) }
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) {
                                Icons.Filled.LightMode
                            } else {
                                Icons.Filled.DarkMode
                            },
                            contentDescription = "Сменить тему",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Кнопка аккаунта
                    IconButton(onClick = onOpenAccount) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Аккаунт",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 3.dp
            ) {
                MainTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (tab == selectedTab) {
                                    tab.selectedIcon
                                } else {
                                    tab.icon
                                },
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (tab == selectedTab) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            when (selectedTab) {
                MainTab.LIBRARY -> LibraryPanel(
                    viewModel = libraryViewModel,
                    onUnauthorized = onUnauthorized,
                    projectId = projectId,
                    entitiesRepo = entitiesRepo
                )
                MainTab.NOTES -> NotesPlaceholder(projectId = projectId)
                MainTab.IMPORT -> ImportPanel(
                    viewModel = importViewModel,
                    onUnauthorized = onUnauthorized
                )
            }
        }
    }
}

@Composable
private fun NotesPlaceholder(projectId: Long) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.EditNote,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Text(
                text = "Заметки",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Функционал в разработке",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
