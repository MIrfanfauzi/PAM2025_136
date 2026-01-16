package com.example.rotibox.ui.pelanggan.detail_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.MenuEntity
import com.example.rotibox.data.repository.KeranjangRepository
import com.example.rotibox.data.repository.MenuRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk halaman detail menu
 */
class DetailMenuViewModel(
    private val menuRepository: MenuRepository,
    private val keranjangRepository: KeranjangRepository
) : ViewModel() {
    
    private val _menu = MutableLiveData<MenuEntity?>()
    val menu: LiveData<MenuEntity?> = _menu
    
    private val _addToCartResult = MutableLiveData<AddToCartResult>()
    val addToCartResult: LiveData<AddToCartResult> = _addToCartResult
    
    /**
     * Load detail menu berdasarkan ID
     */
    fun loadMenu(menuId: String) {
        viewModelScope.launch {
            val menuData = menuRepository.getMenuById(menuId)
            _menu.value = menuData
        }
    }
    
    /**
     * Tambah menu ke keranjang
     */
    fun addToCart(userId: String, menuId: String, quantity: Int) {
        if (quantity <= 0) {
            _addToCartResult.value = AddToCartResult.Error("Jumlah harus lebih dari 0")
            return
        }
        
        // Validasi stok
        val currentMenu = _menu.value
        if (currentMenu != null && quantity > currentMenu.stock) {
            _addToCartResult.value = AddToCartResult.Error("Stok tersedia hanya ${currentMenu.stock}")
            return
        }
        
        viewModelScope.launch {
            try {
                keranjangRepository.addToCart(userId, menuId, quantity)
                _addToCartResult.value = AddToCartResult.Success
            } catch (e: Exception) {
                _addToCartResult.value = AddToCartResult.Error("Gagal menambahkan ke keranjang: ${e.message}")
            }
        }
    }
    
    /**
     * Sealed class untuk hasil tambah ke keranjang
     */
    sealed class AddToCartResult {
        object Success : AddToCartResult()
        data class Error(val message: String) : AddToCartResult()
    }
}
