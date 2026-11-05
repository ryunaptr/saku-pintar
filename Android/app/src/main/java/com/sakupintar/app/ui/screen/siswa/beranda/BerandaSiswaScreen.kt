package com.sakupintar.app.ui.screen.siswa.beranda

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import com.sakupintar.app.R
import com.sakupintar.app.ui.component.AppBackground
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.SakuPintarTheme
import androidx.compose.ui.platform.LocalContext
import com.sakupintar.app.util.AppAudioManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

private val PrimaryBlue = Color(0xFF2F80ED)
private val DarkBlue = Color(0xFF173B73)
private val LightBlue = Color(0xFFDFF2FF)
private val Yellow = Color(0xFFFFC83D)
private val Orange = Color(0xFFFF9F43)
private val Green = Color(0xFF34B66A)

private enum class StepSide {
    LEFT, RIGHT
}

@Composable
fun BerandaSiswaScreen(
    navController: NavHostController,
    siswaId: Int,
    viewModel: BerandaSiswaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isMuted by remember { mutableStateOf(false) }

    LaunchedEffect(siswaId) {
        viewModel.loadSiswa(siswaId)
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AppAudioManager.mulaiMusic(context)
    }

    SakuPintarTheme(isGuruMode = false) {
        AppBackground {
            if (uiState.sedangMemuat) {
                LoadingContent()
                return@AppBackground
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderSiswa(
                    nama = uiState.nama,
                    kelas = uiState.kelas,
                    onBackClick = {  AppAudioManager.playSfxClick()
                        navController.popBackStack() },
                    isMuted = isMuted,
                    onMuteToggle = {
                        isMuted = !isMuted
                        AppAudioManager.setMute(isMuted)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PointCard(
                    totalPoin = uiState.totalPoin
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Langkah belajar hari ini",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkBlue,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    RouteStepCard(
                        nomor = 1,
                        title = "Belajar",
                        subtitle = "Belajar uang",
                        imageRes = R.drawable.icon_belajar,
                        color = PrimaryBlue,
                        side = StepSide.LEFT,
                        onClick = {
                            AppAudioManager.playSfxClick()
                            navController.navigate(Routes.siswaBelajar(siswaId))
                        }
                    )

                    RouteConnector(
                        fromLeft = true,
                        color = PrimaryBlue
                    )

                    RouteStepCard(
                        nomor = 2,
                        title = "Latihan Soal",
                        subtitle = "Kerjakan soal",
                        imageRes = R.drawable.icon_latihan,
                        color = Orange,
                        side = StepSide.RIGHT,
                        onClick = {
                            AppAudioManager.playSfxClick()
                            navController.navigate(Routes.siswaLatihan(siswaId))
                        }
                    )

                    RouteConnector(
                        fromLeft = false,
                        color = Orange
                    )

                    RouteStepCard(
                        nomor = 3,
                        title = "Pencapaianku",
                        subtitle = "Lihat hadiahmu",
                        imageRes = R.drawable.icon_pencapaian,
                        color = Green,
                        side = StepSide.LEFT,
                        onClick = {
                            AppAudioManager.playSfxClick()
                            navController.navigate(Routes.siswaPencapaian(siswaId))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                MotivationCard()

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun HeaderSiswa(
    nama: String,
    kelas: String,
    onBackClick: () -> Unit,
    isMuted: Boolean = false,
    onMuteToggle: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.92f))
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = LightBlue.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable { onBackClick() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "← Ganti Profil",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Halo, $nama! 👋",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkBlue,
                    lineHeight = 32.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = kelas,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Image(
                painter = painterResource(R.drawable.maskot_sapa),
                contentDescription = "Maskot menyapa",
                modifier = Modifier
                    .size(82.dp)
                    .offset(x = 6.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(LightBlue, CircleShape)
                    .clickable { onMuteToggle() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = if (isMuted) "🔇" else "🔊",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PointCard(
    totalPoin: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Yellow.copy(alpha = 0.35f)
            )
            .background(
                color = PrimaryBlue,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                border = BorderStroke(
                    width = 3.dp,
                    color = Color.White.copy(alpha = 0.85f)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(Color.White.copy(alpha = 0.38f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⭐",
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$totalPoin Poin",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Kumpulkan poinmu",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Text(
                text = "🎯",
                fontSize = 28.sp
            )
        }
    }
}

@Composable
private fun RouteStepCard(
    nomor: Int,
    title: String,
    subtitle: String,
    imageRes: Int,
    color: Color,
    side: StepSide,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "route_step_scale_$nomor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .align(
                    if (side == StepSide.LEFT) Alignment.CenterStart
                    else Alignment.CenterEnd
                )
                .scale(scale)
                .shadow(
                    elevation = 7.dp,
                    shape = RoundedCornerShape(26.dp),
                    spotColor = color.copy(alpha = 0.20f)
                )
                .background(
                    color = Color.White.copy(alpha = 0.96f),
                    shape = RoundedCornerShape(26.dp)
                )
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = color.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                modifier = Modifier.size(76.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkBlue
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = LightBlue,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "›",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            }
        }

        Box(
            modifier = Modifier
                .align(
                    if (side == StepSide.LEFT) Alignment.TopStart
                    else Alignment.TopEnd
                )
                .offset(
                    x = if (side == StepSide.LEFT) 8.dp else (-8).dp,
                    y = 2.dp
                )
                .size(50.dp)
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .border(
                    border = BorderStroke(3.dp, color),
                    shape  = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "$nomor",
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = color
            )
        }
    }
}

@Composable
private fun RouteConnector(
    fromLeft: Boolean,
    color: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 24.dp)
    ) {
        val startX = if (fromLeft) size.width * 0.12f else size.width * 0.88f
        val endX   = if (fromLeft) size.width * 0.88f else size.width * 0.12f
        val startY = 0f
        val endY   = size.height
        val controlX = size.width * 0.50f
        val controlY = size.height * 0.50f
        val dotCount = 14

        for (i in 0..dotCount) {
            val t = i / dotCount.toFloat()

            val x = (1 - t) * (1 - t) * startX +
                    2 * (1 - t) * t * controlX +
                    t * t * endX

            val y = (1 - t) * (1 - t) * startY +
                    2 * (1 - t) * t * controlY +
                    t * t * endY

            val radius = if (i == 0 || i == dotCount) 3.5f else 5f

            drawCircle(
                color = color.copy(alpha = 0.70f),
                radius = 5.2f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun MotivationCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(50.dp),
                spotColor = PrimaryBlue.copy(alpha = 0.14f)
            )
            .background(
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(50.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Belajar pelan-pelan, pasti bisa!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.maskot_mikir),
                contentDescription = null,
                modifier = Modifier.size(130.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator(
                color = PrimaryBlue,
                strokeWidth = 4.dp,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Sebentar ya...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
        }
    }
}