package com.example.storyvision_client.ui.library
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Title
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.storyvision_client.data.entities.EventDto


@Composable
fun CreateEntityDialog(
    entityType: EntityType,
    viewModel: LibraryViewModel,
    onDismiss: () -> Unit,
    onUnauthorized: () -> Unit
) {
    var formName by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formLocation by remember { mutableStateOf("") }
    var formTimestamp by remember { mutableStateOf("") }
    var formRelationType by remember { mutableStateOf("") }
    var createError by remember { mutableStateOf<String?>(null) }
    var isCreateLoading by remember { mutableStateOf(false) }
    var showAttrDialog by remember { mutableStateOf(false) }
    val createdAttrs = remember { mutableStateListOf<Pair<String, String>>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (entityType) {
                        EntityType.CHARACTERS -> Icons.Filled.Person
                        EntityType.EVENTS -> Icons.Filled.Event
                        EntityType.RELATIONS -> Icons.Filled.Link
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text("Создать ${entityType.displayName}")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = formName,
                    onValueChange = { formName = it; createError = null },
                    label = { Text("Название") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Filled.Title, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formName.isBlank() && createError != null
                )

                if (entityType == EntityType.EVENTS) {
                    OutlinedTextField(
                        value = formLocation,
                        onValueChange = { formLocation = it },
                        label = { Text("Локация") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Place, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formTimestamp,
                        onValueChange = { formTimestamp = it },
                        label = { Text("Время события (ISO)") },
                        placeholder = { Text("2024-01-01T12:00:00Z") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Schedule, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (entityType == EntityType.RELATIONS) {
                    OutlinedTextField(
                        value = formRelationType,
                        onValueChange = { formRelationType = it },
                        label = { Text("Тип связи") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Category, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = formDescription,
                    onValueChange = { formDescription = it },
                    label = { Text("Описание") },
                    minLines = 3,
                    maxLines = 5,
                    leadingIcon = {
                        Icon(Icons.Filled.Description, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                FilledTonalButton(
                    onClick = { showAttrDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Tag, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Атрибуты")
                    if (createdAttrs.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "${createdAttrs.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (createError != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = createError!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (formName.isBlank()) {
                        createError = "Укажите название"
                        return@Button
                    }
                    val attrs = createdAttrs
                        .filter { it.first.isNotBlank() }
                        .associate { it.first to it.second }

                    isCreateLoading = true
                    createError = null

                    when (entityType) {
                        EntityType.CHARACTERS -> viewModel.createCharacter(
                            name = formName,
                            description = formDescription,
                            attributes = attrs,
                            onSuccess = { isCreateLoading = false; onDismiss() },
                            onError = { isCreateLoading = false; createError = parseDuplicateError(it) },
                            onUnauthorized = { isCreateLoading = false; onUnauthorized() }
                        )
                        EntityType.EVENTS -> viewModel.createEvent(
                            name = formName,
                            location = formLocation,
                            description = formDescription,
                            timestamp = formTimestamp,
                            attributes = attrs,
                            onSuccess = { isCreateLoading = false; onDismiss() },
                            onError = { isCreateLoading = false; createError = parseDuplicateError(it) },
                            onUnauthorized = { isCreateLoading = false; onUnauthorized() }
                        )
                        EntityType.RELATIONS -> viewModel.createRelation(
                            name = formName,
                            type = formRelationType,
                            description = formDescription,
                            attributes = attrs,
                            onSuccess = { isCreateLoading = false; onDismiss() },
                            onError = { isCreateLoading = false; createError = parseDuplicateError(it) },
                            onUnauthorized = { isCreateLoading = false; onUnauthorized() }
                        )
                    }
                },
                enabled = !isCreateLoading
            ) {
                if (isCreateLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Создать")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )

    if (showAttrDialog) {
        AttributesDialog(
            attributes = createdAttrs,
            onDismiss = { showAttrDialog = false }
        )
    }
}
