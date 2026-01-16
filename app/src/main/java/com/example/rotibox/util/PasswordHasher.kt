package com.example.rotibox.util

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Utility untuk hashing dan verifikasi password menggunakan BCrypt
 * Sesuai requirement: Password harus di-hash menggunakan BCrypt library Android Studio
 */
object PasswordHasher {
    
    // BCrypt cost factor (4-31)
    // 12 adalah nilai yang direkomendasikan untuk keamanan dan performa
    private const val BCRYPT_COST = 12
    
    /**
     * Hash password menggunakan BCrypt
     * @param password Password plain text
     * @return Hashed password dalam format BCrypt
     */
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray())
    }
    
    /**
     * Verifikasi password dengan hash yang tersimpan
     * @param password Password plain text yang akan diverifikasi
     * @param storedHash Hash yang tersimpan di database (BCrypt format)
     * @return true jika password cocok, false jika tidak
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            val result = BCrypt.verifyer().verify(password.toCharArray(), storedHash)
            result.verified
        } catch (e: Exception) {
            // Handle invalid hash format
            false
        }
    }
}
