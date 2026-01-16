package com.example.rotibox.ui.auth.daftar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.UserEntity
import com.example.rotibox.data.repository.AuthRepository
import com.example.rotibox.util.Konstanta
import com.example.rotibox.util.isValidEmail
import com.example.rotibox.util.isValidPassword
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel untuk halaman daftar (register)
 */
class DaftarViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    // LiveData untuk hasil registrasi
    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult
    
    /**
     * Fungsi untuk register user baru
     */
    fun register(name: String, email: String, phone: String, address: String, password: String, confirmPassword: String) {
        // Validasi input
        if (name.isBlank()) {
            _registerResult.value = RegisterResult.Error("Nama tidak boleh kosong")
            return
        }
        
        if (email.isBlank()) {
            _registerResult.value = RegisterResult.Error("Email tidak boleh kosong")
            return
        }
        
        if (!email.isValidEmail()) {
            _registerResult.value = RegisterResult.Error("Format email tidak valid")
            return
        }
        
        if (phone.isBlank()) {
            _registerResult.value = RegisterResult.Error("Nomor telepon tidak boleh kosong")
            return
        }
        
        if (address.isBlank()) {
            _registerResult.value = RegisterResult.Error("Alamat tidak boleh kosong")
            return
        }
        
        if (password.isBlank()) {
            _registerResult.value = RegisterResult.Error("Password tidak boleh kosong")
            return
        }
        
        if (!password.isValidPassword()) {
            _registerResult.value = RegisterResult.Error("Password minimal 6 karakter")
            return
        }
        
        if (password != confirmPassword) {
            _registerResult.value = RegisterResult.Error("Password dan konfirmasi password tidak sama")
            return
        }
        
        // Proses registrasi
        viewModelScope.launch {
            try {
                val newUser = UserEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    email = email,
                    phone = phone,
                    address = address,
                    password = password,
                    role = Konstanta.ROLE_PELANGGAN // Default role pelanggan
                )
                
                val success = authRepository.register(newUser)
                if (success) {
                    _registerResult.value = RegisterResult.Success
                } else {
                    _registerResult.value = RegisterResult.Error("Email sudah terdaftar")
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }
    
    /**
     * Sealed class untuk hasil registrasi
     */
    sealed class RegisterResult {
        object Success : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }
}
