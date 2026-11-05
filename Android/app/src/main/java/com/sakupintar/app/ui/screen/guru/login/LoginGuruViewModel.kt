package com.sakupintar.app.ui.screen.guru.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakupintar.app.data.api.RetrofitClient
import com.sakupintar.app.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LoginGuruUiState {
    object Idle    : LoginGuruUiState()
    object Loading : LoginGuruUiState()
    object Success : LoginGuruUiState()
    data class Error(val pesan: String) : LoginGuruUiState()
}

data class LoginGuruFormState(
    val username:     String           = "",
    val password:     String           = "",
    val showPassword: Boolean          = false,
    val state:        LoginGuruUiState = LoginGuruUiState.Idle
)

class LoginGuruViewModel : ViewModel() {

    private val _formState = MutableStateFlow(LoginGuruFormState())
    val formState: StateFlow<LoginGuruFormState> = _formState.asStateFlow()

    fun setUsername(value: String) {
        _formState.update { it.copy(username = value, state = LoginGuruUiState.Idle) }
    }

    fun setPassword(value: String) {
        _formState.update { it.copy(password = value, state = LoginGuruUiState.Idle) }
    }

    fun togglePassword() {
        _formState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun login() {
        val form = _formState.value
        if (form.username.isBlank() || form.password.isBlank()) {
            _formState.update { it.copy(
                state = LoginGuruUiState.Error("Username dan password tidak boleh kosong")
            )}
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(state = LoginGuruUiState.Loading) }
            try {
                val response = RetrofitClient.api.login(form.username, form.password)
                if (response.isSuccessful) {
                    val body = response.body()!!
                    SessionManager.login(body.accessToken, body.namaGuru)
                    _formState.update { it.copy(state = LoginGuruUiState.Success) }
                } else {
                    _formState.update { it.copy(
                        state = LoginGuruUiState.Error("Username atau password salah")
                    )}
                }
            } catch (e: Exception) {
                _formState.update { it.copy(
                    state = LoginGuruUiState.Error("Tidak dapat terhubung ke server. Pastikan backend berjalan.")
                )}
            }
        }
    }
}