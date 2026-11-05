package com.sakupintar.app.ui.screen.guru.beranda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.ColorAccentContainer
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorBorder
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorPrimaryContainer
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.sakupintar.app.util.AppAudioManager

@Composable
fun BerandaGuruScreen(
    navController: NavHostController,
    viewModel: BerandaGuruViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var sudahTampilWelcome by remember { mutableStateOf(false) }

    val tanggalHariIni = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
            .format(Calendar.getInstance().time)
    }

    LaunchedEffect(uiState.namaGuru) {
        if (uiState.namaGuru.isNotBlank() && !sudahTampilWelcome) {
            sudahTampilWelcome = true
            snackbarHostState.showSnackbar(
                message  = "Selamat datang, ${uiState.namaGuru}! 👋",
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(Unit) {
        AppAudioManager.stopMusic()
    }

    SakuPintarTheme(isGuruMode = true) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                //Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("Selamat datang,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextSecondary)
                        Text(uiState.namaGuru,
                            style = MaterialTheme.typography.titleLarge,
                            color = ColorTextPrimary,
                            fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(tanggalHariIni,
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextMuted)
                    }

                    TextButton(
                        onClick = { navController.popBackStack(Routes.HOME, inclusive = false) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Keluar",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                //Ringkasan statistik
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier  = Modifier.weight(1f),
                        emoji     = "👥",
                        angka     = uiState.jumlahSiswa.toString(),
                        label     = "Siswa terdaftar",
                        warnaBg   = ColorPrimaryContainer
                    )
                    StatCard(
                        modifier  = Modifier.weight(1f),
                        emoji     = "📚",
                        angka     = uiState.kondisiTersedia.toString(),
                        label     = "Materi tersedia",
                        warnaBg   = ColorAccentContainer
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)

                Spacer(modifier = Modifier.height(20.dp))

                Text("Menu",
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(16.dp))

                MenuCard("👥", "Kelola Siswa",
                    "Tambah profil & atur parameter instruksional siswa") {
                    navController.navigate(Routes.GURU_KELOLA_SISWA)
                }

                Spacer(modifier = Modifier.height(12.dp))

                MenuCard("📚", "Kelola Materi",
                    "Upload dokumen materi Aritmetika Sosial per kompetensi") {
                    navController.navigate(Routes.GURU_KELOLA_MATERI)
                }

                Spacer(modifier = Modifier.height(12.dp))

                MenuCard("📊", "Pantau Progres",
                    "Lihat perkembangan belajar & capaian gamifikasi siswa") {
                    navController.navigate(Routes.GURU_PROGRES)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    emoji: String,
    angka: String,
    label: String,
    warnaBg: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = warnaBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(angka,
                style = MaterialTheme.typography.titleLarge,
                color = ColorTextPrimary,
                fontWeight = FontWeight.Bold)
            Text(label,
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondary)
        }
    }
}

@Composable
private fun MenuCard(
    emoji: String,
    judul: String,
    deskripsi: String,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border    = androidx.compose.foundation.BorderStroke(0.5.dp, ColorBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(judul,
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(deskripsi,
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("→",
                style = MaterialTheme.typography.titleMedium,
                color = ColorPrimary,
                fontWeight = FontWeight.Bold)
        }
    }
}