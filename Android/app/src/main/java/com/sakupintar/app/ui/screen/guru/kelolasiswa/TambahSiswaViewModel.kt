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

data class ParameterInstruksional(
    val tingkatAtensi:        String = "",
    val kemampuanKebahasaan:  String = "",
    val kapasitasMemori:      String = "",
    val preferensiPersepsi:   String = "",
    val metakognitif:         String = "",
    val jenjangKelas:         String = "",
    val tingkatanTunagrahita: String = ""
)

data class TambahSiswaUiState(
    val nama:                 String                = "",
    val kelas:                String                = "",
    //Foto
    val fotoUri:              Uri?                  = null,
    val namaFileFoto:         String?               = null,
    val sedangUploadFoto:     Boolean               = false,
    val fotoUrl:              String?               = null,
    //PDF asesmen
    val namaFilePdf:          String?               = null,
    val pdfUri:               Uri?                  = null,
    val sedangMemproses:      Boolean               = false,
    val parameterSudahDimuat: Boolean               = false,
    val parameter:            ParameterInstruksional = ParameterInstruksional(),
    //Simpan
    val sedangMenyimpan:      Boolean               = false,
    val pesanSnackbar:        String?               = null,
    val berhasilDisimpan:     Boolean               = false,
    val error:                String?               = null
)

class TambahSiswaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TambahSiswaUiState())
    val uiState: StateFlow<TambahSiswaUiState> = _uiState.asStateFlow()

    fun updateNama(nilai: String) = _uiState.update { it.copy(nama = nilai) }

    fun updateKelas(nilai: String) {
        _uiState.update {
            it.copy(kelas = nilai, parameter = it.parameter.copy(jenjangKelas = nilai))
        }
    }

    //Foto
    fun setFotoFile(uri: Uri, context: Context) {
        val namaFile = context.contentResolver
            .query(uri, null, null, null, null)
            ?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(idx)
            } ?: "foto_siswa.jpg"

        _uiState.update {
            it.copy(fotoUri = uri, namaFileFoto = namaFile, sedangUploadFoto = true, error = null)
        }
        uploadFoto(uri, namaFile, context)
    }

    private fun uploadFoto(uri: Uri, namaFile: String, context: Context) {
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
                    val url = response.body()?.fotoUrl
                    _uiState.update { it.copy(sedangUploadFoto = false, fotoUrl = url) }
                } else {
                    _uiState.update {
                        it.copy(sedangUploadFoto = false, fotoUri = null, namaFileFoto = null,
                            error = "Gagal upload foto (${response.code()})")
                    }
                }
                tempFile.delete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(sedangUploadFoto = false, fotoUri = null, namaFileFoto = null,
                        error = "Error upload foto: ${e.message}")
                }
            }
        }
    }

    //PDF Asesmen

    fun setPdfFile(uri: Uri, context: Context) {
        val namaFile = context.contentResolver
            .query(uri, null, null, null, null)
            ?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(idx)
            } ?: "dokumen_asesmen.pdf"

        _uiState.update {
            it.copy(pdfUri = uri, namaFilePdf = namaFile,
                parameterSudahDimuat = false, error = null)
        }
        prosesAsesmen(uri, context)
    }

    private fun prosesAsesmen(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMemproses = true, error = null) }
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Tidak bisa membuka file PDF")
                val tempFile = File(context.cacheDir, "asesmen_temp.pdf")
                FileOutputStream(tempFile).use { out -> inputStream.copyTo(out) }

                val requestBody = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val multipart   = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

                val response = RetrofitClient.api.profilingAsesmen(
                    token = SessionManager.bearerToken(),
                    file  = multipart
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        _uiState.update { it.copy(sedangMemproses = false, error = "Response kosong.") }
                        return@launch
                    }
                    val p = body.parameter
                    _uiState.update {
                        it.copy(
                            sedangMemproses      = false,
                            parameterSudahDimuat = true,
                            parameter = ParameterInstruksional(
                                tingkatAtensi        = p.ki01 ?: "Sedang",
                                kemampuanKebahasaan  = p.ki02 ?: "Kalimat Sederhana",
                                kapasitasMemori      = p.ki03 ?: "Pengulangan Sedang",
                                preferensiPersepsi   = p.ki04 ?: "Visual Dominan",
                                metakognitif         = p.ki05 ?: "Konfirmasi Akhir Segmen",
                                jenjangKelas         = _uiState.value.kelas.ifBlank { p.ki06 ?: "Kelas 7" },
                                tingkatanTunagrahita = p.ki07 ?: "Ringan"
                            )
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(sedangMemproses = false,
                            error = "Gagal memproses dokumen (${response.code()})")
                    }
                }
                tempFile.delete()
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangMemproses = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun updateParameter(blok: ParameterInstruksional.() -> ParameterInstruksional) =
        _uiState.update { it.copy(parameter = it.parameter.blok()) }

    //Simpan
    fun simpanSiswa() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(sedangMenyimpan = true, error = null) }
            try {
                val siswaResp = RetrofitClient.api.createSiswa(
                    token   = SessionManager.bearerToken(),
                    request = SiswaCreateRequest(
                        nama    = state.nama,
                        kelas   = state.kelas,
                        fotoUrl = state.fotoUrl
                    )
                )
                if (!siswaResp.isSuccessful) {
                    _uiState.update { it.copy(sedangMenyimpan = false, error = "Gagal membuat profil siswa.") }
                    return@launch
                }
                val siswaId = siswaResp.body()!!.id
                val p = state.parameter
                RetrofitClient.api.simpanParameter(
                    token   = SessionManager.bearerToken(),
                    id      = siswaId,
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
                    it.copy(sedangMenyimpan = false,
                        pesanSnackbar = "✓ Siswa ${state.nama} berhasil ditambahkan!",
                        berhasilDisimpan = true)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(sedangMenyimpan = false, error = "Tidak dapat terhubung ke server.") }
            }
        }
    }

    fun clearSnackbar() = _uiState.update { it.copy(pesanSnackbar = null) }
}