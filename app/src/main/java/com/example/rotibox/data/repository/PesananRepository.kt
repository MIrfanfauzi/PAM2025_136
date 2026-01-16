package com.example.rotibox.data.repository

import androidx.lifecycle.LiveData
import com.example.rotibox.data.local.dao.OrderDao
import com.example.rotibox.data.local.dao.OrderItemDao
import com.example.rotibox.data.local.entity.*
import com.example.rotibox.util.Konstanta
import java.util.UUID

/**
 * Repository untuk operasi pesanan
 * Menangani pembuatan order, update status, dan list pesanan
 */
class PesananRepository(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {
    
    /**
     * Buat order baru dari keranjang
     * @param userId ID user yang membuat order
     * @param cartItems List item dari keranjang
     * @param pickupDate Tanggal pengambilan
     * @param delivery Metode pengiriman (ambil/antar)
     * @param note Catatan tambahan
     * @return ID order yang dibuat
     */
    suspend fun createOrder(
        userId: String,
        cartItems: List<CartItemDenganMenu>,
        pickupDate: Long,
        delivery: String,
        note: String
    ): String {
        // Hitung total amount
        var totalAmount: Long = 0
        val orderItems = mutableListOf<OrderItemEntity>()
        
        cartItems.forEach { cartItemDenganMenu ->
            val menu = cartItemDenganMenu.menu
            val quantity = cartItemDenganMenu.cartItem.quantity
            val subtotal = menu.price * quantity
            totalAmount += subtotal
            
            // Buat order item
            val orderItem = OrderItemEntity(
                id = UUID.randomUUID().toString(),
                orderId = "", // Akan diisi setelah order dibuat
                menuId = menu.id,
                menuName = menu.name,
                price = menu.price,
                quantity = quantity,
                subtotal = subtotal
            )
            orderItems.add(orderItem)
        }
        
        // Buat order
        val orderId = UUID.randomUUID().toString()
        val order = OrderEntity(
            id = orderId,
            userId = userId,
            orderDate = System.currentTimeMillis(),
            pickupDate = pickupDate,
            delivery = delivery,
            status = Konstanta.STATUS_MENUNGGU_KONFIRMASI,
            totalAmount = totalAmount,
            note = note
        )
        
        // Insert order
        orderDao.insert(order)
        
        // Insert order items dengan orderId yang sudah dibuat
        val orderItemsWithOrderId = orderItems.map { it.copy(orderId = orderId) }
        orderItemDao.insertAll(orderItemsWithOrderId)
        
        return orderId
    }
    
    /**
     * Ambil semua order milik user
     */
    fun getOrdersByUser(userId: String): LiveData<List<OrderDenganItem>> {
        return orderDao.getOrdersByUser(userId)
    }
    
    /**
     * Ambil semua order (untuk admin)
     */
    fun getAllOrders(): LiveData<List<OrderDenganItem>> {
        return orderDao.getAllOrders()
    }
    
    /**
     * Ambil order berdasarkan ID
     */
    suspend fun getOrderById(orderId: String): OrderDenganItem? {
        return orderDao.getOrderById(orderId)
    }
    
    /**
     * Update status order (admin)
     */
    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        orderDao.updateOrderStatus(orderId, newStatus, System.currentTimeMillis())
    }
    
    /**
     * Ambil order berdasarkan status
     */
    fun getOrdersByStatus(status: String): LiveData<List<OrderDenganItem>> {
        return orderDao.getOrdersByStatus(status)
    }
    
    /**
     * Ambil semua order secara synchronous (untuk laporan)
     */
    suspend fun getAllOrdersSync(): List<OrderDenganItem> {
        return orderDao.getAllOrdersSync()
    }
}
