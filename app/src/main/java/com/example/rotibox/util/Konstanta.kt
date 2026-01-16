package com.example.rotibox.util

/**
 * Konstanta yang digunakan di seluruh aplikasi
 */
object Konstanta {
    
    // SharedPreferences Keys
    const val PREF_NAME = "RotiBoxPreferences"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // User Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_PELANGGAN = "pelanggan"
    
    // Order Status
    const val STATUS_MENUNGGU_KONFIRMASI = "menunggu_konfirmasi"
    const val STATUS_PROSES = "proses"
    const val STATUS_SELESAI = "selesai"
    const val STATUS_DIBATALKAN = "dibatalkan"
    
    // Delivery Types
    const val DELIVERY_AMBIL = "ambil"
    const val DELIVERY_ANTAR = "antar"
    
    // Navigation Arguments
    const val ARG_MENU_ID = "menu_id"
    const val ARG_ORDER_ID = "order_id"
    
    // Date Format
    const val DATE_FORMAT = "dd MMM yyyy"
    const val DATE_TIME_FORMAT = "dd MMM yyyy, HH:mm"
    const val TIME_FORMAT = "HH:mm"
}
