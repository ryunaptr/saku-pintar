package com.sakupintar.app.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakupintar.app.R
import androidx.compose.runtime.remember
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.alpha

//Mapping nominal ke drawable resource
object UangHelper {

    private val resMapKertas = mapOf(
        "100.000" to R.drawable.uang_100000,
        "50.000"  to R.drawable.uang_50000,
        "20.000"  to R.drawable.uang_20000,
        "10.000"  to R.drawable.uang_10000,
        "5.000"   to R.drawable.uang_5000,
        "2.000"   to R.drawable.uang_2000,
        "1.000"   to R.drawable.uang_1000
    )

    private val resMapKoin = mapOf(
        "koin_1000" to R.drawable.uang_koin_1000,
        "500" to R.drawable.uang_koin_500,
        "200" to R.drawable.uang_koin_200,
        "100" to R.drawable.uang_koin_100
    )

    private val warnaMap = mapOf(
        "100.000" to Color(0xFFC62828),
        "50.000"  to Color(0xFF1565C0),
        "20.000"  to Color(0xFF2E7D32),
        "10.000"  to Color(0xFF6A1B9A),
        "5.000"   to Color(0xFF6D4C41),
        "2.000"   to Color(0xFF546E7A),
        "1.000"   to Color(0xFF455A64)
    )

    fun isNominalUang(teks: String): Boolean {
        val cleaned = teks.replace(Regex("[Rr][Pp]\\.?\\s*"), "").trim()
        if (!cleaned.matches(Regex("[\\d.,]+"))) return false
        return resMapKertas.keys.any { cleaned.contains(it) }
                || resMapKoin.keys.any { cleaned.contains(it) }
    }

    fun isKoin(teks: String): Boolean {
        val nominal = extractNominal(teks)
        return resMapKoin.keys.any { nominal.contains(it) }
                && !nominal.contains("1.000")
    }

    fun extractNominal(teks: String): String =
        teks.replace(Regex("[Rr][Pp]\\s*"), "").trim()

    fun getWarna(teks: String): Color {
        val nominal = extractNominal(teks)
        return warnaMap.entries
            .firstOrNull { nominal.contains(it.key) }?.value
            ?: Color(0xFF546E7A)
    }

    fun getResKertas(teks: String): Int? {
        val nominal = extractNominal(teks)
        return resMapKertas.entries
            .firstOrNull { nominal.contains(it.key) }?.value
    }

    fun getResKoin(teks: String): Int? {
        val nominal = extractNominal(teks)
        return resMapKoin.entries
            .firstOrNull { nominal.contains(it.key) }?.value
    }

    fun getResKoin1000(): Int = R.drawable.uang_koin_1000

    fun nominalsFromText(teks: String): List<Int> {
        val validNominals = setOf(100000, 50000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100)
        val regex = Regex("[Rr][Pp]\\.?\\s*([\\d.]+)")
        return regex.findAll(teks)
            .mapNotNull { match ->
                match.groupValues[1].replace(".", "").trim().toIntOrNull()
            }
            .filter { it in validNominals }
            .distinct()
            .toList()
    }

    data class KombinasiUang(val jumlah: Int, val nominalTeks: String)

    fun parseOpsiUang(teks: String): KombinasiUang? {
        val regex = Regex("(\\d+)\\s+(?:lembar|koin)\\s+(Rp\\.?\\s*[\\d.]+)", RegexOption.IGNORE_CASE)
        val match = regex.find(teks)
        if (match != null) {
            val jumlah = match.groupValues[1].toIntOrNull() ?: return null
            return KombinasiUang(jumlah, match.groupValues[2])
        }
        if (isNominalUang(teks)) return KombinasiUang(1, teks)
        return null
    }
}

//Gambar uang kertas
@Composable
fun GambarUangKertas(
    teks: String,
    modifier: Modifier = Modifier
        .width(150.dp)
        .height(80.dp)
) {
    val resId = UangHelper.getResKertas(teks)

    if (resId != null) {
        Image(
            painter            = painterResource(id = resId),
            contentDescription = teks,
            contentScale       = ContentScale.Crop,
            modifier           = modifier.clip(RoundedCornerShape(10.dp))
        )
    } else {
        // Fallback jika foto belum ada
        GambarUangKertasFallback(teks = teks, modifier = modifier)
    }
}

