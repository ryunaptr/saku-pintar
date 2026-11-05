package com.sakupintar.app.ui.screen.guru.progres

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorBorder
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorPrimaryContainer
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme

@Composable
fun ProgresScreen(
    navController: NavHostController,
    viewModel: ProgresViewModel = viewModel()
) {
    val daftarProgres by viewModel.daftarProgresTerfilter.collectAsStateWithLifecycle()
    val filterKelas   by viewModel.filterKelas.collectAsStateWithLifecycle()

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
                    text  = "← Kembali",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = "Pantau Progres",
                style      = MaterialTheme.typography.titleLarge,
                color      = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text  = "${daftarProgres.size} siswa",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter kelas
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
                            text       = kelas,
                            style      = MaterialTheme.typography.labelMedium,
                            color      = if (dipilih) ColorSurface else ColorTextSecondary,
                            fontWeight = if (dipilih) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier        = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding  = PaddingValues(bottom = 24.dp)
            ) {
                items(daftarProgres, key = { it.id }) { siswa ->
                    ProgresCard(
                        siswa   = siswa,
                        onClick = {
                            navController.navigate(Routes.guruDetailProgres(siswa.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgresCard(
    siswa: SiswaProgres,
    onClick: () -> Unit
) {
    val progres = siswa.kompetensiDikuasai / 6f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurface, RoundedCornerShape(14.dp))
            .border(0.5.dp, ColorBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Foto / Inisial
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

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = siswa.nama,
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = ColorTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text  = "⭐ ${siswa.totalPoin}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress   = { progres },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color      = ColorPrimary,
                trackColor = ColorPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text  = "${siswa.kelas}  ·  ${siswa.tingkat}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextMuted
                )
                Text(
                    text  = "${siswa.kompetensiDikuasai}/6 kompetensi",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text  = "→",
            style = MaterialTheme.typography.titleMedium,
            color = ColorTextMuted
        )
    }
}