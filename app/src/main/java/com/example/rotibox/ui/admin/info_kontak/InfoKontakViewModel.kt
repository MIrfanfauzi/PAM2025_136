package com.example.rotibox.ui.admin.info_kontak

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.InfoContactEntity
import com.example.rotibox.data.repository.InfoContactRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk halaman info kontak admin
 */
class InfoKontakViewModel(
    private val infoContactRepository: InfoContactRepository
) : ViewModel() {
    
    private val _infoContact = MutableLiveData<InfoContactEntity?>()
    val infoContact: LiveData<InfoContactEntity?> = _infoContact
    
    private val _updateResult = MutableLiveData<UpdateResult>()
    val updateResult: LiveData<UpdateResult> = _updateResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        loadInfoContact()
    }
    
    /**
     * Load informasi kontak dari database
     */
    fun loadInfoContact() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val info = infoContactRepository.getInfoContact()
                _infoContact.value = info
            } catch (e: Exception) {
                _updateResult.value = UpdateResult.Error("Gagal memuat data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update informasi kontak
     */
    fun updateInfoContact(
        storePhone: String,
        storeEmail: String,
        storeAddress: String,
        infoPayment: String,
        description: String
    ) {
        // Validasi
        if (storePhone.isBlank()) {
            _updateResult.value = UpdateResult.Error("Nomor telepon tidak boleh kosong")
            return
        }
        
        if (storeEmail.isBlank()) {
            _updateResult.value = UpdateResult.Error("Email tidak boleh kosong")
            return
        }
        
        if (storeAddress.isBlank()) {
            _updateResult.value = UpdateResult.Error("Alamat tidak boleh kosong")
            return
        }
        
        if (infoPayment.isBlank()) {
            _updateResult.value = UpdateResult.Error("Informasi pembayaran tidak boleh kosong")
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentInfo = _infoContact.value
                
                if (currentInfo != null) {
                    // Update existing
                    val updatedInfo = currentInfo.copy(
                        storePhone = storePhone,
                        storeEmail = storeEmail,
                        storeAddress = storeAddress,
                        infoPayment = infoPayment,
                        description = description,
                        updatedAt = System.currentTimeMillis()
                    )
                    infoContactRepository.updateInfoContact(updatedInfo)
                } else {
                    // Insert new
                    val newInfo = InfoContactEntity(
                        id = java.util.UUID.randomUUID().toString(),
                        storePhone = storePhone,
                        storeEmail = storeEmail,
                        storeAddress = storeAddress,
                        infoPayment = infoPayment,
                        description = description,
                        updatedAt = System.currentTimeMillis()
                    )
                    infoContactRepository.insertInfoContact(newInfo)
                }
                
                loadInfoContact()
                _updateResult.value = UpdateResult.Success
            } catch (e: Exception) {
                _updateResult.value = UpdateResult.Error("Gagal menyimpan data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Reset update result
     */
    fun resetUpdateResult() {
        _updateResult.value = null
    }
    
    /**
     * Sealed class untuk hasil update
     */
    sealed class UpdateResult {
        object Success : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }
}
