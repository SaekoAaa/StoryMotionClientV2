package com.example.storyvision_client.ui.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.storyvision_client.data.remote.ProjectDto



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel,
    onOpenProject: (ProjectDto) -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onOpenAccount: () -> Unit,
    onUnauthorized: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedProject by remember { mutableStateOf<ProjectDto?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var projectName by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var createError by remember { mutableStateOf<String?>(null) }
    var isCreateLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProjects(onUnauthorized)
    }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Проекты",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    // Логотип приложения (опционально)
                    Icon(
                        imageVector = Icons.Filled.Folder,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    // Кнопка переключения темы
                    IconButton(onClick = { onThemeChange(!isDarkTheme) }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Сменить тему"
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
,
                floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Создать проект") },
                text = { Text("Новый проект") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                state.error != null -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Ошибка",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = state.error ?: "")
                        }
                    }
                }

                else -> {
                    if (state.projects.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "У вас пока нет проектов",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Создайте первый проект, чтобы начать работу.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.projects) { project ->
                                ProjectCard(
                                    project = project,
                                    onOpen = { onOpenProject(project) },
                                    onShowDetails = { selectedProject = project }
                                )
                            }
                        }
                    }
                }
            }

            val project = selectedProject
            if (project != null) {
                ProjectDetailsDialog(project = project, onDismiss = { selectedProject = null})
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCreateDialog = false
                    projectName = ""
                    projectDescription = ""
                    createError = null
                },
                title = {
                    Text(
                        text = "Создать проект",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = projectName,
                            onValueChange = { projectName = it },
                            label = { Text("Название проекта") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = projectDescription,
                            onValueChange = { projectDescription = it },
                            label = { Text("Описание") },
                            minLines = 2
                        )
                        if (createError != null) {
                            Text(
                                text = createError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (projectName.isBlank()) {
                                createError = "Укажите название проекта"
                                return@Button
                            }
                            isCreateLoading = true
                            viewModel.createProject(
                                name = projectName,
                                description = projectDescription,
                                onSuccess = {
                                    isCreateLoading = false
                                    showCreateDialog = false
                                    projectName = ""
                                    projectDescription = ""
                                    createError = null
                                },
                                onError = {
                                    createError = it
                                    isCreateLoading = false
                                },
                                onUnauthorized = {
                                    isCreateLoading = false
                                    showCreateDialog = false
                                    onUnauthorized()
                                }
                            )
                        },
                        enabled = !isCreateLoading
                    ) {
                        if (isCreateLoading) {
                            CircularProgressIndicator(Modifier.size(18.dp))
                        } else {
                            Text("Создать")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCreateDialog = false
                        projectName = ""
                        projectDescription = ""
                        createError = null
                    }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}
@Composable
private fun ProjectCard(
    project: ProjectDto,
    onOpen: () -> Unit,
    onShowDetails: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDetails() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = project.name.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = project.owner_name ?: "Нет владельца",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(onClick = onOpen) {
                Text("Открыть")
            }
        }
    }
}
