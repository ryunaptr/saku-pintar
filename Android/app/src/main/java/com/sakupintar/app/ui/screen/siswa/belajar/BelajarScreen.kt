package com.sakupintar.app.ui.screen.siswa.belajar

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.R
import com.sakupintar.app.ui.component.AppBackground
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.SakuPintarTheme
import com.sakupintar.app.util.KondisiUtils
import com.sakupintar.app.util.AppAudioManager

private val PrimaryBlue = Color(0xFF2F80ED)
private val DarkBlue    = Color(0xFF173B73)
private val LightBlue   = Color(0xFFEEF6FF)

@Composable
fun BelajarScreen(
    navController: NavHostController,
    siswaId: Int,
    viewModel: BelajarViewModel = viewModel()
) {
    val daftarModul by viewModel.daftarModul.collectAsStateWithLifecycle()

    LaunchedEffect(siswaId) { viewModel.loadModul(siswaId) }

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
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .background(LightBlue, RoundedCornerShape(50.dp))
                                    .clickable {  AppAudioManager.playSfxClick()
                                        navController.popBackStack() }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text       = "← Kembali",
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = PrimaryBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text       = "Ayo Belajar! 📚",
                                fontSize   = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = DarkBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text       = "Pilih materi yang ingin dipelajari",
                                fontSize   = 15.sp,
                                color      = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Image(
                            painter            = painterResource(R.drawable.maskot_belajar),
                            contentDescription = null,
                            modifier           = Modifier
                                .size(90.dp)
                                .offset(x = 8.dp),
                            contentScale       = ContentScale.Fit
                        )
                    }
                }

                //PROGRESS BAR
                val selesai = daftarModul.count { it.sudahSelesai }
                val total   = daftarModul.size

                if (total > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                            .shadow(3.dp, RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Column {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(
                                    text       = "Progres Belajar",
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = DarkBlue
                                )
                                Text(
                                    text       = "$selesai / $total selesai",
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color      = PrimaryBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .background(Color(0xFFD6EAFF), CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(
                                            if (total > 0) selesai.toFloat() / total.toFloat()
                                            else 0f
                                        )
                                        .background(PrimaryBlue, CircleShape)
                                )
                            }
                            if (selesai > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text       = if (selesai == total) "🎉 Semua materi selesai!" else "Terus semangat! 💪",
                                    fontSize   = 15.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                //GRID 2 KOLOM
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    val rows = daftarModul.chunked(2)
                    rows.forEach { rowModul ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowModul.forEach { modul ->
                                ModulBelajarCard(
                                    modul    = modul,
                                    modifier = Modifier.weight(1f),
                                    onClick  = {
                                        AppAudioManager.playSfxClick()
                                        navController.navigate(
                                            Routes.siswaMateri(siswaId, modul.kondisiId)
                                        )
                                    }
                                )
                            }
                            if (rowModul.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ModulBelajarCard(
    modul: ModulBelajar,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "card_scale_${modul.kondisiId}"
    )

    val warnaKondisi = KondisiUtils.warna(modul.kondisiId)
    val warnaTerang  = KondisiUtils.warnaTerang(modul.kondisiId)
    val emojiKondisi = KondisiUtils.emoji(modul.kondisiId)

    Column(
        modifier = modifier
            .scale(scale)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication        = null
            ) { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Emoji dalam lingkaran
        Box(
            modifier = Modifier
                .size(60.dp)
                .shadow(2.dp, CircleShape)
                .background(warnaTerang, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(emojiKondisi, fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        //Nama materi
        Text(
            text       = KondisiUtils.nama(modul.kondisiId),
            fontSize   = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = DarkBlue,
            textAlign  = TextAlign.Center,
            lineHeight = 22.sp,
            maxLines   = 2,
            minLines   = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBlue, RoundedCornerShape(12.dp))
                .padding(vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "Buka Materi →",
                fontSize   = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = PrimaryBlue
            )
        }
    }
}