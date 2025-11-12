package id.antasari.p6minda_230104040129.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.antasari.p6minda_230104040129.R

// [Helper 1: Header Gradient]
@Composable
private fun TopGradientHeader(modifier: Modifier = Modifier) {
    val headerHeight = 180.dp
    val colorTop = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
    val colorBottom = Color.Transparent
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(headerHeight)
            .background(
                brush = Brush.verticalGradient(listOf(colorTop, colorBottom))
            )
    )
}

// [Helper 2: Dots Indicator]
@Composable
private fun DotsIndicator(total: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { i ->
            val size = if (i == current - 1) 8.dp else 6.dp
            val color = if (i == current - 1) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}

// [Layar 1: Welcome]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLoginRestore: () -> Unit
) {
    Scaffold { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            TopGradientHeader()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // [MODIFIKASI DI SINI]
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center // Box ini sudah di tengah
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // [1. TAMBAHKAN SPACER INI untuk mendorong dari atas]
                        Spacer(Modifier.weight(1f))

                        Text(
                            "Welcome to",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // [2. GANTI SPACER 'weight(0.1f)' DENGAN JARAK TETAP]
                        Spacer(Modifier.height(16.dp))

                        Text(
                            buildAnnotatedString {
                                append("My ")
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append("Minda")
                                }
                                append("!")
                            },
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
                            textAlign = TextAlign.Center
                        )

                        // [3. SPACER INI SEKARANG AKAN MENYEIMBANGKAN SPACER PERTAMA]
                        Spacer(Modifier.weight(1f))
                    }
                }
                // [Bagian Tombol tetap sama]
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Get started", fontSize = 16.sp)
                }
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onLoginRestore, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Log in and restore", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// [Layar 2: Ask Name]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskNameScreen(
    onConfirm: (String) -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                title = {}
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            TopGradientHeader()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "What's your name?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your name") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.weight(1f))
                DotsIndicator(total = 4, current = 2)
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Skip", fontSize = 16.sp)
                    }
                    Button(
                        onClick = { onConfirm(name.trim()) },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// [Helper 3: Info Card untuk Layar Hello]
@Composable
private fun HelloInfoCard(icon: ImageVector, title: String, subtitle: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// [Layar 3: Hello <Name>]
@Composable
fun HelloScreen(
    userName: String,
    onNext: () -> Unit
) {
    val nameToShow = if (userName.isBlank()) "there" else userName
    Scaffold { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            TopGradientHeader()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome to your Minda, $nameToShow!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    HelloInfoCard(Icons.Outlined.Book, "Save your moments", "Anytime you feel something, write it.")
                    HelloInfoCard(Icons.Outlined.Lock, "Keep it private", "All data is stored offline on your device.")
                    HelloInfoCard(Icons.Outlined.Mood, "Reflect your mood", "Track your mood patterns over time.")
                }
                Spacer(Modifier.weight(1f))
                DotsIndicator(total = 4, current = 3)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Next", fontSize = 16.sp)
                }
            }
        }
    }
}

// [Layar 4: Start Journaling (CTA)]
@Composable
fun StartJournalingScreen(
    onStart: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.banner_diary), // [Gunakan banner yang sama]
                contentDescription = "Diary banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(48.dp))
            Text(
                "You're all set!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            DotsIndicator(total = 4, current = 4)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Got it!", fontSize = 16.sp)
            }
        }
    }
}