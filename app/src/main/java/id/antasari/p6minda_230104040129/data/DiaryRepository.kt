package id.antasari.p6minda_230104040129.data

import kotlinx.coroutines.flow.Flow
class DiaryRepository(
    private val dao: DiaryDao
) {
    fun entriesFlow(): Flow<List<DiaryEntry>> = dao.observeAll()

    // READ semua entry (untuk HomeScreen)
    suspend fun allEntries(): List<DiaryEntry> { //
        return dao.getAll() //
    }

    // READ satu entry by id (untuk NoteDetailScreen & EditEntryScreen)
    suspend fun getById(id: Int): DiaryEntry? { //
        return dao.getById(id) //
    }

    // [UBAH FUNGSI INI]
    // [Sebelumnya: addEntry(title: String, ...)]
    // CREATE entry baru (dipakai seed otomatis di HomeScreen)
    suspend fun add(entry: DiaryEntry): Long { // [Nama fungsi baru: 'add']
        return dao.insert(entry) // [Memanggil DAO dan mengembalikan Long]
    }

    // UPDATE entry (dipakai tombol Save di EditEntryScreen)
    suspend fun edit(entry: DiaryEntry) { //
        dao.update(entry) //
    }

    // DELETE entry (dipakai Delete di NoteDetailScreen)
    suspend fun remove(entry: DiaryEntry) { //
        dao.delete(entry) //
    }
}