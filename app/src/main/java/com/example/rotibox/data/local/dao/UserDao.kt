package com.example.rotibox.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rotibox.data.local.entity.UserEntity

/**
 * DAO untuk tabel users
 * Berisi query untuk operasi CRUD user
 */
@Dao
interface UserDao {
    
    /**
     * Insert user baru
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)
    
    /**
     * Update data user
     */
    @Update
    suspend fun update(user: UserEntity)
    
    /**
     * Cari user berdasarkan email dan password (untuk login)
     */
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?
    
    /**
     * Cari user berdasarkan email saja (untuk cek duplikasi saat daftar)
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    /**
     * Ambil user berdasarkan ID
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?
    
    /**
     * Ambil semua user (untuk admin)
     */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun getAllUsers(): LiveData<List<UserEntity>>
}
