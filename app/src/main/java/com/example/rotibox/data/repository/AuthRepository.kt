package com.example.rotibox.data.repository

import com.example.rotibox.data.local.dao.UserDao
import com.example.rotibox.data.local.entity.UserEntity
import com.example.rotibox.util.PasswordHasher

/**
 * Repository untuk operasi autentikasi
 * Menangani login, register, dan manajemen user dengan password hashing
 */
class AuthRepository(private val userDao: UserDao) {
    
    /**
     * Login user dengan email dan password
     * Return user jika berhasil, null jika gagal
     */
    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.getUserByEmail(email)
        
        // Jika user tidak ditemukan, return null
        if (user == null) {
            return null
        }
        
        // Verifikasi password menggunakan PasswordHasher
        return if (PasswordHasher.verifyPassword(password, user.password)) {
            user
        } else {
            null
        }
    }
    
    /**
     * Register user baru (pelanggan)
     * Return true jika berhasil, false jika email sudah terdaftar
     */
    suspend fun register(user: UserEntity): Boolean {
        // Cek apakah email sudah terdaftar
        val existingUser = userDao.getUserByEmail(user.email)
        if (existingUser != null) {
            return false // Email sudah terdaftar
        }
        
        // Hash password sebelum menyimpan
        val hashedPassword = PasswordHasher.hashPassword(user.password)
        val userWithHashedPassword = user.copy(password = hashedPassword)
        
        // Insert user baru
        userDao.insert(userWithHashedPassword)
        return true
    }
    
    /**
     * Ambil user berdasarkan email
     */
    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
    
    /**
     * Ambil user berdasarkan ID
     */
    suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }
    
    /**
     * Update data user
     */
    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }
    
    /**
     * Update password user
     */
    suspend fun updatePassword(userId: String, newPassword: String) {
        val user = userDao.getUserById(userId)
        user?.let {
            val hashedPassword = PasswordHasher.hashPassword(newPassword)
            val updatedUser = it.copy(password = hashedPassword)
            userDao.update(updatedUser)
        }
    }
}

