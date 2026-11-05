package com.sakupintar.app.ui.screen.guru.kelolamateri

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.local.SessionManager
import com.sakupintar.app.data.model.MateriRingkasan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KelolaMateriUiState(
    val sedangMemuat: Boolean        = true,
    val namaFile: String             = "",
    val tanggalUpload: String        = "",
    val daftarKondisi: List<MateriRingkasan> = emptyList(),
    val error: String?               = null
)

class KelolaMateriViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(KelolaMateriUiState())
    val uiState: StateFlow<KelolaMateriUiState> = _uiState.asStateFlow()

    init { loadSummary() }

    fun loadSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemuat = true, error = null) }
            try {
                val response = RetrofitClient.api.getKondisiSummary(
                    token = SessionManager.bearerToken()
                )
                if (response.isSuccessful) {
                    val body = response.body()!!
                    val daftarKondisi = body.kondisi.map { (id, detail) ->
                        MateriRingkasan(
                            kondisiId = id,
                            label     = detail.label,
                            tersedia  = detail.tersedia
                        )
                    }.sortedBy { it.kondisiId }

                    _uiState.update {
                        it.copy(
                            sedangMemuat  = false,
                            namaFile      = body.dokumen?.namaFile ?: "Belum ada dokumen",
                            tanggalUpload = body.dokumen?.tanggalUpload ?: "",
                            daftarKondisi = daftarKondisi
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            sedangMemuat = false,
                            error = "Gagal memuat data materi (${response.code()})"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        sedangMemuat = false,
                        error = "Tidak dapat terhubung ke server: ${e.message}"
                    )
                }
            }
        }
    }
}