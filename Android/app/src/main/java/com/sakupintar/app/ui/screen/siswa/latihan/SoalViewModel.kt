package com.sakupintar.app.ui.screen.siswa.latihan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.model.SesiLatihanRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SoalItem(
    val pertanyaan:   String,
    val pilihan:      List<String>,
    val jawabanBenar: Int
)

data class SoalUiState(
    val sedangMemuat:       Boolean        = true,
    val soalList:           List<SoalItem> = emptyList(),
    val soalSaatIni:        Int            = 0,
    val jawabanDipilih:     Int?           = null,
    val sudahJawab:         Boolean        = false,
    val jawabanBenarSoalIni: Boolean       = false,
    val jumlahBenar:        Int            = 0,
    val selesai:            Boolean        = false,
    val error:              String?        = null
)

class SoalViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SoalUiState())
    val uiState: StateFlow<SoalUiState> = _uiState.asStateFlow()

    private val namaModulMap = mapOf(
        "A" to "Jenis-Jenis Uang",
        "B" to "Nilai Uang",
        "C" to "Menghitung Uang",
        "D" to "Kesetaraan Uang",
        "E" to "Harga Barang",
        "F" to "Mana Lebih Murah?"
    )

    fun loadSoal(siswaId: Int, kondisiId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    sedangMemuat        = true,
                    error               = null,
                    soalSaatIni         = 0,
                    jawabanDipilih      = null,
                    sudahJawab          = false,
                    jawabanBenarSoalIni = false,
                    jumlahBenar         = 0,
                    selesai             = false
                )
            }
            try {
                val response = RetrofitClient.api.generateSoal(siswaId, kondisiId)
                if (response.isSuccessful) {
                    val soal = response.body()!!.soal.map {
                        SoalItem(it.pertanyaan, it.pilihan, it.jawabanBenar)
                    }
                    _uiState.update {
                        it.copy(sedangMemuat = false, soalList = soal)
                    }
                } else {
                    _uiState.update { it.copy(sedangMemuat = false, error = "Gagal memuat soal dari server") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangMemuat = false, error = "Tidak dapat terhubung ke server") }
            }
        }
    }

    //Dipanggil saat siswa tap pilihan jawaban
    fun pilihJawaban(index: Int) {
        val soal  = _uiState.value.soalList.getOrNull(_uiState.value.soalSaatIni) ?: return
        val benar = index == soal.jawabanBenar
        _uiState.update {
            it.copy(
                jawabanDipilih       = index,
                sudahJawab           = true,
                jawabanBenarSoalIni  = benar,
                jumlahBenar          = if (benar) it.jumlahBenar + 1 else it.jumlahBenar
            )
        }
    }

    //Dipanggil saat siswa tap tombol "Soal Berikutnya"
    fun soalBerikutnya(siswaId: Int, kondisiId: String) {
        val current = _uiState.value.soalSaatIni
        if (current < 4) {
            _uiState.update {
                it.copy(
                    soalSaatIni         = current + 1,
                    jawabanDipilih      = null,
                    sudahJawab          = false,
                    jawabanBenarSoalIni = false
                )
            }
        } else {
            simpanHasil(siswaId, kondisiId)
        }
    }

    private fun simpanHasil(siswaId: Int, kondisiId: String) {
        val state = _uiState.value
        val skor  = state.jumlahBenar * 20

        viewModelScope.launch {
            try {
                RetrofitClient.api.simpanSesiLatihan(
                    SesiLatihanRequest(
                        siswaId      = siswaId,
                        kondisiId    = kondisiId,
                        labelKondisi = namaModulMap[kondisiId] ?: kondisiId,
                        jumlahBenar  = state.jumlahBenar,
                        skor         = skor
                    )
                )
            } catch (e: Exception) {

            } finally {
                _uiState.update { it.copy(selesai = true) }
            }
        }
    }
}