package com.example.rotibox.data.repository

import androidx.lifecycle.LiveData
import com.example.rotibox.data.local.dao.MenuDao
import com.example.rotibox.data.local.entity.MenuEntity

/**
 * Repository untuk operasi menu
 * Menangani CRUD menu dan filter menu aktif
 */
class MenuRepository(private val menuDao: MenuDao) {
    
    /**
     * Ambil semua menu aktif (untuk pelanggan)
     */
    fun getAllActiveMenus(): LiveData<List<MenuEntity>> {
        return menuDao.getAllActiveMenus()
    }
    
    /**
     * Ambil semua menu (untuk admin)
     */
    fun getAllMenus(): LiveData<List<MenuEntity>> {
        return menuDao.getAllMenus()
    }
    
    /**
     * Ambil menu berdasarkan ID
     */
    suspend fun getMenuById(menuId: String): MenuEntity? {
        return menuDao.getMenuById(menuId)
    }
    
    /**
     * Tambah menu baru (admin)
     */
    suspend fun insertMenu(menu: MenuEntity) {
        menuDao.insert(menu)
    }
    
    /**
     * Update menu (admin)
     */
    suspend fun updateMenu(menu: MenuEntity) {
        menuDao.update(menu)
    }
    
    /**
     * Hapus menu (admin)
     */
    suspend fun deleteMenu(menu: MenuEntity) {
        menuDao.delete(menu)
    }
    
    /**
     * Toggle status aktif menu (admin)
     */
    suspend fun toggleMenuStatus(menuId: String, isActive: Boolean) {
        menuDao.updateMenuStatus(menuId, isActive, System.currentTimeMillis())
    }

    suspend fun getTotalRotiCount(): Int {
        return menuDao.getRotiCount()
    }
    
    /**
     * Get all menus synchronously (untuk statistik)
     */
    suspend fun getAllMenusSync(): List<MenuEntity> {
        return menuDao.getAllMenusSync()
    }
}
