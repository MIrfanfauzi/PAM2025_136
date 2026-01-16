package com.example.rotibox.ui.admin.beranda_admin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.repository.MenuRepository
import com.example.rotibox.data.repository.PesananRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class BerandaAdminViewModel(
    private val menuRepository: MenuRepository,
    private val pesananRepository: PesananRepository
) : ViewModel() {
    private val _totalMenuAktif = mutableStateOf(0)
    val totalMenuAktif: State<Int> = _totalMenuAktif
    
    private val _totalPesananPending = mutableStateOf(0)
    val totalPesananPending: State<Int> = _totalPesananPending
    
    private val _totalPesananHariIni = mutableStateOf(0)
    val totalPesananHariIni: State<Int> = _totalPesananHariIni

    fun loadStatistik() {
        viewModelScope.launch {
            // Total menu aktif
            val allMenus = menuRepository.getAllMenusSync()
            _totalMenuAktif.value = allMenus.count { it.isActive }
            
            // Total pesanan pending (menunggu konfirmasi)
            val allOrders = pesananRepository.getAllOrdersSync()
            _totalPesananPending.value = allOrders.count { it.order.status == "pending" }
            
            // Total pesanan hari ini (pickup date = hari ini)
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            val todayStart = today.timeInMillis
            
            today.set(Calendar.HOUR_OF_DAY, 23)
            today.set(Calendar.MINUTE, 59)
            today.set(Calendar.SECOND, 59)
            val todayEnd = today.timeInMillis
            
            _totalPesananHariIni.value = allOrders.count { 
                it.order.pickupDate in todayStart..todayEnd
            }
        }
    }
}

