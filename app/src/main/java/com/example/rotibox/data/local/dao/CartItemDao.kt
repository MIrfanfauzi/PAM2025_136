package com.example.rotibox.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rotibox.data.local.entity.CartItemDenganMenu
import com.example.rotibox.data.local.entity.CartItemEntity

/**
 * DAO untuk tabel cart_items
 * Berisi query untuk operasi keranjang belanja
 */
@Dao
interface CartItemDao {
    
    /**
     * Insert item ke keranjang
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartItemEntity)
    
    /**
     * Update item di keranjang (misalnya ubah quantity)
     */
    @Update
    suspend fun update(cartItem: CartItemEntity)
    
    /**
     * Delete item dari keranjang
     */
    @Delete
    suspend fun delete(cartItem: CartItemEntity)
    
    /**
     * Ambil semua item di keranjang user tertentu beserta detail menu
     */
    @Transaction
    @Query("SELECT * FROM cart_items WHERE user_id = :userId ORDER BY created_at DESC")
    fun getCartItemsByUser(userId: String): LiveData<List<CartItemDenganMenu>>
    
    /**
     * Cek apakah menu sudah ada di keranjang user
     */
    @Query("SELECT * FROM cart_items WHERE user_id = :userId AND menu_id = :menuId LIMIT 1")
    suspend fun getCartItem(userId: String, menuId: String): CartItemEntity?
    
    /**
     * Hapus semua item di keranjang user (setelah checkout)
     */
    @Query("DELETE FROM cart_items WHERE user_id = :userId")
    suspend fun clearCart(userId: String)
    
    /**
     * Hitung jumlah item di keranjang user
     */
    @Query("SELECT COUNT(*) FROM cart_items WHERE user_id = :userId")
    fun getCartItemCount(userId: String): LiveData<Int>
}
