package com.example.rotibox.ui.admin.kelola_pesanan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.rotibox.util.formatRupiah
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageOrdersScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: KelolaPesananViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val orderList by viewModel.orderList.observeAsState(emptyList())
    val updateStatusResult by viewModel.updateStatusResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedFilter by remember { mutableStateOf("Menunggu") }
    
    // Handle update status result
    LaunchedEffect(updateStatusResult) {
        updateStatusResult?.let { result ->
            when (result) {
                is KelolaPesananViewModel.UpdateStatusResult.Success -> {
                    snackbarHostState.showSnackbar(result.message)
                }
                is KelolaPesananViewModel.UpdateStatusResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }
    
    // Filter orders based on selected filter
    val filteredOrders = when (selectedFilter) {
        "Menunggu" -> orderList.filter { it.order.status == "pending" }
        "Dikonfirmasi" -> orderList.filter { it.order.status == "confirmed" }
        "Selesai" -> orderList.filter { it.order.status == "completed" }
        "Dibatalkan" -> orderList.filter { it.order.status == "cancelled" }
        else -> orderList
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val pendingCount = orderList.count { it.order.status == "pending" }
                val confirmedCount = orderList.count { it.order.status == "confirmed" }
                val completedCount = orderList.count { it.order.status == "completed" }
                val cancelledCount = orderList.count { it.order.status == "cancelled" }
                
                item {
                    FilterChip(
                        selected = selectedFilter == "Semua",
                        onClick = { selectedFilter = "Semua" },
                        label = { Text("Semua (${orderList.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "Menunggu",
                        onClick = { selectedFilter = "Menunggu" },
                        label = { Text("Menunggu ($pendingCount)") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "Dikonfirmasi",
                        onClick = { selectedFilter = "Dikonfirmasi" },
                        label = { Text("Proses ($confirmedCount)") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "Selesai",
                        onClick = { selectedFilter = "Selesai" },
                        label = { Text("Selesai ($completedCount)") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "Dibatalkan",
                        onClick = { selectedFilter = "Dibatalkan" },
                        label = { Text("Dibatalkan ($cancelledCount)") }
                    )
                }
            }
            
            // Order List
            if (filteredOrders.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Receipt,
                    message = if (selectedFilter == "Semua") "Belum ada pesanan" else "Tidak ada pesanan $selectedFilter",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { orderDetail ->
                        AdminOrderCard(
                            orderDetail = orderDetail,
                            onClick = { onOrderClick(orderDetail.order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(
    orderDetail: OrderDenganItem,
    onClick: () -> Unit
) {
    val order = orderDetail.order
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                
                // Status Badge
                Surface(
                    color = when (order.status) {
                        "pending" -> MaterialTheme.colorScheme.errorContainer
                        "confirmed" -> MaterialTheme.colorScheme.tertiaryContainer
                        "completed" -> MaterialTheme.colorScheme.primaryContainer
                        "cancelled" -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = when (order.status) {
                            "pending" -> "Menunggu"
                            "confirmed" -> "Dikonfirmasi"
                            "completed" -> "Selesai"
                            "cancelled" -> "Dibatalkan"
                            else -> order.status
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = when (order.status) {
                            "pending" -> MaterialTheme.colorScheme.onErrorContainer
                            "confirmed" -> MaterialTheme.colorScheme.onTertiaryContainer
                            "completed" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "cancelled" -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            Divider()
            
            // Order Date
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateFormat.format(Date(order.orderDate)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Delivery Method
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (order.delivery == "ambil") Icons.Default.Store else Icons.Default.DeliveryDining,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (order.delivery == "ambil") "Ambil di Toko" else "Antar ke Alamat",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Total Items
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${orderDetail.items.sumOf { it.quantity }} item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Divider()
            
            // Total Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Pembayaran",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
