package com.sakupintar.app.ui.screen.siswa.pencapaian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LencanaItem(
    val kondisiId: String,
    val nama: String,
    val emoji: String,
    val diperoleh: Boolean
)

data class PencapaianUiState(
    val sedangMemuat: Boolean = true,
    val namaSiswa: String = "",
    val totalPoin: Int = 0,
    val daftarLencana: List<LencanaItem> = emptyList()
)

class PencapaianViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PencapaianUiState())
    val uiState: StateFlow<PencapaianUiState> = _uiState.asStateFlow()

    fun loadPencapaian(siswaId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemuat = true) }
            try {
                val siswaResp = RetrofitClient.api.getSiswa(siswaId)
                val gamiResp  = RetrofitClient.api.getGamifikasi(siswaId)

                if (siswaResp.isSuccessful && gamiResp.isSuccessful) {
                    val siswa = siswaResp.body()!!
                    val gami  = gamiResp.body()!!

                    val daftarLencana = listOf(
                        LencanaItem("A", "Jago Jenis Uang",   "💵", gami.badgeA),
                        LencanaItem("B", "Jago Nilai Uang",    "💰", gami.badgeB),
                        LencanaItem("C", "Jago Menghitung",    "🧮", gami.badgeC),
                        LencanaItem("D", "Jago Kesetaraan",    "⚖️", gami.badgeD),
                        LencanaItem("E", "Jago Harga Barang",  "🏷️", gami.badgeE),
                        LencanaItem("F", "Jago Belanja",       "🛒", gami.badgeF)
                    )

                    _uiState.update {
                        it.copy(
                            sedangMemuat  = false,
                            namaSiswa     = siswa.nama,
                            totalPoin     = gami.totalPoin,
                            daftarLencana = daftarLencana
                        )
                    }
                } else {
                    _uiState.update { it.copy(sedangMemuat = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangMemuat = false) }
            }
        }
    }
}