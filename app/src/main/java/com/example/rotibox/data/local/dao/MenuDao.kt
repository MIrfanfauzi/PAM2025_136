package com.example.rotibox.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rotibox.data.local.entity.MenuEntity

/**
 * DAO untuk tabel menus
 * Berisi query untuk operasi CRUD menu
 */
@Dao
interface MenuDao {
    
    /**
     * Insert menu baru
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(menu: MenuEntity)
    
    /**
     * Update menu
     */
    @Update
    suspend fun update(menu: MenuEntity)
    
    /**
     * Delete menu
     */
    @Delete
    suspend fun delete(menu: MenuEntity)
    
    /**
     * Ambil semua menu aktif (untuk pelanggan)
     */
    @Query("SELECT * FROM menus WHERE is_active = 1 ORDER BY name ASC")
    fun getAllActiveMenus(): LiveData<List<MenuEntity>>
    
    /**
     * Ambil semua menu (untuk admin)
     */
    @Query("SELECT * FROM menus ORDER BY created_at DESC")
    fun getAllMenus(): LiveData<List<MenuEntity>>
    
    /**
     * Ambil menu berdasarkan ID
     */
    @Query("SELECT * FROM menus WHERE id = :menuId LIMIT 1")
    suspend fun getMenuById(menuId: String): MenuEntity?
    
    /**
     * Toggle status aktif menu
     */
    @Query("UPDATE menus SET is_active = :isActive, updated_at = :updatedAt WHERE id = :menuId")
    suspend fun updateMenuStatus(menuId: String, isActive: Boolean, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM menus") // Replace 'roti_table' with your actual table name
    suspend fun getRotiCount(): Int
    
    /**
     * Get all menus synchronously (untuk statistik)
     */
    @Query("SELECT * FROM menus")
    suspend fun getAllMenusSync(): List<MenuEntity>

}
