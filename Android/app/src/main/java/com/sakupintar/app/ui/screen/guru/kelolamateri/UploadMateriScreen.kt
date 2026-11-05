package com.sakupintar.app.ui.screen.guru.kelolamateri

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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
fun UploadMateriScreen(
    navController: NavHostController,
    viewModel: UploadMateriViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val context        = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.setPdfFile(it, context) } }

    LaunchedEffect(formState.pesanSnackbar) {
        formState.pesanSnackbar?.let { pesan ->
            snackbarHostState.showSnackbar(pesan, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    val state     = formState.state
    val formValid = formState.pdfUri != null
            && state is UploadMateriUiState.Idle

    SakuPintarTheme(isGuruMode = true) {
        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(paddingValues)
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

                Text("Upload Materi",
                    style = MaterialTheme.typography.titleLarge,
                    color = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Dokumen PDF akan diproses menggunakan Contextual Retrieval " +
                            "dan disimpan ke sistem secara otomatis.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                //Form
                if (state !is UploadMateriUiState.Berhasil) {

                    FormLabel("Dokumen Materi (PDF)")
                    Spacer(modifier = Modifier.height(6.dp))

                    if (formState.pdfUri == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
                                .background(ColorSurface, RoundedCornerShape(12.dp))
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📄", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                TextButton(onClick = { pdfLauncher.launch("application/pdf") }) {
                                    Text("Pilih File PDF",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ColorPrimary,
                                        fontWeight = FontWeight.SemiBold)
                                }
                                Text("Dokumen materi Aritmetika Sosial",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextMuted)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ColorSuccessContainer, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text("✓  ${formState.namaFile}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextPrimary,
                                    fontWeight = FontWeight.SemiBold)
                                if (state is UploadMateriUiState.Idle) {
                                    TextButton(
                                        onClick = { pdfLauncher.launch("application/pdf") },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Ganti file",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ColorTextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    when (state) {
                        is UploadMateriUiState.Memproses -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        ColorPrimary.copy(alpha = 0.08f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        modifier    = Modifier.size(32.dp),
                                        color       = ColorPrimary,
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text("Memproses Contextual Retrieval...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ColorTextPrimary,
                                        fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Mohon tunggu, ini mungkin memakan waktu 30–60 detik.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ColorTextSecondary,
                                        textAlign = TextAlign.Center)
                                }
                            }
                        }

                        is UploadMateriUiState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ColorAccentContainer, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Text("⚠  ${state.pesan}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorOnAccentContainer)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick  = { viewModel.uploadMateri() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape    = RoundedCornerShape(14.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                            ) {
                                Text("Coba Lagi",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ColorSurface,
                                    fontWeight = FontWeight.SemiBold)
                            }
                        }

                        else -> {
                            Button(
                                onClick  = { viewModel.uploadMateri() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape    = RoundedCornerShape(14.dp),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor         = ColorPrimary,
                                    disabledContainerColor = ColorDisabled
                                ),
                                enabled = formValid
                            ) {
                                Text("Proses & Simpan Materi",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ColorSurface,
                                    fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                if (state is UploadMateriUiState.Berhasil) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ColorSuccessContainer, RoundedCornerShape(14.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("✓  Berhasil!",
                                style = MaterialTheme.typography.titleMedium,
                                color = ColorTextPrimary,
                                fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(state.pesan,
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorTextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.reset() },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                            ) {
                                Text("Upload Materi Lain",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ColorSurface,
                                    fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}