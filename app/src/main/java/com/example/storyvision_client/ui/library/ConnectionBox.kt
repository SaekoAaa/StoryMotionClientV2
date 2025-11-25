package com.example.storyvision_client.ui.library
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyvision_client.data.entities.ConnectionDto
@Composable
fun ConnectionBox(conn: ConnectionDto, highlightId: String) {
    // Используем цвета из Material 3 theme, которые автоматически меняются
    val characterColor = MaterialTheme.colorScheme.primaryContainer
    val eventColor = MaterialTheme.colorScheme.secondaryContainer

    val leftColor = if (conn.from_type == "Character") characterColor else eventColor
    val rightColor = if (conn.to_type == "Character") characterColor else eventColor

    val leftTextColor = if (conn.from_type == "Character")
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    val rightTextColor = if (conn.to_type == "Character")
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    val arrowIcon = Icons.AutoMirrored.Filled.ArrowForward

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {
        // Left entity box
        Surface(
            color = leftColor,
            shape = RoundedCornerShape(8.dp),
            tonalElevation = if (conn.from_entity_id == highlightId) 4.dp else 0.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = conn.from_name,
                    fontWeight = if (conn.from_entity_id == highlightId) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = leftTextColor
                )
                Text(
                    text = conn.from_type,
                    style = MaterialTheme.typography.bodySmall,
                    color = leftTextColor.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            imageVector = arrowIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(8.dp))

        // Right entity box
        Surface(
            color = rightColor,
            shape = RoundedCornerShape(8.dp),
            tonalElevation = if (conn.to_entity_id == highlightId) 4.dp else 0.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = conn.to_name,
                    fontWeight = if (conn.to_entity_id == highlightId) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = rightTextColor
                )
                Text(
                    text = conn.to_type,
                    style = MaterialTheme.typography.bodySmall,
                    color = rightTextColor.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = conn.relation_type,
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
