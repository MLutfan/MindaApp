package id.antasari.p6minda_230104040129.ui

import androidx.compose.foundation.Canvas // [Untuk DonutChart]
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.antasari.p6minda_230104040129.data.DiaryEntry
import id.antasari.p6minda_230104040129.data.DiaryRepository
import id.antasari.p6minda_230104040129.data.MindaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.math.roundToInt

// [HELPER UI BARU]
@Composable
private fun BorderedCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        content = content
    )
}

@Composable
private fun SummaryColumn(number: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(min = 60.dp)) {
        Text(number.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DonutChart(
    fractions: List<Float>,
    colors: List<Color>,
    size: Dp = 92.dp,
    thickness: Dp = 22.dp
) {
    val normalized = if (fractions.isEmpty()) listOf(1f) else fractions
    val total = normalized.sum()
    val parts = normalized.map { it / total }
    val fallbackColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.size(size)) {
        var start = -90f
        parts.forEachIndexed { i, p ->
            val color = colors.getOrElse(i) { fallbackColor }
            val sweep = p * 360f
            drawArc(
                color = color,
                startAngle = start,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
            )
            start += sweep
        }
    }
}

@Composable
private fun LegendRowTight(dotColor: Color, label: String, percent: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text("${percent.roundToInt()}%", style = MaterialTheme.typography.bodySmall)
    }
}

// [INSIGHTS (Screen 7) - ROMBAK TOTAL]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onEntryTemplates: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }
    var entries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        entries = withContext(Dispatchers.IO) { repo.allEntries() }
    }

    // [Kalkulasi Statistik]
    val totalEntries = entries.size
    val distinctMoods = entries.map { it.mood }.toSet().size
    val datesSet = remember(entries) { entries.map { it.timestamp.toLocalDate() }.toSet() }
    val currentStreak = remember(datesSet) { calcCurrentStreak(datesSet) }

    // [Kalkulasi Mood untuk Donut Chart]
    val moodCounts = remember(entries) { entries.groupingBy { it.mood }.eachCount() }
    val totalForPercent = moodCounts.values.sum().toFloat().coerceAtLeast(1f)
    val trendData = moodCounts.entries.sortedByDescending { it.value }
        .map { (mood, count) -> Triple(mood, count, (count / totalForPercent) * 100f) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Insights", fontWeight = FontWeight.SemiBold) }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // [Kartu 1: Summary (Border)]
            item {
                BorderedCard {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryColumn(number = totalEntries, label = "Entries")
                        SummaryColumn(number = distinctMoods, label = "Moods")
                        SummaryColumn(number = currentStreak, label = "Streak")
                    }
                }
            }

            // [Kartu 2: Ideas (Latar Belakang Soft)]
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("No ideas to write about?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text("Try out the writing templates!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(onClick = onEntryTemplates, shape = RoundedCornerShape(12.dp)) {
                            Text("Try it!")
                        }
                    }
                }
            }

            // [Kartu 3: Trends (Border) - Donut Chart]
            item {
                BorderedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text("Trends", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(4.dp))
                        Spacer(Modifier.height(8.dp))

                        if (trendData.isEmpty()) {
                            Text("No mood data yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(4.dp))
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // [Donut Chart Kiri]
                                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    DonutChart(
                                        fractions = trendData.map { it.third },
                                        colors = trendData.map { moodColor(it.first) },
                                    )
                                }
                                // [Legend Kanan]
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    trendData.forEachIndexed { idx, (mood, _, fract) ->
                                        LegendRowTight(
                                            dotColor = moodColor(mood),
                                            label = moodLabel(mood),
                                            percent = fract
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// [SETTINGS (Screen 8) - ROMBAK TOTAL]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(userName: String?) {
    val displayName = userName ?: "Anonymous"
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings", fontWeight = FontWeight.SemiBold) }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // [Seksi 1: PERSONAL]
            item { SectionHeader("PERSONAL") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Person, null) }, label = "Your name") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Lock, null) }, label = "Password (PIN)") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Palette, null) }, label = "Themes") }

            // [Seksi 2: MY DATA]
            item { SectionHeader("MY DATA") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Cloud, null) }, label = "Backup & Restore") }
            item { SettingsItem(leading = { Text("🗑️") }, label = "Delete all data") }

            // [Seksi 3: REMINDERS]
            item { SectionHeader("REMINDERS") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Notifications, null) }, label = "Daily logging reminder") }

            // [Seksi 4: OTHER]
            item { SectionHeader("OTHER") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Share, null) }, label = "Share with friends") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Help, null) }, label = "Help and Feedback") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Star, null) }, label = "Rate app") }
        }
    }
}

// [Helper 4: Judul Seksi Setting]
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// [Helper 5: Item Setting]
@Composable
private fun SettingsItem(leading: @Composable () -> Unit, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) { leading() }
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, "Next")
    }
}


// [Helper 6: Fungsi Helper Tanggal & Mood (dari L7 & L9)]
private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

private val dateHeaderFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM yyyy")
private fun formatDateHeader(date: LocalDate): String = date.format(dateHeaderFormatter)

// [Fungsi Helper Statistik (Baru)]
private fun calcCurrentStreak(datesSet: Set<LocalDate>): Int {
    if (datesSet.isEmpty()) return 0
    var streak = 0
    var cursor = LocalDate.now()
    while (datesSet.contains(cursor)) {
        streak++
        cursor = cursor.minusDays(1)
    }
    return streak
}

// [Fungsi Helper Warna & Label Mood (Baru)]
private fun moodColor(mood: String): Color = when (mood.lowercase()) {
    "😊", "happy" -> Color(0xFF4CAF50) // Hijau
    "😌", "calm" -> Color(0xFF42A5F5) // Biru
    "😢", "sad" -> Color(0xFFFFB300) // Kuning/Oranye
    "😡", "angry" -> Color(0xFFF44336) // Merah
    "😴", "tired" -> Color(0xFF7E57C2) // Ungu
    "😎", "cool" -> Color(0xFF26A69A) // Teal
    else -> Color.Gray
}
private fun moodLabel(mood: String): String = when (mood.lowercase()) {
    "😊", "happy" -> "Happy"
    "😌", "calm" -> "Calm"
    "😢", "sad" -> "Sad"
    "😡", "angry" -> "Angry"
    "😴", "tired" -> "Tired"
    "😎", "cool" -> "Cool"
    else -> mood
}