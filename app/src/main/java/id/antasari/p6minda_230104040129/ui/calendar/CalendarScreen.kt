package id.antasari.p6minda_230104040129.ui.calendar

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import id.antasari.p6minda_230104040129.data.DiaryEntry
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// [Data class untuk sel kalender]
private data class DayCellData(val date: LocalDate, val inCurrentMonth: Boolean)

// [Composable Utama]
@Composable
fun CalendarScreen(
    onEdit: (Int) -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as Application
    val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.provideFactory(app))

    // [Ambil data real-time dari ViewModel]
    val diaryByDate by vm.diaryByDate.collectAsStateWithLifecycle()

    val today = remember { LocalDate.now() }
    var visibleMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        // [Header: "November 2025" + Picker]
        MonthHeaderWithDayMonthYearPicker(
            visibleMonth = visibleMonth,
            selectedDate = selectedDate,
            onPick = {
                selectedDate = it
                visibleMonth = YearMonth.from(it)
            }
        )

        // [Baris Hari: Sun, Mon, Tue, ...]
        DayOfWeekHeader()
        Spacer(Modifier.height(6.dp))

        // [Grid Kalender]
        val cells = remember(visibleMonth) { buildMonthCells(visibleMonth) }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cells) { c ->
                DayCell(
                    date = c.date,
                    inCurrentMonth = c.inCurrentMonth,
                    selected = c.date == selectedDate,
                    hasDiary = diaryByDate[c.date]?.isNotEmpty() == true, // [Cek dot]
                    cellHeight = 40.dp,
                    onClick = {
                        selectedDate = c.date
                        visibleMonth = YearMonth.from(c.date)
                    }
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        Spacer(Modifier.height(6.dp))

        // [Daftar Entri untuk Tanggal yang Dipilih]
        DiaryListForDate(
            date = selectedDate,
            entries = diaryByDate[selectedDate].orEmpty(), // [Ambil data dari Map]
            onEdit = onEdit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// [Composable untuk Header Bulan + Picker]
@Composable
private fun MonthHeaderWithDayMonthYearPicker(
    visibleMonth: YearMonth,
    selectedDate: LocalDate,
    onPick: (LocalDate) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val currentYear = LocalDate.now().year
    val monthName = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val headerText = if (visibleMonth.year == currentYear) monthName else "$monthName ${visibleMonth.year}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { showPicker = true }
        ) {
            Text(
                text = headerText,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                fontSize = 19.sp
            )
            Text(
                " ▼", // [Ikon palsu untuk picker]
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showPicker) {
        DayMonthYearPickerDialog(
            initial = selectedDate,
            onDismiss = { showPicker = false },
            onConfirm = { picked ->
                onPick(picked)
                showPicker = false
            }
        )
    }
}

// [Composable untuk Baris Hari (S, M, T, W, T, F, S)]
@Composable
private fun DayOfWeekHeader(gridSpacing: Dp = 2.dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
    ) {
        val days = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
        days.forEach { dow ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// [Logika untuk membangun 42 sel kalender]
private fun buildMonthCells(ym: YearMonth): List<DayCellData> {
    val first = ym.atDay(1)
    val sundayIndex = first.dayOfWeek.value % 7 // [Minggu = 0, Sabtu = 6]
    val start = first.minusDays(sundayIndex.toLong())
    return (0 until 42).map { i ->
        val d = start.plusDays(i.toLong())
        DayCellData(d, d.month == ym.month)
    }
}

// [Composable untuk 1 Sel Tanggal]
@Composable
private fun DayCell(
    date: LocalDate,
    inCurrentMonth: Boolean,
    selected: Boolean,
    hasDiary: Boolean,
    cellHeight: Dp,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val textColor = if (inCurrentMonth) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cellHeight)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            // [Latar belakang 'pill' ungu jika dipilih]
            Box(Modifier.fillMaxSize().padding(top = 1.dp).clip(RoundedCornerShape(10.dp)).background(primary))
        }

        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier.padding(top = 2.dp),
            color = if (selected) onPrimary else textColor,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )

        if (hasDiary) {
            // [Dot (titik) indikator]
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-6).dp)
                    .clip(CircleShape)
                    .background(if (selected) onPrimary else primary)
            )
        }
    }
}

// [Composable untuk daftar entri di bawah kalender]
@Composable
private fun DiaryListForDate(
    date: LocalDate,
    entries: List<DiaryEntry>,
    onEdit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        // [Kolom tanggal]
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
            Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.headlineSmall)
            Text(
                date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                date.year.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // [Kolom daftar entri]
        Column(Modifier.weight(1f)) {
            if (entries.isEmpty()) {
                val label = date.format(DateTimeFormatter.ofPattern("d MMM"))
                Text(
                    "No diary on $label",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries) { e -> DiaryCardItem(e, onEdit) }
                }
            }
        }
    }
}

// [Composable untuk 1 kartu entri di daftar]
@Composable
private fun DiaryCardItem(e: DiaryEntry, onEdit: (Int) -> Unit) {
    val timeStr = Instant.ofEpochMilli(e.timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(DateTimeFormatter.ofPattern("h:mm a"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onEdit(e.id) }
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 12.dp)
    ) {
        Text(timeStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(e.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 2.dp))
        if (e.content.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                e.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// [Composable untuk Dialog Picker (dari modul)]
@Composable
private fun DayMonthYearPickerDialog(
    initial: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    var pickedYear by remember { mutableStateOf(initial.year) }
    var pickedMonth by remember { mutableStateOf(initial.month) }
    var pickedDay by remember { mutableStateOf(initial.dayOfMonth) }

    fun clampDay() {
        val max = YearMonth.of(pickedYear, pickedMonth).lengthOfMonth()
        if (pickedDay > max) pickedDay = max
        if (pickedDay < 1) pickedDay = 1
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                clampDay()
                onConfirm(LocalDate.of(pickedYear, pickedMonth, pickedDay))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Select date") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // [Picker Hari]
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(min = 64.dp)) {
                    Text("▲", fontSize = 18.sp, modifier = Modifier.clickable { pickedDay += 1; clampDay() })
                    Text("$pickedDay", style = MaterialTheme.typography.titleMedium)
                    Text("▼", fontSize = 18.sp, modifier = Modifier.clickable { pickedDay -= 1; clampDay() })
                }
                // [Picker Bulan]
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(min = 72.dp)) {
                    Text("▲", fontSize = 18.sp, modifier = Modifier.clickable { pickedMonth = pickedMonth.plus(1); clampDay() })
                    Text(pickedMonth.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), style = MaterialTheme.typography.titleMedium)
                    Text("▼", fontSize = 18.sp, modifier = Modifier.clickable { pickedMonth = pickedMonth.minus(1); clampDay() })
                }
                // [Picker Tahun]
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(min = 84.dp)) {
                    Text("▲", fontSize = 18.sp, modifier = Modifier.clickable { pickedYear += 1; clampDay() })
                    Text(pickedYear.toString(), style = MaterialTheme.typography.titleMedium)
                    Text("▼", fontSize = 18.sp, modifier = Modifier.clickable { pickedYear -= 1; clampDay() })
                }
            }
        }
    )
}