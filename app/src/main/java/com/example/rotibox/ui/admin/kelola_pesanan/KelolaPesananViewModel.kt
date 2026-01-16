package com.example.rotibox.ui.admin.kelola_pesanan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.OrderDenganItem
import com.example.rotibox.data.repository.PesananRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk halaman kelola pesanan (admin)
 */
class KelolaPesananViewModel(private val pesananRepository: PesananRepository) : ViewModel() {
    
    val orderList: LiveData<List<OrderDenganItem>> = pesananRepository.getAllOrders()
    
    private val _updateStatusResult = MutableLiveData<UpdateStatusResult>()
    val updateStatusResult: LiveData<UpdateStatusResult> = _updateStatusResult
    
    /**
     * Get order by ID
     */
    suspend fun getOrderById(orderId: String): OrderDenganItem? {
        return try {
            pesananRepository.getOrderById(orderId)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Update status pesanan
     */
    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                pesananRepository.updateOrderStatus(orderId, newStatus)
                _updateStatusResult.value = UpdateStatusResult.Success("Status pesanan berhasil diupdate")
            } catch (e: Exception) {
                _updateStatusResult.value = UpdateStatusResult.Error("Gagal update status: ${e.message}")
            }
        }
    }
    
    /**
     * Sealed class untuk hasil update status
     */
    sealed class UpdateStatusResult {
        data class Success(val message: String) : UpdateStatusResult()
        data class Error(val message: String) : UpdateStatusResult()
    }
}
