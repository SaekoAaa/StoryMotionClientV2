package com.example.storyvision_client.ui.library
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.storyvision_client.data.entities.CharacterDto
import com.example.storyvision_client.data.entities.EntitiesRepository

@Composable
fun LibraryPanel(
    viewModel: LibraryViewModel,
    onUnauthorized: () -> Unit,
    projectId: Long,
    entitiesRepo: EntitiesRepository,
) {
    val state by viewModel.state.collectAsState()
    val entityTypes = EntityType.values().toList()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showAttrDialog by remember { mutableStateOf(false) }

    var createError by remember { mutableStateOf<String?>(null) }
    var isCreateLoading by remember { mutableStateOf(false) }

    var formName by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formLocation by remember { mutableStateOf("") }
    var formTimestamp by remember { mutableStateOf("") }
    var formRelationType by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val createdAttrs = remember { mutableStateListOf<Pair<String, String>>() }
    var showDetailsDialog by remember { mutableStateOf<CharacterDto?>(null) }

    LaunchedEffect(state.selectedType) {
        viewModel.load(state.selectedType, page = 1, onUnauthorized = onUnauthorized)
        formName = ""
        formDescription = ""
        formLocation = ""
        formTimestamp = ""
        formRelationType = ""
        createdAttrs.clear()
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, "Добавить")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Combobox (DropDown)
            Box {
                OutlinedTextField(
                    value = state.selectedType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Тип сущности") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Выбрать тип")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    entityTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                expanded = false
                                viewModel.load(type, page = 1, onUnauthorized = onUnauthorized)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                state.isLoading -> Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                state.error != null -> {
                    Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error)
                }

                else -> {
                    when (state.selectedType) {
                        EntityType.CHARACTERS -> {
                            Text("Персонажи:", style = MaterialTheme.typography.titleMedium)
                            state.characters.forEach { ch ->
                                Card(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { showDetailsDialog = ch }
                                ) {
                                    Column(Modifier.padding(8.dp)) {
                                        Text(ch.name, style = MaterialTheme.typography.bodyLarge)
                                        ch.description?.let {
                                            Text(it, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }

                        }

                        EntityType.EVENTS -> {
                            Text("События:", style = MaterialTheme.typography.titleMedium)
                            state.events.forEach { ev ->
                                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Column(Modifier.padding(8.dp)) {
                                        Text(ev.name, style = MaterialTheme.typography.bodyLarge)
                                        ev.location?.let {
                                            Text(
                                                "Локация: $it",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        ev.description?.let {
                                            Text(it, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }

                        EntityType.RELATIONS -> {
                            Text("Типы связей:", style = MaterialTheme.typography.titleMedium)
                            state.relations.forEach { rel ->
                                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Column(Modifier.padding(8.dp)) {
                                        Text(rel.name, style = MaterialTheme.typography.bodyLarge)
                                        Text(
                                            rel.relation_type,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        rel.description?.let {
                                            Text(it, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (showDetailsDialog != null) {
                CharacterDetailsDialog(
                    character = showDetailsDialog!!,
                    projectId = projectId,
                    entitiesRepo = entitiesRepo,
                    onClose = { showDetailsDialog = null },
                    onUnauthorized = onUnauthorized
                )
            }
            // Pagination, если нужно
            if (state.hasMore) {
                Button(
                    onClick = {
                        viewModel.load(
                            state.selectedType,
                            state.page + 1,
                            onUnauthorized = onUnauthorized
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp)
                ) {
                    Text("Загрузить ещё")
                }
            }
        }
        if (showCreateDialog) {
            val type = state.selectedType
            AlertDialog(
                onDismissRequest = {
                    showCreateDialog = false
                    formName = ""; formDescription = ""; formLocation = "";
                    formTimestamp = ""; formRelationType = ""; createdAttrs.clear(); createError =
                    null
                },
                title = { Text("Создать ${type.displayName}") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = formName, onValueChange = { formName = it },
                            label = { Text("Название") }, singleLine = true
                        )
                        if (type == EntityType.EVENTS) {
                            OutlinedTextField(
                                value = formLocation, onValueChange = { formLocation = it },
                                label = { Text("Локация") }, singleLine = true
                            )
                            OutlinedTextField(
                                value = formTimestamp, onValueChange = { formTimestamp = it },
                                label = { Text("Время события (ISO)") }, singleLine = true
                            )
                        }
                        if (type == EntityType.RELATIONS) {
                            OutlinedTextField(
                                value = formRelationType, onValueChange = { formRelationType = it },
                                label = { Text("Тип связи") }, singleLine = true
                            )
                        }
                        OutlinedTextField(
                            value = formDescription, onValueChange = { formDescription = it },
                            label = { Text("Описание") }, minLines = 2, maxLines = 4
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { showAttrDialog = true }) {
                            Text("Атрибуты (${createdAttrs.size})")
                        }
                        if (createError != null) {
                            Text(createError!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Сбор атрибутов
                            val attrs = createdAttrs
                                .filter { it.first.isNotBlank() }
                                .associate { it.first to it.second }
                            isCreateLoading = true
                            createError = null
                            when (type) {
                                EntityType.CHARACTERS -> viewModel.createCharacter(
                                    name = formName,
                                    description = formDescription,
                                    attributes = attrs,
                                    onSuccess = {
                                        isCreateLoading = false
                                        showCreateDialog = false
                                    },
                                    onError = { err ->
                                        isCreateLoading = false
                                        createError = parseDuplicateError(err)
                                    },
                                    onUnauthorized = { isCreateLoading = false; onUnauthorized() }
                                )

                                EntityType.EVENTS -> viewModel.createEvent(
                                    name = formName,
                                    location = formLocation,
                                    description = formDescription,
                                    timestamp = formTimestamp,
                                    attributes = attrs,
                                    onSuccess = {
                                        isCreateLoading = false
                                        showCreateDialog = false
                                    },
                                    onError = { err ->
                                        isCreateLoading = false
                                        createError = parseDuplicateError(err)
                                    },
                                    onUnauthorized = { isCreateLoading = false; onUnauthorized() }
                                )

                                EntityType.RELATIONS -> viewModel.createRelation(
                                    name = formName,
                                    type = formRelationType,
                                    description = formDescription,
                                    attributes = attrs,
                                    onSuccess = {
                                        isCreateLoading = false
                                        showCreateDialog = false
                                    },
                                    onError = { err ->
                                        isCreateLoading = false
                                        createError = parseDuplicateError(err)
                                    },
                                    onUnauthorized = { isCreateLoading = false; onUnauthorized() }
                                )
                            }
                        },
                        enabled = !isCreateLoading && formName.isNotBlank()
                    ) {
                        if (isCreateLoading) CircularProgressIndicator(Modifier.size(16.dp))
                        else Text("Создать")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCreateDialog = false
                        formName = ""; formDescription = ""; formLocation = "";
                        formTimestamp = ""; formRelationType =
                        ""; createdAttrs.clear(); createError = null
                    }) { Text("Отмена") }
                }
            )
        }

        // Диалог добавления атрибутов
        if (showAttrDialog) {
            AlertDialog(
                onDismissRequest = { showAttrDialog = false },
                title = { Text("Атрибуты") },
                text = {
                    Column {
                        createdAttrs.forEachIndexed { idx, (key, value) ->
                            Row {
                                OutlinedTextField(
                                    value = key,
                                    onValueChange = { newKey -> createdAttrs[idx] = newKey to value },
                                    label = { Text("Название") }, modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(4.dp))
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { newValue -> createdAttrs[idx] = key to newValue },
                                    label = { Text("Значение") }, modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    if (createdAttrs.size > idx) {
                                        createdAttrs.removeAt(idx)
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, "Удалить")
                                }
                            }
                        }
                        Button(onClick = { createdAttrs.add("" to "") }) {
                            Text("Добавить атрибут")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAttrDialog = false }) { Text("ОК") }
                }
            )
        }
    }
}
fun parseDuplicateError(message: String?): String? = when {
    message?.contains("CHARACTER_ALREADY_EXISTS") == true -> "Персонаж с таким именем уже есть."
    message?.contains("EVENT_ALREADY_EXISTS") == true -> "Событие с таким именем уже есть."
    message?.contains("RELATION_ALREADY_EXISTS") == true -> "Связь с таким именем уже есть."
    else -> message
}