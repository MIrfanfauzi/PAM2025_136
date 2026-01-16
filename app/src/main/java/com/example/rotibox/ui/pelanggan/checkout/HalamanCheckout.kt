package com.example.rotibox.ui.pelanggan.checkout

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.util.SessionManager
import com.example.rotibox.util.Formatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    viewModel: CheckoutViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId() ?: ""
    
    val userProfile by viewModel.userProfile.observeAsState()
    val infoContact by viewModel.infoContact.observeAsState()
    val checkoutResult by viewModel.checkoutResult.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val cartItems by viewModel.cartItems.observeAsState(emptyList())
    
    // Form states
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var pickupDate by remember { mutableStateOf<Long?>(null) }
    var deliveryMethod by remember { mutableStateOf("ambil") }
    var paymentMethod by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showInfoTokoDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedDelivery by remember { mutableStateOf(false) }
    var expandedPayment by remember { mutableStateOf(false) }
    
    // Load user profile, info contact, dan cart items
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.loadUserProfile(userId)
            viewModel.loadInfoContact()
            viewModel.loadCartItems(userId)
        }
    }
    
    // Auto-fill data dari profil
    LaunchedEffect(userProfile) {
        userProfile?.let { user ->
            customerName = user.name
            customerPhone = user.phone
            customerAddress = user.address
        }
    }
    
    // Handle checkout result
    LaunchedEffect(checkoutResult) {
        when (checkoutResult) {
            is CheckoutViewModel.CheckoutResult.Success -> {
                showSuccessDialog = true
                viewModel.resetCheckoutResult()
            }
            is CheckoutViewModel.CheckoutResult.Error -> {
                errorMessage = (checkoutResult as CheckoutViewModel.CheckoutResult.Error).message
                viewModel.resetCheckoutResult()
            }
            null -> {}
        }
    }
    
    // Date picker dialog
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                pickupDate = calendar.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // Min tomorrow
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Data Pemesan (Auto-filled)
                Text(
                    text = "Data Pemesan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Nomor Telepon") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = { customerAddress = it },
                    label = { Text("Alamat") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 2,
                    maxLines = 4
                )
                
                Divider()
                
                // Section: Tanggal Pengantaran
                Text(
                    text = "Tanggal Pengantaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = pickupDate?.let { Formatter.formatTanggal(it) } ?: "",
                    onValueChange = {},
                    label = { Text("Pilih Tanggal") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Pilih Tanggal")
                        }
                    }
                )
                
                // Section: Metode Pengiriman
                Text(
                    text = "Metode Pengiriman",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                ExposedDropdownMenuBox(
                    expanded = expandedDelivery,
                    onExpandedChange = { expandedDelivery = !expandedDelivery && !isLoading }
                ) {
                    OutlinedTextField(
                        value = when (deliveryMethod) {
                            "ambil" -> "Ambil di Toko"
                            "antar" -> "Antar ke Alamat"
                            else -> ""
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Metode") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDelivery) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDelivery,
                        onDismissRequest = { expandedDelivery = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ambil di Toko") },
                            onClick = {
                                deliveryMethod = "ambil"
                                expandedDelivery = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Antar ke Alamat") },
                            onClick = {
                                deliveryMethod = "antar"
                                expandedDelivery = false
                            }
                        )
                    }
                }
                
                // Section: Metode Pembayaran
                Text(
                    text = "Metode Pembayaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                ExposedDropdownMenuBox(
                    expanded = expandedPayment,
                    onExpandedChange = { expandedPayment = !expandedPayment && !isLoading }
                ) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Metode Pembayaran") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPayment) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPayment,
                        onDismissRequest = { expandedPayment = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Transfer Bank") },
                            onClick = {
                                paymentMethod = "Transfer Bank"
                                expandedPayment = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cash on Delivery (COD)") },
                            onClick = {
                                paymentMethod = "Cash on Delivery (COD)"
                                expandedPayment = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("E-Wallet") },
                            onClick = {
                                paymentMethod = "E-Wallet"
                                expandedPayment = false
                            }
                        )
                    }
                }
                
                // Section: Catatan (Optional)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Catatan (Opsional)") },
                    placeholder = { Text("Tambahkan catatan untuk pesanan Anda") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 3,
                    maxLines = 5
                )
                
                // Tombol Konfirmasi Pesanan
                Button(
                    onClick = {
                        viewModel.checkout(
                            userId = userId,
                            pickupDate = pickupDate ?: 0L,
                            delivery = deliveryMethod,
                            paymentMethod = paymentMethod,
                            note = note
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && pickupDate != null && paymentMethod.isNotBlank() && cartItems.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isLoading) "Memproses..." else "Konfirmasi Pesanan")
                }
            }
            
            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    
    // Success Dialog dengan Info Pembayaran
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "Pesanan Berhasil!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Pesanan Anda telah berhasil dibuat.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    Text(
                        text = "Silakan melakukan pembayaran sesuai dengan informasi berikut:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    infoContact?.let { info ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = info.infoPayment,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                
                                Text(
                                    text = "Kontak Toko:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Telp: ${info.storePhone}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Email: ${info.storeEmail}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showInfoTokoDialog = true }
                    ) {
                        Text("Info Toko")
                    }
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onCheckoutSuccess()
                        }
                    ) {
                        Text("Lihat Pesanan")
                    }
                }
            }
        )
    }
    
    // Info Toko Dialog
    if (showInfoTokoDialog) {
        InfoTokoDialog(
            infoContact = infoContact,
            onDismiss = { showInfoTokoDialog = false }
        )
    }
    
    // Error Snackbar
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
        }
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(message)
        }
    }
}

@Composable
fun InfoTokoDialog(
    infoContact: com.example.rotibox.data.local.entity.InfoContactEntity?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Informasi Toko",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            infoContact?.let { info ->
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Store Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "RotiBox",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "📍 ${info.storeAddress}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "📞 ${info.storePhone}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "📧 ${info.storeEmail}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    // Payment Info
                    Text(
                        text = "Informasi Pembayaran",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = info.infoPayment,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    // Delivery Info
                    Text(
                        text = "Informasi Pengantaran",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = info.description,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            } ?: Text("Informasi toko tidak tersedia")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}
