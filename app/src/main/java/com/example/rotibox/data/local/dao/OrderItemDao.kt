package com.example.rotibox.data.local.dao

import androidx.room.*
import com.example.rotibox.data.local.entity.OrderItemEntity

/**
 * DAO untuk tabel order_items
 * Berisi query untuk operasi item pesanan
 */
@Dao
interface OrderItemDao {
    
    /**
     * Insert item pesanan
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderItem: OrderItemEntity)
    
    /**
     * Insert multiple items sekaligus
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orderItems: List<OrderItemEntity>)
    
    /**
     * Ambil semua item dari order tertentu
     */
    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>
    
    /**
     * Delete item pesanan
     */
    @Delete
    suspend fun delete(orderItem: OrderItemEntity)
}
