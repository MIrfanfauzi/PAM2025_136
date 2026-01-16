package com.example.rotibox.data.local.dao

import androidx.room.*
import com.example.rotibox.data.local.entity.InfoContactEntity

/**
 * DAO untuk tabel info_contacts
 * Berisi query untuk informasi kontak toko
 */
@Dao
interface InfoContactDao {
    
    /**
     * Insert atau update info kontak
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(infoContact: InfoContactEntity)
    
    /**
     * Update info kontak
     */
    @Update
    suspend fun update(infoContact: InfoContactEntity)
    
    /**
     * Ambil info kontak (biasanya hanya ada 1 record)
     */
    @Query("SELECT * FROM info_contacts LIMIT 1")
    suspend fun getInfoContact(): InfoContactEntity?
}
