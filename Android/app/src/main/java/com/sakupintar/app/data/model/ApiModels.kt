package com.sakupintar.app.data.model

import com.google.gson.annotations.SerializedName

//Auth
data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type")   val tokenType: String,
    @SerializedName("nama_guru")    val namaGuru: String
)

//Siswa
data class SiswaResponse(
    val id:        Int,
    val nama:      String,
    val kelas:     String,
    @SerializedName("foto_url") val fotoUrl: String?,
    val parameter: ParameterResponse?
)

data class ParameterResponse(
    @SerializedName("ki_01_tingkat_atensi")        val ki01: String,
    @SerializedName("ki_02_kemampuan_kebahasaan")  val ki02: String,
    @SerializedName("ki_03_kapasitas_memori")      val ki03: String,
    @SerializedName("ki_04_preferensi_persepsi")   val ki04: String,
    @SerializedName("ki_05_metakognitif")          val ki05: String,
    @SerializedName("ki_06_jenjang_kelas")         val ki06: String,
    @SerializedName("ki_07_tingkatan_tunagrahita") val ki07: String
)

data class SiswaCreateRequest(
    val nama:     String,
    val kelas:    String,
    @SerializedName("foto_url") val fotoUrl: String? = null
)

data class ParameterRequest(
    @SerializedName("ki_01_tingkat_atensi")        val ki01: String,
    @SerializedName("ki_02_kemampuan_kebahasaan")  val ki02: String,
    @SerializedName("ki_03_kapasitas_memori")      val ki03: String,
    @SerializedName("ki_04_preferensi_persepsi")   val ki04: String,
    @SerializedName("ki_05_metakognitif")          val ki05: String,
    @SerializedName("ki_06_jenjang_kelas")         val ki06: String,
    @SerializedName("ki_07_tingkatan_tunagrahita") val ki07: String
)

//Materi
data class KondisiSummaryResponse(
    val dokumen: DokumenInfo?,
    val kondisi: Map<String, KondisiStatus>
)

data class DokumenInfo(
    @SerializedName("nama_file")      val namaFile: String,
    @SerializedName("tanggal_upload") val tanggalUpload: String
)

data class MateriRingkasan(
    val kondisiId: String,
    val label: String,
    val tersedia: Boolean
)

data class KondisiStatus(
    val label:    String,
    val tersedia: Boolean
)

data class KondisiPreviewResponse(
    @SerializedName("kondisi_id")   val kondisiId: String,
    val label:                       String,
    @SerializedName("nama_dokumen") val namaDokumen: String,
    val isi:                         String
)

//Pipeline
data class ProfilingResponse(
    val status:    String,
    val parameter: ParameterHasil
)

data class ParameterHasil(
    @SerializedName("ki_01_tingkat_atensi")        val ki01: String? = null,
    @SerializedName("ki_02_kemampuan_kebahasaan")  val ki02: String? = null,
    @SerializedName("ki_03_kapasitas_memori")      val ki03: String? = null,
    @SerializedName("ki_04_preferensi_persepsi")   val ki04: String? = null,
    @SerializedName("ki_05_metakognitif")          val ki05: String? = null,
    @SerializedName("ki_06_jenjang_kelas")         val ki06: String? = null,
    @SerializedName("ki_07_tingkatan_tunagrahita") val ki07: String? = null
)

data class HalamanMateri(
    val tipePage:     String,
    val judul:        String,
    val isi:          String,
    val emoji:        String,
    val opsi:         List<String>? = null,
    val jawabanBenar: Int?          = null
)
data class KontenAdaptifResponse(
    val status:   String,
    val halaman:  List<HalamanMateri>,
    val iterasi:  Int
)

data class MateriStatusResponse(
    @SerializedName("kondisi_tersedia") val kondisiTersedia: List<String>
)

data class SoalItem(
    val pertanyaan: String,
    val pilihan:    List<String>,
    val jawabanBenar: Int
)

data class SoalResponse(
    val status: String,
    val soal:   List<SoalItem>
)

data class SesiLatihanItemResponse(
    @SerializedName("kondisi_id")    val kondisiId:    String,
    @SerializedName("label_kondisi") val labelKondisi: String,
    @SerializedName("jumlah_benar")  val jumlahBenar:  Int,
    val skor:                         Int,
    @SerializedName("created_at")   val createdAt:    String
)

//Progres
data class SesiLatihanRequest(
    @SerializedName("siswa_id")      val siswaId:      Int,
    @SerializedName("kondisi_id")    val kondisiId:    String,
    @SerializedName("label_kondisi") val labelKondisi: String,
    @SerializedName("jumlah_benar")  val jumlahBenar:  Int,
    val skor:                         Int
)

data class GamifikasiResponse(
    @SerializedName("total_poin") val totalPoin: Int,
    @SerializedName("badge_a")    val badgeA: Boolean,
    @SerializedName("badge_b")    val badgeB: Boolean,
    @SerializedName("badge_c")    val badgeC: Boolean,
    @SerializedName("badge_d")    val badgeD: Boolean,
    @SerializedName("badge_e")    val badgeE: Boolean,
    @SerializedName("badge_f")    val badgeF: Boolean
)

data class RingkasanProgresResponse(
    @SerializedName("siswa_id")            val siswaId:             Int,
    val nama:                               String,
    val kelas:                              String,
    @SerializedName("total_poin")          val totalPoin:           Int,
    @SerializedName("kompetensi_dikuasai") val kompetensiDikuasai:  Int,
    @SerializedName("total_badge")         val totalBadge:          Int,
    @SerializedName("jumlah_sesi")         val jumlahSesi:          Int
)