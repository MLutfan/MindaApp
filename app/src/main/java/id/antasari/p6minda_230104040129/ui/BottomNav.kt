package id.antasari.p6minda_230104040129.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import id.antasari.p6minda_230104040129.ui.navigation.Routes

// [Data class BottomNavItem masih sama]
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home),
    BottomNavItem(Routes.CALENDAR, "Calendar", Icons.Filled.CalendarMonth),
    BottomNavItem(Routes.INSIGHTS, "Insights", Icons.Filled.BarChart),
    BottomNavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings),
)

// [FUNGSI UTAMA YANG BARU - Menggunakan Row]
@Composable
fun BottomNavBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp) // [Tinggi bottom bar]
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (homeItem, calendarItem, insightsItem, settingsItem) = bottomItems

        // [Item 1: Home]
        BottomNavButton(
            item = homeItem,
            selected = currentRoute == homeItem.route, // [Gunakan Routes.HOME]
            onClick = {
                if (currentRoute != homeItem.route) {
                    navController.navigate(homeItem.route) { // [Gunakan Routes.HOME]
                        popUpTo(Routes.HOME) { inclusive = false } // [Gunakan Routes.HOME]
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.weight(1f).padding(start = 10.dp)
        )

        // [Item 2: Calendar]
        BottomNavButton(
            item = calendarItem,
            selected = currentRoute == calendarItem.route, // [Gunakan Routes.CALENDAR]
            onClick = {
                if (currentRoute != calendarItem.route) {
                    navController.navigate(calendarItem.route) { // [G... ]
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.weight(1f).padding(start = 10.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // [Item 3: SPACER UNTUK FAB]
        Spacer(modifier = Modifier.weight(1f))

        // [Item 4: Insights]
        BottomNavButton(
            item = insightsItem,
            selected = currentRoute == insightsItem.route, // [G... ]
            onClick = {
                if (currentRoute != insightsItem.route) {
                    navController.navigate(insightsItem.route) { // [G... ]
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.weight(1f).padding(end = 10.dp)
        )

        // [Item 5: Settings]
        BottomNavButton(
            item = settingsItem,
            selected = currentRoute == settingsItem.route, // [G... ]
            onClick = {
                if (currentRoute != settingsItem.route) {
                    navController.navigate(settingsItem.route) { // [G... ]
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.weight(1f).padding(end = 10.dp)
        )
    }
}

// [COMPOSABLE BARU UNTUK TOMBOL TAB KUSTOM]
@Composable
private fun BottomNavButton(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = colors.onSurfaceVariant
    val iconColor = if (selected) activeColor else inactiveColor
    val textColor = if (selected) activeColor else inactiveColor

    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor
            )
            Spacer(modifier = Modifier.height(0.dp)) // [Atur jarak ikon-teks di sini]
            Text(
                text = item.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        }
    }
}