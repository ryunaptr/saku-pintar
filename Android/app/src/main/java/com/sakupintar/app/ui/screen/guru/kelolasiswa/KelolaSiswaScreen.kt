package com.sakupintar.app.ui.screen.guru.kelolasiswa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.ColorAccentContainer
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorOnAccentContainer
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorPrimaryContainer
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme

@Composable
fun KelolaSiswaScreen(
    navController: NavHostController,
    viewModel: KelolaSiswaViewModel = viewModel()
) {
    val daftarSiswa  by viewModel.daftarSiswa.collectAsStateWithLifecycle()
    val sedangMemuat by viewModel.sedangMemuat.collectAsStateWithLifecycle()
    val siswaYangAkanDihapus by viewModel.siswaYangAkanDihapus.collectAsStateWithLifecycle()
    val pesanError           by viewModel.pesanError.collectAsStateWithLifecycle()
    val filterKelas by viewModel.filterKelas.collectAsStateWithLifecycle()

    SakuPintarTheme(isGuruMode = true) {
        siswaYangAkanDihapus?.let { siswa ->
            AlertDialog(
                onDismissRequest = { viewModel.batalHapus() },
                title = { Text("Hapus Siswa?", fontWeight = FontWeight.SemiBold) },
                text  = { Text("Data ${siswa.nama} dan seluruh progresnya akan dihapus permanen.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.hapusSiswa(siswa.id) }) {
                        Text("Hapus", color = androidx.compose.ui.graphics.Color(0xFFE53935))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.batalHapus() }) {
                        Text("Batal", color = ColorTextSecondary)
                    }
                }
            )
        }
        Scaffold(
            containerColor = ColorBackground,
            floatingActionButton = {
                FloatingActionButton(
                    onClick        = { navController.navigate(Routes.GURU_TAMBAH_SISWA) },
                    containerColor = ColorPrimary,
                    shape          = RoundedCornerShape(16.dp)
                ) {
                    Text("+", fontSize = 28.sp, color = ColorSurface, fontWeight = FontWeight.Light)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                TextButton(
                    onClick = { navController.popBackStack() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("← Kembali",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorTextSecondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Kelola Siswa",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(4.dp))

                Text("${daftarSiswa.size} siswa terdaftar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextMuted)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Semua", "Kelas 7", "Kelas 8", "Kelas 9").forEach { kelas ->
                        val dipilih = filterKelas == kelas
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (dipilih) ColorPrimary else ColorSurface,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setFilterKelas(kelas) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text  = kelas,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (dipilih) ColorSurface else ColorTextSecondary,
                                fontWeight = if (dipilih) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                when {
                    sedangMemuat -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = ColorPrimary) }
                    }

                    daftarSiswa.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("👤", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Belum ada siswa terdaftar.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ColorTextMuted)
                                Text("Tap + untuk menambahkan siswa baru.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextMuted)
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 88.dp)
                        ) {
                            itemsIndexed(daftarSiswa, key = { _, siswa -> siswa.id }) { index, siswa ->
                                SiswaCard(
                                    nomor   = index + 1,
                                    siswa   = siswa,
                                    onClick = { navController.navigate(Routes.guruDetailSiswa(siswa.id)) },
                                    onHapus = { viewModel.konfirmasiHapus(siswa) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SiswaCard(
    nomor: Int,
    siswa: SiswaItem,
    onClick: () -> Unit,
    onHapus: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurface, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Nomor
        Box(
            modifier = Modifier
                .size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "$nomor",
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = ColorTextMuted
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        //Foto
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(ColorPrimaryContainer, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (siswa.fotoUrl != null) {
                AsyncImage(
                    model              = siswa.fotoUrl,
                    contentDescription = "Foto ${siswa.nama}",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text       = siswa.nama.first().uppercaseChar().toString(),
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = ColorPrimary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(siswa.nama,
                style      = MaterialTheme.typography.bodyLarge,
                color      = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgeKecil(siswa.kelas)
                BadgeKecil(
                    teks      = siswa.tingkat,
                    warnaBg   = if (siswa.tingkat == "Ringan") ColorPrimaryContainer else ColorAccentContainer,
                    warnaTeks = if (siswa.tingkat == "Ringan") ColorPrimary else ColorOnAccentContainer
                )
            }
        }

        //Tombol detail
        Box(
            modifier = Modifier
                .size(40.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("→", style = MaterialTheme.typography.bodyMedium, color = ColorTextMuted)
        }

        Spacer(modifier = Modifier.width(4.dp))

        //Tombol hapus
        Box(
            modifier = Modifier
                .size(40.dp)
                .clickable { onHapus() },
            contentAlignment = Alignment.Center
        ) {
            Text("🗑", fontSize = 18.sp)
        }
    }
}

@Composable
private fun BadgeKecil(
    teks: String,
    warnaBg: androidx.compose.ui.graphics.Color = ColorPrimaryContainer,
    warnaTeks: androidx.compose.ui.graphics.Color = ColorPrimary
) {
    Box(
        modifier = Modifier
            .background(warnaBg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(teks, style = MaterialTheme.typography.labelSmall, color = warnaTeks)
    }
}