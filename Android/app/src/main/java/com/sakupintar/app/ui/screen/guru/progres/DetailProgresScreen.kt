package com.sakupintar.app.ui.screen.guru.progres

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.ui.theme.ColorAccentContainer
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorBorder
import com.sakupintar.app.ui.theme.ColorOnAccentContainer
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorPrimaryContainer
import com.sakupintar.app.ui.theme.ColorSuccess
import com.sakupintar.app.ui.theme.ColorSuccessContainer
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun DetailProgresScreen(
    navController: NavHostController,
    siswaId: Int,
    viewModel: DetailProgresViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(siswaId) {
        viewModel.loadProgres(siswaId)
    }

    SakuPintarTheme(isGuruMode = true) {
        if (uiState.sedangMemuat) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorBackground),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ColorPrimary)
            }
            return@SakuPintarTheme
        }

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
                Text(
                    text = "← Kembali",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Card profil siswa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorPrimaryContainer, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(ColorSurface, CircleShape)
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
                            text       = uiState.nama.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = ColorPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = uiState.nama,
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.kelas}  ·  Tunagrahita ${uiState.tingkat}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Poin & badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RingkasanCard(
                    modifier = Modifier.weight(1f),
                    emoji = "⭐",
                    angka = uiState.totalPoin.toString(),
                    label = "Total Poin"
                )
                RingkasanCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🏅",
                    angka = uiState.totalBadge.toString(),
                    label = "Badge Diraih"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(20.dp))

            //Penugasan Kompetensi
            Text(
                text = "Penguasaan Kompetensi",
                style = MaterialTheme.typography.titleMedium,
                color = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.daftarKompetensi.forEach { kompetensi ->
                    KompetensiRow(kompetensi = kompetensi)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = ColorBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(20.dp))

            //Riwayat Latihan
            Text(
                text = "Riwayat Latihan Soal",
                style = MaterialTheme.typography.titleMedium,
                color = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.riwayatLatihan.isEmpty()) {
                Text(
                    text = "Belum ada riwayat latihan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.riwayatLatihan.forEach { riwayat ->
                        RiwayatCard(riwayat = riwayat)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun RingkasanCard(
    modifier: Modifier = Modifier,
    emoji: String,
    angka: String,
    label: String
) {
    Column(
        modifier = modifier
            .background(ColorSurface, RoundedCornerShape(12.dp))
            .border(0.5.dp, ColorBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = angka,
            style = MaterialTheme.typography.titleLarge,
            color = ColorTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = ColorTextSecondary
        )
    }
}

@Composable
private fun KompetensiRow(kompetensi: KompetensiProgres) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (kompetensi.dikuasai) ColorSuccessContainer else ColorSurface,
                RoundedCornerShape(10.dp)
            )
            .border(
                0.5.dp,
                if (kompetensi.dikuasai) ColorSuccess else ColorBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (kompetensi.dikuasai) ColorSuccess else ColorPrimaryContainer,
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = kompetensi.kode,
                style = MaterialTheme.typography.bodyMedium,
                color = if (kompetensi.dikuasai) ColorSurface else ColorPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = kompetensi.label,
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = if (kompetensi.dikuasai) "✓" else "○",
            style = MaterialTheme.typography.titleMedium,
            color = if (kompetensi.dikuasai) ColorSuccess else ColorTextMuted,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RiwayatCard(riwayat: RiwayatLatihan) {
    val warnaLatar = if (riwayat.skor >= 80) ColorSuccessContainer else ColorAccentContainer
    val warnaTeks = if (riwayat.skor >= 80) ColorSuccess else ColorOnAccentContainer

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurface, RoundedCornerShape(10.dp))
            .border(0.5.dp, ColorBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = riwayat.labelKompetensi,
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${riwayat.jumlahBenar}/5 benar  ·  ${riwayat.tanggal}",
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondary
            )
        }

        Box(
            modifier = Modifier
                .background(warnaLatar, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${riwayat.skor}",
                style = MaterialTheme.typography.titleMedium,
                color = warnaTeks,
                fontWeight = FontWeight.Bold
            )
        }
    }
}