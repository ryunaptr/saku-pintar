package com.sakupintar.app.ui.screen.siswa.pencapaian

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.R
import com.sakupintar.app.ui.theme.SakuPintarTheme
import com.sakupintar.app.util.KondisiUtils
import com.sakupintar.app.util.AppAudioManager

private val PrimaryBlue = Color(0xFF2F80ED)
private val DarkBlue    = Color(0xFF173B73)
private val LightBlue   = Color(0xFFEEF6FF)
private val Yellow      = Color(0xFFFFC83D)

private val posisiBadge = listOf(
    Pair(0.58f, 0.83f),
    Pair(0.22f, 0.72f),
    Pair(0.55f, 0.59f),
    Pair(0.25f, 0.46f),
    Pair(0.52f, 0.33f),
    Pair(0.25f, 0.19f),
)

@Composable
fun PencapaianScreen(
    navController: NavHostController,
    siswaId: Int,
    viewModel: PencapaianViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(siswaId) { viewModel.loadPencapaian(siswaId) }

    val badgeCount = uiState.daftarLencana.count { it.diperoleh }

    SakuPintarTheme(isGuruMode = false) {
        Column(modifier = Modifier.fillMaxSize()) {

            //HEADER PUTIH
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .background(LightBlue, RoundedCornerShape(50.dp))
                                .clickable { AppAudioManager.playSfxClick()
                                    navController.popBackStack() }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text       = "← Beranda",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color      = PrimaryBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text       = "Pencapaianku 🏆",
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = DarkBlue
                        )
                        Text(
                            text       = uiState.namaSiswa,
                            fontSize   = 14.sp,
                            color      = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Image(
                        painter            = painterResource(
                            if (badgeCount == 6) R.drawable.maskot_juara
                            else R.drawable.maskot_benar
                        ),
                        contentDescription = null,
                        modifier           = Modifier.size(70.dp),
                        contentScale       = ContentScale.Fit
                    )
                }
            }

//KOTAK KUNING
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .shadow(3.dp, RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFF4D6), RoundedCornerShape(20.dp))
                        .padding(horizontal = 38.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🏆", fontSize = 36.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text       = "${uiState.totalPoin}",
                                fontSize   = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = DarkBlue
                            )
                            Text(
                                text       = "POIN",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = DarkBlue.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

//KOTAK PUTIH PROGRESS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(3.dp, RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = "$badgeCount dari 6 kompetensi dikuasai",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        repeat(6) { i ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .shadow(2.dp, CircleShape)
                                    .background(
                                        if (i < badgeCount) PrimaryBlue else Color(0xFFE0E0E0),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text     = if (i < badgeCount) "⭐" else "🔒",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            //PETA JALUR
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val lebarLayar  = constraints.maxWidth.toFloat()
                val tinggiLayar = constraints.maxHeight.toFloat()

                val density = LocalDensity.current
                val lebarBadgeDp  = 155.dp
                val tinggiBadgeDp = 52.dp
                val lebarBadgePx  = with(density) { lebarBadgeDp.toPx() }
                val tinggiBadgePx = with(density) { tinggiBadgeDp.toPx() }

                //Background peta
                Image(
                    painter            = painterResource(R.drawable.background_pencapaian),
                    contentDescription = null,
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                )

                //Badge di atas peta
                uiState.daftarLencana.forEachIndexed { index, lencana ->
                    val (xPersen, yPersen) = posisiBadge.getOrElse(index) { Pair(0.5f, 0.5f) }

                    val xPx = (lebarLayar * xPersen - with(density) { lebarBadgeDp.toPx() } / 2).toInt()
                    val yPx = (tinggiLayar * yPersen - with(density) { tinggiBadgeDp.toPx() } / 2).toInt()

                    BadgePeta(
                        lencana  = lencana,
                        nomor    = index + 1,
                        modifier = Modifier
                            .offset { IntOffset(xPx, yPx) }
                            .width(lebarBadgeDp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgePeta(
    lencana: LencanaItem,
    nomor: Int,
    modifier: Modifier = Modifier
) {
    val warnaKondisi = KondisiUtils.warna(lencana.kondisiId)
    val warnaTerang  = KondisiUtils.warnaTerang(lencana.kondisiId)

    val pulse by rememberInfiniteTransition(label = "pulse_$nomor").animateFloat(
        initialValue  = 1f,
        targetValue   = if (lencana.diperoleh) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_badge_$nomor"
    )

    //Pill horizontal: ikon + teks nama
    Row(
        modifier = modifier
            .scale(if (lencana.diperoleh) pulse else 1f)
            .shadow(
                elevation = if (lencana.diperoleh) 6.dp else 2.dp,
                shape     = RoundedCornerShape(50.dp)
            )
            .background(
                color = if (lencana.diperoleh) Color.White else Color(0xFFEEEEEE),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Lingkaran ikon kecil
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (lencana.diperoleh) warnaKondisi else Color(0xFFBBBBBB),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = if (lencana.diperoleh) KondisiUtils.emoji(lencana.kondisiId) else "🔒",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        //Teks nama
        Text(
            text = "$nomor. ${KondisiUtils.nama(lencana.kondisiId)}",
            fontSize   = 12.sp,
            fontWeight = FontWeight.Bold,
            color      = if (lencana.diperoleh) DarkBlue else Color(0xFF999999),
            maxLines   = 1
        )
    }
}