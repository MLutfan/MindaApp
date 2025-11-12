package id.antasari.p6minda_230104040129.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// [1. Definisikan file DataStore kita, bernama "user_prefs"]
private val Context.userPrefsDataStore by preferencesDataStore(
    name = "user_prefs" //
)

class UserPrefsRepository(private val context: Context) {

    // [2. Buat "kunci" (key) untuk mengakses data nama]
    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")//
    }

    // [3. BuFungsi untuk MEMBACA nama user]
    // [Ini adalah Flow, artinya UI bisa "mendengarkan" perubahan nama]
    val userNameFlow: Flow<String?> =
        context.userPrefsDataStore.data.map { prefs ->
            prefs[Keys.USER_NAME] // [Ambil nama, bisa jadi null jika belum ada]
        }

    val onboardingCompletedFlow: Flow<Boolean> = //
        context.userPrefsDataStore.data.map { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] ?: false
        }

    // [4. Fungsi untuk MENYIMPAN nama user]
    // [Ini adalah 'suspend function', dipanggil saat user submit nama]
    suspend fun saveUserName(name: String) { //
        context.userPrefsDataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name //
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) { //
        context.userPrefsDataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }
}