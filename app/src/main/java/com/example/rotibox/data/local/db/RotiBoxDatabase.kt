package com.example.rotibox.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rotibox.data.local.dao.*
import com.example.rotibox.data.local.entity.*
import com.example.rotibox.util.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Room Database untuk aplikasi RotiBox
 * Versi 1 - Database lokal dengan seed data
 */
@Database(
    entities = [
        UserEntity::class,
        MenuEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        InfoContactEntity::class
    ],
    version = 3, // Update ke version 3 untuk BCrypt
    exportSchema = false
)
abstract class RotiBoxDatabase : RoomDatabase() {
    
    // DAO declarations
    abstract fun userDao(): UserDao
    abstract fun menuDao(): MenuDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun infoContactDao(): InfoContactDao
    
    companion object {
        @Volatile
        private var INSTANCE: RotiBoxDatabase? = null
        
        /**
         * Migration dari versi 1 ke 2: Tambah kolom stock di tabel menus
         */
        // Migration 1 -> 2: Tambah kolom stock di menus
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE menus ADD COLUMN stock INTEGER NOT NULL DEFAULT 100"
                )
            }
        }
        
        // Migration 2 -> 3: Update password ke BCrypt format
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Hapus semua user kecuali admin
                database.execSQL("DELETE FROM users WHERE role != 'admin'")
                
                // Update admin password dengan BCrypt
                // Password: admin123
                val newAdminPassword = "\$2a\$12\$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIeKHq5jm6"
                database.execSQL(
                    "UPDATE users SET password = ? WHERE email = 'admin@rotibox.com'",
                    arrayOf(newAdminPassword)
                )
            }
        }
        
        /**
         * Singleton pattern untuk database
         * Memastikan hanya ada 1 instance database
         */
        fun getDatabase(context: Context): RotiBoxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RotiBoxDatabase::class.java,
                    "rotibox_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Tambah MIGRATION_2_3
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Callback untuk seed data saat database pertama kali dibuat
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database)
                    }
                }
            }
        }
        
        /**
         * Fungsi untuk mengisi data awal (seed data)
         * Dipanggil saat database pertama kali dibuat
         */
        private suspend fun seedDatabase(database: RotiBoxDatabase) {
            val userDao = database.userDao()
            val menuDao = database.menuDao()
            val infoContactDao = database.infoContactDao()
            
            // Seed Admin Default
            val adminId = UUID.randomUUID().toString()
            val admin = UserEntity(
                id = adminId,
                name = "Administrator",
                email = "admin@rotibox.com",
                password = PasswordHasher.hashPassword("admin123"), // Hash password
                phone = "081234567890",
                role = "admin",
                address = "Jl. Roti Box No. 1, Jakarta"
            )
            userDao.insert(admin)
            
            // Seed Menu Contoh
            val menus = listOf(
                MenuEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Roti Tawar",
                    description = "Roti tawar lembut dan segar, cocok untuk sarapan",
                    price = 15000,
                    imageUrl = "placeholder_roti_tawar",
                    isActive = true,
                    stock = 100
                ),
                MenuEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Roti Cokelat",
                    description = "Roti manis dengan isian cokelat premium",
                    price = 18000,
                    imageUrl = "placeholder_roti_cokelat",
                    isActive = true,
                    stock = 100
                ),
                MenuEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Roti Keju",
                    description = "Roti dengan topping keju melimpah",
                    price = 20000,
                    imageUrl = "placeholder_roti_keju",
                    isActive = true,
                    stock = 100
                ),
                MenuEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Roti Pisang",
                    description = "Roti dengan isian pisang segar",
                    price = 17000,
                    imageUrl = "placeholder_roti_pisang",
                    isActive = true,
                    stock = 100
                ),
                MenuEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Roti Abon",
                    description = "Roti dengan taburan abon sapi berkualitas",
                    price = 22000,
                    imageUrl = "placeholder_roti_abon",
                    isActive = true,
                    stock = 100
                ),
                MenuEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Roti Kismis",
                    description = "Roti manis dengan kismis pilihan",
                    price = 16000,
                    imageUrl = "placeholder_roti_kismis",
                    isActive = true,
                    stock = 100
                )
            )
            
            menus.forEach { menu ->
                menuDao.insert(menu)
            }
            
            // Seed Info Kontak
            val infoContact = InfoContactEntity(
                id = UUID.randomUUID().toString(),
                storePhone = "081234567890",
                storeEmail = "info@rotibox.com",
                storeAddress = "Jl. Roti Box No. 1, Jakarta Selatan",
                infoPayment = "Transfer Bank BCA: 1234567890 a.n. RotiBox\nCOD tersedia untuk pengiriman",
                description = "RotiBox - Toko Roti Terbaik dengan Rasa Istimewa"
            )
            infoContactDao.insert(infoContact)
        }
    }
}
