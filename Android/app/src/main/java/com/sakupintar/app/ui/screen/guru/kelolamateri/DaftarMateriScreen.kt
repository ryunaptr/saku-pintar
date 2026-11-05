package com.sakupintar.app.ui.screen.guru.kelolamateri

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorBorder
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorPrimaryContainer
import com.sakupintar.app.ui.theme.ColorSuccess
import com.sakupintar.app.ui.theme.ColorSuccessContainer
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme

@Composable
fun DaftarMateriScreen(
    navController: NavHostController,
    viewModel: DaftarMateriViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SakuPintarTheme(isGuruMode = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBackground)
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

            Text("Daftar Materi",
                style = MaterialTheme.typography.titleLarge,
                color = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(4.dp))

            if (uiState.namaDokumen != null) {
                Text("Dokumen: ${uiState.namaDokumen}  ·  Diunggah: ${uiState.tanggalUpload}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextMuted)
            } else {
                Text("Belum ada dokumen yang diunggah.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextMuted)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.sedangMemuat) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorPrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.daftarKondisi, key = { it.kode }) { kondisi ->
                        KondisiMateriCard(
                            kondisi = kondisi,
                            onClick = {
                                if (kondisi.tersedia) {
                                    navController.navigate(Routes.guruDetailMateri(kondisi.kode))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KondisiMateriCard(
    kondisi: KondisiMateriItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (kondisi.tersedia) ColorSuccessContainer else ColorSurface,
                RoundedCornerShape(14.dp)
            )
            .border(
                0.5.dp,
                if (kondisi.tersedia) ColorSuccess else ColorBorder,
                RoundedCornerShape(14.dp)
            )
            .then(if (kondisi.tersedia) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (kondisi.tersedia) ColorSuccess else ColorPrimaryContainer,
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(kondisi.kode,
                style = MaterialTheme.typography.bodyMedium,
                color = if (kondisi.tersedia) ColorSurface else ColorPrimary,
                fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(kondisi.label,
                style = MaterialTheme.typography.bodyLarge,
                color = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold)
            Text(
                if (kondisi.tersedia) "✓ Materi tersedia — Tap untuk lihat isi"
                else "○ Belum ada materi",
                style = MaterialTheme.typography.bodySmall,
                color = if (kondisi.tersedia) ColorSuccess else ColorTextMuted
            )
        }

        if (kondisi.tersedia) {
            Text("→", style = MaterialTheme.typography.bodyMedium, color = ColorTextMuted)
        }
    }
}