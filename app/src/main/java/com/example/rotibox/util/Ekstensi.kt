package com.example.rotibox.util

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.rotibox.R

/**
 * Extension functions untuk mempermudah coding
 */

/**
 * Extension untuk load placeholder image
 */
fun ImageView.loadPlaceholder(imageName: String) {
    // Untuk sementara gunakan placeholder drawable
    // Nanti bisa diganti dengan library image loading seperti Glide/Coil
    val drawableRes = when {
        imageName.contains("tawar") -> R.drawable.ic_placeholder_roti
        imageName.contains("cokelat") -> R.drawable.ic_placeholder_roti
        imageName.contains("keju") -> R.drawable.ic_placeholder_roti
        imageName.contains("pisang") -> R.drawable.ic_placeholder_roti
        imageName.contains("abon") -> R.drawable.ic_placeholder_roti
        imageName.contains("kismis") -> R.drawable.ic_placeholder_roti
        else -> R.drawable.ic_placeholder_roti
    }
    
    try {
        setImageResource(drawableRes)
    } catch (e: Exception) {
        // Fallback jika drawable tidak ditemukan
        setImageResource(android.R.drawable.ic_menu_gallery)
    }
}

/**
 * Extension untuk validasi email sederhana
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Extension untuk validasi password (minimal 6 karakter)
 */
fun String.isValidPassword(): Boolean {
    return this.length >= 6
}

/**
 * Format angka menjadi format Rupiah
 */
fun formatRupiah(amount: Long): String {
    return "Rp ${String.format("%,d", amount).replace(',', '.')}"
}
