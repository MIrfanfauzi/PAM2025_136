package com.example.rotibox.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel info_contacts
 * Menyimpan informasi kontak toko
 */
@Entity(tableName = "info_contacts")
data class InfoContactEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "store_phone")
    val storePhone: String,
    
    @ColumnInfo(name = "store_email")
    val storeEmail: String,
    
    @ColumnInfo(name = "store_address")
    val storeAddress: String,
    
    @ColumnInfo(name = "info_payment")
    val infoPayment: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
