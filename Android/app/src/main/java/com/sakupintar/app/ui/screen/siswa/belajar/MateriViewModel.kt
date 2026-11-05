package com.sakupintar.app.ui.screen.siswa.belajar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KontenHalaman(
    val tipePage:     String,
    val judul:        String,
    val isi:          String,
    val emoji:        String,
    val opsi:         List<String>? = null,
    val jawabanBenar: Int?          = null
)

data class MateriUiState(
    val sedangMemuat:    Boolean             = true,
    val namaModul:       String              = "",
    val halamanSaatIni:  Int                = 0,
    val konten:          List<KontenHalaman> = emptyList(),
    val error:           String?             = null,
    val jawabanDipilih:  Int?               = null,
    val sudahMenjawab:   Boolean            = false,
    val jawabBenar:      Boolean            = false
)

class MateriViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MateriUiState())
    val uiState: StateFlow<MateriUiState> = _uiState.asStateFlow()

    private val namaModulMap = mapOf(
        "A" to "Jenis-Jenis Uang",
        "B" to "Nilai Uang",
        "C" to "Menghitung Uang",
        "D" to "Kesetaraan Uang",
        "E" to "Harga Barang",
        "F" to "Mana Lebih Murah?"
    )

    fun loadMateri(siswaId: Int, kondisiId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemuat = true, error = null, halamanSaatIni = 0) }
            try {
                val response = RetrofitClient.api.getKontenAdaptif(siswaId, kondisiId)
                if (response.isSuccessful) {
                    val body = response.body()!!
                    val konten = body.halaman.map {
                        KontenHalaman(
                            tipePage = it.tipePage,
                            judul    = it.judul,
                            isi      = it.isi,
                            emoji    = it.emoji,
                            opsi         = it.opsi,
                            jawabanBenar = it.jawabanBenar
                        )
                    }
                    _uiState.update {
                        it.copy(
                            sedangMemuat  = false,
                            namaModul     = namaModulMap[kondisiId] ?: "Materi",
                            konten        = konten,
                            halamanSaatIni = 0
                        )
                    }
                } else {
                    _uiState.update { it.copy(
                        sedangMemuat = false,
                        error = "Gagal memuat materi. Pastikan backend berjalan."
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    sedangMemuat = false,
                    error = "Tidak dapat terhubung ke server: ${e.message}"
                )}
            }
        }
    }

    fun halamanSebelumnya() {
        val current = _uiState.value.halamanSaatIni
        if (current > 0) _uiState.update { it.copy(halamanSaatIni = current - 1) }
    }

    fun pilihJawaban(index: Int) {
        val halaman = _uiState.value.konten
            .getOrNull(_uiState.value.halamanSaatIni) ?: return

        val benar = index == halaman.jawabanBenar

        _uiState.update {
            it.copy(
                jawabanDipilih = index,
                sudahMenjawab  = true,
                jawabBenar     = benar
            )
        }
    }

    fun halamanBerikutnya() {
        val current = _uiState.value.halamanSaatIni
        if (current < 3) {
            _uiState.update {
                it.copy(
                    halamanSaatIni = current + 1,
                    jawabanDipilih = null,
                    sudahMenjawab  = false,
                    jawabBenar     = false
                )
            }
        }
    }
}