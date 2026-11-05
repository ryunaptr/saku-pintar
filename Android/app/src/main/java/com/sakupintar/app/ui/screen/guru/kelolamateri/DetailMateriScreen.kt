package com.sakupintar.app.ui.screen.guru.kelolamateri

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sakupintar.app.ui.theme.ColorBackground
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorPrimaryContainer
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary
import com.sakupintar.app.ui.theme.ColorTextSecondary
import com.sakupintar.app.ui.theme.SakuPintarTheme

@Composable
fun DetailMateriScreen(
    navController: NavHostController,
    kondisiId: String,
    viewModel: DetailMateriViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(kondisiId) { viewModel.loadDetail(kondisiId) }

    SakuPintarTheme(isGuruMode = true) {
        if (uiState.sedangMemuat) {
            Box(modifier = Modifier.fillMaxSize().background(ColorBackground),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ColorPrimary)
            }
            return@SakuPintarTheme
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            TextButton(onClick = { navController.popBackStack() },
                contentPadding = PaddingValues(0.dp)) {
                Text("← Kembali", style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Header info kondisi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorPrimaryContainer, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text("Kondisi ${uiState.kondisiId}",
                    style = MaterialTheme.typography.labelMedium,
                    color = ColorPrimary, fontWeight = FontWeight.Bold)
                Text(uiState.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorTextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Sumber: ${uiState.namaDokumen}",
                    style = MaterialTheme.typography.bodySmall, color = ColorTextMuted)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Isi Materi",
                style = MaterialTheme.typography.titleMedium,
                color = ColorTextPrimary, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Ini adalah ringkasan materi yang sudah diproses sistem dari dokumen.",
                style = MaterialTheme.typography.bodySmall, color = ColorTextMuted)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorSurface, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(uiState.isi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextPrimary, lineHeight = androidx.compose.ui.unit.TextUnit(22f,
                        androidx.compose.ui.unit.TextUnitType.Sp))
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}