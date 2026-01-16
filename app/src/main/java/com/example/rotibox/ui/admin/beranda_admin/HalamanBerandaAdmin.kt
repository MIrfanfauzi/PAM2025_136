package com.example.rotibox.ui.admin.beranda_admin

import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.components.RotiBoxButton
import com.example.rotibox.ui.components.RotiBoxCard
import com.example.rotibox.ui.components.StatCard
import com.example.rotibox.util.SessionManager

/**
 * Admin Dashboard Screen - Composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToManageMenu: () -> Unit,
    onNavigateToManageOrders: () -> Unit,
    onNavigateToSalesReport: () -> Unit,
    onNavigateToInfoKontak: () -> Unit,
    onOrderClick: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: BerandaAdminViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val totalMenuAktif by viewModel.totalMenuAktif
    val totalPesananPending by viewModel.totalPesananPending
    val totalPesananHariIni by viewModel.totalPesananHariIni
    
    LaunchedEffect(Unit) {
        viewModel.loadStatistik()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Admin") },
                actions = {
                    IconButton(onClick = {
                        showLogoutDialog = true
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Statistics Cards
            Text(
                text = "Statistik",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Menu Aktif",
                    value = totalMenuAktif.toString(),
                    icon = Icons.Default.Fastfood,
                    modifier = Modifier.weight(1f)
                )
                
                StatCard(
                    title = "Perlu Konfirmasi",
                    value = totalPesananPending.toString(),
                    icon = Icons.Default.PendingActions,
                    modifier = Modifier.weight(1f)
                )
            }
            
            StatCard(
                title = "Pesanan Hari Ini",
                value = totalPesananHariIni.toString(),
                icon = Icons.Default.Today,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Quick Actions
            Text(
                text = "Menu Cepat",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            RotiBoxCard(onClick = onNavigateToManageMenu) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Kelola Menu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tambah, ubah, atau hapus menu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            RotiBoxCard(onClick = onNavigateToManageOrders) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Kelola Pesanan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lihat dan update status pesanan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            RotiBoxCard(onClick = onNavigateToSalesReport) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Laporan Penjualan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lihat laporan dan statistik penjualan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            RotiBoxCard(onClick = onNavigateToInfoKontak) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Informasi Kontak Toko",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Edit informasi kontak dan pembayaran",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari dashboard admin?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        sessionManager.clearSession()
                        onLogout()
                    }
                ) {
                    Text("Ya, Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
