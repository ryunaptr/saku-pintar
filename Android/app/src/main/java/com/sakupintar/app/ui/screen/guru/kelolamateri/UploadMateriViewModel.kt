package com.sakupintar.app.ui.screen.guru.kelolamateri

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class UploadMateriUiState {
    object Idle      : UploadMateriUiState()
    object Memproses : UploadMateriUiState()
    data class Berhasil(val pesan: String) : UploadMateriUiState()
    data class Error(val pesan: String)   : UploadMateriUiState()
}

data class UploadMateriFormState(
    val namaFile:      String? = null,
    val pdfUri:        Uri?    = null,
    val state:         UploadMateriUiState = UploadMateriUiState.Idle,
    val pesanSnackbar: String? = null
)

class UploadMateriViewModel : ViewModel() {

    private val _formState = MutableStateFlow(UploadMateriFormState())
    val formState: StateFlow<UploadMateriFormState> = _formState.asStateFlow()

    private var savedContext: Context? = null

    fun setPdfFile(uri: Uri, context: Context) {
        savedContext = context
        val namaFile = context.contentResolver
            .query(uri, null, null, null, null)
            ?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(idx)
            } ?: "dokumen_materi.pdf"

        _formState.update {
            it.copy(pdfUri = uri, namaFile = namaFile, state = UploadMateriUiState.Idle)
        }
    }

    fun uploadMateri() {
        val ctx = savedContext ?: return
        val uri = _formState.value.pdfUri ?: return

        viewModelScope.launch {
            _formState.update { it.copy(state = UploadMateriUiState.Memproses) }
            try {
                val inputStream = ctx.contentResolver.openInputStream(uri)
                    ?: throw Exception("Tidak bisa membuka file PDF")
                val tempFile = File(ctx.cacheDir, "materi_upload_temp.pdf")
                FileOutputStream(tempFile).use { out -> inputStream.copyTo(out) }

                val requestBody      = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val originalFileName = _formState.value.namaFile ?: "materi.pdf"
                val multipart        = MultipartBody.Part.createFormData(
                    "file", originalFileName, requestBody
                )
                val deskripsi = "Materi Aritmetika Sosial"
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.api.uploadMateri(
                    token     = SessionManager.bearerToken(),
                    file      = multipart,
                    deskripsi = deskripsi
                )

                if (response.isSuccessful) {
                    _formState.update {
                        it.copy(
                            state         = UploadMateriUiState.Berhasil(
                                "Materi berhasil diproses dan disimpan. " +
                                        "Siswa dapat mulai belajar materi ini."
                            ),
                            pesanSnackbar = "✓ Materi berhasil disimpan ke sistem!"
                        )
                    }
                } else {
                    _formState.update {
                        it.copy(state = UploadMateriUiState.Error(
                            "Upload gagal (${response.code()}). Pastikan format PDF benar."
                        ))
                    }
                }
                tempFile.delete()

            } catch (e: Exception) {
                _formState.update {
                    it.copy(state = UploadMateriUiState.Error(
                        "Tidak dapat terhubung ke server: ${e.message}"
                    ))
                }
            }
        }
    }

    fun clearSnackbar() { _formState.update { it.copy(pesanSnackbar = null) } }
    fun reset()         { _formState.update { UploadMateriFormState() } }
}