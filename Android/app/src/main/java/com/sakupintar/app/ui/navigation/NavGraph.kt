package com.sakupintar.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sakupintar.app.ui.screen.guru.beranda.BerandaGuruScreen
import com.sakupintar.app.ui.screen.guru.kelolamateri.DaftarMateriScreen
import com.sakupintar.app.ui.screen.guru.kelolamateri.DetailMateriScreen
import com.sakupintar.app.ui.screen.guru.kelolamateri.KelolaMateriScreen
import com.sakupintar.app.ui.screen.guru.kelolamateri.UploadMateriScreen
import com.sakupintar.app.ui.screen.guru.kelolasiswa.DetailSiswaScreen
import com.sakupintar.app.ui.screen.guru.kelolasiswa.KelolaSiswaScreen
import com.sakupintar.app.ui.screen.guru.kelolasiswa.TambahSiswaScreen
import com.sakupintar.app.ui.screen.guru.login.LoginGuruScreen
import com.sakupintar.app.ui.screen.guru.progres.DetailProgresScreen
import com.sakupintar.app.ui.screen.guru.progres.ProgresScreen
import com.sakupintar.app.ui.screen.home.HomeScreen
import com.sakupintar.app.ui.screen.siswa.belajar.BelajarScreen
import com.sakupintar.app.ui.screen.siswa.belajar.MateriScreen
import com.sakupintar.app.ui.screen.siswa.beranda.BerandaSiswaScreen
import com.sakupintar.app.ui.screen.siswa.latihan.LatihanScreen
import com.sakupintar.app.ui.screen.siswa.latihan.SoalScreen
import com.sakupintar.app.ui.screen.siswa.pencapaian.PencapaianScreen
import com.sakupintar.app.ui.screen.siswa.pilihprofil.PilihProfilScreen
import com.sakupintar.app.ui.screen.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        //Umum
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        //Guru
        composable(Routes.GURU_LOGIN) {
            LoginGuruScreen(navController = navController)
        }

        composable(Routes.GURU_BERANDA) {
            BerandaGuruScreen(navController = navController)
        }

        composable(Routes.GURU_KELOLA_SISWA) {
            KelolaSiswaScreen(navController = navController)
        }

        composable(Routes.GURU_TAMBAH_SISWA) {
            TambahSiswaScreen(navController = navController)
        }

        composable(
            route = Routes.GURU_DETAIL_SISWA,
            arguments = listOf(navArgument("siswaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getInt("siswaId") ?: 0
            DetailSiswaScreen(navController = navController, siswaId = siswaId)
        }

        composable(Routes.GURU_KELOLA_MATERI) {
            KelolaMateriScreen(navController = navController)
        }

        composable(Routes.GURU_UPLOAD_MATERI) {
            UploadMateriScreen(navController = navController)
        }

        composable(Routes.GURU_DAFTAR_MATERI) {
            DaftarMateriScreen(navController = navController)
        }

        composable(
            route = Routes.GURU_DETAIL_MATERI,
            arguments = listOf(
                navArgument("kondisiId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val kondisiId = backStackEntry.arguments?.getString("kondisiId") ?: "A"
            DetailMateriScreen(navController = navController, kondisiId = kondisiId)
        }

        composable(Routes.GURU_PROGRES) {
            ProgresScreen(navController = navController)
        }

        composable(
            route = Routes.GURU_DETAIL_PROGRES,
            arguments = listOf(navArgument("siswaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getInt("siswaId") ?: 0
            DetailProgresScreen(navController = navController, siswaId = siswaId)
        }

        //Siswa
        composable(Routes.SISWA_PILIH_PROFIL) {
            PilihProfilScreen(navController = navController)
        }

        composable(
            route = Routes.SISWA_BERANDA,
            arguments = listOf(navArgument("siswaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getInt("siswaId") ?: 0
            BerandaSiswaScreen(navController = navController, siswaId = siswaId)
        }

        composable(
            route = Routes.SISWA_BELAJAR,
            arguments = listOf(navArgument("siswaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getInt("siswaId") ?: 0
            BelajarScreen(navController = navController, siswaId = siswaId)
        }

        composable(
            route = Routes.SISWA_MATERI,
            arguments = listOf(
                navArgument("siswaId") { type = NavType.IntType },
                navArgument("kondisiId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getInt("siswaId") ?: 0
            val kondisiId = backStackEntry.arguments?.getString("kondisiId") ?: "A"
            MateriScreen(
                navController = navController,
                siswaId = siswaId,
                kondisiId = kondisiId
            )
        }

        composable(
            route = Routes.SISWA_LATIHAN,
            arguments = listOf(navArgument("siswaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getInt("siswaId") ?: 0
            LatihanScreen(navController = navController, siswaId = siswaId)
        }

        composable(
            route = Routes.SISWA_SOAL,
            arguments = listOf(
                navArgument("siswaId") { type = NavType.IntType },
                navArgument("kondisiId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getInt("siswaId") ?: 0
            val kondisiId = backStackEntry.arguments?.getString("kondisiId") ?: "A"
            SoalScreen(
                navController = navController,
                siswaId = siswaId,
                kondisiId = kondisiId
            )
        }

        composable(
            route = Routes.SISWA_PENCAPAIAN,
            arguments = listOf(navArgument("siswaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getInt("siswaId") ?: 0
            PencapaianScreen(navController = navController, siswaId = siswaId)
        }
    }
}