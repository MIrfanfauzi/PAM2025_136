package com.example.rotibox

import android.app.Application
import com.example.rotibox.data.local.db.RotiBoxDatabase

/**
 * Application class untuk inisialisasi database
 */
class RotiBoxApplication : Application() {
    
    // Database instance yang bisa diakses dari seluruh aplikasi
    val database: RotiBoxDatabase by lazy {
        RotiBoxDatabase.getDatabase(this)
    }
    
    override fun onCreate() {
        super.onCreate()
    }
}
