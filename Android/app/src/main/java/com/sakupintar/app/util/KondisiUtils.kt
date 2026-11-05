package com.sakupintar.app.util

import androidx.compose.ui.graphics.Color

object KondisiUtils {
    val warna = mapOf(
        "A" to Color(0xFFE65100),
        "B" to Color(0xFF1565C0),
        "C" to Color(0xFF6A1B9A),
        "D" to Color(0xFF2E7D32),
        "E" to Color(0xFFC62828),
        "F" to Color(0xFF00695C)
    )
    val warnaTerang = mapOf(
        "A" to Color(0xFFFFF3E0),
        "B" to Color(0xFFE3F2FD),
        "C" to Color(0xFFF3E5F5),
        "D" to Color(0xFFE8F5E9),
        "E" to Color(0xFFFFEBEE),
        "F" to Color(0xFFE0F2F1)
    )
    val emoji = mapOf(
        "A" to "💵",
        "B" to "💰",
        "C" to "🧮",
        "D" to "⚖️",
        "E" to "🏷️",
        "F" to "🛒"
    )
    val namaModul = mapOf(
        "A" to "Jenis-Jenis Uang",
        "B" to "Nilai Uang",
        "C" to "Menghitung Uang",
        "D" to "Kesetaraan Uang",
        "E" to "Harga Barang",
        "F" to "Mana Lebih Murah?"
    )

    fun warna(id: String)      = warna[id]      ?: Color(0xFF1565C0)
    fun warnaTerang(id: String) = warnaTerang[id] ?: Color(0xFFE3F2FD)
    fun emoji(id: String)      = emoji[id]      ?: "📚"
    fun nama(id: String)       = namaModul[id]  ?: id
}