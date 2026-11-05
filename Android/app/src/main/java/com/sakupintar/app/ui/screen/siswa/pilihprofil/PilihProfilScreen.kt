package com.sakupintar.app.ui.screen.siswa.pilihprofil

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sakupintar.app.R
import com.sakupintar.app.ui.component.AppBackground
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.SakuPintarTheme
import com.sakupintar.app.util.AppAudioManager

@Composable
fun PilihProfilScreen(
    navController: NavHostController,
    viewModel: PilihProfilViewModel = viewModel()
) {
    val daftarSiswa  by viewModel.daftarSiswa.collectAsStateWithLifecycle()
    val sedangMemuat by viewModel.sedangMemuat.collectAsStateWithLifecycle()
    val error        by viewModel.error.collectAsStateWithLifecycle()

    //Kelompokkan per kelas
    val kelompokKelas = daftarSiswa.groupBy { it.kelas }
        .toSortedMap(compareBy {
            when (it) { "Kelas 7" -> 0; "Kelas 8" -> 1; "Kelas 9" -> 2; else -> 3 }
        })

    SakuPintarTheme(isGuruMode = false) {
        AppBackground {
            Column(modifier = Modifier.fillMaxSize()) {

                //HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        TextButton(
                            onClick        = {
                                AppAudioManager.playSfxClick()
                                navController.popBackStack() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text  = "← Kembali",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2F80ED)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier          = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text       = "Halo!",
                                    fontSize   = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color      = Color(0xFF173B73)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text      = "Pilih namamu di bawah ini",
                                    fontSize  = 15.sp,
                                    color     = Color(0xFF2F80ED),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Image(
                                painter            = painterResource(R.drawable.maskot_sapa),
                                contentDescription = null,
                                modifier           = Modifier.size(80.dp),
                                contentScale       = ContentScale.Fit
                            )
                        }
                    }
                }

                //KONTEN
                when {
                    sedangMemuat -> {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter            = painterResource(R.drawable.maskot_mikir),
                                    contentDescription = null,
                                    modifier           = Modifier.size(120.dp),
                                    contentScale       = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                CircularProgressIndicator(
                                    color       = Color(0xFF2F80ED),
                                    strokeWidth = 4.dp,
                                    modifier    = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Memuat daftar siswa...",
                                    fontSize = 15.sp,
                                    color    = Color(0xFF2F80ED)
                                )
                            }
                        }
                    }

                    error != null -> {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier            = Modifier.padding(24.dp)
                            ) {
                                Image(
                                    painter            = painterResource(R.drawable.maskot_salah),
                                    contentDescription = null,
                                    modifier           = Modifier.size(120.dp),
                                    contentScale       = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text      = error ?: "Terjadi kesalahan",
                                    fontSize  = 15.sp,
                                    color     = Color(0xFF173B73),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .shadow(4.dp, RoundedCornerShape(16.dp))
                                        .background(Color(0xFF2F80ED), RoundedCornerShape(16.dp))
                                        .clickable { viewModel.loadSiswa() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Coba Lagi",
                                        fontSize   = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color.White
                                    )
                                }
                            }
                        }
                    }

                    daftarSiswa.isEmpty() -> {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter            = painterResource(R.drawable.maskot_mikir),
                                    contentDescription = null,
                                    modifier           = Modifier.size(120.dp),
                                    contentScale       = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Belum ada siswa terdaftar.",
                                    fontSize = 15.sp,
                                    color    = Color(0xFF173B73)
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier       = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start  = 20.dp,
                                end    = 20.dp,
                                top    = 16.dp,
                                bottom = 32.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            kelompokKelas.forEach { (kelas, listSiswa) ->
                                //Label pemisah kelas
                                item(key = "label_$kelas") {
                                    Row(
                                        modifier          = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFF2F80ED),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text       = kelas,
                                                fontSize   = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color      = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(1.dp)
                                                .background(Color(0xFF2F80ED).copy(alpha = 0.2f))
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                //Kartu per siswa
                                items(listSiswa, key = { it.id }) { siswa ->
                                    ProfilCard(
                                        siswa   = siswa,
                                        onClick = {
                                            AppAudioManager.playSfxClick()
                                            navController.navigate(Routes.siswaBeranda(siswa.id))
                                        }
                                    )
                                }

                                item(key = "spacer_$kelas") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfilCard(
    siswa: ProfilSiswa,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "profil_scale_${siswa.id}"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .background(Color.White, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Foto/Inisial
        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(2.dp, CircleShape)
                .background(Color(0xFFD6EAFF), CircleShape)
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
                    fontSize   = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color(0xFF173B73)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        //Nama + kelas
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = siswa.nama,
                fontSize   = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color(0xFF173B73),
                maxLines   = 2,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFEEF6FF), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text       = siswa.kelas,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF2F80ED)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        //Tombol Ini Aku
        Box(
            modifier = Modifier
                .height(44.dp)
                .shadow(3.dp, RoundedCornerShape(12.dp))
                .background(Color(0xFF2F80ED), RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null
                ) { onClick() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "Ini Aku 👋",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
        }
    }
}