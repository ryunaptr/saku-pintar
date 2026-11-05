package com.sakupintar.app.ui.screen.home

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sakupintar.app.R
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.util.AppAudioManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(navController: NavHostController) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AppAudioManager.mulaiMusic(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        //BACKGROUND HOMESCREEN
        Image(
            painter            = painterResource(R.drawable.background_home),
            contentDescription = null,
            modifier           = Modifier.fillMaxSize(),
            contentScale       = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            //TOMBOL GURU
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TombolGuru(
                    onClick = {
                        AppAudioManager.playSfxClick()
                        navController.navigate(Routes.GURU_LOGIN) }
                )

            }

            Spacer(modifier = Modifier.weight(0.3f))

            //LOGO
            Image(
                painter            = painterResource(R.drawable.logo_saku_pintar),
                contentDescription = "Logo Saku Pintar",
                modifier           = Modifier.size(300.dp),
                contentScale       = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            //TAGLINE
            Box(
                modifier = Modifier
                    .background(
                        Color.White.copy(alpha = 0.85f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {

            }

            Spacer(modifier = Modifier.weight(0.4f))

            //MASKOT
            Image(
                painter            = painterResource(R.drawable.maskot_sapa),
                contentDescription = "Maskot Saku Pintar",
                modifier           = Modifier
                    .size(200.dp)
                    .align(Alignment.Start)
                    .offset(x = (-8).dp),
                contentScale       = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            //TOMBOL SISWA BESAR
            TombolSiswa(
                onClick = {
                    AppAudioManager.playSfxClick()
                    navController.navigate(Routes.SISWA_PILIH_PROFIL) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TombolSiswa(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "siswa_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(28.dp))
            .background(Color(0xFF1565C0), RoundedCornerShape(28.dp))
            .clickable(
                interactionSource = interactionSource,
                indication        = null
            ) { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFFFB300), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "Rp",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Mulai Belajar Yuk",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "›",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }
        }
    }
}

@Composable
private fun TombolGuru(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "guru_scale"
    )

    Row(
        modifier = Modifier
            .scale(scale)
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .background(Color.White, RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication        = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("👨‍🏫", fontSize = 20.sp)
        Text(
            text       = "Guru",
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF1565C0)
        )
        Text(
            text       = "›",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF1565C0)
        )
    }
}