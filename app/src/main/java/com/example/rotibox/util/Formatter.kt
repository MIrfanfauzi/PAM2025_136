package com.example.rotibox.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Formatter untuk format mata uang dan tanggal
 */
object Formatter {
    
    /**
     * Format angka ke format Rupiah
     * Contoh: 15000 -> Rp 15.000
     */
    fun formatRupiah(amount: Long): String {
        val localeID = Locale.Builder().setLanguage("in").setRegion("ID").build()
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        return formatter.format(amount).replace("Rp", "Rp ")



    }
    
    /**
     * Format timestamp ke format tanggal
     * Contoh: 1234567890 -> 25 Des 2024
     */
    fun formatTanggal(timestamp: Long): String {

        val sdf = SimpleDateFormat(Konstanta.DATE_FORMAT, Locale.Builder().setLanguage("in").setRegion("ID").build())
        return sdf.format(Date(timestamp))

    }
    
    /**
     * Format timestamp ke format tanggal dan waktu
     * Contoh: 1234567890 -> 25 Des 2024, 14:30
     */
    fun formatTanggalWaktu(timestamp: Long): String {
        val sdf = SimpleDateFormat(Konstanta.DATE_TIME_FORMAT, Locale.Builder().setLanguage("in").setRegion("ID").build())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp ke format waktu saja
     * Contoh: 1234567890 -> 14:30
     */
    fun formatWaktu(timestamp: Long): String {
        val sdf = SimpleDateFormat(Konstanta.TIME_FORMAT, Locale.Builder().setLanguage("in").setRegion("ID").build())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format status order ke bahasa Indonesia yang lebih readable
     */
    fun formatStatusOrder(status: String): String {
        return when (status) {
            Konstanta.STATUS_MENUNGGU_KONFIRMASI -> "Menunggu Konfirmasi"
            Konstanta.STATUS_PROSES -> "Sedang Diproses"
            Konstanta.STATUS_SELESAI -> "Selesai"
            Konstanta.STATUS_DIBATALKAN -> "Dibatalkan"
            else -> status
        }
    }
    
    /**
     * Format delivery type ke bahasa Indonesia
     */
    fun formatDelivery(delivery: String): String {
        return when (delivery) {
            Konstanta.DELIVERY_AMBIL -> "Ambil Sendiri"
            Konstanta.DELIVERY_ANTAR -> "Diantar"
            else -> delivery
        }
    }
}
