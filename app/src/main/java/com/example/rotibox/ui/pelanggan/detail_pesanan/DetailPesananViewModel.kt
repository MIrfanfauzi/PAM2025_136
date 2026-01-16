package com.example.rotibox.ui.pelanggan.detail_pesanan

import androidx.lifecycle.ViewModel
import com.example.rotibox.data.local.entity.OrderDenganItem
import com.example.rotibox.data.repository.PesananRepository

/**
 * ViewModel untuk halaman detail pesanan
 */
class DetailPesananViewModel(private val pesananRepository: PesananRepository) : ViewModel() {
    
    /**
     * Get order detail by ID
     */
    suspend fun getOrderById(orderId: String): OrderDenganItem? {
        return pesananRepository.getOrderById(orderId)
    }
}
