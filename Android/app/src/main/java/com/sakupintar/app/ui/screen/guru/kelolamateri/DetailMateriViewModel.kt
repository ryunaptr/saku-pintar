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

data class DetailMateriUiState(
    val sedangMemuat: Boolean = true,
    val kondisiId:    String  = "",
    val label:        String  = "",
    val namaDokumen:  String  = "",
    val isi:          String  = "",
    val error:        String? = null
)

class DetailMateriViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetailMateriUiState())
    val uiState: StateFlow<DetailMateriUiState> = _uiState.asStateFlow()

    fun loadDetail(kondisiId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemuat = true, error = null) }
            try {
                val response = RetrofitClient.api.getKondisiPreview(
                    SessionManager.bearerToken(), kondisiId
                )
                if (response.isSuccessful) {
                    val body = response.body()!!
                    _uiState.update {
                        it.copy(
                            sedangMemuat = false,
                            kondisiId    = body.kondisiId,
                            label        = body.label,
                            namaDokumen  = body.namaDokumen,
                            isi          = body.isi
                        )
                    }
                } else {
                    _uiState.update { it.copy(
                        sedangMemuat = false,
                        error = "Gagal memuat konten kondisi $kondisiId"
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    sedangMemuat = false,
                    error = "Tidak dapat terhubung ke server"
                )}
            }
        }
    }
}