package com.sakupintar.app.ui.screen.guru.kelolasiswa

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.ui.component.DropdownPilihan
import com.sakupintar.app.ui.component.FormLabel
import com.sakupintar.app.ui.theme.ColorAccentContainer
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorBorder
import com.sakupintar.app.ui.theme.ColorDisabled
import com.sakupintar.app.ui.theme.ColorOnAccentContainer
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorSuccessContainer
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme

@Composable
fun TambahSiswaScreen(
    navController: NavHostController,
    viewModel: TambahSiswaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val fotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.setFotoFile(it, context) } }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.setPdfFile(it, context) } }

    LaunchedEffect(uiState.berhasilDisimpan) {
        if (uiState.berhasilDisimpan) {
            snackbarHostState.showSnackbar(
                message  = uiState.pesanSnackbar ?: "Siswa berhasil ditambahkan",
                duration = SnackbarDuration.Short
            )
            navController.popBackStack()
        }
    }

    val formValid = uiState.nama.isNotBlank()
            && uiState.kelas.isNotBlank()
            && uiState.parameterSudahDimuat
            && !uiState.sedangUploadFoto

    SakuPintarTheme(isGuruMode = true) {
        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorBackground)
                    .verticalScroll(rememberScrollState())
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

                Text("Tambah Siswa Baru",
                    style = MaterialTheme.typography.titleLarge,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(28.dp))

                //SECTION 1: DATA DASAR
                Text("1. Data Dasar",
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(16.dp))

                FormLabel("Nama")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.nama,
                    onValueChange = { viewModel.updateNama(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Masukkan nama siswa",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextMuted)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = com.sakupintar.app.ui.component.formFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormLabel("Kelas")
                Spacer(modifier = Modifier.height(6.dp))
                DropdownPilihan(
                    nilai = uiState.kelas,
                    opsi  = listOf("Kelas 7", "Kelas 8", "Kelas 9"),
                    placeholder = "Pilih kelas",
                    onNilaiDipilih = { viewModel.updateKelas(it) }
                )

                Spacer(modifier = Modifier.height(28.dp))
                HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(28.dp))

                //SECTION 2: FOTO PROFIL
                Text("2. Foto Profil (Opsional)",
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Foto profil ditampilkan di layar pilih siswa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary)

                Spacer(modifier = Modifier.height(14.dp))

                when {
                    uiState.sedangUploadFoto -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = ColorPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Mengupload foto...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextSecondary)
                            }
                        }
                    }

                    uiState.fotoUri != null && uiState.fotoUrl != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ColorSuccessContainer, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text("✓  Foto berhasil diupload",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextPrimary,
                                    fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(uiState.namaFileFoto ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(
                                    onClick = { fotoLauncher.launch("image/*") },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Ganti foto",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ColorTextSecondary)
                                }
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
                                .background(ColorSurface, RoundedCornerShape(12.dp))
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🖼️", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { fotoLauncher.launch("image/*") }) {
                                    Text("Pilih Foto",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ColorPrimary,
                                        fontWeight = FontWeight.SemiBold)
                                }
                                Text("JPG, PNG, atau WEBP (opsional)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
                HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(28.dp))

                //SECTION 3: UPLOAD ASESMEN
                Text("3. Dokumen Asesmen Psikologis",
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Upload file PDF laporan hasil asesmen psikologis dari psikolog sekolah. " +
                            "Sistem akan otomatis mengekstrak parameter instruksional siswa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                when {
                    uiState.pdfUri == null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
                                .background(ColorSurface, RoundedCornerShape(12.dp))
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📄", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { pdfLauncher.launch("application/pdf") }) {
                                    Text("Pilih File PDF",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ColorPrimary,
                                        fontWeight = FontWeight.SemiBold)
                                }
                                Text("Laporan hasil asesmen psikologis",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextMuted)
                            }
                        }
                    }

                    uiState.sedangMemproses -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = ColorPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("LLM sedang membaca dokumen...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextSecondary)
                            }
                        }
                    }

                    uiState.parameterSudahDimuat -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ColorSuccessContainer, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text("✓  Dokumen berhasil diproses",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextPrimary,
                                    fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(uiState.namaFilePdf ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(
                                    onClick = { pdfLauncher.launch("application/pdf") },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Ganti dokumen",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ColorTextSecondary)
                                }
                            }
                        }
                    }
                }

                //SECTION 4: PARAMETER
                AnimatedVisibility(
                    visible = uiState.parameterSudahDimuat,
                    enter   = fadeIn() + expandVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(28.dp))
                        HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(28.dp))

                        Text("4. Parameter Instruksional",
                            style = MaterialTheme.typography.titleMedium,
                            color = ColorTextPrimary,
                            fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ColorAccentContainer, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                "⚠  Periksa hasil ekstraksi LLM sebelum menyimpan. Edit jika ada parameter yang tidak sesuai.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorOnAccentContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val spasi = 14.dp

                        FormLabel("KI-01 · Tingkat Atensi")
                        Spacer(modifier = Modifier.height(6.dp))
                        DropdownPilihan(
                            nilai = uiState.parameter.tingkatAtensi,
                            opsi  = listOf("Sangat Pendek", "Pendek", "Sedang"),
                            onNilaiDipilih = { viewModel.updateParameter { copy(tingkatAtensi = it) } }
                        )
                        Spacer(modifier = Modifier.height(spasi))

                        FormLabel("KI-02 · Kemampuan Kebahasaan")
                        Spacer(modifier = Modifier.height(6.dp))
                        DropdownPilihan(
                            nilai = uiState.parameter.kemampuanKebahasaan,
                            opsi  = listOf("Kata Sangat Dasar", "Kalimat Sederhana", "Kalimat Kompleks"),
                            onNilaiDipilih = { viewModel.updateParameter { copy(kemampuanKebahasaan = it) } }
                        )
                        Spacer(modifier = Modifier.height(spasi))

                        FormLabel("KI-03 · Kapasitas Memori")
                        Spacer(modifier = Modifier.height(6.dp))
                        DropdownPilihan(
                            nilai = uiState.parameter.kapasitasMemori,
                            opsi  = listOf("Pengulangan Intensif", "Pengulangan Sedang", "Tanpa Pengulangan Khusus"),
                            onNilaiDipilih = { viewModel.updateParameter { copy(kapasitasMemori = it) } }
                        )
                        Spacer(modifier = Modifier.height(spasi))

                        FormLabel("KI-04 · Preferensi Persepsi")
                        Spacer(modifier = Modifier.height(6.dp))
                        DropdownPilihan(
                            nilai = uiState.parameter.preferensiPersepsi,
                            opsi  = listOf("Visual Dominan", "Seimbang", "Teks Dominan"),
                            onNilaiDipilih = { viewModel.updateParameter { copy(preferensiPersepsi = it) } }
                        )
                        Spacer(modifier = Modifier.height(spasi))

                        FormLabel("KI-05 · Metakognitif")
                        Spacer(modifier = Modifier.height(6.dp))
                        DropdownPilihan(
                            nilai = uiState.parameter.metakognitif,
                            opsi  = listOf("Konfirmasi Setiap Langkah", "Konfirmasi Akhir Segmen", "Tanpa Konfirmasi Khusus"),
                            onNilaiDipilih = { viewModel.updateParameter { copy(metakognitif = it) } }
                        )
                        Spacer(modifier = Modifier.height(spasi))

                        FormLabel("KI-06 · Jenjang Kelas")
                        Spacer(modifier = Modifier.height(6.dp))
                        DropdownPilihan(
                            nilai = uiState.parameter.jenjangKelas,
                            opsi  = listOf("Kelas 7", "Kelas 8", "Kelas 9"),
                            onNilaiDipilih = { viewModel.updateParameter { copy(jenjangKelas = it) } }
                        )
                        Spacer(modifier = Modifier.height(spasi))

                        FormLabel("KI-07 · Tingkatan Tunagrahita")
                        Spacer(modifier = Modifier.height(6.dp))
                        DropdownPilihan(
                            nilai = uiState.parameter.tingkatanTunagrahita,
                            opsi  = listOf("Ringan", "Sedang", "Berat"),
                            onNilaiDipilih = { viewModel.updateParameter { copy(tingkatanTunagrahita = it) } }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick  = { viewModel.simpanSiswa() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = ColorPrimary,
                        disabledContainerColor = ColorDisabled
                    ),
                    enabled = formValid && !uiState.sedangMenyimpan
                ) {
                    if (uiState.sedangMenyimpan) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(22.dp),
                            color       = ColorSurface,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text("Simpan Profil Siswa",
                            style      = MaterialTheme.typography.titleMedium,
                            color      = ColorSurface,
                            fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}