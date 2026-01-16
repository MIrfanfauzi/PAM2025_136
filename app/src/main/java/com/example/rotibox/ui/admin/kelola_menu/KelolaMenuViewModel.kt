package com.example.rotibox.ui.admin.kelola_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.MenuEntity
import com.example.rotibox.data.repository.MenuRepository
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel untuk halaman kelola menu (admin)
 */
class KelolaMenuViewModel(private val menuRepository: MenuRepository) : ViewModel() {
    
    val menuList: LiveData<List<MenuEntity>> = menuRepository.getAllMenus()
    
    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult
    
    /**
     * Tambah menu baru
     */
    fun addMenu(name: String, description: String, price: Long, stock: Int, imageUrl: String) {
        if (name.isBlank()) {
            _operationResult.value = OperationResult.Error("Nama menu tidak boleh kosong")
            return
        }
        
        if (price <= 0) {
            _operationResult.value = OperationResult.Error("Harga harus lebih dari 0")
            return
        }
        
        if (stock < 0) {
            _operationResult.value = OperationResult.Error("Stok tidak boleh negatif")
            return
        }
        
        viewModelScope.launch {
            try {
                val menu = MenuEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = description,
                    price = price,
                    stock = stock,
                    imageUrl = imageUrl
                )
                menuRepository.insertMenu(menu)
                _operationResult.value = OperationResult.Success("Menu berhasil ditambahkan")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Gagal menambahkan menu: ${e.message}")
            }
        }
    }
    
    /**
     * Get menu by ID untuk edit
     */
    suspend fun getMenuById(menuId: String): MenuEntity? {
        return try {
            menuRepository.getMenuById(menuId)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Update menu
     */
    fun updateMenu(menu: MenuEntity) {
        viewModelScope.launch {
            try {
                menuRepository.updateMenu(menu.copy(updatedAt = System.currentTimeMillis()))
                _operationResult.value = OperationResult.Success("Menu berhasil diupdate")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Gagal update menu: ${e.message}")
            }
        }
    }
    
    /**
     * Toggle status aktif menu
     */
    fun toggleMenuStatus(menuId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                menuRepository.toggleMenuStatus(menuId, isActive)
                val status = if (isActive) "diaktifkan" else "dinonaktifkan"
                _operationResult.value = OperationResult.Success("Menu berhasil $status")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Gagal mengubah status: ${e.message}")
            }
        }
    }
    
    /**
     * Hapus menu
     */
    fun deleteMenu(menu: MenuEntity) {
        viewModelScope.launch {
            try {
                menuRepository.deleteMenu(menu)
                _operationResult.value = OperationResult.Success("Menu berhasil dihapus")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Gagal menghapus menu: ${e.message}")
            }
        }
    }
    
    /**
     * Sealed class untuk hasil operasi
     */
    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
    }
}
