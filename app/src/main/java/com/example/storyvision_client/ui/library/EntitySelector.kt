package com.example.storyvision_client.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EntityTypeSelector(
    selectedType: EntityType,
    onTypeSelected: (EntityType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val entityTypes = EntityType.entries

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.elevatedCardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        )
    ) {
        Box(modifier = Modifier.padding(4.dp)) {
            OutlinedTextField(
                value = selectedType.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Тип сущности") },
                leadingIcon = {
                    Icon(
                        imageVector = when (selectedType) {
                            EntityType.CHARACTERS -> Icons.Filled.Person
                            EntityType.EVENTS -> Icons.Filled.Event
                            EntityType.RELATIONS -> Icons.Filled.Link
                        },
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Выбрать тип")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                enabled = false
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                entityTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        leadingIcon = {
                            Icon(
                                imageVector = when (type) {
                                    EntityType.CHARACTERS -> Icons.Filled.Person
                                    EntityType.EVENTS -> Icons.Filled.Event
                                    EntityType.RELATIONS -> Icons.Filled.Link
                                },
                                contentDescription = null
                            )
                        },
                        onClick = {
                            expanded = false
                            onTypeSelected(type)
                        }
                    )
                }
            }
        }
    }
}
