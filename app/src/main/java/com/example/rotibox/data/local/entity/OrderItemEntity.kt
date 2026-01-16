package com.example.rotibox.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel order_items
 * Menyimpan detail item dalam setiap pesanan
 */
@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["order_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MenuEntity::class,
            parentColumns = ["id"],
            childColumns = ["menu_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["order_id"]),
        Index(value = ["menu_id"])
    ]
)
data class OrderItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "order_id")
    val orderId: String,
    
    @ColumnInfo(name = "menu_id")
    val menuId: String,
    
    @ColumnInfo(name = "menu_name")
    val menuName: String,
    
    @ColumnInfo(name = "price")
    val price: Long,
    
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    
    @ColumnInfo(name = "subtotal")
    val subtotal: Long
)
