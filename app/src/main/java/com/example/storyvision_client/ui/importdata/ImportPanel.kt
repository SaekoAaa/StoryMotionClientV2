import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.storyvision_client.ui.importdata.ImportViewModel
import java.io.File

@Composable
fun ImportPanel(
    viewModel: ImportViewModel,
    onUnauthorized: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        if (uri != null) showDialog = true
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            onClick = { pickFileLauncher.launch("application/json") },
            enabled = !state.isLoading
        ) { Text("Выбрать .json файл") }

        if (showDialog && selectedFileUri != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false; selectedFileUri = null },
                title = { Text("Импорт файла") },
                text = { Text("Импортировать выбранный файл?") },
                confirmButton = {
                    Button(
                        onClick = {
                            val file = uriToFile(context, selectedFileUri!!)
                            viewModel.importJson(file, onUnauthorized)
                            showDialog = false
                        },
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) CircularProgressIndicator(Modifier.size(16.dp))
                        else Text("Импортировать")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false; selectedFileUri = null }) { Text("Отмена") }
                }
            )
        }

        state.error?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearStatus()
            }
        }

        state.success?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearStatus()
            }
        }
    }
}

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val tempFile = File.createTempFile("import", ".json", context.cacheDir)
    tempFile.outputStream().use { fileOut ->
        inputStream.copyTo(fileOut)
    }
    return tempFile
}
