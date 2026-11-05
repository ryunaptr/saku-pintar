package com.sakupintar.app.ui.screen.guru.progres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KompetensiProgres(val kode: String, val label: String, val dikuasai: Boolean)
data class RiwayatLatihan(
    val tanggal:        String,
    val labelKompetensi: String,
    val jumlahBenar:    Int,
    val skor:           Int
)
data class DetailProgresUiState(
    val sedangMemuat:    Boolean               = true,
    val nama:            String                = "",
    val kelas:           String                = "",
    val tingkat:         String                = "",
    val fotoUrl:          String?                  = null,
    val totalPoin:       Int                   = 0,
    val totalBadge:      Int                   = 0,
    val daftarKompetensi: List<KompetensiProgres> = emptyList(),
    val riwayatLatihan:  List<RiwayatLatihan>  = emptyList()
)

class DetailProgresViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetailProgresUiState())
    val uiState: StateFlow<DetailProgresUiState> = _uiState.asStateFlow()

    private val labelKondisi = mapOf(
        "A" to "Mengenal Jenis Uang",
        "B" to "Mengenal Nilai Nominal Uang",
        "C" to "Menghitung Uang",
        "D" to "Kesetaraan Nilai Uang",
        "E" to "Mengidentifikasi Harga Barang",
        "F" to "Membandingkan Harga Barang"
    )

    fun loadProgres(siswaId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemuat = true) }
            try {
                val siswaResp = RetrofitClient.api.getSiswa(siswaId)
                val gamiResp  = RetrofitClient.api.getGamifikasi(siswaId)
                val latihanResp = RetrofitClient.api.getRiwayatLatihan(siswaId)

                if (siswaResp.isSuccessful && gamiResp.isSuccessful) {
                    val siswa = siswaResp.body()!!
                    val gami  = gamiResp.body()!!
                    val latihan = if (latihanResp.isSuccessful) latihanResp.body() ?: emptyList() else emptyList()

                    val badges = listOf(gami.badgeA, gami.badgeB, gami.badgeC,
                        gami.badgeD, gami.badgeE, gami.badgeF)
                    val kodeList = listOf("A","B","C","D","E","F")

                    val kompetensiList = kodeList.mapIndexed { idx, kode ->
                        KompetensiProgres(kode, labelKondisi[kode] ?: kode, badges[idx])
                    }

                    val riwayatList = latihan.take(10).map { s ->
                        RiwayatLatihan(
                            tanggal         = s.createdAt.take(10),
                            labelKompetensi = s.labelKondisi,
                            jumlahBenar     = s.jumlahBenar,
                            skor            = s.skor
                        )
                    }

                    _uiState.update {
                        it.copy(
                            sedangMemuat     = false,
                            nama             = siswa.nama,
                            kelas            = siswa.kelas,
                            tingkat          = siswa.parameter?.ki07 ?: "Ringan",
                            fotoUrl          = siswa.fotoUrl,
                            totalPoin        = gami.totalPoin,
                            totalBadge       = badges.count { b -> b },
                            daftarKompetensi = kompetensiList,
                            riwayatLatihan   = riwayatList
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangMemuat = false) }
            }
        }
    }
}