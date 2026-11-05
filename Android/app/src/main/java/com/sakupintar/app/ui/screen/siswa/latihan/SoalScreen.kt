package com.sakupintar.app.ui.screen.siswa.latihan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.R
import com.sakupintar.app.ui.component.AppBackground
import com.sakupintar.app.ui.component.KartuUangItem
import com.sakupintar.app.ui.component.UangHelper
import com.sakupintar.app.ui.navigation.Routes
import com.sakupintar.app.ui.screen.siswa.belajar.MaskotFeedbackDialog
import com.sakupintar.app.ui.theme.ColorDisabled
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme
import com.sakupintar.app.util.AppAudioManager
import com.sakupintar.app.util.KondisiUtils
import kotlin.math.roundToInt
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.foundation.clickable

private val PrimaryBlue = Color(0xFF2F80ED)
private val DarkBlue    = Color(0xFF173B73)
private val LightBlue   = Color(0xFFEEF6FF)

@Composable
fun SoalScreen(
    navController: NavHostController,
    siswaId: Int,
    kondisiId: String,
    viewModel: SoalViewModel = viewModel()
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val warnaKondisi = KondisiUtils.warna(kondisiId)
    val warnaTerang  = KondisiUtils.warnaTerang(kondisiId)

    // Ganti blok TTS yang ada dengan ini:
    val context = LocalContext.current
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember(context) {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                android.util.Log.d("TTS", "TTS siap")
            } else {
                android.util.Log.e("TTS", "TTS gagal: $status")
            }
        }
    }

