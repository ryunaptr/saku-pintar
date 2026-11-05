package com.sakupintar.app.ui.screen.guru.kelolamateri

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DaftarMateriUiState(
    val sedangMemuat:    Boolean                = true,
    val namaDokumen:     String?                = null,
    val tanggalUpload:   String?                = null,
    val daftarKondisi:   List<KondisiMateriItem> = emptyList()
)

data class KondisiMateriItem(
    val kode:     String,
    val label:    String,
    val tersedia: Boolean
)

class DaftarMateriViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DaftarMateriUiState())
    val uiState: StateFlow<DaftarMateriUiState> = _uiState.asStateFlow()

    init { loadDaftarMateri() }

    fun loadDaftarMateri() {
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemuat = true) }
            try {
                val response = RetrofitClient.api.getKondisiSummary(SessionManager.bearerToken())
                if (response.isSuccessful) {
                    val body = response.body()!!
                    val daftarKondisi = body.kondisi.entries
                        .sortedBy { it.key }
                        .map { (kode, status) ->
                            KondisiMateriItem(kode, status.label, status.tersedia)
                        }
                    _uiState.update {
                        it.copy(
                            sedangMemuat  = false,
                            namaDokumen   = body.dokumen?.namaFile,
                            tanggalUpload = body.dokumen?.tanggalUpload?.take(10),
                            daftarKondisi = daftarKondisi
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangMemuat = false) }
            }
        }
    }
}