package com.sakupintar.app.data.api

import com.sakupintar.app.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    //Auth
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    //Siswa
    @GET("siswa/")
    suspend fun getDaftarSiswa(): Response<List<SiswaResponse>>

    @GET("siswa/{id}")
    suspend fun getSiswa(@Path("id") id: Int): Response<SiswaResponse>

    @POST("siswa/")
    suspend fun createSiswa(
        @Header("Authorization") token: String,
        @Body request: SiswaCreateRequest
    ): Response<SiswaResponse>

    @PUT("siswa/{id}")
    suspend fun updateSiswa(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: SiswaCreateRequest
    ): Response<SiswaResponse>

    @PUT("siswa/{id}/parameter")
    suspend fun simpanParameter(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: ParameterRequest
    ): Response<ParameterResponse>

    @Multipart
    @POST("pipeline/profiling")
    suspend fun profilingAsesmen(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("kelas") kelas: RequestBody  // ← tambah ini
    ): Response<ProfilingResponse>

    @Multipart
    @POST("siswa/upload-foto")
    suspend fun uploadFotoSiswa(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<UploadFotoResponse>

    @DELETE("siswa/{id}")
    suspend fun deleteSiswa(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    //Materi
    @GET("materi/kondisi/summary")
    suspend fun getKondisiSummary(
        @Header("Authorization") token: String
    ): Response<KondisiSummaryResponse>

    @GET("materi/kondisi/{kondisiId}/preview")
    suspend fun getKondisiPreview(
        @Header("Authorization") token: String,
        @Path("kondisiId") kondisiId: String
    ): Response<KondisiPreviewResponse>

    @Multipart
    @POST("materi/upload")
    suspend fun uploadMateri(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("deskripsi") deskripsi: RequestBody
    ): Response<Any>

    //Pipeline
    @Multipart
    @POST("pipeline/profiling")
    suspend fun profilingAsesmen(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<ProfilingResponse>

    @GET("pipeline/konten/{siswaId}/{kondisiId}")
    suspend fun getKontenAdaptif(
        @Path("siswaId")   siswaId:   Int,
        @Path("kondisiId") kondisiId: String
    ): Response<KontenAdaptifResponse>

    @POST("pipeline/soal/{siswaId}/{kondisiId}")
    suspend fun generateSoal(
        @Path("siswaId")   siswaId:   Int,
        @Path("kondisiId") kondisiId: String
    ): Response<SoalResponse>

    //Progres
    @POST("progres/latihan")
    suspend fun simpanSesiLatihan(
        @Body request: SesiLatihanRequest
    ): Response<Any>

    @GET("progres/{siswaId}/gamifikasi")
    suspend fun getGamifikasi(
        @Path("siswaId") siswaId: Int
    ): Response<GamifikasiResponse>

    @GET("progres/{siswaId}/ringkasan")
    suspend fun getRingkasanProgres(
        @Header("Authorization") token: String,
        @Path("siswaId") siswaId: Int
    ): Response<RingkasanProgresResponse>

    @GET("progres/{siswaId}/latihan")
    suspend fun getRiwayatLatihan(
        @Path("siswaId") siswaId: Int
    ): Response<List<SesiLatihanItemResponse>>

    @GET("siswa/{siswaId}/materi-status")
    suspend fun getMateriStatus(
        @Path("siswaId") siswaId: Int
    ): Response<MateriStatusResponse>
}


data class UploadFotoResponse(
    @com.google.gson.annotations.SerializedName("foto_url")
    val fotoUrl: String
)