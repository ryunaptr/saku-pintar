package com.sakupintar.app.ui.navigation

object Routes {

    //Umum
    const val SPLASH = "splash"
    const val HOME = "home"

    //Guru
    const val GURU_LOGIN = "guru/login"
    const val GURU_BERANDA = "guru/beranda"
    const val GURU_KELOLA_SISWA = "guru/kelola_siswa"
    const val GURU_TAMBAH_SISWA = "guru/tambah_siswa"
    const val GURU_DETAIL_SISWA = "guru/detail_siswa/{siswaId}"
    const val GURU_KELOLA_MATERI = "guru/kelola_materi"
    const val GURU_UPLOAD_MATERI = "guru/upload_materi"
    const val GURU_DAFTAR_MATERI = "guru/daftar_materi"
    const val GURU_DETAIL_MATERI = "guru/detail_materi/{kondisiId}"
    const val GURU_PROGRES = "guru/progres"
    const val GURU_DETAIL_PROGRES = "guru/detail_progres/{siswaId}"

    //Siswa
    const val SISWA_PILIH_PROFIL = "siswa/pilih_profil"
    const val SISWA_BERANDA = "siswa/beranda/{siswaId}"
    const val SISWA_BELAJAR = "siswa/belajar/{siswaId}"
    const val SISWA_MATERI = "siswa/materi/{siswaId}/{kondisiId}"
    const val SISWA_LATIHAN = "siswa/latihan/{siswaId}"
    const val SISWA_SOAL = "siswa/soal/{siswaId}/{kondisiId}"
    const val SISWA_PENCAPAIAN = "siswa/pencapaian/{siswaId}"

    //Helper
    fun guruDetailSiswa(siswaId: Int) = "guru/detail_siswa/$siswaId"
    fun guruDetailMateri(kondisiId: String) = "guru/detail_materi/$kondisiId"
    fun guruDetailProgres(siswaId: Int) = "guru/detail_progres/$siswaId"
    fun siswaBeranda(siswaId: Int) = "siswa/beranda/$siswaId"
    fun siswaBelajar(siswaId: Int) = "siswa/belajar/$siswaId"
    fun siswaMateri(siswaId: Int, kondisiId: String) = "siswa/materi/$siswaId/$kondisiId"
    fun siswaLatihan(siswaId: Int) = "siswa/latihan/$siswaId"
    fun siswaSoal(siswaId: Int, kondisiId: String) = "siswa/soal/$siswaId/$kondisiId"
    fun siswaPencapaian(siswaId: Int) = "siswa/pencapaian/$siswaId"
}