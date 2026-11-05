package com.sakupintar.app.ui.screen.guru.kelolasiswa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SiswaItem(
    val id:                  Int,
    val nama:                String,
    val kelas:               String,
    val tingkat:             String,
    val sudahAdaParameter:   Boolean,
    val fotoUrl:           String? = null
)

class KelolaSiswaViewModel : ViewModel() {

    private val _daftarSiswa = MutableStateFlow<List<SiswaItem>>(emptyList())
    val daftarSiswa: StateFlow<List<SiswaItem>> = _daftarSiswa.asStateFlow()

    private val _sedangMemuat = MutableStateFlow(true)
    val sedangMemuat: StateFlow<Boolean> = _sedangMemuat.asStateFlow()

    init { loadSiswa() }

    fun loadSiswa() {
        viewModelScope.launch {
            _sedangMemuat.value = true
            try {
                val response = RetrofitClient.api.getDaftarSiswa()
                if (response.isSuccessful) {
                    val hasil = response.body()?.map {
                        SiswaItem(
                            id                = it.id,
                            nama              = it.nama,
                            kelas             = it.kelas,
                            tingkat           = it.parameter?.ki07 ?: "Ringan",
                            sudahAdaParameter = it.parameter != null,
                            fotoUrl           = it.fotoUrl
                        )
                    }?.sortedWith(
                        compareBy(
                            { when (it.kelas) { "Kelas 7" -> 0; "Kelas 8" -> 1; "Kelas 9" -> 2; else -> 3 } },
                            { it.nama }
                        )
                    ) ?: emptyList()

                    _semuaSiswa.value = hasil
                    terapkanFilter()
                }
            } catch (e: Exception) {

            } finally {
                _sedangMemuat.value = false
            }
        }
    }

    private val _siswaYangAkanDihapus = MutableStateFlow<SiswaItem?>(null)
    val siswaYangAkanDihapus: StateFlow<SiswaItem?> = _siswaYangAkanDihapus.asStateFlow()

    private val _pesanError = MutableStateFlow<String?>(null)
    val pesanError: StateFlow<String?> = _pesanError.asStateFlow()

    private val _filterKelas = MutableStateFlow("Semua")
    val filterKelas: StateFlow<String> = _filterKelas.asStateFlow()

    private val _semuaSiswa = MutableStateFlow<List<SiswaItem>>(emptyList())

    fun setFilterKelas(kelas: String) {
        _filterKelas.value = kelas
        terapkanFilter()
    }

    private fun terapkanFilter() {
        val semua = _semuaSiswa.value
        _daftarSiswa.value = if (_filterKelas.value == "Semua") {
            semua
        } else {
            semua.filter { it.kelas == _filterKelas.value }
        }
    }

    fun konfirmasiHapus(siswa: SiswaItem) {
        _siswaYangAkanDihapus.value = siswa
    }

    fun batalHapus() {
        _siswaYangAkanDihapus.value = null
    }

    fun hapusSiswa(siswaId: Int) {
        viewModelScope.launch {
            if (!SessionManager.isLoggedIn()) {
                _pesanError.value = "Sesi guru tidak ditemukan, silakan login ulang"
                _siswaYangAkanDihapus.value = null
                return@launch
            }
            try {
                val response = RetrofitClient.api.deleteSiswa(
                    token = SessionManager.bearerToken(),
                    id    = siswaId
                )
                if (response.isSuccessful) {
                    _siswaYangAkanDihapus.value = null
                    loadSiswa()
                } else {
                    _pesanError.value = "Gagal menghapus siswa (kode: ${response.code()})"
                    _siswaYangAkanDihapus.value = null
                }
            } catch (e: Exception) {
                _pesanError.value = "Tidak dapat terhubung ke server"
                _siswaYangAkanDihapus.value = null
            }
        }
    }

    fun resetError() {
        _pesanError.value = null
    }
}

