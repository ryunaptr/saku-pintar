package com.sakupintar.app.ui.screen.guru.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BerandaGuruUiState(
    val namaGuru:         String = "",
    val jumlahSiswa:      Int    = 0,
    val kondisiTersedia:  Int    = 0
)

class BerandaGuruViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BerandaGuruUiState())
    val uiState: StateFlow<BerandaGuruUiState> = _uiState.asStateFlow()

    init { loadStats() }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(namaGuru = SessionManager.namaGuru) }
            try {
                val siswaResp = RetrofitClient.api.getDaftarSiswa()
                val materiResp = RetrofitClient.api.getKondisiSummary(SessionManager.bearerToken())

                val jumlahSiswa = if (siswaResp.isSuccessful) siswaResp.body()?.size ?: 0 else 0
                val kondisiTersedia = if (materiResp.isSuccessful) {
                    materiResp.body()?.kondisi?.values?.count { it.tersedia } ?: 0
                } else 0

                _uiState.update {
                    it.copy(
                        jumlahSiswa     = jumlahSiswa,
                        kondisiTersedia = kondisiTersedia
                    )
                }
            } catch (e: Exception) {

            }
        }
    }
}