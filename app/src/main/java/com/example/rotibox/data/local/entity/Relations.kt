package com.example.rotibox.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class untuk relasi Order dengan OrderItems dan User
 * Digunakan untuk mengambil data order beserta semua itemnya dan info pelanggan
 */
data class OrderDenganItem(
    @Embedded
    val order: OrderEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "order_id"
    )
    val items: List<OrderItemEntity>,
    
    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val user: UserEntity? = null
)

/**
 * Data class untuk relasi CartItem dengan Menu
 * Digunakan untuk menampilkan keranjang dengan detail menu
 */
data class CartItemDenganMenu(
    @Embedded
    val cartItem: CartItemEntity,
    
    @Relation(
        parentColumn = "menu_id",
        entityColumn = "id"
    )
    val menu: MenuEntity
)
