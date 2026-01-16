package com.example.rotibox.ui.pelanggan.beranda

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rotibox.data.local.entity.MenuEntity
import com.example.rotibox.data.repository.MenuRepository

/**
 * ViewModel untuk halaman beranda pelanggan
 * Menampilkan daftar menu aktif
 */
class BerandaPelangganViewModel(private val menuRepository: MenuRepository) : ViewModel() {
    
    /**
     * LiveData untuk daftar menu aktif
     */
    val menuList: LiveData<List<MenuEntity>> = menuRepository.getAllActiveMenus()
}
