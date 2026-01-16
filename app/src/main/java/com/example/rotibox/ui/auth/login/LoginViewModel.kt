package com.example.rotibox.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.UserEntity
import com.example.rotibox.data.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk halaman login
 */
class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    // LiveData untuk hasil login
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    
    /**
     * Fungsi untuk login
     * Validasi input dan panggil repository
     */
    fun login(email: String, password: String) {
        // Validasi input
        if (email.isBlank()) {
            _loginResult.value = LoginResult.Error("Email tidak boleh kosong")
            return
        }
        
        if (password.isBlank()) {
            _loginResult.value = LoginResult.Error("Password tidak boleh kosong")
            return
        }
        
        // Proses login
        viewModelScope.launch {
            try {
                val user = authRepository.login(email, password)
                if (user != null) {
                    _loginResult.value = LoginResult.Success(user)
                } else {
                    _loginResult.value = LoginResult.Error("Email atau password salah")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }
    
    /**
     * Sealed class untuk hasil login
     */
    sealed class LoginResult {
        data class Success(val user: UserEntity) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}
