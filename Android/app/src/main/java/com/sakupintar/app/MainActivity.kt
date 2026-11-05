package com.sakupintar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sakupintar.app.ui.navigation.NavGraph
import com.sakupintar.app.ui.theme.SakuPintarTheme
import com.sakupintar.app.ui.component.AppBackground
import com.sakupintar.app.util.AppAudioManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppAudioManager.inisialisasi(this)
        enableEdgeToEdge()
        setContent {
            AppBackground {
            }
            SakuPintarTheme {
                NavGraph()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppAudioManager.release()
    }
}


