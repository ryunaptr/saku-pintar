package com.sakupintar.app.ui.screen.siswa.latihan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ModulLatihan(
    val kondisiId:       String,
    val nama:            String,
    val emoji:           String,
    val jumlahPercobaan: Int
)

class LatihanViewModel : ViewModel() {

    private val _daftarModul = MutableStateFlow<List<ModulLatihan>>(emptyList())
    val daftarModul: StateFlow<List<ModulLatihan>> = _daftarModul.asStateFlow()

    private val templateModul = listOf(
        Triple("A", "Jenis-Jenis Uang",  "💵"),
        Triple("B", "Nilai Uang",         "💰"),
        Triple("C", "Menghitung Uang",    "🧮"),
        Triple("D", "Kesetaraan Uang",    "⚖️"),
        Triple("E", "Harga Barang",       "🏷️"),
        Triple("F", "Mana Lebih Murah?",  "🛒")
    )

    fun loadModul(siswaId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getRiwayatLatihan(siswaId)
                if (response.isSuccessful) {
                    val riwayat = response.body() ?: emptyList()
                    //Hitung percobaan per kondisi
                    val percobaanMap = riwayat
                        .groupBy { it.kondisiId }
                        .mapValues { it.value.size }

                    _daftarModul.value = templateModul.map { (kode, nama, emoji) ->
                        ModulLatihan(kode, nama, emoji, percobaanMap[kode] ?: 0)
                    }
                }
            } catch (e: Exception) {
                _daftarModul.value = templateModul.map { (kode, nama, emoji) ->
                    ModulLatihan(kode, nama, emoji, 0)
                }
            }
        }
    }
}