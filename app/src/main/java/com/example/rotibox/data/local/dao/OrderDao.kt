package com.example.rotibox.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rotibox.data.local.entity.OrderDenganItem
import com.example.rotibox.data.local.entity.OrderEntity

/**
 * DAO untuk tabel orders
 * Berisi query untuk operasi pesanan
 */
@Dao
interface OrderDao {
    
    /**
     * Insert order baru
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity): Long
    
    /**
     * Update order (misalnya ubah status)
     */
    @Update
    suspend fun update(order: OrderEntity)
    
    /**
     * Ambil semua order milik user tertentu beserta itemnya
     */
    @Transaction
    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY order_date DESC")
    fun getOrdersByUser(userId: String): LiveData<List<OrderDenganItem>>
    
    /**
     * Ambil semua order (untuk admin) beserta itemnya
     */
    @Transaction
    @Query("SELECT * FROM orders ORDER BY order_date DESC")
    fun getAllOrders(): LiveData<List<OrderDenganItem>>
    
    /**
     * Ambil order berdasarkan ID beserta itemnya
     */
    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderDenganItem?
    
    /**
     * Update status order
     */
    @Query("UPDATE orders SET status = :status, updated_at = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, updatedAt: Long)
    
    /**
     * Ambil order berdasarkan status tertentu
     */
    @Transaction
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY order_date DESC")
    fun getOrdersByStatus(status: String): LiveData<List<OrderDenganItem>>
    
    /**
     * Hitung total penjualan dari order dengan status selesai
     * dalam rentang tanggal tertentu
     */
    @Query("SELECT SUM(total_amount) FROM orders WHERE status = 'selesai' AND order_date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSales(startDate: Long, endDate: Long): Long?
    
    /**
     * Hitung jumlah transaksi selesai dalam rentang tanggal
     */
    @Query("SELECT COUNT(*) FROM orders WHERE status = 'selesai' AND order_date BETWEEN :startDate AND :endDate")
    suspend fun getCompletedOrderCount(startDate: Long, endDate: Long): Int
    
    /**
     * Ambil semua order secara synchronous (untuk laporan)
     */
    @Transaction
    @Query("SELECT * FROM orders ORDER BY order_date DESC")
    suspend fun getAllOrdersSync(): List<OrderDenganItem>
}
