package com.sakupintar.app.ui.screen.siswa.pilihprofil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfilSiswa(
    val id:    Int,
    val nama:  String,
    val kelas: String,
    val fotoUrl: String? = null
)

class PilihProfilViewModel : ViewModel() {

    private val _daftarSiswa = MutableStateFlow<List<ProfilSiswa>>(emptyList())
    val daftarSiswa: StateFlow<List<ProfilSiswa>> = _daftarSiswa.asStateFlow()

    private val _sedangMemuat = MutableStateFlow(true)
    val sedangMemuat: StateFlow<Boolean> = _sedangMemuat.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { loadSiswa() }

    fun loadSiswa() {
        viewModelScope.launch {
            _sedangMemuat.value = true
            _error.value = null
            try {
                val response = RetrofitClient.api.getDaftarSiswa()
                if (response.isSuccessful) {
                    _daftarSiswa.value = response.body()?.map {
                        ProfilSiswa(it.id, it.nama, it.kelas, it.fotoUrl)
                    }?.sortedBy { it.nama } ?: emptyList()
                } else {
                    _error.value = "Gagal memuat daftar siswa"
                }
            } catch (e: Exception) {
                _error.value = "Tidak dapat terhubung ke server"
            } finally {
                _sedangMemuat.value = false
            }
        }
    }
}