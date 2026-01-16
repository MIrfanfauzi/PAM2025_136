package com.example.rotibox.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel menus
 * Menyimpan data menu roti yang dijual
 */
@Entity(tableName = "menus")
data class MenuEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "price")
    val price: Long, // Harga dalam rupiah
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String = "", // Path lokal atau placeholder
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "stock")
    val stock: Int = 0, // Stok maksimal (tidak berkurang, hanya batas pemesanan)
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
