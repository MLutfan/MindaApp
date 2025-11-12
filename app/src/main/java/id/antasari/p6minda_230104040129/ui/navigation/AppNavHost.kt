package id.antasari.p6minda_230104040129.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import id.antasari.p6minda_230104040129.ui.*
import id.antasari.p6minda_230104040129.ui.calendar.CalendarScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    // [Data baru: flag apakah onboarding sudah selesai?]
    hasCompletedOnboarding: Boolean, //
    storedName: String?,
    onSaveUserName: (String) -> Unit,
    // [Callback baru: untuk menandai onboarding selesai]
    onSetOnboardingCompleted: () -> Unit, //
    modifier: Modifier
) {
    // [UPDATE: Start destination sekarang HANYA bergantung pada flag 'completed']
    val start = if (hasCompletedOnboarding) Routes.HOME else Routes.ONBOARD_WELCOME //

    NavHost(
        navController = navController,
        startDestination = start,
        modifier = modifier
    ) {
        // [Rute 1: Welcome]
        composable(Routes.ONBOARD_WELCOME) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Routes.ONBOARD_ASKNAME) },
                onLoginRestore = { /* Abaikan untuk sekarang */ }
            )
        }
        // [Rute 2: Ask Name]
        composable(Routes.ONBOARD_ASKNAME) {
            AskNameScreen(
                onConfirm = { typedName ->
                    onSaveUserName(typedName) // [Simpan nama]
                    navController.navigate(Routes.ONBOARD_HELLO) // [Lanjut]
                },
                onSkip = {
                    onSaveUserName("") // [Simpan nama kosong]
                    navController.navigate(Routes.ONBOARD_HELLO) // [Lanjut]
                },
                onBack = { navController.popBackStack() }
            )
        }
        // [Rute 3: Hello]
        composable(Routes.ONBOARD_HELLO) {
            HelloScreen(
                userName = storedName ?: "",
                onNext = { navController.navigate(Routes.ONBOARD_CTA) }
            )
        }
        // [Rute 4: CTA (Final)]
        composable(Routes.ONBOARD_CTA) {
            StartJournalingScreen(
                onStart = {
                    // [AKSI PENTING: Tandai onboarding selesai!]
                    onSetOnboardingCompleted() //

                    // [Navigasi ke Home & hapus semua stack onboarding]
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARD_WELCOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ... [Rute HOME, CALENDAR, INSIGHTS, SETTINGS, NEW, DETAIL, EDIT] ...
        // ... [TIDAK ADA PERUBAHAN DI SINI] ...
        composable(Routes.HOME) {
            HomeScreen(
                userName = storedName,
                onOpenEntry = { id -> navController.navigate("detail/$id") },
                onNewEntry = { navController.navigate(Routes.NEW) }
            )
        }
        composable(Routes.CALENDAR) {
            CalendarScreen(
                onEdit = { id -> navController.navigate("edit/$id") }
            )
        }
        composable(Routes.INSIGHTS) { InsightsScreen() }
        composable(Routes.SETTINGS) { SettingsScreen(userName = storedName) }
        composable(Routes.NEW) {
            NewEntryScreen(
                onBack = { navController.popBackStack() },
                onSaved = { newId ->
                    navController.popBackStack()
                    navController.navigate("detail/$newId") { launchSingleTop = true }
                }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("entryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("entryId") ?: -1
            NoteDetailScreen(
                entryId = id,
                onBack = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() },
                onEdit = { eid -> navController.navigate("edit/$eid") }
            )
        }
        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("entryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("entryId") ?: -1
            EditEntryScreen(
                entryId = id,
                onBack = { navController.popBackStack() },
                onSaved = { savedId ->
                    navController.popBackStack()
                    navController.navigate("detail/$savedId") { launchSingleTop = true }
                }
            )
        }
    }
}