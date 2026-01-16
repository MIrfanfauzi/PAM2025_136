package com.example.rotibox.ui.pelanggan.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.CartItemDenganMenu
import com.example.rotibox.data.local.entity.InfoContactEntity
import com.example.rotibox.data.local.entity.UserEntity
import com.example.rotibox.data.repository.AuthRepository
import com.example.rotibox.data.repository.InfoContactRepository
import com.example.rotibox.data.repository.KeranjangRepository
import com.example.rotibox.data.repository.PesananRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk halaman checkout
 */
class CheckoutViewModel(
    private val keranjangRepository: KeranjangRepository,
    private val pesananRepository: PesananRepository,
    private val authRepository: AuthRepository,
    private val infoContactRepository: InfoContactRepository
) : ViewModel() {
    
    private val _checkoutResult = MutableLiveData<CheckoutResult>()
    val checkoutResult: LiveData<CheckoutResult> = _checkoutResult
    
    private val _userProfile = MutableLiveData<UserEntity?>()
    val userProfile: LiveData<UserEntity?> = _userProfile
    
    private val _infoContact = MutableLiveData<InfoContactEntity?>()
    val infoContact: LiveData<InfoContactEntity?> = _infoContact
    
    private val _cartItems = MutableLiveData<List<CartItemDenganMenu>>()
    val cartItems: LiveData<List<CartItemDenganMenu>> = _cartItems
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    /**
     * Load cart items untuk ditampilkan di checkout
     */
    fun loadCartItems(userId: String) {
        viewModelScope.launch {
            try {
                // Observe cart items from repository
                keranjangRepository.getCartItems(userId).observeForever { items ->
                    _cartItems.value = items
                }
            } catch (e: Exception) {
                // Handle error silently or show message
            }
        }
    }
    
    /**
     * Load user profile untuk auto-fill data
     */
    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val user = authRepository.getUserById(userId)
                _userProfile.value = user
            } catch (e: Exception) {
                // Handle error silently or show message
            }
        }
    }
    
    /**
     * Load informasi kontak toko untuk ditampilkan di popup sukses
     */
    fun loadInfoContact() {
        viewModelScope.launch {
            try {
                val info = infoContactRepository.getInfoContact()
                _infoContact.value = info
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
    
    /**
     * Proses checkout
     * Membuat order dari keranjang dan mengosongkan keranjang
     */
    fun checkout(
        userId: String,
        pickupDate: Long,
        delivery: String,
        paymentMethod: String,
        note: String
    ) {
        val currentCartItems = _cartItems.value ?: emptyList()
        
        // Validasi
        if (currentCartItems.isEmpty()) {
            _checkoutResult.value = CheckoutResult.Error("Keranjang kosong")
            return
        }
        
        if (pickupDate <= System.currentTimeMillis()) {
            _checkoutResult.value = CheckoutResult.Error("Tanggal pengantaran harus di masa depan")
            return
        }
        
        if (paymentMethod.isBlank()) {
            _checkoutResult.value = CheckoutResult.Error("Pilih metode pembayaran")
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Buat order dengan catatan yang menyertakan metode pembayaran
                val fullNote = "Metode Pembayaran: $paymentMethod\n$note"
                
                val orderId = pesananRepository.createOrder(
                    userId = userId,
                    cartItems = currentCartItems,
                    pickupDate = pickupDate,
                    delivery = delivery,
                    note = fullNote
                )
                
                // Kosongkan keranjang
                keranjangRepository.clearCart(userId)
                
                _checkoutResult.value = CheckoutResult.Success(orderId)
            } catch (e: Exception) {
                _checkoutResult.value = CheckoutResult.Error("Gagal membuat pesanan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Reset checkout result
     */
    fun resetCheckoutResult() {
        _checkoutResult.value = null
    }
    
    /**
     * Sealed class untuk hasil checkout
     */
    sealed class CheckoutResult {
        data class Success(val orderId: String) : CheckoutResult()
        data class Error(val message: String) : CheckoutResult()
    }
}
