package com.sakupintar.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "saku_pintar_session")

class SessionDataStore(private val context: Context) {

    companion object {
        private val KEY_TOKEN    = stringPreferencesKey("access_token")
        private val KEY_NAMA_GURU = stringPreferencesKey("nama_guru")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val namaGuru: Flow<String?> = context.dataStore.data.map { it[KEY_NAMA_GURU] }

    suspend fun simpanSession(token: String, namaGuru: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN]     = token
            prefs[KEY_NAMA_GURU] = namaGuru
        }
    }

    suspend fun hapusSession() {
        context.dataStore.edit { it.clear() }
    }

    fun getBearerToken(token: String) = "Bearer $token"
}