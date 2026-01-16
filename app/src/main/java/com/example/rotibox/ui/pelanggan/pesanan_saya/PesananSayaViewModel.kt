package com.example.rotibox.ui.pelanggan.pesanan_saya

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rotibox.data.local.entity.OrderDenganItem
import com.example.rotibox.data.repository.PesananRepository

/**
 * ViewModel untuk halaman pesanan saya
 */
class PesananSayaViewModel(private val pesananRepository: PesananRepository) : ViewModel() {
    
    /**
     * Load pesanan milik user
     */
    fun loadPesanan(userId: String): LiveData<List<OrderDenganItem>> {
        return pesananRepository.getOrdersByUser(userId)
    }
}
