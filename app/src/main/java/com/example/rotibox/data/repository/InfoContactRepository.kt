package com.example.rotibox.data.repository

import com.example.rotibox.data.local.dao.InfoContactDao
import com.example.rotibox.data.local.entity.InfoContactEntity

/**
 * Repository untuk mengelola informasi kontak toko
 */
class InfoContactRepository(
    private val infoContactDao: InfoContactDao
) {
    
    /**
     * Ambil informasi kontak toko
     */
    suspend fun getInfoContact(): InfoContactEntity? {
        return infoContactDao.getInfoContact()
    }
    
    /**
     * Update informasi kontak toko
     */
    suspend fun updateInfoContact(infoContact: InfoContactEntity) {
        infoContactDao.update(infoContact)
    }
    
    /**
     * Insert informasi kontak toko (untuk pertama kali)
     */
    suspend fun insertInfoContact(infoContact: InfoContactEntity) {
        infoContactDao.insert(infoContact)
    }
}
