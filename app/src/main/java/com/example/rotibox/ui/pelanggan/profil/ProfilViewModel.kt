package com.example.rotibox.ui.pelanggan.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotibox.data.local.entity.InfoContactEntity
import com.example.rotibox.data.local.entity.UserEntity
import com.example.rotibox.data.repository.AuthRepository
import com.example.rotibox.data.repository.InfoContactRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk halaman profil
 */
class ProfilViewModel(
    private val authRepository: AuthRepository,
    private val infoContactRepository: InfoContactRepository
) : ViewModel() {
    
    private val _user = MutableLiveData<UserEntity?>()
    val user: LiveData<UserEntity?> = _user
    
    private val _infoContact = MutableLiveData<InfoContactEntity?>()
    val infoContact: LiveData<InfoContactEntity?> = _infoContact
    
    private val _updateResult = MutableLiveData<UpdateResult>()
    val updateResult: LiveData<UpdateResult> = _updateResult
    
    /**
     * Load data user
     */
    fun loadUser(userId: String) {
        viewModelScope.launch {
            val userData = authRepository.getUserById(userId)
            _user.value = userData
        }
    }
    
    /**
     * Update profil user
     */
    fun updateProfile(user: UserEntity) {
        viewModelScope.launch {
            try {
                authRepository.updateUser(user)
                _updateResult.value = UpdateResult.Success
            } catch (e: Exception) {
                _updateResult.value = UpdateResult.Error("Gagal update profil: ${e.message}")
            }
        }
    }
    
    /**
     * Load info contact toko
     */
    fun loadInfoContact() {
        viewModelScope.launch {
            val info = infoContactRepository.getInfoContact()
            _infoContact.value = info
        }
    }
    
    /**
     * Sealed class untuk hasil update
     */
    sealed class UpdateResult {
        object Success : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }
}
