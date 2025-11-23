package com.example.storyvision_client.ui.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.storyvision_client.data.remote.ProjectDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel,
    onOpenProject: (ProjectDto) -> Unit,
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Выбор проекта") },
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
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Создать проект")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error)
                }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.projects) { project ->
                        ProjectCard(
                            project = project,
                            onOpen = { onOpenProject(project) },
                            onShowDetails = { selectedProject = project }
                        )
                    }
                }
            }

            // Модальное окно с подробной инфой о проекте
            val project = selectedProject
            if (project != null) {
                AlertDialog(
                    onDismissRequest = { selectedProject = null },
                    title = { Text(project.name) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Описание: ${project.description}")
                            Text("Владелец: ${project.owner_name ?: ""}")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { selectedProject = null }) { Text("Закрыть") }
                    },
                    dismissButton = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { /* TODO: редактировать */ }) { Text("Редактировать") }
                            TextButton(onClick = { /* TODO: удалить */ }) { Text("Удалить") }
                            TextButton(onClick = { /* TODO: добавить участника */ }) { Text("Добавить участника") }
                        }
                    }
                )
            }
        }

        // Диалог создания проекта
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCreateDialog = false
                    projectName = ""
                    projectDescription = ""
                    createError = null
                },
                title = { Text("Создать проект") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = projectName,
                            onValueChange = { projectName = it },
                            label = { Text("Название проекта") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = projectDescription,
                            onValueChange = { projectDescription = it },
                            label = { Text("Описание") }
                        )
                        if (createError != null) {
                            Text(
                                text = createError ?: "",
                                color = MaterialTheme.colorScheme.error
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
                        if (isCreateLoading) CircularProgressIndicator(Modifier.size(18.dp))
                        else Text("Создать")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCreateDialog = false
                        projectName = ""
                        projectDescription = ""
                        createError = null
                    }) { Text("Отмена") }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDetails() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = project.owner_name ?: "Not owner",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = onOpen) {
                Text("Открыть")
            }
        }
    }
}
