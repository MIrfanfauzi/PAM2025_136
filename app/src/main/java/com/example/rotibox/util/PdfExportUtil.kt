package com.example.rotibox.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.rotibox.data.local.entity.OrderDenganItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class untuk export PDF struk pembelian
 */
object PdfExportUtil {
    
    /**
     * Generate PDF struk pembelian
     * @param context Context untuk akses file system
     * @param orderDetail Detail pesanan dengan items dan user
     * @return File path PDF yang di-generate
     */
    fun generateStrukPDF(context: Context, orderDetail: OrderDenganItem): String? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = android.graphics.Paint()
            
            // Setup fonts
            paint.textSize = 12f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            
            var yPos = 50f
            val leftMargin = 50f
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
            
            // Header
            paint.textSize = 20f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            paint.textAlign = android.graphics.Paint.Align.CENTER
            canvas.drawText("ROTIBOX", pageInfo.pageWidth / 2f, yPos, paint)
            yPos += 30f
            
            paint.textSize = 14f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            canvas.drawText("Struk Pembelian", pageInfo.pageWidth / 2f, yPos, paint)
            yPos += 40f
            
            // Reset alignment
            paint.textAlign = android.graphics.Paint.Align.LEFT
            paint.textSize = 12f
            
            // Separator
            canvas.drawLine(leftMargin, yPos, pageInfo.pageWidth - leftMargin, yPos, paint)
            yPos += 20f
            
            // Customer Name (if available)
            orderDetail.user?.let { user ->
                paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                canvas.drawText("Pelanggan: ${user.name}", leftMargin, yPos, paint)
                yPos += 25f
                paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            }
            
            // Order Info
            canvas.drawText("Order ID: #${orderDetail.order.id.takeLast(8)}", leftMargin, yPos, paint)
            yPos += 20f
            canvas.drawText("Tanggal: ${dateFormat.format(Date(orderDetail.order.orderDate))}", leftMargin, yPos, paint)
            yPos += 20f
            canvas.drawText("Pengantaran: ${dateFormat.format(Date(orderDetail.order.pickupDate))}", leftMargin, yPos, paint)
            yPos += 20f
            canvas.drawText("Metode: ${if (orderDetail.order.delivery == "ambil") "Ambil di Toko" else "Antar ke Alamat"}", leftMargin, yPos, paint)
            yPos += 30f
            
            // Separator
            canvas.drawLine(leftMargin, yPos, pageInfo.pageWidth - leftMargin, yPos, paint)
            yPos += 20f
            
            // Items Header
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            canvas.drawText("Daftar Pesanan", leftMargin, yPos, paint)
            yPos += 25f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            
            // Items
            orderDetail.items.forEach { item ->
                canvas.drawText("${item.menuName} x${item.quantity}", leftMargin, yPos, paint)
                yPos += 20f
                val priceText = "Rp ${formatRupiah(item.price)}"
                val subtotalText = "Rp ${formatRupiah(item.price * item.quantity)}"
                canvas.drawText(priceText, leftMargin + 20f, yPos, paint)
                paint.textAlign = android.graphics.Paint.Align.RIGHT
                canvas.drawText(subtotalText, pageInfo.pageWidth - leftMargin, yPos, paint)
                paint.textAlign = android.graphics.Paint.Align.LEFT
                yPos += 25f
            }
            
            // Separator
            canvas.drawLine(leftMargin, yPos, pageInfo.pageWidth - leftMargin, yPos, paint)
            yPos += 20f
            
            // Total
            paint.textSize = 16f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            canvas.drawText("TOTAL", leftMargin, yPos, paint)
            paint.textAlign = android.graphics.Paint.Align.RIGHT
            canvas.drawText("Rp ${formatRupiah(orderDetail.order.totalAmount)}", pageInfo.pageWidth - leftMargin, yPos, paint)
            yPos += 30f
            
            // Separator
            paint.textAlign = android.graphics.Paint.Align.LEFT
            canvas.drawLine(leftMargin, yPos, pageInfo.pageWidth - leftMargin, yPos, paint)
            yPos += 30f
            
            // Footer
            paint.textSize = 12f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            paint.textAlign = android.graphics.Paint.Align.CENTER
            canvas.drawText("Terima Kasih!", pageInfo.pageWidth / 2f, yPos, paint)
            
            pdfDocument.finishPage(page)
            
            // Save PDF
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fileName = "Struk_${orderDetail.order.id.takeLast(8)}_${System.currentTimeMillis()}.pdf"
            val file = File(downloadsDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            
            pdfDocument.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun formatRupiah(amount: Long): String {
        return String.format("%,d", amount).replace(',', '.')
    }
}
