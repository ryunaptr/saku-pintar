package com.sakupintar.app.data.local

object SessionManager {
    var token: String    = ""
    var namaGuru: String = ""

    fun login(token: String, namaGuru: String) {
        this.token    = token
        this.namaGuru = namaGuru
    }

    fun logout() {
        token    = ""
        namaGuru = ""
    }

    fun isLoggedIn() = token.isNotBlank()

    fun bearerToken() = "Bearer $token"
}