//Gambar uang koin
@Composable
fun GambarUangKoin(
    teks: String,
    ukuran: Dp = 72.dp,
    modifier: Modifier = Modifier
) {
    val resId = UangHelper.getResKoin(teks)

    if (resId != null) {
        Image(
            painter            = painterResource(id = resId),
            contentDescription = teks,
            contentScale       = ContentScale.Crop,
            modifier           = modifier
                .size(ukuran)
                .clip(CircleShape)
        )
    } else {
        GambarUangKoinFallback(teks = teks, ukuran = ukuran, modifier = modifier)
    }
}

//Komponen gambar
@Composable
fun GambarUang(
    teks: String,
    modifier: Modifier = Modifier
) {
    if (UangHelper.isKoin(teks)) {
        GambarUangKoin(teks = teks, modifier = modifier)
    } else {
        GambarUangKertas(teks = teks, modifier = modifier)
    }
}

//Fallback pakai ilustrasi
@Composable
private fun GambarUangKertasFallback(teks: String, modifier: Modifier) {
    val nominal = UangHelper.extractNominal(teks)
    val warna   = UangHelper.getWarna(teks)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(warna.copy(alpha = 0.08f))
            .border(2.dp, warna, RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .width(16.dp)
                .fillMaxHeight()
                .background(warna, RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 10.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Rp", fontSize = 11.sp, color = warna.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                Text(nominal, fontSize = 19.sp, color = warna, fontWeight = FontWeight.ExtraBold)
            }
            Text("🏛", fontSize = 18.sp)
        }
    }
}

@Composable
private fun GambarUangKoinFallback(teks: String, ukuran: Dp, modifier: Modifier) {
    val nominal = UangHelper.extractNominal(teks)
    Box(
        modifier = modifier
            .size(ukuran)
            .clip(CircleShape)
            .background(Color(0xFFDAA520))
            .border(2.5.dp, Color(0xFFB8860B), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Rp", fontSize = (ukuran.value * 0.13f).sp, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
            Text(nominal, fontSize = (ukuran.value * 0.16f).sp, color = Color(0xFF1A1A1A), fontWeight = FontWeight.ExtraBold)
        }
    }
}

//Nominal dari teks LLM
private val nominalKeRes = mapOf(
    100000 to R.drawable.uang_100000,
    50000  to R.drawable.uang_50000,
    20000  to R.drawable.uang_20000,
    10000  to R.drawable.uang_10000,
    5000   to R.drawable.uang_5000,
    2000   to R.drawable.uang_2000,
    1000   to R.drawable.uang_1000,
    500    to R.drawable.uang_koin_500,
    200    to R.drawable.uang_koin_200,
    100    to R.drawable.uang_koin_100
)

private fun Int.keFormatNominal(): String = when (this) {
    100000 -> "100.000"
    50000  -> "50.000"
    20000  -> "20.000"
    10000  -> "10.000"
    5000   -> "5.000"
    2000   -> "2.000"
    1000   -> "1.000"
    else   -> this.toString()
}

private fun scanNominalDariTeks(teks: String): List<Int> {
    val regex = Regex("[Rr][Pp]\\.?\\s*([\\d.]+)")
    return regex.findAll(teks)
        .mapNotNull { match ->
            val nominalStr = match.groupValues[1]
                .replace(".", "")
                .trim()
            nominalStr.toIntOrNull()
        }
        .filter { it in nominalKeRes.keys }
        .distinct()
        .sortedDescending()
        .toList()
}

@Composable
private fun namaWarnaUang(nominal: Int): String = when (nominal) {
    100000 -> "🔴 Warna Merah"
    50000  -> "🔵 Warna Biru"
    20000  -> "🟢 Warna Hijau"
    10000  -> "🟣 Warna Ungu"
    5000   -> "🟤 Warna Cokelat"
    2000   -> "⚫ Warna Abu-abu"
    1000   -> "⚫ Warna Abu-abu"
    500    -> "🪙 Uang Koin"
    200    -> "🪙 Uang Koin"
    100    -> "🪙 Uang Koin"
    else   -> ""
}

