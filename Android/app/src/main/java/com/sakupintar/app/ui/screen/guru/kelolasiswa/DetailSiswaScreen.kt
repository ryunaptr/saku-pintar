package com.sakupintar.app.ui.screen.guru.kelolasiswa

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sakupintar.app.ui.component.DropdownPilihan
import com.sakupintar.app.ui.component.FormLabel
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorBorder
import com.sakupintar.app.ui.theme.ColorDisabled
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme

private val PrimaryBlue  = Color(0xFF2F80ED)
private val DarkBlue     = Color(0xFF173B73)
private val LightBlue    = Color(0xFFEEF6FF)
private val SuccessGreen = Color(0xFF27AE60)

@Composable
fun DetailSiswaScreen(
    navController: NavHostController,
    siswaId: Int,
    viewModel: DetailSiswaViewModel = viewModel()
) {
    val uiState           by viewModel.uiState.collectAsStateWithLifecycle()
    val context           = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val fotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.setFotoFile(it, context) } }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.setPdfUntukReprofiling(it, context) } }

    LaunchedEffect(siswaId) { viewModel.loadSiswa(siswaId) }

    LaunchedEffect(uiState.pesanSnackbar) {
        uiState.pesanSnackbar?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    SakuPintarTheme(isGuruMode = true) {
        Scaffold(
            snackbarHost      = { SnackbarHost(snackbarHostState) },
            containerColor    = ColorBackground
        ) { paddingValues ->

            if (uiState.sedangMemuat) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorBackground)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = PrimaryBlue) }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                TextButton(
                    onClick        = { navController.popBackStack() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "← Kembali",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Detail Siswa",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = DarkBlue,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                //Card profile
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .background(PrimaryBlue, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        //Foto/Inisial
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .shadow(3.dp, CircleShape)
                                .background(Color.White.copy(alpha = 0.3f), CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.fotoUrl != null) {
                                AsyncImage(
                                    model              = uiState.fotoUrl,
                                    contentDescription = "Foto ${uiState.nama}",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text       = uiState.nama.firstOrNull()
                                        ?.uppercaseChar()?.toString() ?: "?",
                                    fontSize   = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color      = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text       = uiState.nama,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                //Badge kelas
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text      = uiState.kelas,
                                        fontSize  = 12.sp,
                                        color     = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                //Badge tingkatan
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (uiState.parameter.tingkatanTunagrahita == "Ringan")
                                                Color.White.copy(alpha = 0.2f)
                                            else Color(0xFFFF9F43).copy(alpha = 0.85f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text      = uiState.parameter.tingkatanTunagrahita,
                                        fontSize  = 12.sp,
                                        color     = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Data Dasar
                SectionHeader("Data Dasar")
                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Nama")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value         = uiState.nama,
                    onValueChange = { viewModel.updateNama(it) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = com.sakupintar.app.ui.component.formFieldColors(),
                    singleLine    = true,
                    textStyle     = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Kelas")
                Spacer(modifier = Modifier.height(6.dp))
                DropdownPilihan(
                    nilai          = uiState.kelas,
                    opsi           = listOf("Kelas 7", "Kelas 8", "Kelas 9"),
                    onNilaiDipilih = { viewModel.updateKelas(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Foto Profil (Opsional)")
                Spacer(modifier = Modifier.height(6.dp))

                when {
                    uiState.sedangUploadFoto -> StatusBox(
                        sedangProses = true,
                        pesan        = "Mengupload foto..."
                    )
                    uiState.namaFileFoto != null || uiState.fotoUrl != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    "✓  Foto tersedia",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = SuccessGreen
                                )
                                if (uiState.namaFileFoto != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        uiState.namaFileFoto!!,
                                        fontSize = 12.sp,
                                        color    = ColorTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Ganti foto",
                                    fontSize  = 13.sp,
                                    color     = PrimaryBlue,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier  = Modifier.clickable {
                                        fotoLauncher.launch("image/*")
                                    }
                                )
                            }
                        }
                    }
                    else -> OutlinedButton(
                        onClick  = { fotoLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                    ) {
                        Text("+ Pilih Foto", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(24.dp))

                //Re-profiling
                SectionHeader("Perbarui Dokumen Asesmen")
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Upload ulang laporan psikologis untuk memperbarui parameter secara otomatis.",
                    fontSize = 13.sp,
                    color    = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                when {
                    uiState.sedangProfiling -> StatusBox(
                        sedangProses = true,
                        pesan        = "LLM sedang membaca dokumen..."
                    )
                    uiState.namaFilePdf != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    "✓  Dokumen berhasil diproses",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = SuccessGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    uiState.namaFilePdf!!,
                                    fontSize = 12.sp,
                                    color    = ColorTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Parameter sudah diperbarui. Tekan Simpan untuk menyimpan.",
                                    fontSize = 12.sp,
                                    color    = ColorTextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Ganti dokumen",
                                    fontSize  = 13.sp,
                                    color     = PrimaryBlue,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier  = Modifier.clickable {
                                        pdfLauncher.launch("application/pdf")
                                    }
                                )
                            }
                        }
                    }
                    else -> Button(
                        onClick  = { pdfLauncher.launch("application/pdf") },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(
                            "📄  Upload Ulang Dokumen Asesmen",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(24.dp))

                //Parameter Instruksional
                SectionHeader("Parameter Instruksional")

                //Info box
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBlue, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        "⚠  Periksa hasil ekstraksi sebelum menyimpan.",
                        fontSize = 13.sp,
                        color    = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val spasi = 14.dp
                val paramItems = listOf(
                    Triple("KI-01 · Tingkat Atensi",
                        uiState.parameter.tingkatAtensi,
                        listOf("Sangat Pendek", "Pendek", "Sedang")),
                    Triple("KI-02 · Kemampuan Kebahasaan",
                        uiState.parameter.kemampuanKebahasaan,
                        listOf("Kata Sangat Dasar", "Kalimat Sederhana", "Kalimat Kompleks")),
                    Triple("KI-03 · Kapasitas Memori",
                        uiState.parameter.kapasitasMemori,
                        listOf("Pengulangan Intensif", "Pengulangan Sedang", "Tanpa Pengulangan Khusus")),
                    Triple("KI-04 · Preferensi Persepsi",
                        uiState.parameter.preferensiPersepsi,
                        listOf("Visual Dominan", "Seimbang", "Teks Dominan")),
                    Triple("KI-05 · Metakognitif",
                        uiState.parameter.metakognitif,
                        listOf("Konfirmasi Setiap Langkah", "Konfirmasi Akhir Segmen", "Tanpa Konfirmasi Khusus")),
                    Triple("KI-06 · Jenjang Kelas",
                        uiState.parameter.jenjangKelas,
                        listOf("Kelas 7", "Kelas 8", "Kelas 9")),
                    Triple("KI-07 · Tingkatan Tunagrahita",
                        uiState.parameter.tingkatanTunagrahita,
                        listOf("Ringan", "Sedang", "Berat"))
                )

                paramItems.forEachIndexed { idx, (label, nilai, opsi) ->
                    FormLabel(label)
                    Spacer(modifier = Modifier.height(6.dp))
                    DropdownPilihan(
                        nilai          = nilai,
                        opsi           = opsi,
                        onNilaiDipilih = { pilihan ->
                            when (idx) {
                                0 -> viewModel.updateParameter { copy(tingkatAtensi = pilihan) }
                                1 -> viewModel.updateParameter { copy(kemampuanKebahasaan = pilihan) }
                                2 -> viewModel.updateParameter { copy(kapasitasMemori = pilihan) }
                                3 -> viewModel.updateParameter { copy(preferensiPersepsi = pilihan) }
                                4 -> viewModel.updateParameter { copy(metakognitif = pilihan) }
                                5 -> viewModel.updateParameter { copy(jenjangKelas = pilihan) }
                                6 -> viewModel.updateParameter { copy(tingkatanTunagrahita = pilihan) }
                            }
                        }
                    )
                    if (idx < paramItems.size - 1) Spacer(modifier = Modifier.height(spasi))
                }

                Spacer(modifier = Modifier.height(28.dp))

                //Tombol simpan
                Button(
                    onClick  = { viewModel.simpanPerubahan() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = PrimaryBlue,
                        disabledContainerColor = ColorDisabled
                    ),
                    enabled  = !uiState.sedangMenyimpan && !uiState.sedangUploadFoto
                ) {
                    if (uiState.sedangMenyimpan) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(22.dp),
                            color       = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            "Simpan Perubahan",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(judul: String) {
    Text(
        text       = judul,
        fontSize   = 17.sp,
        fontWeight = FontWeight.ExtraBold,
        color      = DarkBlue
    )
}

@Composable
private fun StatusBox(sedangProses: Boolean, pesan: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEF6FF), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (sedangProses) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(16.dp),
                    color       = PrimaryBlue,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(pesan, fontSize = 13.sp, color = ColorTextSecondary)
        }
    }
}

@Composable
private fun BadgePill(
    teks: String,
    warnaBg: Color,
    warnaTeks: Color
) {
    Box(
        modifier = Modifier
            .background(warnaBg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = teks, style = MaterialTheme.typography.labelSmall, color = warnaTeks)
    }
}