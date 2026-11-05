package com.sakupintar.app.ui.screen.guru.progres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SiswaProgres(
    val id:                  Int,
    val nama:                String,
    val kelas:               String,
    val tingkat:             String,
    val kompetensiDikuasai:  Int,
    val totalPoin:           Int,
    val fotoUrl:             String? = null
)

class ProgresViewModel : ViewModel() {

    private val _daftarProgres = MutableStateFlow<List<SiswaProgres>>(emptyList())
    val daftarProgres: StateFlow<List<SiswaProgres>> = _daftarProgres.asStateFlow()

    private val _filterKelas = MutableStateFlow("Semua")
    val filterKelas: StateFlow<String> = _filterKelas.asStateFlow()

    val daftarProgresTerfilter: StateFlow<List<SiswaProgres>> = combine(
        _daftarProgres, _filterKelas
    ) { daftar, filter ->
        val terurut = daftar.sortedWith(compareBy({ it.kelas }, { it.nama }))
        if (filter == "Semua") terurut
        else terurut.filter { it.kelas == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilterKelas(kelas: String) {
        _filterKelas.value = kelas
    }

    private val _sedangMemuat = MutableStateFlow(true)
    val sedangMemuat: StateFlow<Boolean> = _sedangMemuat.asStateFlow()

    init { loadProgres() }

    fun loadProgres() {
        viewModelScope.launch {
            _sedangMemuat.value = true
            try {
                val siswaResp = RetrofitClient.api.getDaftarSiswa()
                if (siswaResp.isSuccessful) {
                    val daftarSiswa = siswaResp.body() ?: emptyList()

                    val hasilProgres = daftarSiswa.map { siswa ->
                        try {
                            val ringkasanResp = RetrofitClient.api.getRingkasanProgres(
                                SessionManager.bearerToken(), siswa.id
                            )
                            if (ringkasanResp.isSuccessful) {
                                val r = ringkasanResp.body()!!
                                SiswaProgres(
                                    id                 = siswa.id,
                                    nama               = siswa.nama,
                                    kelas              = siswa.kelas,
                                    tingkat            = siswa.parameter
                                        ?.ki07 ?: "Ringan",
                                    kompetensiDikuasai = r.kompetensiDikuasai,
                                    totalPoin          = r.totalPoin,
                                    fotoUrl            = siswa.fotoUrl
                                )
                            } else {
                                SiswaProgres(siswa.id, siswa.nama, siswa.kelas, "Ringan", 0, 0)
                            }
                        } catch (e: Exception) {
                            SiswaProgres(siswa.id, siswa.nama, siswa.kelas, "Ringan", 0, 0)
                        }
                    }
                    _daftarProgres.value = hasilProgres
                }
            } catch (e: Exception) {
                //Biarkan kosong
            } finally {
                _sedangMemuat.value = false
            }
        }
    }
}