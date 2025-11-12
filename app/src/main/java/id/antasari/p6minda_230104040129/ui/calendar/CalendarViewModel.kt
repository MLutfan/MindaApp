package id.antasari.p6minda_230104040129.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.antasari.p6minda_230104040129.data.DiaryEntry
import id.antasari.p6minda_230104040129.data.MindaDatabase
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// [Helper untuk konversi Long ke LocalDate]
private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

class CalendarViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = MindaDatabase.getInstance(app).diaryDao()

    // [Logika Inti ViewModel]
    // [1. Ambil Flow real-time dari DAO]
    // [2. Ubah (map) list of entries -> Map<LocalDate, List<DiaryEntry>>]
    // [3. Ubah menjadi StateFlow agar UI bisa 'collect']
    val diaryByDate: StateFlow<Map<LocalDate, List<DiaryEntry>>> =
        dao.observeAll() // [Flow<List<DiaryEntry>>]
            .map { list ->
                // [Kelompokkan berdasarkan tanggal & urutkan entri per hari]
                list.groupBy { it.timestamp.toLocalDate() }
                    .mapValues { (_, entries) -> entries.sortedBy { it.timestamp } }
            }
            .stateIn( // [Ubah menjadi StateFlow]
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    // [Factory untuk membuat ViewModel dengan aman]
    companion object {
        fun provideFactory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                        return CalendarViewModel(app) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}