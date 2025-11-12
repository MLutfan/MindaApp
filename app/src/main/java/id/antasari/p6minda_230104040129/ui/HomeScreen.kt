package id.antasari.p6minda_230104040129.ui

// [Import baru: Image, Search, clip, layout, painterResource]
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// [Import R, pastikan package Anda benar]
import id.antasari.p6minda_230104040129.R
import id.antasari.p6minda_230104040129.data.DiaryEntry
import id.antasari.p6minda_230104040129.data.DiaryRepository
import id.antasari.p6minda_230104040129.data.MindaDatabase
import id.antasari.p6minda_230104040129.util.formatTimestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// [Import baru untuk format tanggal]
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String?, // [Sekarang tidak dipakai di AppBar, tapi biarkan untuk langkah 10]
    onOpenEntry: (Int) -> Unit,
    onNewEntry: () -> Unit // [Callback FAB masih ada, tapi FAB pindah ke MainActivity]
) {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }

    var entries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }
    // [State baru untuk search bar]
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // [LaunchedEffect untuk ambil data + seed data]
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val current = repo.allEntries()
            if (current.isEmpty()) {
                val sample = DiaryEntry(
                    id = 0,
                    title = "Gratitude journal",
                    content = "What am I thankful for today?\nWho made my day better?",
                    mood = "😊",
                    timestamp = System.currentTimeMillis()
                )
                repo.add(sample)
            }
            // [Urutkan berdasarkan timestamp terbaru]
            entries = repo.allEntries().sortedByDescending { it.timestamp }
        }
    }

    // [Logika filter berdasarkan pencarian]
    val filteredEntries = remember(entries, searchQuery) {
        val q = searchQuery.trim()
        if (q.isBlank()) entries
        else entries.filter { e ->
            e.title.contains(q, ignoreCase = true) ||
                    e.content.contains(q, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Diary", // [Judul baru]
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    // [Tombol Search]
                    IconButton(onClick = {
                        isSearching = !isSearching
                        if (!isSearching) searchQuery = "" // [Reset pencarian saat ditutup]
                    }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // [Item 1: Search Bar (jika aktif)]
            if (isSearching) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search your entries") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        singleLine = true
                    )
                }
            }

            // [Item 2: Banner Image]
            item {
                Image(
                    painter = painterResource(id = R.drawable.banner_diary), // [Ambil dari drawable]
                    contentDescription = "Diary banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(12.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop // [Pastikan gambar memenuhi frame]
                )
            }

            // [Item 3: Daftar Jurnal (Hasil filter)]
            items(filteredEntries) { entry ->
                DiaryListItem( // [Panggil item list baru]
                    entry = entry,
                    onClick = { onOpenEntry(entry.id) }
                )
            }
        }
    }
}

// [COMPOSABLE BARU UNTUK ITEM LIST]
@Composable
private fun DiaryListItem(
    entry: DiaryEntry,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // [Logika untuk format tanggal di kiri]
        val localDateTime = Instant.ofEpochMilli(entry.timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val formatterDay = DateTimeFormatter.ofPattern("dd")
        val formatterMonth = DateTimeFormatter.ofPattern("MMM")
        val formatterYear = DateTimeFormatter.ofPattern("yyyy")

        // [Kolom Kiri: Tanggal]
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = localDateTime.format(formatterDay),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = localDateTime.format(formatterMonth),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = localDateTime.format(formatterYear),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // [Kolom Kanan: Konten]
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = formatTimestamp(entry.timestamp), // [Timestamp lengkap]
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = entry.content.take(80), // [Cuplikan 80 karakter]
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    Divider(
        color = MaterialTheme.colorScheme.surfaceVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}