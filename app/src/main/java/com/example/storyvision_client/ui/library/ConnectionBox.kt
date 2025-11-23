package com.example.storyvision_client.ui.library
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyvision_client.data.entities.CharacterDto
import com.example.storyvision_client.data.entities.ConnectionDto
import com.example.storyvision_client.data.entities.EntitiesRepository
@Composable
fun ConnectionBox(conn: ConnectionDto, highlightId: String) {
    // зелёный для Character, синий для Event
    val leftColor = if (conn.from_type == "Character") Color(0xFFD0F5D7) else Color(0xFFE3F0FC)
    val rightColor = if (conn.to_type == "Character") Color(0xFFD0F5D7) else Color(0xFFE3F0FC)

    val arrowIcon = Icons.AutoMirrored.Filled.ArrowForward

    // кто "основной"? выравниваем по highlightId
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            Modifier
                .background(leftColor, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column {
                Text(conn.from_name, fontWeight = if (conn.from_entity_id == highlightId) FontWeight.Bold else FontWeight.Normal)
                Text(conn.from_type, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.width(6.dp))
        Icon(arrowIcon, contentDescription = null)
        Spacer(Modifier.width(6.dp))
        Box(
            Modifier
                .background(rightColor, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column {
                Text(conn.to_name, fontWeight = if (conn.to_entity_id == highlightId) FontWeight.Bold else FontWeight.Normal)
                Text(conn.to_type, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            conn.relation_type,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
