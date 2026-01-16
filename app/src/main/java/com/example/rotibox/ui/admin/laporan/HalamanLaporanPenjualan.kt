package com.example.rotibox.ui.admin.laporan

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rotibox.data.local.entity.OrderDenganItem
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.components.EmptyState
import com.example.rotibox.util.CsvExportUtil
import com.example.rotibox.util.formatRupiah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: LaporanViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val filteredOrders by viewModel.filteredOrders.observeAsState(emptyList())
    val totalRevenue by viewModel.totalRevenue.observeAsState(0L)
    val totalOrders by viewModel.totalOrders.observeAsState(0)
    val averageOrderValue by viewModel.averageOrderValue.observeAsState(0L)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val exportResult by viewModel.exportResult.observeAsState()
    
    var selectedPreset by remember { mutableStateOf(LaporanViewModel.FilterPreset.TODAY) }
    var isExporting by remember { mutableStateOf(false) }
    
    // Handle export result
    LaunchedEffect(exportResult) {
        exportResult?.let { result ->
            when (result) {
                is LaporanViewModel.ExportResult.Success -> {
                    Toast.makeText(context, "CSV berhasil disimpan di Downloads", Toast.LENGTH_LONG).show()
                }
                is LaporanViewModel.ExportResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.clearExportResult()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Penjualan") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Preset Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedPreset == LaporanViewModel.FilterPreset.TODAY,
                    onClick = {
                        selectedPreset = LaporanViewModel.FilterPreset.TODAY
                        viewModel.loadPresetFilter(LaporanViewModel.FilterPreset.TODAY)
                    },
                    label = { Text("Hari Ini") }
                )
                FilterChip(
                    selected = selectedPreset == LaporanViewModel.FilterPreset.LAST_7_DAYS,
                    onClick = {
                        selectedPreset = LaporanViewModel.FilterPreset.LAST_7_DAYS
                        viewModel.loadPresetFilter(LaporanViewModel.FilterPreset.LAST_7_DAYS)
                    },
                    label = { Text("7 Hari") }
                )
                FilterChip(
                    selected = selectedPreset == LaporanViewModel.FilterPreset.LAST_30_DAYS,
                    onClick = {
                        selectedPreset = LaporanViewModel.FilterPreset.LAST_30_DAYS
                        viewModel.loadPresetFilter(LaporanViewModel.FilterPreset.LAST_30_DAYS)
                    },
                    label = { Text("30 Hari") }
                )
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Dashboard Summary
                    item {
                        DashboardSummarySection(
                            totalRevenue = totalRevenue,
                            totalOrders = totalOrders,
                            averageOrderValue = averageOrderValue
                        )
                    }
                    
                    // Export Button
                    item {
                        OutlinedButton(
                            onClick = {
                                if (filteredOrders.isNotEmpty()) {
                                    isExporting = true
                                    coroutineScope.launch {
                                        val csvPath = withContext(Dispatchers.IO) {
                                            val calendar = Calendar.getInstance()
                                            CsvExportUtil.exportSalesReport(
                                                context,
                                                filteredOrders,
                                                0L,
                                                calendar.timeInMillis
                                            )
                                        }
                                        isExporting = false
                                        if (csvPath != null) {
                                            viewModel.setExportResult(LaporanViewModel.ExportResult.Success(csvPath))
                                        } else {
                                            viewModel.setExportResult(LaporanViewModel.ExportResult.Error("Gagal membuat CSV"))
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isExporting && filteredOrders.isNotEmpty()
                        ) {
                            if (isExporting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Download, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isExporting) "Membuat CSV..." else "Export ke CSV")
                        }
                    }
                    
                    // Order List Header
                    item {
                        Text(
                            text = "Daftar Pesanan Selesai",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Order List
                    if (filteredOrders.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.Receipt,
                                message = "Tidak ada pesanan selesai",
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                            )
                        }
                    } else {
                        items(filteredOrders) { orderDetail ->
                            SalesOrderCard(orderDetail)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSummarySection(
    totalRevenue: Long,
    totalOrders: Int,
    averageOrderValue: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ringkasan Penjualan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Divider()
            
            // Total Revenue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        text = "Total Pendapatan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = formatRupiah(totalRevenue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            // Total Orders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        text = "Total Pesanan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = "$totalOrders pesanan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            // Average Order Value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        text = "Rata-rata/Pesanan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = formatRupiah(averageOrderValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun SalesOrderCard(orderDetail: OrderDenganItem) {
    val order = orderDetail.order
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Order #${order.id.takeLast(8)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(Date(order.orderDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${orderDetail.items.sumOf { it.quantity }} item",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = formatRupiah(order.totalAmount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
