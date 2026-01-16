package com.example.rotibox.ui.admin.info_kontak

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rotibox.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanInfoKontak(
    onNavigateBack: () -> Unit,
    viewModel: InfoKontakViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val infoContact by viewModel.infoContact.observeAsState()
    val updateResult by viewModel.updateResult.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    
    var storePhone by remember { mutableStateOf("") }
    var storeEmail by remember { mutableStateOf("") }
    var storeAddress by remember { mutableStateOf("") }
    var infoPayment by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load data saat pertama kali
    LaunchedEffect(infoContact) {
        infoContact?.let { info ->
            storePhone = info.storePhone
            storeEmail = info.storeEmail
            storeAddress = info.storeAddress
            infoPayment = info.infoPayment
            description = info.description
        }
    }
    
    // Handle update result
    LaunchedEffect(updateResult) {
        when (updateResult) {
            is InfoKontakViewModel.UpdateResult.Success -> {
                showSuccessDialog = true
                viewModel.resetUpdateResult()
            }
            is InfoKontakViewModel.UpdateResult.Error -> {
                errorMessage = (updateResult as InfoKontakViewModel.UpdateResult.Error).message
                viewModel.resetUpdateResult()
            }
            null -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informasi Kontak Toko") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.updateInfoContact(
                                storePhone = storePhone,
                                storeEmail = storeEmail,
                                storeAddress = storeAddress,
                                infoPayment = infoPayment,
                                description = description
                            )
                        },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Simpan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                // Nomor Telepon Toko
                OutlinedTextField(
                    value = storePhone,
                    onValueChange = { storePhone = it },
                    label = { Text("Nomor Telepon Toko") },
                    placeholder = { Text("Contoh: 081234567890") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                // Email Toko
                OutlinedTextField(
                    value = storeEmail,
                    onValueChange = { storeEmail = it },
                    label = { Text("Email Toko") },
                    placeholder = { Text("Contoh: info@rotibox.com") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                // Alamat Toko
                OutlinedTextField(
                    value = storeAddress,
                    onValueChange = { storeAddress = it },
                    label = { Text("Alamat Toko") },
                    placeholder = { Text("Masukkan alamat lengkap toko") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 3,
                    maxLines = 5
                )
                
                // Informasi Pembayaran
                OutlinedTextField(
                    value = infoPayment,
                    onValueChange = { infoPayment = it },
                    label = { Text("Informasi Pembayaran") },
                    placeholder = { Text("Contoh: Transfer Bank BCA: 1234567890 a.n. RotiBox") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 4,
                    maxLines = 8
                )
                
                // Deskripsi Toko
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Toko") },
                    placeholder = { Text("Masukkan deskripsi atau tagline toko") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 3,
                    maxLines = 5
                )
                
                // Tombol Simpan
                Button(
                    onClick = {
                        viewModel.updateInfoContact(
                            storePhone = storePhone,
                            storeEmail = storeEmail,
                            storeAddress = storeAddress,
                            infoPayment = infoPayment,
                            description = description
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isLoading) "Menyimpan..." else "Simpan Perubahan")
                }
            }
            
            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Berhasil") },
            text = { Text("Informasi kontak toko berhasil diperbarui") },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("OK")
                }
            }
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
