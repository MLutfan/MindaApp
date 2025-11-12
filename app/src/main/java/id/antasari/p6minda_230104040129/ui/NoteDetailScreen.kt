package id.antasari.p6minda_230104040129.ui

// [Import lengkap, pastikan 'util.formatTimestamp' ada]
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.antasari.p6minda_230104040129.data.DiaryEntry
import id.antasari.p6minda_230104040129.data.DiaryRepository
import id.antasari.p6minda_230104040129.data.MindaDatabase
import id.antasari.p6minda_230104040129.util.formatTimestamp // [Pastikan import ini benar]
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    entryId: Int,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    onEdit: (Int) -> Unit
) {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }

    var entry by remember { mutableStateOf<DiaryEntry?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(entryId) {
        val loaded = withContext(Dispatchers.IO) {
            repo.getById(entryId)
        }
        entry = loaded
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Your entry",
                        style = MaterialTheme.typography.titleLarge, // [Judul lebih besar]
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(entryId) }, enabled = entry != null) { // [Tombol Edit]
                        Icon(Icons.Filled.Edit, "Edit")
                    }
                    Box { // [Tombol More/Delete]
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, "More")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    if (entry != null) {
                                        showConfirmDelete = true
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val e = entry
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // [Scroll]
        ) {
            if (e == null) {
                Text("Loading...", style = MaterialTheme.typography.bodyMedium)
            } else {
                // [Konten Detail]
                Text(
                    text = "${e.mood} ${e.title}", // [Gabungkan Mood + Title]
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = formatTimestamp(e.timestamp), // [Timestamp]
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Text( // [Isi Jurnal]
                    text = e.content,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                // [Prompt Refleksi]
                Text(
                    text = "Reflection prompts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "What did you learn about yourself today?\n" +
                            "What are you grateful for right now?\n" +
                            "Is there something you want to let go?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // [Dialog Konfirmasi Delete (tetap sama)]
    if (showConfirmDelete && entry != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Delete entry?") },
            text = { Text("This action cannot be undone. Are you sure you want to delete this diary entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val toDelete = entry!!
                        scope.launch(Dispatchers.IO) {
                            repo.remove(toDelete)
                            withContext(Dispatchers.Main) {
                                showConfirmDelete = false
                                onDeleted()
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) { Text("Cancel") }
            }
        )
    }
}