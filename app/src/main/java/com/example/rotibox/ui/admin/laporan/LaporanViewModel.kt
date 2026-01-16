package com.example.rotibox.ui.admin.laporan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.OrderDenganItem
import com.example.rotibox.data.repository.PesananRepository
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel untuk halaman laporan penjualan (admin)
 */
class LaporanViewModel(private val pesananRepository: PesananRepository) : ViewModel() {
    
    private val _filteredOrders = MutableLiveData<List<OrderDenganItem>>(emptyList())
    val filteredOrders: LiveData<List<OrderDenganItem>> = _filteredOrders
    
    private val _totalRevenue = MutableLiveData<Long>(0L)
    val totalRevenue: LiveData<Long> = _totalRevenue
    
    private val _totalOrders = MutableLiveData<Int>(0)
    val totalOrders: LiveData<Int> = _totalOrders
    
    private val _averageOrderValue = MutableLiveData<Long>(0L)
    val averageOrderValue: LiveData<Long> = _averageOrderValue
    
    private val _exportResult = MutableLiveData<ExportResult?>()
    val exportResult: LiveData<ExportResult?> = _exportResult
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        // Load today's data by default
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis
        
        loadSalesData(startOfDay, endOfDay)
    }
    
    /**
     * Load sales data berdasarkan range tanggal
     */
    fun loadSalesData(startDate: Long, endDate: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val allOrders = pesananRepository.getAllOrdersSync()
                
                // Filter: completed orders within date range
                val filtered = allOrders.filter { orderDetail ->
                    orderDetail.order.status == "completed" &&
                    orderDetail.order.orderDate >= startDate &&
                    orderDetail.order.orderDate <= endDate
                }
                
                _filteredOrders.value = filtered
                
                // Calculate metrics
                val revenue = filtered.sumOf { it.order.totalAmount }
                val count = filtered.size
                val average = if (count > 0) revenue / count else 0L
                
                _totalRevenue.value = revenue
                _totalOrders.value = count
                _averageOrderValue.value = average
                
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _exportResult.value = ExportResult.Error("Gagal memuat data: ${e.message}")
            }
        }
    }
    
    /**
     * Load data untuk preset filter
     */
    fun loadPresetFilter(preset: FilterPreset) {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        
        val startDate = when (preset) {
            FilterPreset.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            FilterPreset.LAST_7_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            FilterPreset.LAST_30_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                calendar.timeInMillis
            }
        }
        
        loadSalesData(startDate, endDate)
    }
    
    /**
     * Clear export result
     */
    fun clearExportResult() {
        _exportResult.value = null
    }
    
    /**
     * Set export result
     */
    fun setExportResult(result: ExportResult) {
        _exportResult.value = result
    }
    
    /**
     * Enum untuk preset filter
     */
    enum class FilterPreset {
        TODAY,
        LAST_7_DAYS,
        LAST_30_DAYS
    }
    
    /**
     * Sealed class untuk hasil export
     */
    sealed class ExportResult {
        data class Success(val filePath: String) : ExportResult()
        data class Error(val message: String) : ExportResult()
    }
}
