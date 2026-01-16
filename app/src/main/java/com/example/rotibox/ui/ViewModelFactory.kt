package com.example.rotibox.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rotibox.RotiBoxApplication
import com.example.rotibox.data.repository.*
import com.example.rotibox.ui.admin.beranda_admin.BerandaAdminViewModel
import com.example.rotibox.ui.admin.info_kontak.InfoKontakViewModel
import com.example.rotibox.ui.admin.kelola_menu.KelolaMenuViewModel
import com.example.rotibox.ui.admin.kelola_pesanan.KelolaPesananViewModel
import com.example.rotibox.ui.admin.laporan.LaporanViewModel
import com.example.rotibox.ui.auth.daftar.DaftarViewModel
import com.example.rotibox.ui.auth.login.LoginViewModel
import com.example.rotibox.ui.pelanggan.beranda.BerandaPelangganViewModel
import com.example.rotibox.ui.pelanggan.checkout.CheckoutViewModel
import com.example.rotibox.ui.pelanggan.detail_menu.DetailMenuViewModel
import com.example.rotibox.ui.pelanggan.detail_pesanan.DetailPesananViewModel
import com.example.rotibox.ui.pelanggan.keranjang.KeranjangViewModel
import com.example.rotibox.ui.pelanggan.pesanan_saya.PesananSayaViewModel
import com.example.rotibox.ui.pelanggan.profil.ProfilViewModel

/**
 * ViewModelFactory untuk membuat ViewModel dengan dependency injection manual
 * Ini adalah cara sederhana tanpa menggunakan Hilt/Dagger
 */
class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val menuRepository: MenuRepository,
    private val keranjangRepository: KeranjangRepository,
    private val pesananRepository: PesananRepository,
    private val laporanRepository: LaporanRepository,
    private val infoContactRepository: InfoContactRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Auth ViewModels
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(DaftarViewModel::class.java) -> {
                DaftarViewModel(authRepository) as T
            }
            
            // Pelanggan ViewModels
            modelClass.isAssignableFrom(BerandaPelangganViewModel::class.java) -> {
                BerandaPelangganViewModel(menuRepository) as T
            }
            modelClass.isAssignableFrom(DetailMenuViewModel::class.java) -> {
                DetailMenuViewModel(menuRepository, keranjangRepository) as T
            }
            modelClass.isAssignableFrom(KeranjangViewModel::class.java) -> {
                KeranjangViewModel(keranjangRepository) as T
            }
            modelClass.isAssignableFrom(CheckoutViewModel::class.java) -> {
                CheckoutViewModel(keranjangRepository, pesananRepository, authRepository, infoContactRepository) as T
            }
            modelClass.isAssignableFrom(PesananSayaViewModel::class.java) -> {
                PesananSayaViewModel(pesananRepository) as T
            }
            modelClass.isAssignableFrom(DetailPesananViewModel::class.java) -> {
                DetailPesananViewModel(pesananRepository) as T
            }
            modelClass.isAssignableFrom(ProfilViewModel::class.java) -> {
                ProfilViewModel(authRepository, infoContactRepository) as T
            }
            
            // Admin ViewModels
            modelClass.isAssignableFrom(BerandaAdminViewModel::class.java) -> {
                BerandaAdminViewModel(menuRepository, pesananRepository) as T
            }
            modelClass.isAssignableFrom(KelolaMenuViewModel::class.java) -> {
                KelolaMenuViewModel(menuRepository) as T
            }
            modelClass.isAssignableFrom(KelolaPesananViewModel::class.java) -> {
                KelolaPesananViewModel(pesananRepository) as T
            }
            modelClass.isAssignableFrom(LaporanViewModel::class.java) -> {
                LaporanViewModel(pesananRepository) as T
            }
            modelClass.isAssignableFrom(InfoKontakViewModel::class.java) -> {
                InfoKontakViewModel(infoContactRepository) as T
            }
            
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        
        /**
         * Get singleton instance of ViewModelFactory
         * Untuk digunakan di Composable functions
         */
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val app = context.applicationContext as RotiBoxApplication
                val database = app.database
                ViewModelFactory(
                    AuthRepository(database.userDao()),
                    MenuRepository(database.menuDao()),
                    KeranjangRepository(database.cartItemDao()),
                    PesananRepository(database.orderDao(), database.orderItemDao()),
                    LaporanRepository(database.orderDao()),
                    InfoContactRepository(database.infoContactDao())
                ).also { INSTANCE = it }
            }
        }
    }
}

