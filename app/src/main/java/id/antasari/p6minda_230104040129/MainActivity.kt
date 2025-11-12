package id.antasari.p6minda_230104040129

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // [IMPORT BARU]
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.antasari.p6minda_230104040129.data.UserPrefsRepository
import id.antasari.p6minda_230104040129.ui.BottomNavBar
import id.antasari.p6minda_230104040129.ui.navigation.AppNavHost
import id.antasari.p6minda_230104040129.ui.navigation.Routes
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindaTheme {
                val userPrefs = remember { UserPrefsRepository(this@MainActivity) }
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()

                // [BACA KEDUA FLOW DARI DATASTORE SECARA REAL-TIME]
                val storedName by userPrefs.userNameFlow.collectAsStateWithLifecycle(initialValue = null)
                val hasCompletedOnboarding by userPrefs.onboardingCompletedFlow.collectAsStateWithLifecycle(initialValue = false)

                // [State 'ready' baru: siap hanya jika 'storedName' sudah dibaca (bukan null)]
                // [Ini mencegah error 'null' saat pertama kali app dibuka]
                val ready = storedName != null

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (shouldShowBottomBar(currentRoute)) {
                            BottomNavBar(navController = navController)
                        }
                    },
                    floatingActionButton = {
                        if (shouldShowBottomBar(currentRoute)) {
                            FloatingActionButton(
                                onClick = { navController.navigate(Routes.NEW) },
                                modifier = Modifier.offset(y = 40.dp),
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.primary, //
                                contentColor = MaterialTheme.colorScheme.onPrimary //
                            ){
                                Icon(Icons.Default.Add, "New entry")
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { innerPadding ->

                    if (!ready) {
                        // [Tampilkan layar loading selagi DataStore dibaca]
                        Surface(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {}
                    } else {
                        // [Panggil AppNavHost dengan logika baru]
                        AppNavHost(
                            navController = navController,
                            hasCompletedOnboarding = hasCompletedOnboarding, // [Kirim flag]
                            storedName = storedName,
                            onSaveUserName = { name ->
                                scope.launch { userPrefs.saveUserName(name) }
                            },
                            // [Kirim callback untuk menandai onboarding selesai]
                            onSetOnboardingCompleted = { //
                                scope.launch { userPrefs.setOnboardingCompleted(true) }
                            },
                            modifier = Modifier.fillMaxSize().padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun shouldShowBottomBar(route: String?): Boolean =
        route in setOf(Routes.HOME, Routes.CALENDAR, Routes.INSIGHTS, Routes.SETTINGS)
}