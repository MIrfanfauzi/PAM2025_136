package com.example.rotibox.util

import android.content.Context
import android.os.Environment
import com.example.rotibox.data.local.entity.OrderDenganItem
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class untuk export CSV laporan penjualan
 */
object CsvExportUtil {
    
    /**
     * Export laporan penjualan ke CSV
     * @param context Context untuk akses file system
     * @param orders List pesanan yang akan di-export
     * @param startDate Tanggal mulai filter
     * @param endDate Tanggal akhir filter
     * @return File path CSV yang di-generate
     */
    fun exportSalesReport(
        context: Context,
        orders: List<OrderDenganItem>,
        startDate: Long,
        endDate: Long
    ): String? {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))
            val fileName = "Laporan_Penjualan_${System.currentTimeMillis()}.csv"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            FileWriter(file).use { writer ->
                // Header
                writer.append("Order ID,Tanggal,Jumlah Item,Total\n")
                
                // Data rows
                orders.forEach { orderDetail ->
                    val orderId = "#${orderDetail.order.id.takeLast(8)}"
                    val tanggal = dateFormat.format(Date(orderDetail.order.orderDate))
                    val jumlahItem = orderDetail.items.sumOf { it.quantity }
                    val total = orderDetail.order.totalAmount
                    
                    writer.append("$orderId,$tanggal,$jumlahItem,$total\n")
                }
            }
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Export detailed sales report dengan item breakdown
     */
    fun exportDetailedSalesReport(
        context: Context,
        orders: List<OrderDenganItem>
    ): String? {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID"))
            val fileName = "Laporan_Detail_${System.currentTimeMillis()}.csv"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            FileWriter(file).use { writer ->
                // Header
                writer.append("Order ID,Tanggal,Pelanggan,Item,Quantity,Harga,Subtotal,Total Order\n")
                
                // Data rows
                orders.forEach { orderDetail ->
                    val orderId = "#${orderDetail.order.id.takeLast(8)}"
                    val tanggal = dateFormat.format(Date(orderDetail.order.orderDate))
                    val pelanggan = orderDetail.user?.name ?: "N/A"
                    val totalOrder = orderDetail.order.totalAmount
                    
                    orderDetail.items.forEachIndexed { index, item ->
                        if (index == 0) {
                            // First item includes order info
                            writer.append("$orderId,$tanggal,$pelanggan,${item.menuName},${item.quantity},${item.price},${item.price * item.quantity},$totalOrder\n")
                        } else {
                            // Subsequent items
                            writer.append(",,,${item.menuName},${item.quantity},${item.price},${item.price * item.quantity},\n")
                        }
                    }
                }
            }
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
