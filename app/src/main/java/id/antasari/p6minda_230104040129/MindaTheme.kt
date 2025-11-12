// Lokasi: id/antasari/p6minda_230104040129/MindaTheme.kt

package id.antasari.p6minda_230104040129

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// [DEFINISI PALET WARNA BIRU BARU KITA]
private val BlueLightColorScheme = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F8),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6B5778),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF3DAFF),
    onTertiaryContainer = Color(0xFF251432),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF73777F),
)

@Composable
fun MindaTheme(
    darkTheme: Boolean = false, // [Kita tidak implement dark mode, tapi ini standar]
    dynamicColor: Boolean = true, // [Izinkan Android 12+ menimpa warna]
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // [Gunakan warna dinamis (wallpaper) jika di Android 12+]
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // [Jika tidak, gunakan palet BIRU kustom kita]
        darkTheme -> BlueLightColorScheme // [Kita pakai light mode untuk dark (sementara)]
        else -> BlueLightColorScheme //
    }

    MaterialTheme(
        colorScheme = colorScheme, // [Terapkan skema warna baru]
        typography = MaterialTheme.typography, // [Tipografi tetap standar]
        content = content
    )
}