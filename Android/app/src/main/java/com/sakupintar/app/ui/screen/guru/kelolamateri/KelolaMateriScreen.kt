package com.sakupintar.app.ui.screen.guru.kelolamateri

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorBorder
import com.sakupintar.app.ui.theme.ColorPrimaryContainer
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme

@Composable
fun KelolaMateriScreen(
    navController: NavHostController,
    viewModel: KelolaMateriViewModel = viewModel()
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
                Text(
                    text = "← Kembali",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kelola Materi",
                style = MaterialTheme.typography.titleLarge,
                color = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${uiState.daftarKondisi.count { it.tersedia }} dari 6 kompetensi sudah memiliki materi.",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            //Card Upload Materi
            MenuMateriCard(
                emoji = "📤",
                judul = "Upload Materi",
                deskripsi = "Unggah dokumen materi per kompetensi ke sistem",
                onClick = { navController.navigate(Routes.GURU_UPLOAD_MATERI) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            //Card Daftar Materi
            MenuMateriCard(
                emoji = "📋",
                judul = "Daftar Materi",
                deskripsi = "Lihat seluruh materi yang sudah diunggah",
                onClick = { navController.navigate(Routes.GURU_DAFTAR_MATERI) }
            )
        }
    }
}

@Composable
private fun MenuMateriCard(
    emoji: String,
    judul: String,
    deskripsi: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurface, androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
            .border(
                0.5.dp,
                ColorBorder,
                androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    ColorPrimaryContainer,
                    androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 26.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = judul,
                style = MaterialTheme.typography.titleMedium,
                color = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = deskripsi,
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondary
            )
        }

        Text(
            text = "→",
            style = MaterialTheme.typography.titleMedium,
            color = ColorTextMuted,
            fontWeight = FontWeight.Bold
        )
    }
}