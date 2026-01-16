package com.example.rotibox.ui.pelanggan.keranjang

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.CartItemDenganMenu
import com.example.rotibox.data.local.entity.CartItemEntity
import com.example.rotibox.data.repository.KeranjangRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk halaman keranjang
 */
class KeranjangViewModel(private val keranjangRepository: KeranjangRepository) : ViewModel() {
    
    private val _cartItems = MutableLiveData<List<CartItemDenganMenu>>()
    val cartItems: LiveData<List<CartItemDenganMenu>> = _cartItems
    
    private val _totalAmount = MutableLiveData<Long>()
    val totalAmount: LiveData<Long> = _totalAmount
    
    /**
     * Load cart items untuk user tertentu
     */
    fun loadCartItems(userId: String) {
        viewModelScope.launch {
            keranjangRepository.getCartItems(userId).observeForever { items ->
                _cartItems.value = items
                calculateTotal(items)
            }
        }
    }
    
    /**
     * Hitung total harga
     */
    private fun calculateTotal(items: List<CartItemDenganMenu>) {
        var total: Long = 0
        items.forEach { item ->
            total += item.menu.price * item.cartItem.quantity
        }
        _totalAmount.value = total
    }
    
    /**
     * Update quantity item
     */
    fun updateQuantity(cartItem: CartItemEntity, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                keranjangRepository.updateCartItemQuantity(cartItem, newQuantity)
            } else {
                keranjangRepository.removeFromCart(cartItem)
            }
        }
    }
    
    /**
     * Hapus item dari keranjang
     */
    fun removeItem(cartItem: CartItemEntity) {
        viewModelScope.launch {
            keranjangRepository.removeFromCart(cartItem)
        }
    }
}
