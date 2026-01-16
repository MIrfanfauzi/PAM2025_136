package com.example.rotibox.ui.admin.kelola_pesanan

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.rotibox.util.formatRupiah
import com.example.rotibox.util.PdfExportUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: KelolaPesananViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var orderDetail by remember { mutableStateOf<OrderDenganItem?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var isExportingPdf by remember { mutableStateOf(false) }
    val updateStatusResult by viewModel.updateStatusResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Load order detail
    LaunchedEffect(orderId) {
        orderDetail = viewModel.getOrderById(orderId)
    }
    
    // Handle update status result
    LaunchedEffect(updateStatusResult) {
        updateStatusResult?.let { result ->
            when (result) {
                is KelolaPesananViewModel.UpdateStatusResult.Success -> {
                    snackbarHostState.showSnackbar(result.message)
                    // Reload order detail
                    orderDetail = viewModel.getOrderById(orderId)
                }
                is KelolaPesananViewModel.UpdateStatusResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
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
        if (orderDetail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Customer Info Section (if available)
                orderDetail!!.user?.let { user ->
                    AdminCustomerInfoSection(user)
                }
                
                // Order Info Section
                AdminOrderInfoSection(orderDetail!!)
                
                // Order Items Section
                AdminOrderItemsSection(orderDetail!!)
                
                // Payment Summary Section
                AdminPaymentSummarySection(orderDetail!!)
                
                // Export PDF Button
                OutlinedButton(
                    onClick = {
                        isExportingPdf = true
                        coroutineScope.launch {
                            val pdfPath = withContext(Dispatchers.IO) {
                                PdfExportUtil.generateStrukPDF(context, orderDetail!!)
                            }
                            isExportingPdf = false
                            if (pdfPath != null) {
                                Toast.makeText(context, "PDF berhasil disimpan di Downloads", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Gagal membuat PDF", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isExportingPdf
                ) {
                    if (isExportingPdf) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Print, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isExportingPdf) "Membuat PDF..." else " Cetak Struk")
                }
                
                // Update Status Button
                if (orderDetail!!.order.status != "completed" && orderDetail!!.order.status != "cancelled") {
                    Button(
                        onClick = { showStatusDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ubah Status Pesanan")
                    }
                }
            }
        }
    }
    
    // Status Update Dialog
    if (showStatusDialog && orderDetail != null) {
        UpdateStatusDialog(
            currentStatus = orderDetail!!.order.status,
            onDismiss = { showStatusDialog = false },
            onConfirm = { newStatus ->
                viewModel.updateOrderStatus(orderId, newStatus)
                showStatusDialog = false
            }
        )
    }
}

@Composable
fun AdminCustomerInfoSection(user: com.example.rotibox.data.local.entity.UserEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Informasi Pelanggan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Divider()
            
            // Customer Name
            InfoRow(
                icon = Icons.Default.Person,
                label = "Nama",
                value = user.name
            )
            
            // Email
            InfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = user.email
            )
            
            // Phone
            InfoRow(
                icon = Icons.Default.Phone,
                label = "Telepon",
                value = user.phone
            )
            
            // Address (if available)
            if (user.address.isNotBlank()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Alamat",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = user.address,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOrderInfoSection(orderDetail: OrderDenganItem) {
    val order = orderDetail.order
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Informasi Pesanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            // Order ID
            InfoRow(
                icon = Icons.Default.Tag,
                label = "Order ID",
                value = "#${order.id.takeLast(8)}"
            )
            
            // Status
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f)
                )
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
                            "pending" -> "Menunggu Konfirmasi"
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
            
            // Order Date
            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = "Tanggal Pesan",
                value = dateFormat.format(Date(order.orderDate))
            )
            
            // Pickup Date
            InfoRow(
                icon = Icons.Default.Event,
                label = "Tanggal Pengantaran",
                value = dateFormat.format(Date(order.pickupDate))
            )
            
            // Delivery Method
            InfoRow(
                icon = if (order.delivery == "ambil") Icons.Default.Store else Icons.Default.DeliveryDining,
                label = "Metode Pengiriman",
                value = if (order.delivery == "ambil") "Ambil di Toko" else "Antar ke Alamat"
            )
            
            // Note (if exists)
            if (order.note.isNotBlank()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Note,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Catatan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = order.note,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOrderItemsSection(orderDetail: OrderDenganItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Daftar Pesanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            orderDetail.items.forEachIndexed { index, item ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.menuName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = formatRupiah(item.price),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "x${item.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatRupiah(item.price * item.quantity),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    
                    if (index < orderDetail.items.lastIndex) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPaymentSummarySection(orderDetail: OrderDenganItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Pembayaran",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = formatRupiah(orderDetail.order.totalAmount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun UpdateStatusDialog(
    currentStatus: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    
    val statusOptions = listOf(
        "pending" to "Menunggu Konfirmasi",
        "confirmed" to "Dikonfirmasi",
        "completed" to "Selesai",
        "cancelled" to "Dibatalkan"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Status Pesanan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pilih status baru untuk pesanan ini:")
                
                statusOptions.forEach { (value, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == value,
                            onClick = { selectedStatus = value }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedStatus) },
                enabled = selectedStatus != currentStatus
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
