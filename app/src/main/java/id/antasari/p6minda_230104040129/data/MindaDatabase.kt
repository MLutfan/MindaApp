package id.antasari.p6minda_230104040129.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DiaryEntry::class],
    version = 1,
    exportSchema = false
)
abstract class MindaDatabase : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao

    companion object {
        @Volatile
        private var INSTANCE: MindaDatabase? = null

        fun getInstance(context: Context): MindaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MindaDatabase::class.java,
                    "minda.db" // [Nama file database lokal]
                )
                    .build()
                    .also { db -> INSTANCE = db }
            }
        }
    }
}