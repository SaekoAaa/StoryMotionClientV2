package com.example.storyvision_client.ui.library
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.storyvision_client.data.entities.CharacterDto

@Composable
fun EntityContent(
    state: LibraryUiState,
    onCharacterClick: (CharacterDto) -> Unit,
    onLoadMore: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (state.selectedType) {
            EntityType.CHARACTERS -> CharactersList(
                characters = state.characters,
                onCharacterClick = onCharacterClick
            )
            EntityType.EVENTS -> EventsList(events = state.events)
            EntityType.RELATIONS -> RelationsList(relations = state.relations)
        }

        if (state.hasMore) {
            Spacer(Modifier.height(12.dp))
            FilledTonalButton(
                onClick = onLoadMore,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Filled.ExpandMore, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Загрузить ещё")
            }
        }
    }
}
