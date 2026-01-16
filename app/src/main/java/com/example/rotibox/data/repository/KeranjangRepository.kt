package com.example.rotibox.data.repository

import androidx.lifecycle.LiveData
import com.example.rotibox.data.local.dao.CartItemDao
import com.example.rotibox.data.local.entity.CartItemDenganMenu
import com.example.rotibox.data.local.entity.CartItemEntity
import java.util.UUID

/**
 * Repository untuk operasi keranjang belanja
 * Menangani tambah, ubah, hapus item di keranjang
 */
class KeranjangRepository(private val cartItemDao: CartItemDao) {
    
    /**
     * Ambil semua item di keranjang user beserta detail menu
     */
    fun getCartItems(userId: String): LiveData<List<CartItemDenganMenu>> {
        return cartItemDao.getCartItemsByUser(userId)
    }
    
    /**
     * Tambah item ke keranjang
     * Jika item sudah ada, tambahkan quantity
     */
    suspend fun addToCart(userId: String, menuId: String, quantity: Int = 1) {
        val existingItem = cartItemDao.getCartItem(userId, menuId)
        
        if (existingItem != null) {
            // Item sudah ada, update quantity
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + quantity,
                updatedAt = System.currentTimeMillis()
            )
            cartItemDao.update(updatedItem)
        } else {
            // Item baru, insert
            val newItem = CartItemEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                menuId = menuId,
                quantity = quantity
            )
            cartItemDao.insert(newItem)
        }
    }
    
    /**
     * Update quantity item di keranjang
     */
    suspend fun updateCartItemQuantity(cartItem: CartItemEntity, newQuantity: Int) {
        val updatedItem = cartItem.copy(
            quantity = newQuantity,
            updatedAt = System.currentTimeMillis()
        )
        cartItemDao.update(updatedItem)
    }
    
    /**
     * Hapus item dari keranjang
     */
    suspend fun removeFromCart(cartItem: CartItemEntity) {
        cartItemDao.delete(cartItem)
    }
    
    /**
     * Kosongkan keranjang user (setelah checkout)
     */
    suspend fun clearCart(userId: String) {
        cartItemDao.clearCart(userId)
    }
    
    /**
     * Hitung jumlah item di keranjang
     */
    fun getCartItemCount(userId: String): LiveData<Int> {
        return cartItemDao.getCartItemCount(userId)
    }
}
