package com.example.rotibox.ui.pelanggan.pesanan_saya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rotibox.data.local.entity.OrderDenganItem
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.components.EmptyState
import com.example.rotibox.util.SessionManager
import com.example.rotibox.util.Formatter
import com.example.rotibox.util.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onOrderClick: (String) -> Unit,
    viewModel: PesananSayaViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId() ?: ""
    
    // Load orders - observe LiveData directly
    val orders by viewModel.loadPesanan(userId).observeAsState(initial = emptyList())
    
    Scaffold(
        topBar = {
            com.example.rotibox.ui.components.RotiBoxTopAppBar(
                title = "Pesanan Saya"
            )
        }
    ) { paddingValues ->
        if (orders.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Receipt,
                message = "Belum ada pesanan",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { orderWithItems ->
                    OrderCard(
                        orderWithItems = orderWithItems,
                        onClick = { onOrderClick(orderWithItems.order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    orderWithItems: OrderDenganItem,
    onClick: () -> Unit
) {
    val order = orderWithItems.order
    val statusColor = when (order.status) {
        "pending" -> androidx.compose.ui.graphics.Color(0xFFFFA726) // Orange terang
        "confirmed" -> androidx.compose.ui.graphics.Color(0xFF42A5F5) // Biru terang
        "completed" -> androidx.compose.ui.graphics.Color(0xFF66BB6A) // Hijau terang
        "cancelled" -> androidx.compose.ui.graphics.Color(0xFFEF5350) // Merah terang
        else -> MaterialTheme.colorScheme.outline
    }
    
    val statusText = when (order.status) {
        "pending" -> "Menunggu Konfirmasi"
        "confirmed" -> "Dikonfirmasi"
        "completed" -> "Selesai"
        "cancelled" -> "Dibatalkan"
        else -> order.status
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Order ID & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id.takeLast(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Divider()
            
            // Order Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Tanggal: ${Formatter.formatTanggal(order.pickupDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.LocalShipping,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = if (order.delivery == "ambil") "Ambil di Toko" else "Antar ke Alamat",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Items count
            Text(
                text = "${orderWithItems.items.size} item",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Divider()
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatRupiah(order.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
