package com.sakupintar.app.ui.screen.siswa.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BerandaSiswaUiState(
    val sedangMemuat: Boolean = true,
    val nama:         String  = "",
    val kelas:        String  = "",
    val totalPoin:    Int     = 0,
    val error:        String? = null
)

class BerandaSiswaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BerandaSiswaUiState())
    val uiState: StateFlow<BerandaSiswaUiState> = _uiState.asStateFlow()

    fun loadSiswa(siswaId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemuat = true, error = null) }
            try {
                val siswaResp = RetrofitClient.api.getSiswa(siswaId)
                val gamiResp  = RetrofitClient.api.getGamifikasi(siswaId)

                if (siswaResp.isSuccessful) {
                    val siswa = siswaResp.body()!!
                    val poin  = if (gamiResp.isSuccessful)
                        gamiResp.body()?.totalPoin ?: 0 else 0

                    _uiState.update {
                        it.copy(
                            sedangMemuat = false,
                            nama         = siswa.nama,
                            kelas        = siswa.kelas,
                            totalPoin    = poin
                        )
                    }
                } else {
                    _uiState.update { it.copy(
                        sedangMemuat = false,
                        error        = "Gagal memuat data siswa"
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    sedangMemuat = false,
                    error        = "Tidak dapat terhubung ke server"
                )}
            }
        }
    }
}