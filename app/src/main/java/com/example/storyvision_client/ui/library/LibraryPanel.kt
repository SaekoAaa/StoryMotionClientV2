package com.example.storyvision_client.ui.library
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.storyvision_client.data.entities.CharacterDto
import com.example.storyvision_client.data.entities.EntitiesRepository


@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun LibraryPanel(
    viewModel: LibraryViewModel,
    onUnauthorized: () -> Unit,
    projectId: Long,
    entitiesRepo: EntitiesRepository,
) {
    val state by viewModel.state.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf<CharacterDto?>(null) }

    LaunchedEffect(state.selectedType) {
        viewModel.load(state.selectedType, page = 1, onUnauthorized = onUnauthorized)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Создать") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            EntityTypeSelector(
                selectedType = state.selectedType,
                onTypeSelected = { type ->
                    viewModel.load(type, page = 1, onUnauthorized = onUnauthorized)
                }
            )

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(error = state.error!!)
                else -> {
                    EntityContent(
                        state = state,
                        onCharacterClick = { showDetailsDialog = it },
                        onLoadMore = {
                            viewModel.load(
                                state.selectedType, state.page + 1, 10, onUnauthorized
                            )
                        }
                    )
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

        if (showCreateDialog) {
            CreateEntityDialog(
                entityType = state.selectedType,
                viewModel = viewModel,
                onDismiss = { showCreateDialog = false },
                onUnauthorized = onUnauthorized
            )
        }
    }
}

fun parseDuplicateError(message: String?): String? = when {
    message?.contains("CHARACTER_ALREADY_EXISTS", ignoreCase = true) == true ->
        "Персонаж с таким именем уже существует"
    message?.contains("EVENT_ALREADY_EXISTS", ignoreCase = true) == true ->
        "Событие с таким именем уже существует"
    message?.contains("RELATION_ALREADY_EXISTS", ignoreCase = true) == true ->
        "Тип связи с таким именем уже существует"
    else -> message
}