@Composable
private fun KartuUang(nominal: Int, modifier: Modifier = Modifier) {
    val teksNominal = "Rp ${nominal.keFormatNominal()}"
    val isKoin      = nominal <= 500
    val warnaKondisi = UangHelper.getWarna(teksNominal)

    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, warnaKondisi.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isKoin) {
            GambarUangKoin(
                teks   = teksNominal,
                ukuran = 90.dp
            )
        } else {
            GambarUangKertas(
                teks     = teksNominal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text       = teksNominal,
            fontSize   = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = warnaKondisi
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text       = namaWarnaUang(nominal),
            fontSize   = 14.sp,
            color      = Color(0xFF546E7A),
            fontWeight = FontWeight.Medium
        )
    }
}

//Galeri Uang
@Composable
fun GaleriUang(
    teks: String,
    modifier: Modifier = Modifier
) {
    val nominals = remember(teks) { scanNominalDariTeks(teks) }
    if (nominals.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text       = "Kenali uang berikut ini:",
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF37474F)
        )

        Spacer(modifier = Modifier.height(12.dp))

        nominals.chunked(2).forEach { baris ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                baris.forEach { nominal ->
                    KartuUang(
                        nominal  = nominal,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (baris.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun KartuUangItem(nominal: Int, modifier: Modifier = Modifier) {
    val teksNominal = "Rp ${nominal.keFormatNominal()}"
    val isKoin      = nominal < 1000
    val warnaItem   = UangHelper.getWarna(teksNominal)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isKoin) {
            GambarUangKoin(teks = teksNominal, ukuran = 80.dp)
        } else {
            GambarUangKertas(
                teks     = teksNominal,
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(teksNominal, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = warnaItem)
        Text(namaWarnaUang(nominal), fontSize = 12.sp, color = Color(0xFF546E7A))
    }
}

@Composable
fun GaleriUangGrouped(
    teks: String,
    warnaKondisi: Color,
    modifier: Modifier = Modifier
) {
    val nominals   = remember(teks) { scanNominalDariTeks(teks) }
    if (nominals.isEmpty()) return

    val kertasList = nominals.filter { it >= 1000 }.sortedDescending()
    val koinList   = nominals.filter { it < 1000  }.sortedDescending()

    Column(modifier = modifier.fillMaxWidth()) {
        if (kertasList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💵", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Uang Kertas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = warnaKondisi)
            }
            Spacer(modifier = Modifier.height(10.dp))
            kertasList.chunked(2).forEach { baris ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    baris.forEach { nominal -> KartuUangItem(nominal, Modifier.weight(1f)) }
                    if (baris.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        if (koinList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🪙", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Uang Koin", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = warnaKondisi)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                koinList.forEach { nominal -> KartuUangItem(nominal, Modifier.weight(1f)) }
                repeat(maxOf(0, 3 - koinList.size)) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun GambarKombinasiUang(
    teks: String,
    warnaKondisi: Color,
    isDragging: Boolean,
    modifier: Modifier = Modifier
) {
    val kombinasi = remember(teks) { UangHelper.parseOpsiUang(teks) }

    if (kombinasi == null) {
        Text(
            teks,
            style      = MaterialTheme.typography.bodyMedium,
            color      = if (isDragging) Color.White else warnaKondisi,
            fontWeight = FontWeight.SemiBold,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.fillMaxWidth()
        )
        return
    }

    val isKoin = UangHelper.isKoin(kombinasi.nominalTeks)

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (isKoin) {
            val tampilkan = minOf(kombinasi.jumlah, 4)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    repeat(tampilkan) {
                        GambarUangKoin(teks = kombinasi.nominalTeks, ukuran = 34.dp)
                    }
                    if (kombinasi.jumlah > 4) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(warnaKondisi.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "+${kombinasi.jumlah - 4}",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color      = warnaKondisi
                            )
                        }
                    }
                }
            }
        } else {
            val tampilkan = minOf(kombinasi.jumlah, 3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                repeat(tampilkan) { i ->
                    val isTop     = i == tampilkan - 1
                    val offsetY   = (-(tampilkan - 1 - i) * 5).dp
                    val rotasi    = (tampilkan - 1 - i) * 3f * if (i % 2 == 0) 1f else -1f

                    GambarUangKertas(
                        teks     = kombinasi.nominalTeks,
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .height(50.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = offsetY)
                            .rotate(rotasi)
                            .alpha(if (isTop) 1f else 0.72f)
                            .zIndex(i.toFloat())
                    )
                }

                //Badge Jumlah
                if (kombinasi.jumlah > 1) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .background(warnaKondisi, CircleShape)
                            .zIndex(10f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "×${kombinasi.jumlah}",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }
                }
            }
        }
    }
}
