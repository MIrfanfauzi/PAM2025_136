package com.example.rotibox.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel orders
 * Menyimpan data pesanan pelanggan
 */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class OrderEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "order_date")
    val orderDate: Long,
    
    @ColumnInfo(name = "pickup_date")
    val pickupDate: Long,
    
    @ColumnInfo(name = "delivery")
    val delivery: String, // "ambil" atau "antar"
    
    @ColumnInfo(name = "status")
    val status: String, // "menunggu_konfirmasi", "proses", "selesai", "dibatalkan"
    
    @ColumnInfo(name = "total_amount")
    val totalAmount: Long,
    
    @ColumnInfo(name = "note")
    val note: String = "",
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
