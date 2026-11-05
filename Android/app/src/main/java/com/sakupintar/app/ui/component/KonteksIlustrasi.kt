package com.sakupintar.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IlustrasiKonteks(kondisiId: String, tipePage: String) {
    if (tipePage != "Penjelasan" && tipePage != "Contoh") return

    val (scene, keterangan, warnaBg) = when (kondisiId) {
        "A" -> Triple(
            "👛💵🪙",
            "Uang kertas dan uang koin",
            Color(0xFFFFF8E1)
        )
        "B" -> Triple(
            "🔴💵  🔵💵  🟢💵",
            "Setiap warna punya nilai berbeda",
            Color(0xFFE3F2FD)
        )
        "C" -> Triple(
            "🧒🏪→ 💵💵 → 🛍️",
            "Membayar di kantin sekolah",
            Color(0xFFE8F5E9)
        )
        "D" -> Triple(
            "💵 = 🪙🪙🪙🪙🪙",
            "Uang yang nilainya sama",
            Color(0xFFF3E5F5)
        )
        "E" -> Triple(
            "🏷️ Rp5.000 → 🍕",
            "Membaca harga barang",
            Color(0xFFFFEBEE)
        )
        "F" -> Triple(
            "🏷️ Rp3.000  vs  🏷️ Rp5.000",
            "Mana yang lebih murah?",
            Color(0xFFE0F2F1)
        )
        else -> return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(warnaBg, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(scene, fontSize = 36.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                keterangan,
                fontSize   = 14.sp,
                color      = Color(0xFF455A64),
                fontWeight = FontWeight.Medium,
                textAlign  = TextAlign.Center
            )
        }
    }
}