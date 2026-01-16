package com.example.rotibox.util

import android.content.Context
import android.content.SharedPreferences

/**
 * SessionManager untuk mengelola sesi login user
 * Menggunakan SharedPreferences untuk menyimpan data user yang sedang login
 */
class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Konstanta.PREF_NAME,
        Context.MODE_PRIVATE
    )
    
    private val editor: SharedPreferences.Editor = prefs.edit()
    
    /**
     * Simpan data user saat login
     */
    fun saveUserSession(userId: String, userName: String, userEmail: String, userRole: String) {
        editor.apply {
            putString(Konstanta.KEY_USER_ID, userId)
            putString(Konstanta.KEY_USER_NAME, userName)
            putString(Konstanta.KEY_USER_EMAIL, userEmail)
            putString(Konstanta.KEY_USER_ROLE, userRole)
            putBoolean(Konstanta.KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    /**
     * Ambil User ID yang sedang login
     */
    fun getUserId(): String? {
        return prefs.getString(Konstanta.KEY_USER_ID, null)
    }
    
    /**
     * Ambil nama user yang sedang login
     */
    fun getUserName(): String? {
        return prefs.getString(Konstanta.KEY_USER_NAME, null)
    }
    
    /**
     * Ambil email user yang sedang login
     */
    fun getUserEmail(): String? {
        return prefs.getString(Konstanta.KEY_USER_EMAIL, null)
    }
    
    /**
     * Ambil role user yang sedang login
     */
    fun getUserRole(): String? {
        return prefs.getString(Konstanta.KEY_USER_ROLE, null)
    }
    
    /**
     * Cek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Konstanta.KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Cek apakah user adalah admin
     */
    fun isAdmin(): Boolean {
        return getUserRole() == Konstanta.ROLE_ADMIN
    }
    
    /**
     * Cek apakah user adalah pelanggan
     */
    fun isPelanggan(): Boolean {
        return getUserRole() == Konstanta.ROLE_PELANGGAN
    }
    
    /**
     * Hapus sesi user (logout)
     */
    fun clearSession() {
        editor.clear().apply()
    }
}
