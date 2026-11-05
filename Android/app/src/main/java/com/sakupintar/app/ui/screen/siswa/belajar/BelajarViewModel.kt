package com.sakupintar.app.ui.screen.siswa.belajar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ModulBelajar(
    val kondisiId:    String,
    val nama:         String,
    val emoji:        String,
    val sudahSelesai: Boolean
)

class BelajarViewModel : ViewModel() {

    private val _daftarModul = MutableStateFlow<List<ModulBelajar>>(emptyList())
    val daftarModul: StateFlow<List<ModulBelajar>> = _daftarModul.asStateFlow()

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
                val statusResponse = RetrofitClient.api.getMateriStatus(siswaId)
                val kondisiTersedia = if (statusResponse.isSuccessful) {
                    statusResponse.body()?.kondisiTersedia ?: emptyList()
                } else emptyList()

                _daftarModul.value = templateModul.map { (kode, nama, emoji) ->
                    ModulBelajar(
                        kondisiId    = kode,
                        nama         = nama,
                        emoji        = emoji,
                        sudahSelesai = kondisiTersedia.contains(kode)
                    )
                }
            } catch (e: Exception) {
                _daftarModul.value = templateModul.map { (kode, nama, emoji) ->
                    ModulBelajar(kode, nama, emoji, false)
                }
            }
        }
    }
}