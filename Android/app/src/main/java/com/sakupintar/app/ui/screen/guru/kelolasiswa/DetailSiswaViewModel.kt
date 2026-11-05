package com.sakupintar.app.ui.screen.guru.kelolasiswa

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.local.SessionManager
import com.sakupintar.app.data.model.ParameterRequest
import com.sakupintar.app.data.model.SiswaCreateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import okhttp3.RequestBody.Companion.toRequestBody

data class DetailSiswaUiState(
    val sedangMemuat:     Boolean                = true,
    val nama:             String                 = "",
    val kelas:            String                 = "",
    val fotoUrl:          String?                = null,
    val namaFileFoto:     String?                = null,
    val sedangUploadFoto: Boolean                = false,
    val parameter:        ParameterInstruksional = ParameterInstruksional(),
    val sedangMenyimpan:  Boolean                = false,
    val pesanSnackbar:    String?                = null,
    val error:            String?                = null,
    val sedangProfiling:    Boolean                = false,
    val namaFilePdf:        String?                = null
)

class DetailSiswaViewModel : ViewModel() {

    private var siswaIdAktif: Int = 0
    private val _uiState = MutableStateFlow(DetailSiswaUiState())
    val uiState: StateFlow<DetailSiswaUiState> = _uiState.asStateFlow()

    fun loadSiswa(siswaId: Int) {
        siswaIdAktif = siswaId
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemuat = true, error = null) }
            try {
                val response = RetrofitClient.api.getSiswa(siswaId)
                if (response.isSuccessful) {
                    val siswa = response.body()!!
                    val p     = siswa.parameter
                    _uiState.update {
                        it.copy(
                            sedangMemuat = false,
                            nama         = siswa.nama,
                            kelas        = siswa.kelas,
                            fotoUrl      = siswa.fotoUrl,
                            parameter    = if (p != null) ParameterInstruksional(
                                tingkatAtensi        = p.ki01,
                                kemampuanKebahasaan  = p.ki02,
                                kapasitasMemori      = p.ki03,
                                preferensiPersepsi   = p.ki04,
                                metakognitif         = p.ki05,
                                jenjangKelas         = p.ki06,
                                tingkatanTunagrahita = p.ki07
                            ) else ParameterInstruksional()
                        )
                    }
                } else {
                    _uiState.update { it.copy(sedangMemuat = false, error = "Gagal memuat data siswa.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangMemuat = false, error = "Tidak dapat terhubung ke server.") }
            }
        }
    }

    fun updateNama(nilai: String) = _uiState.update { it.copy(nama = nilai) }

    fun updateKelas(nilai: String) {
        _uiState.update {
            it.copy(kelas = nilai, parameter = it.parameter.copy(jenjangKelas = nilai))
        }
    }

    fun updateParameter(blok: ParameterInstruksional.() -> ParameterInstruksional) =
        _uiState.update { it.copy(parameter = it.parameter.blok()) }

    fun setFotoFile(uri: Uri, context: Context) {
        val namaFile = context.contentResolver
            .query(uri, null, null, null, null)
            ?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(idx)
            } ?: "foto_siswa.jpg"

        _uiState.update { it.copy(namaFileFoto = namaFile, sedangUploadFoto = true, error = null) }

        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Tidak bisa membuka file foto")
                val ekstensi = namaFile.substringAfterLast(".", "jpg")
                val tempFile = File(context.cacheDir, "foto_temp.$ekstensi")
                FileOutputStream(tempFile).use { out -> inputStream.copyTo(out) }

                val mimeType = when (ekstensi.lowercase()) {
                    "png"  -> "image/png"
                    "webp" -> "image/webp"
                    else   -> "image/jpeg"
                }
                val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val multipart   = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

                val response = RetrofitClient.api.uploadFotoSiswa(
                    token = SessionManager.bearerToken(),
                    file  = multipart
                )
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(sedangUploadFoto = false, fotoUrl = response.body()?.fotoUrl)
                    }
                } else {
                    _uiState.update {
                        it.copy(sedangUploadFoto = false, namaFileFoto = null,
                            error = "Gagal upload foto (${response.code()})")
                    }
                }
                tempFile.delete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(sedangUploadFoto = false, namaFileFoto = null,
                        error = "Error upload foto: ${e.message}")
                }
            }
        }
    }

    fun setPdfUntukReprofiling(uri: Uri, context: Context) {
        val namaFile = context.contentResolver
            .query(uri, null, null, null, null)
            ?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(idx)
            } ?: "asesmen.pdf"

        _uiState.update { it.copy(namaFilePdf = namaFile, sedangProfiling = true, error = null) }

        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Tidak bisa membuka file PDF")
                val tempFile = File(context.cacheDir, "reprofiling_temp.pdf")
                FileOutputStream(tempFile).use { out -> inputStream.copyTo(out) }

                val requestBody = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val multipart   = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

                val kelasBody = _uiState.value.kelas
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.api.profilingAsesmen(
                    token = SessionManager.bearerToken(),
                    file  = multipart,
                    kelas = kelasBody
                )

                if (response.isSuccessful) {
                    val p = response.body()?.parameter
                    if (p != null) {
                        _uiState.update {
                            it.copy(
                                sedangProfiling = false,
                                pesanSnackbar   = "✓ Parameter berhasil diperbarui dari dokumen",
                                parameter = ParameterInstruksional(
                                    tingkatAtensi        = p.ki01 ?: it.parameter.tingkatAtensi,
                                    kemampuanKebahasaan  = p.ki02 ?: it.parameter.kemampuanKebahasaan,
                                    kapasitasMemori      = p.ki03 ?: it.parameter.kapasitasMemori,
                                    preferensiPersepsi   = p.ki04 ?: it.parameter.preferensiPersepsi,
                                    metakognitif         = p.ki05 ?: it.parameter.metakognitif,
                                    jenjangKelas         = _uiState.value.kelas.ifBlank { p.ki06 ?: it.parameter.jenjangKelas },
                                    tingkatanTunagrahita = p.ki07 ?: it.parameter.tingkatanTunagrahita
                                )
                            )
                        }
                    } else {
                        _uiState.update { it.copy(sedangProfiling = false, error = "Response profiling kosong.") }
                    }
                } else {
                    _uiState.update {
                        it.copy(sedangProfiling = false, error = "Gagal memproses dokumen (${response.code()})")
                    }
                }
                tempFile.delete()
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangProfiling = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun simpanPerubahan() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMenyimpan = true, error = null) }
            try {
                RetrofitClient.api.updateSiswa(
                    token   = SessionManager.bearerToken(),
                    id      = siswaIdAktif,
                    request = SiswaCreateRequest(
                        nama    = state.nama,
                        kelas   = state.kelas,
                        fotoUrl = state.fotoUrl
                    )
                )
                val p = state.parameter
                RetrofitClient.api.simpanParameter(
                    token   = SessionManager.bearerToken(),
                    id      = siswaIdAktif,
                    request = ParameterRequest(
                        ki01 = p.tingkatAtensi,
                        ki02 = p.kemampuanKebahasaan,
                        ki03 = p.kapasitasMemori,
                        ki04 = p.preferensiPersepsi,
                        ki05 = p.metakognitif,
                        ki06 = p.jenjangKelas,
                        ki07 = p.tingkatanTunagrahita
                    )
                )
                _uiState.update {
                    it.copy(sedangMenyimpan = false, pesanSnackbar = "✓ Data siswa berhasil disimpan!")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangMenyimpan = false, error = "Gagal menyimpan perubahan.") }
            }
        }
    }

    fun clearSnackbar() = _uiState.update { it.copy(pesanSnackbar = null) }
}