// Ganti DisposableEffect dengan yang lebih aman:
    androidx.compose.runtime.DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
            android.util.Log.d("TTS", "TTS shutdown")
        }
    }

    fun bacakanSoal(teks: String) {
        if (ttsReady) {
            tts.stop()
            val result = tts.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.getDefault())
            }
            tts.speak(teks, TextToSpeech.QUEUE_FLUSH, null, "soal_${System.currentTimeMillis()}")
            android.util.Log.d("TTS", "Speak dipanggil: $teks")
        } else {
            android.util.Log.e("TTS", "TTS belum siap saat speak dipanggil")
        }
    }

    LaunchedEffect(uiState.soalSaatIni, ttsReady) {
        if (ttsReady && !uiState.sedangMemuat && !uiState.selesai && uiState.soalList.isNotEmpty()) {
            val soalSaatIni = uiState.soalList.getOrNull(uiState.soalSaatIni)
            soalSaatIni?.let {
                kotlinx.coroutines.delay(1000)
                val teksOpsi = it.pilihan.mapIndexed { i, o -> "${('A' + i)}. ${bersihkanTeksOpsiUntukSuara(o)}" }.joinToString(", ")
                bacakanSoal("${it.pertanyaan}. Pilihan jawaban: $teksOpsi")
            }
        }
    }

    LaunchedEffect(siswaId, kondisiId) { viewModel.loadSoal(siswaId, kondisiId) }

    SakuPintarTheme(isGuruMode = false) {
        AppBackground {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── LOADING ──────────────────────────────────
                if (uiState.sedangMemuat) {
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
                                color       = warnaKondisi,
                                strokeWidth = 4.dp,
                                modifier    = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    "Menyiapkan soal untukmu...",
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = DarkBlue,
                                    textAlign  = TextAlign.Center
                                )
                            }
                        }
                    }
                    return@AppBackground
                }

                // ── ERROR ────────────────────────────────────
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
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Text(
                                    uiState.error ?: "Terjadi kesalahan",
                                    fontSize  = 15.sp,
                                    color     = DarkBlue,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    AppAudioManager.playSfxClick()
                                    navController.popBackStack()
                                },
                                colors   = ButtonDefaults.buttonColors(containerColor = warnaKondisi),
                                shape    = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Text("Kembali", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    return@AppBackground
                }

                // ── HASIL ────────────────────────────────────
                if (uiState.selesai) {
                    HasilSoalScreen(
                        uiState      = uiState,
                        warnaKondisi = warnaKondisi,
                        warnaTerang  = warnaTerang,
                        emojiKondisi = KondisiUtils.emoji(kondisiId),
                        onUlang      = {
                            AppAudioManager.playSfxClick()
                            viewModel.loadSoal(siswaId, kondisiId)
                        },
                        onKembali    = {
                            AppAudioManager.playSfxClick()
                            navController.navigate(Routes.siswaLatihan(siswaId)) {
                                popUpTo(Routes.siswaLatihan(siswaId)) { inclusive = true }
                            }
                        }
                    )
                    return@AppBackground
                }

                val soal  = uiState.soalList.getOrNull(uiState.soalSaatIni) ?: return@AppBackground
                val nomor = uiState.soalSaatIni

                // ── HEADER PROGRESS ──────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                "Soal ${nomor + 1} dari 5",
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
                                    KondisiUtils.nama(kondisiId),
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = warnaKondisi
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(Color(0xFFE0E0E0), CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth((nomor + 1).toFloat() / 5f)
                                    .background(warnaKondisi, CircleShape)
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
                            val soalSaatIni = uiState.soalList.getOrNull(uiState.soalSaatIni)
                            soalSaatIni?.let {
                                val teksOpsi = it.pilihan.mapIndexed { i, o -> "${('A' + i)}. ${bersihkanTeksOpsiUntukSuara(o)}" }.joinToString(", ")
                                bacakanSoal("${it.pertanyaan}. Pilihan jawaban: $teksOpsi")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔊", fontSize = 18.sp)
                }

                // ── KONTEN SOAL ──────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    // Kartu pertanyaan
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                            .border(1.dp, warnaKondisi.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            soal.pertanyaan,
                            fontSize   = 20.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = DarkBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Drag & Drop aktivitas
                    key(uiState.soalSaatIni) {
                        DragDropSoal(
                            opsi         = soal.pilihan,
                            jawabanBenar = soal.jawabanBenar,
                            sudahJawab   = uiState.sudahJawab,
                            jawabBenar   = uiState.jawabanBenarSoalIni,
                            warnaKondisi = warnaKondisi,
                            warnaTerang  = warnaTerang,
                            onJawab      = { idx -> viewModel.pilihJawaban(idx) }
                        )
                    }

                    // Popup maskot feedback
                    if (uiState.sudahJawab) {
                        MaskotFeedbackDialog(jawabBenar = uiState.jawabanBenarSoalIni)
                    }

                    // Feedback banner
                    AnimatedVisibility(
                        visible = uiState.sudahJawab,
                        enter   = fadeIn() + scaleIn(initialScale = 0.8f)
                    ) {
                        val bgFeedback    = if (uiState.jawabanBenarSoalIni) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        val fgFeedback    = if (uiState.jawabanBenarSoalIni) Color(0xFF1B5E20) else Color(0xFF7F0000)
                        val pesanFeedback = if (uiState.jawabanBenarSoalIni) "Benar! Kamu pintar!" else "Belum tepat, coba ingat lagi!"

                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bgFeedback, RoundedCornerShape(16.dp))
                                .padding(18.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(
                                        if (uiState.jawabanBenarSoalIni) R.drawable.maskot_benar
                                        else R.drawable.maskot_salah
                                    ),
                                    contentDescription = null,
                                    modifier           = Modifier.size(56.dp),
                                    contentScale       = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        pesanFeedback,
                                        fontSize   = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color      = fgFeedback
                                    )
                                    Text(
                                        if (uiState.jawabanBenarSoalIni) "Terus semangat!" else "Kamu pasti bisa!",
                                        fontSize = 13.sp,
                                        color    = fgFeedback.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // ── TOMBOL LANJUT ────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = {
                            AppAudioManager.playSfxClick()
                            viewModel.soalBerikutnya(siswaId, kondisiId)
                        },
                        enabled  = uiState.sudahJawab,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp),
                        shape    = RoundedCornerShape(18.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = warnaKondisi,
                            disabledContainerColor = ColorDisabled
                        )
                    ) {
                        Text(
                            if (uiState.soalSaatIni < 4) "Soal Berikutnya →" else "Lihat Hasil 🏆",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = if (uiState.sudahJawab) Color.White else ColorTextMuted
                        )
                    }
                }
            }
        }
    }
}

