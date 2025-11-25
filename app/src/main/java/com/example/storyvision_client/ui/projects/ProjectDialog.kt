package com.example.storyvision_client.ui.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.storyvision_client.data.remote.ProjectDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsDialog(
    project: ProjectDto,
    onDismiss: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onAddMember: () -> Unit = {}
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            var actionsExpanded by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = 560.dp)
                    .padding(24.dp)
            ) {
                // Заголовок + close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Закрыть")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Контент
                Text(
                    text = "Описание",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = project.description.ifBlank { "Нет описания" },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Владелец: ${project.owner_name ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                // Действия
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Закрыть") }

                    Box {
                        FilledTonalButton(
                            onClick = { actionsExpanded = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.MoreVert, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Еще")
                        }
                        DropdownMenu(
                            expanded = actionsExpanded,
                            onDismissRequest = { actionsExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Редактировать") },
                                leadingIcon = { Icon(Icons.Filled.Edit, null) },
                                onClick = { actionsExpanded = false; onEdit() }
                            )
                            DropdownMenuItem(
                                text = { Text("Удалить", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = { actionsExpanded = false; onDelete() }
                            )
                            DropdownMenuItem(
                                text = { Text("Добавить участника") },
                                leadingIcon = { Icon(Icons.Filled.PersonAdd, null) },
                                onClick = { actionsExpanded = false; onAddMember() }
                            )
                        }
                    }
                }
            }
        }
    }
}
