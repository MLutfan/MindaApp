package id.antasari.p6minda_230104040129.ui

// [Import baru yang sangat banyak untuk UI baru]
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.antasari.p6minda_230104040129.data.DiaryEntry
import id.antasari.p6minda_230104040129.data.DiaryRepository
import id.antasari.p6minda_230104040129.data.MindaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// [Daftar mood baru kita]
private val moodOptions = listOf(
    "😊" to "Happy",
    "😌" to "Calm",
    "😢" to "Sad",
    "😡" to "Angry",
    "😴" to "Tired",
    "😎" to "Cool"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryScreen(
    onBack: () -> Unit,
    onSaved: (Int) -> Unit
) {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }
    val scope = rememberCoroutineScope()

    // [State untuk form]
    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") } // [Mood default baru]

    // [State untuk Date/Time Picker]
    val now = remember { Instant.now().atZone(ZoneId.systemDefault()) }
    var selectedDate by remember { mutableStateOf(now.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(now.toLocalTime()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // [Formatter untuk tampilan Tanggal & Waktu]
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()) }
    val formattedDate = selectedDate.format(dateFormatter)
    val formattedTime = selectedTime.format(timeFormatter)

    // [Fungsi untuk menggabungkan tanggal & waktu menjadi timestamp (Long)]
    fun combineDateTimeMillis(): Long {
        val ldt = selectedDate.atTime(selectedTime)
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    // [Fungsi untuk menyimpan]
    fun goSave() {
        if (titleText.isBlank() || contentText.isBlank()) return // [Validasi]
        scope.launch(Dispatchers.IO) {
            val newEntry = DiaryEntry(
                id = 0,
                title = titleText,
                content = contentText,
                mood = selectedMood,
                timestamp = combineDateTimeMillis() // [Gunakan timestamp baru]
            )
            val newId = repo.add(newEntry).toInt()
            withContext(Dispatchers.Main) {
                onSaved(newId) // [Kirim ID baru]
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New entry", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    // [Tombol Save pindah ke AppBar agar rapi]
                    Button(
                        onClick = ::goSave,
                        enabled = titleText.isNotBlank() && contentText.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Done")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // [Baris Tanggal & Waktu (Bisa diklik)]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                    Text("  •  ", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable { showTimePicker = true }
                    )
                }

                // [Form Judul & Konten]
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    label = { Text("What's on your mind?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp),
                    singleLine = false,
                    maxLines = 8
                )
                Spacer(Modifier.height(16.dp))

                // [Mood Picker]
                Text(
                    "Mood",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                MoodRow( // [Komponen Chip Emoji]
                    options = moodOptions,
                    selected = selectedMood,
                    onSelect = { selectedMood = it }
                )
            }
        }
    }

    // [Dialog Date Picker]
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Save") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // [Dialog Time Picker]
    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                Button(onClick = {
                    selectedTime = LocalTime.of(timeState.hour, timeState.minute)
                    showTimePicker = false
                }) { Text("Save") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    TimePicker(state = timeState)
                }
            }
        )
    }
}

// [Helper 6: Komponen untuk 1 baris Mood (Emoji Chip)]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodRow(
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (emoji, label) ->
            val color = moodColor(emoji)
            FilterChip(
                selected = selected == emoji,
                onClick = { onSelect(emoji) },
                label = { Text("$emoji $label") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = color.copy(alpha = 0.20f),
                    selectedLabelColor = color
                )
            )
        }
    }
}

// [Helper 7: Warna untuk Mood]
@Composable
private fun moodColor(mood: String): Color = when (mood.lowercase()) {
    "😊", "happy" -> Color(0xFF4CAF50) // Hijau
    "😌", "calm" -> Color(0xFF42A5F5) // Biru
    "😢", "sad" -> Color(0xFFFFB300) // Kuning/Oranye
    "😡", "angry" -> Color(0xFFF44336) // Merah
    "😴", "tired" -> Color(0xFF7E57C2) // Ungu
    "😎", "cool" -> Color(0xFF26A69A) // Teal
    else -> MaterialTheme.colorScheme.primary
}