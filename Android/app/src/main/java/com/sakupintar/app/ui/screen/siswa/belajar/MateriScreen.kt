package com.sakupintar.app.ui.screen.siswa.belajar

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.R
import com.sakupintar.app.ui.component.KartuUangItem
import com.sakupintar.app.ui.component.UangHelper
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.theme.SakuPintarTheme
import com.sakupintar.app.util.KondisiUtils
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalContext
import com.sakupintar.app.util.AppAudioManager
import android.speech.tts.TextToSpeech
import java.util.Locale

private val PrimaryBlue = Color(0xFF2F80ED)
private val DarkBlue    = Color(0xFF173B73)
private val LightBlue   = Color(0xFFEEF6FF)

@Composable
fun MateriScreen(
    navController: NavHostController,
    siswaId: Int,
    kondisiId: String,
    viewModel: MateriViewModel = viewModel()
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val warnaKondisi = KondisiUtils.warna(kondisiId)
    val warnaTerang  = KondisiUtils.warnaTerang(kondisiId)

    // --- TTS Setup ---
    val context = LocalContext.current
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember(context) {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
    }
    androidx.compose.runtime.DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }
    fun bacakanTeks(teks: String) {
        if (ttsReady) {
            tts.stop()
            val result = tts.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.getDefault())
            }
            tts.speak(teks, TextToSpeech.QUEUE_FLUSH, null, "materi_${System.currentTimeMillis()}")
        }
    }

    LaunchedEffect(siswaId, kondisiId) { viewModel.loadMateri(siswaId, kondisiId) }

    SakuPintarTheme(isGuruMode = false) {
        Box(modifier = Modifier.fillMaxSize()) {

            //Background
            Image(
                painter            = painterResource(R.drawable.background),
                contentDescription = null,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )

            Column(modifier = Modifier.fillMaxSize()) {

                //LOADING
                if (uiState.sedangMemuat) {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter            = painterResource(R.drawable.maskot_belajar),
                                contentDescription = null,
                                modifier           = Modifier.size(130.dp),
                                contentScale       = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                color       = warnaKondisi,
                                strokeWidth = 4.dp,
                                modifier    = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Menyiapkan materi khusus untukmu...",
                                        fontSize   = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = DarkBlue,
                                        textAlign  = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "(Pertama kali ± 1 menit, selanjutnya instan)",
                                        fontSize  = 13.sp,
                                        color     = PrimaryBlue,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    return@Column
                }

                //ERROR
                if (uiState.error != null) {
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
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    uiState.error ?: "Terjadi kesalahan",
                                    fontSize  = 16.sp,
                                    color     = DarkBlue,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick  = { AppAudioManager.playSfxClick()
                                    navController.popBackStack() },
                                colors   = ButtonDefaults.buttonColors(containerColor = warnaKondisi),
                                shape    = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Text(
                                    "Kembali",
                                    color      = Color.White,
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    return@Column
                }

                val konten = uiState.konten
                if (konten.isEmpty()) return@Column

                val halamanIni = uiState.halamanSaatIni
                val halaman    = konten[halamanIni]

                //HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Column {
                        Row(
                            modifier          = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text       = uiState.namaModul,
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = DarkBlue
                            )
                            Box(
                                modifier = Modifier
                                    .background(warnaTerang, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text       = halaman.tipePage,
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = warnaKondisi
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        //Progress bar halaman
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            repeat(4) { idx ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(if (idx == halamanIni) 10.dp else 7.dp)
                                        .shadow(
                                            if (idx == halamanIni) 2.dp else 0.dp,
                                            CircleShape
                                        )
                                        .background(
                                            when {
                                                idx == halamanIni -> warnaKondisi
                                                idx < halamanIni  -> warnaKondisi.copy(alpha = 0.5f)
                                                else              -> Color(0xFFE0E0E0)
                                            },
                                            CircleShape
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text       = "${halamanIni + 1}/4",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = warnaKondisi
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(warnaTerang, CircleShape)
                        .clickable {
                            AppAudioManager.playSfxClick()
                            val teksBersih = bersihkanIsiUntukSuara(halaman.isi)
                            val teksOpsi = if (!halaman.opsi.isNullOrEmpty()) {
                                ". Pilihan jawaban: " + halaman.opsi.mapIndexed { i, o ->
                                    "${('A' + i)}. ${bersihkanTeksOpsiUntukSuara(o)}"
                                }.joinToString(", ")
                            } else ""
                            bacakanTeks("${halaman.judul}. $teksBersih$teksOpsi")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔊", fontSize = 18.sp)
                }
                //KONTEN
                Crossfade(
                    targetState = halamanIni,
                    modifier    = Modifier.weight(1f),
                    label       = "halaman_crossfade"
                ) { currentPage ->
                    val pageKonten = konten.getOrNull(currentPage) ?: return@Crossfade

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //Emoji besar dalam lingkaran
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .shadow(4.dp, CircleShape)
                                .background(warnaTerang, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(pageKonten.emoji, fontSize = 56.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        //Judul halaman
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text       = pageKonten.judul,
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = DarkBlue,
                                textAlign  = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        KontenHalamanMateri(
                            isi          = pageKonten.isi,
                            warnaKondisi = warnaKondisi,
                            tipePage     = pageKonten.tipePage
                        )

                        //Aktivitas
                        if (!pageKonten.opsi.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))

                            if ((currentPage == 2 || currentPage == 3) && pageKonten.opsi.size >= 2) {
                                DragDropAktivitas(
                                    opsi          = pageKonten.opsi,
                                    jawabanBenar  = pageKonten.jawabanBenar ?: 0,
                                    sudahMenjawab = uiState.sudahMenjawab,
                                    jawabBenar    = uiState.jawabBenar,
                                    warnaKondisi  = warnaKondisi,
                                    warnaTerang   = warnaTerang,
                                    onJawab       = { idx -> viewModel.pilihJawaban(idx) }
                                )
                            } else {
                                TapToSelectAktivitas(
                                    opsi           = pageKonten.opsi,
                                    jawabanBenar   = pageKonten.jawabanBenar ?: 0,
                                    jawabanDipilih = uiState.jawabanDipilih,
                                    sudahMenjawab  = uiState.sudahMenjawab,
                                    jawabBenar     = uiState.jawabBenar,
                                    warnaKondisi   = warnaKondisi,
                                    warnaTerang    = warnaTerang,
                                    onPilih        = { idx -> viewModel.pilihJawaban(idx) }
                                )
                            }

                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                //NAVIGASI
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (halamanIni > 0) {
                            OutlinedButton(
                                onClick  = { AppAudioManager.playSfxClick()
                                    viewModel.halamanSebelumnya() },
                                modifier = Modifier.weight(1f).height(60.dp),
                                shape    = RoundedCornerShape(16.dp),
                                colors   = ButtonDefaults.outlinedButtonColors(
                                    contentColor = PrimaryBlue
                                )
                            ) {
                                Text(
                                    "← Kembali",
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = PrimaryBlue
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick  = { AppAudioManager.playSfxClick()
                                    navController.popBackStack() },
                                modifier = Modifier.weight(1f).height(60.dp),
                                shape    = RoundedCornerShape(16.dp),
                                colors   = ButtonDefaults.outlinedButtonColors(
                                    contentColor = PrimaryBlue
                                )
                            ) {
                                Text(
                                    "← Belajar",
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = PrimaryBlue
                                )
                            }
                        }

                        if (halamanIni < 3) {
                            Button(
                                onClick  = { AppAudioManager.playSfxClick()
                                    viewModel.halamanBerikutnya() },
                                modifier = Modifier.weight(1f).height(60.dp),
                                shape    = RoundedCornerShape(16.dp),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor = warnaKondisi
                                )
                            ) {
                                Text(
                                    "Lanjut →",
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color      = Color.White
                                )
                            }
                        } else {
                            Column(
                                modifier            = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick  = {
                                        AppAudioManager.playSfxClick()
                                        navController.navigate(Routes.siswaSoal(siswaId, kondisiId))
                                    },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape    = RoundedCornerShape(14.dp),
                                    colors   = ButtonDefaults.buttonColors(
                                        containerColor = warnaKondisi
                                    )
                                ) {
                                    Text(
                                        "Yuk Latihan! →",
                                        fontSize   = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color      = Color.White
                                    )
                                }
                                OutlinedButton(
                                    onClick  = {
                                        AppAudioManager.playSfxClick()
                                        navController.navigate(Routes.siswaBelajar(siswaId)) {
                                            popUpTo(Routes.siswaBelajar(siswaId)) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    shape    = RoundedCornerShape(12.dp),
                                    colors   = ButtonDefaults.outlinedButtonColors(
                                        contentColor = PrimaryBlue
                                    )
                                ) {
                                    Text(
                                        "Kembali ke Belajar",
                                        fontSize = 14.sp,
                                        color    = PrimaryBlue
                                    )
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
private fun KontenHalamanMateri(
    isi: String,
    warnaKondisi: Color,
    tipePage: String = ""
) {
    val semuaBaris = isi.split("\n").map { it.trim() }.filter { it.isNotBlank() }

    val baris = semuaBaris

    val kalimatPembuka = baris.filter { !it.startsWith("•") && !it.startsWith("-") }
    val bulletPoints   = baris.filter { it.startsWith("•") || it.startsWith("-") }
    val tampilkanGambar = tipePage == "Penjelasan" || tipePage == "Contoh"

    Column(
        modifier            = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (kalimatPembuka.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(16.dp))
                    .border(1.dp, warnaKondisi.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text       = kalimatPembuka.joinToString("\n"),
                    fontSize   = 18.sp,
                    color      = DarkBlue,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (bulletPoints.isNotEmpty()) {
            var sudahTampilKertas by remember { mutableStateOf(false) }
            var sudahTampilKoin   by remember { mutableStateOf(false) }

            Column(
                modifier            = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                bulletPoints.forEach { baris ->
                    val teks = baris.removePrefix("•").removePrefix("-").trim()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                            Text(
                                text       = teks,
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = DarkBlue,
                                lineHeight = 28.sp
                            )

                    }

                    if (tampilkanGambar) {
                        val nominalsInBullet = UangHelper.nominalsFromText(teks)
                        if (nominalsInBullet.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Contoh:",
                                fontSize   = 13.sp,
                                color      = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                nominalsInBullet.take(3).forEach { nominal ->
                                    KartuUangItem(nominal = nominal, modifier = Modifier.weight(1f))
                                }
                                repeat(maxOf(0, 3 - nominalsInBullet.take(3).size)) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        } else {
                            val adaKertas = teks.contains("kertas", ignoreCase = true) ||
                                    teks.contains("LEMBARAN", ignoreCase = true)
                            val adaKoin   = teks.contains("koin", ignoreCase = true) ||
                                    teks.contains("BULAT", ignoreCase = true)
                            if (adaKertas && !adaKoin && !sudahTampilKertas) {
                                sudahTampilKertas = true
                                MiniGaleriKertas()
                            } else if (adaKoin && !adaKertas && !sudahTampilKoin) {
                                sudahTampilKoin = true
                                MiniGaleriKoin()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniGaleriKertas() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Contoh uang kertas:",
            fontSize   = 13.sp,
            color      = PrimaryBlue,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        listOf(
            listOf(1000, 2000),
            listOf(5000, 10000),
            listOf(20000, 50000),
            listOf(100000)
        ).forEach { baris ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                baris.forEach { nominal ->
                    KartuUangItem(nominal = nominal, modifier = Modifier.weight(1f))
                }
                if (baris.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MiniGaleriKoin() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Contoh uang koin:",
            fontSize   = 13.sp,
            color      = PrimaryBlue,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(100, 200, 500).forEach { nominal ->
                KartuUangItem(nominal = nominal, modifier = Modifier.weight(1f))
            }
            Column(
                modifier            = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.foundation.Image(
                    painter            = androidx.compose.ui.res.painterResource(
                        id = UangHelper.getResKoin1000()
                    ),
                    contentDescription = "Rp 1.000 koin",
                    contentScale       = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier           = Modifier
                        .size(80.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Rp 1.000",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = PrimaryBlue
                )
                Text("🪙 Uang Koin", fontSize = 12.sp, color = PrimaryBlue)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun TapToSelectAktivitas(
    opsi: List<String>,
    jawabanBenar: Int,
    jawabanDipilih: Int?,
    sudahMenjawab: Boolean,
    jawabBenar: Boolean,
    warnaKondisi: Color,
    warnaTerang: Color,
    onPilih: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Pilih jawabanmu:",
                fontSize   = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = DarkBlue
            )
            Spacer(modifier = Modifier.height(12.dp))

            opsi.forEachIndexed { idx, opsiTeks ->
                val dipilih  = jawabanDipilih == idx
                val iniBenar = idx == jawabanBenar

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue   = if (isPressed) 0.96f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label         = "opsi_scale_$idx"
                )

                val (bg, borderC, tekstC) = when {
                    !sudahMenjawab       -> Triple(LightBlue, PrimaryBlue.copy(alpha = 0.3f), DarkBlue)
                    iniBenar             -> Triple(Color(0xFFE8F5E9), Color(0xFF27AE60), Color(0xFF1B5E20))
                    dipilih && !iniBenar -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Color(0xFF7F0000))
                    else                 -> Triple(Color(0xFFF5F5F5), Color(0xFFE0E0E0), Color(0xFF9E9E9E))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .scale(scale)
                        .shadow(2.dp, RoundedCornerShape(14.dp))
                        .background(bg, RoundedCornerShape(14.dp))
                        .border(1.5.dp, borderC, RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication        = null
                        ) { onPilih(idx) }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(borderC.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = ('A' + idx).toString(),
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = tekstC
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text       = opsiTeks,
                        fontSize   = 17.sp,
                        color      = tekstC,
                        fontWeight = if (iniBenar && sudahMenjawab) FontWeight.ExtraBold else FontWeight.Medium,
                        modifier   = Modifier.weight(1f),
                        lineHeight = 24.sp
                    )
                    if (sudahMenjawab) {
                        Text(
                            text     = if (iniBenar) "✓" else if (dipilih) "✗" else "",
                            fontSize = 20.sp,
                            color    = borderC
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DragDropAktivitas(
    opsi: List<String>,
    jawabanBenar: Int,
    sudahMenjawab: Boolean,
    jawabBenar: Boolean,
    warnaKondisi: Color,
    warnaTerang: Color,
    onJawab: (Int) -> Unit
) {
    var jawabanDiDrop  by remember { mutableStateOf<Int?>(null) }
    var dropZoneY      by remember { mutableStateOf(0f) }
    var dropZoneHeight by remember { mutableStateOf(0f) }

    //Popup maskot
    if (sudahMenjawab) {
        MaskotFeedbackDialog(jawabBenar = jawabBenar)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "Tarik jawaban yang benar ke kotak ini!",
                fontSize   = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = DarkBlue,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            //DROP ZONE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .onGloballyPositioned { coords ->
                        dropZoneY      = coords.positionInRoot().y
                        dropZoneHeight = coords.size.height.toFloat()
                    }
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .background(
                        when {
                            !sudahMenjawab && jawabanDiDrop == null -> warnaTerang
                            sudahMenjawab && jawabBenar             -> Color(0xFFE8F5E9)
                            sudahMenjawab && !jawabBenar            -> Color(0xFFFFEBEE)
                            else                                    -> warnaTerang
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        2.dp,
                        if (jawabanDiDrop != null && sudahMenjawab) {
                            if (jawabBenar) Color(0xFF27AE60) else Color(0xFFC62828)
                        } else warnaKondisi,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (jawabanDiDrop != null) {
                    val teksDropped = opsi.getOrElse(jawabanDiDrop!!) { "" }
                    OpsiItem(
                        teks         = teksDropped,
                        warnaKondisi = warnaKondisi,
                        warnaTerang  = warnaTerang,
                        isDragging   = false,
                        ukuranBesar  = true
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⬆", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text       = "Tarik ke sini",
                            fontSize   = 14.sp,
                            color      = warnaKondisi.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier            = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                opsi.forEachIndexed { idx, opsiTeks ->
                    val sudahDiDrop = jawabanDiDrop == idx
                    var offsetY    by remember { mutableStateOf(0f) }
                    var isDragging by remember { mutableStateOf(false) }
                    var cardRootY  by remember { mutableStateOf(0f) }

                    val animOffset by animateFloatAsState(
                        targetValue   = if (isDragging) offsetY else 0f,
                        animationSpec = if (!isDragging) spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness    = Spring.StiffnessMedium
                        ) else snap(),
                        label = "drag_$idx"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coords ->
                                cardRootY = coords.positionInRoot().y
                            }
                            .offset { IntOffset(0, animOffset.roundToInt()) }
                            .shadow(
                                if (isDragging) 8.dp else 3.dp,
                                RoundedCornerShape(14.dp)
                            )
                            .background(
                                when {
                                    sudahDiDrop && sudahMenjawab -> Color(0xFFF0F0F0)
                                    isDragging                   -> warnaKondisi.copy(alpha = 0.15f)
                                    else                         -> Color.White
                                },
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                2.dp,
                                if (isDragging) warnaKondisi
                                else warnaKondisi.copy(alpha = 0.25f),
                                RoundedCornerShape(14.dp)
                            )
                            .then(
                                if (!sudahMenjawab && !sudahDiDrop)
                                    Modifier.pointerInput(idx) {
                                        detectDragGestures(
                                            onDragStart = { isDragging = true },
                                            onDragEnd   = {
                                                isDragging = false
                                                val cardTopY       = cardRootY + offsetY
                                                val dropZoneBottom = dropZoneY + dropZoneHeight
                                                if (cardTopY < dropZoneBottom && offsetY < -40f) {
                                                    jawabanDiDrop = idx
                                                    onJawab(idx)
                                                }
                                                offsetY = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                offsetY += dragAmount.y
                                                if (offsetY > 10f) offsetY = 10f
                                            }
                                        )
                                    }
                                else Modifier
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!sudahDiDrop || !sudahMenjawab) {
                            OpsiItem(
                                teks         = opsiTeks,
                                warnaKondisi = warnaKondisi,
                                warnaTerang  = warnaTerang,
                                isDragging   = isDragging,
                                ukuranBesar  = false
                            )
                        } else {
                            Text(
                                text      = "✓ Sudah dipilih",
                                fontSize  = 14.sp,
                                color     = Color(0xFFBBBBBB),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OpsiItem(
    teks: String,
    warnaKondisi: Color,
    warnaTerang: Color,
    isDragging: Boolean,
    ukuranBesar: Boolean
) {
    val isNominal = UangHelper.nominalsFromText(teks).isNotEmpty()
    val isUangKertas = teks.contains("kertas", ignoreCase = true) ||
            teks.contains("lembaran", ignoreCase = true)
    val isUangKoin = teks.contains("koin", ignoreCase = true) ||
            teks.contains("logam", ignoreCase = true) ||
            teks.contains("bulat", ignoreCase = true)

    when {
        //Opsi berupa nominal uang, maka tampilkan gambar uang
        isNominal -> {
            val nominal = UangHelper.nominalsFromText(teks).first()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.fillMaxWidth()
            ) {
                KartuUangItem(
                    nominal  = nominal,
                    modifier = Modifier
                        .fillMaxWidth(if (ukuranBesar) 0.6f else 0.85f)
                        .height(if (ukuranBesar) 70.dp else 60.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = teks,
                    fontSize   = if (ukuranBesar) 15.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (isDragging) Color.White else DarkBlue,
                    textAlign  = TextAlign.Center
                )
            }
        }

        isUangKertas -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter            = painterResource(R.drawable.uang_kertas_kolase),
                    contentDescription = "Uang Kertas",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(if (ukuranBesar) 80.dp else 70.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = teks,
                    fontSize   = if (ukuranBesar) 15.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (isDragging) Color.White else DarkBlue,
                    textAlign  = TextAlign.Center
                )
            }
        }

        isUangKoin -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter            = painterResource(R.drawable.uang_koin_kolase),
                    contentDescription = "Uang Koin",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(if (ukuranBesar) 70.dp else 60.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = teks,
                    fontSize   = if (ukuranBesar) 15.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (isDragging) Color.White else DarkBlue,
                    textAlign  = TextAlign.Center
                )
            }
        }

        else -> {
            Text(
                text       = teks,
                fontSize   = if (ukuranBesar) 17.sp else 15.sp,
                fontWeight = FontWeight.Bold,
                color      = if (isDragging) Color.White else DarkBlue,
                textAlign  = TextAlign.Center
            )
        }
    }
}

//Popup maskot feedback
@Composable
fun MaskotFeedbackDialog(jawabBenar: Boolean) {
    var tampil by remember { mutableStateOf(true) }
    val context = LocalContext.current

    if (!tampil) return

    val scale by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "popup_scale"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        tampil = false
    }

    LaunchedEffect(Unit) {
        if (jawabBenar) AppAudioManager.playSfxCorrect()
        else AppAudioManager.playSfxWrong()
    }

    Dialog(onDismissRequest = { tampil = false }) {
        Box(
            modifier         = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .scale(scale)
                    .shadow(12.dp, RoundedCornerShape(28.dp))
                    .background(Color.White, RoundedCornerShape(28.dp))
                    .padding(horizontal = 32.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter            = painterResource(
                        if (jawabBenar) R.drawable.maskot_benar
                        else R.drawable.maskot_salah
                    ),
                    contentDescription = null,
                    modifier           = Modifier.size(120.dp),
                    contentScale       = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text       = if (jawabBenar) "Benar! 🎉" else "Belum tepat 💪",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = if (jawabBenar) Color(0xFF27AE60) else Color(0xFFC62828),
                    textAlign  = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text      = if (jawabBenar) "Kamu hebat!" else "Coba lagi ya!",
                    fontSize  = 16.sp,
                    color     = DarkBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .background(
                            if (jawabBenar) Color(0xFF27AE60) else Color(0xFF2F80ED),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { tampil = false }
                        .padding(horizontal = 32.dp, vertical = 12.dp)
                ) {
                    Text(
                        text       = "Lanjut →",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TombolBesar(
    teks: String,
    warna: Color,
    warnaTeks: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .background(warna, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = teks,
            fontSize   = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color      = warnaTeks
        )
    }
}

private fun bersihkanTeksOpsiUntukSuara(teks: String): String {
    if (!teks.startsWith("Rp")) return teks
    return when {
        teks.contains("(") && teks.contains(")") -> {
            val start = teks.indexOf("(")
            val end = teks.indexOf(")")
            if (end > start) teks.substring(start + 1, end).trim() else teks
        }
        teks.contains("=") -> teks.substringAfter("=").trim()
        else -> teks
    }
}

private fun bersihkanIsiUntukSuara(isi: String): String {
    return isi.split("\n")
        .map { baris ->
            val bersih = baris.removePrefix("•").removePrefix("-").trim()
            if (bersih.contains("Rp") && bersih.contains("=")) {
                bersih.substringAfter("=").trim()
            } else {
                bersih
            }
        }
        .filter { it.isNotBlank() }
        .joinToString(". ")
}