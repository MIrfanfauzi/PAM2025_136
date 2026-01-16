package com.example.rotibox.data.repository

import com.example.rotibox.data.local.dao.OrderDao

/**
 * Repository untuk laporan penjualan
 * Menangani perhitungan total penjualan dan statistik
 */
class LaporanRepository(private val orderDao: OrderDao) {
    
    /**
     * Data class untuk hasil laporan
     */
    data class LaporanPenjualan(
        val totalPenjualan: Long,
        val jumlahTransaksi: Int
    )
    
    /**
     * Hitung total penjualan dalam rentang tanggal
     * @param startDate Tanggal mulai (timestamp)
     * @param endDate Tanggal akhir (timestamp)
     * @return LaporanPenjualan dengan total dan jumlah transaksi
     */
    suspend fun getLaporanPenjualan(startDate: Long, endDate: Long): LaporanPenjualan {
        val totalPenjualan = orderDao.getTotalSales(startDate, endDate) ?: 0L
        val jumlahTransaksi = orderDao.getCompletedOrderCount(startDate, endDate)
        
        return LaporanPenjualan(
            totalPenjualan = totalPenjualan,
            jumlahTransaksi = jumlahTransaksi
        )
    }
    
    /**
     * Hitung total penjualan hari ini
     */
    suspend fun getLaporanHariIni(): LaporanPenjualan {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis
        
        return getLaporanPenjualan(startOfDay, endOfDay)
    }
    
    /**
     * Hitung total penjualan bulan ini
     */
    suspend fun getLaporanBulanIni(): LaporanPenjualan {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        val endOfMonth = calendar.timeInMillis
        
        return getLaporanPenjualan(startOfMonth, endOfMonth)
    }
}
