package com.example.storyvision_client.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyvision_client.data.entities.CharacterDto
import com.example.storyvision_client.data.entities.EntitiesRepository

@Composable
fun CharacterDetailsDialog(
    character: CharacterDto,
    projectId: Long,
    entitiesRepo: EntitiesRepository,
    onClose: () -> Unit,
    onUnauthorized: () -> Unit
) {
    val viewModel: CharacterDetailsViewModel = viewModel(
        factory = CharacterDetailsViewModelFactory(entitiesRepo, projectId)
    )

    LaunchedEffect(character.id) {
        // теперь явно передаём id
        viewModel.loadConnections(character.id, onUnauthorized)
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Персонаж: ${character.name}") },
        text = {
            Column {
                Text("Описание: ${character.description ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Text("Связи:", style = MaterialTheme.typography.titleMedium)
                if (viewModel.loading) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                if (viewModel.error != null) {
                    Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
                }
                viewModel.connections.forEach { conn ->
                    ConnectionBox(conn, highlightId = character.id)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) { Text("Закрыть") }
        }
    )
}