// ── DRAG DROP SOAL ───────────────────────────────────────────
@Composable
private fun DragDropSoal(
    opsi: List<String>,
    jawabanBenar: Int,
    sudahJawab: Boolean,
    jawabBenar: Boolean,
    warnaKondisi: Color,
    warnaTerang: Color,
    onJawab: (Int) -> Unit
) {
    var jawabanDiDrop  by remember { mutableStateOf<Int?>(null) }
    var dropZoneY      by remember { mutableStateOf(0f) }
    var dropZoneHeight by remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return Offset.Zero
            }
        }
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

            // DROP ZONE
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
                            !sudahJawab && jawabanDiDrop == null -> warnaTerang
                            sudahJawab && jawabBenar             -> Color(0xFFE8F5E9)
                            sudahJawab && !jawabBenar            -> Color(0xFFFFEBEE)
                            else                                 -> warnaTerang
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        2.dp,
                        if (jawabanDiDrop != null && sudahJawab) {
                            if (jawabBenar) Color(0xFF27AE60) else Color(0xFFC62828)
                        } else warnaKondisi,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (jawabanDiDrop != null) {
                    Text(
                        text       = opsi.getOrElse(jawabanDiDrop!!) { "" },
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = DarkBlue,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.padding(horizontal = 12.dp)
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

            // OPSI
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .nestedScroll(nestedScrollConnection),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            )  {
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
                        label = "drag_soal_$idx"
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
                                    sudahDiDrop && sudahJawab -> Color(0xFFF0F0F0)
                                    isDragging                -> warnaKondisi.copy(alpha = 0.15f)
                                    else                      -> Color.White
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
                                if (!sudahJawab && !sudahDiDrop)
                                    Modifier.pointerInput(idx) {
                                        detectDragGestures(
                                            onDragStart = { isDragging = true },
                                            onDragEnd   = {
                                                isDragging = false
                                                val cardTopY       = cardRootY + offsetY
                                                val dropZoneBottom = dropZoneY + dropZoneHeight
                                                if (cardTopY < dropZoneBottom && offsetY < -40f) {
                                                    jawabanDiDrop = idx
                                                    AppAudioManager.playSfxClick()
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
                        if (!sudahDiDrop || !sudahJawab) {
                            OpsiSoalItem(
                                teks         = opsiTeks,
                                warnaKondisi = warnaKondisi,
                                isDragging   = isDragging,
                                ukuranBesar  = false
                            )
                        } else {
                            Text(
                                text       = "✓ Sudah dipilih",
                                fontSize   = 14.sp,
                                color      = Color(0xFFBBBBBB),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── OPSI ITEM ────────────────────────────────────────────────
@Composable
private fun OpsiSoalItem(
    teks: String,
    warnaKondisi: Color,
    isDragging: Boolean,
    ukuranBesar: Boolean
) {
    val isNominal    = UangHelper.nominalsFromText(teks).isNotEmpty()
    val isUangKertas = teks.contains("kertas", ignoreCase = true) ||
            teks.contains("lembaran", ignoreCase = true)
    val isUangKoin   = teks.contains("koin", ignoreCase = true) ||
            teks.contains("logam", ignoreCase = true) ||
            teks.contains("bulat", ignoreCase = true)

    when {
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
                        .fillMaxWidth(if (ukuranBesar) 0.7f else 0.9f)
                        .height(if (ukuranBesar) 70.dp else 60.dp)
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

// ── HASIL SOAL SCREEN ────────────────────────────────────────
@Composable
private fun HasilSoalScreen(
    uiState: SoalUiState,
    warnaKondisi: Color,
    warnaTerang: Color,
    emojiKondisi: String,
    onUlang: () -> Unit,
    onKembali: () -> Unit
) {
    val skor    = uiState.jumlahBenar * 20
    val lulus   = uiState.jumlahBenar >= 4
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(
                if (lulus) R.drawable.maskot_juara else R.drawable.maskot_benar
            ),
            contentDescription = null,
            modifier           = Modifier.size(140.dp),
            contentScale       = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            if (lulus) "Luar Biasa!" else "Tetap Semangat!",
            fontSize   = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = DarkBlue,
            textAlign  = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(14.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                if (lulus) "Kamu berhasil menguasai materi ini!"
                else "Coba lagi untuk hasil yang lebih baik!",
                fontSize  = 15.sp,
                color     = DarkBlue,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Kartu skor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (lulus) Color(0xFFE8F5E9) else warnaTerang,
                    RoundedCornerShape(24.dp)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$skor",
                    fontSize   = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = if (lulus) Color(0xFF1B5E20) else warnaKondisi
                )
                Text(
                    "POIN",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = if (lulus) Color(0xFF2E7D32) else warnaKondisi
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "${uiState.jumlahBenar} dari 5 soal benar",
                    fontSize = 15.sp,
                    color    = ColorTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Ringkasan jawaban
        Text(
            "Ringkasan Jawaban:",
            fontSize   = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = DarkBlue
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(5) { idx ->
                val benar = idx < uiState.jumlahBenar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            if (benar) Color(0xFF27AE60) else Color(0xFFEF9A9A),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (benar) "✓" else "✗",
                        fontSize = 22.sp,
                        color    = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick  = onKembali,
            modifier = Modifier.fillMaxWidth().height(68.dp),
            shape    = RoundedCornerShape(18.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = warnaKondisi)
        ) {
            Text(
                "Kembali ke Latihan",
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick  = onUlang,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape    = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Coba Lagi",
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color      = warnaKondisi
            )
        }
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