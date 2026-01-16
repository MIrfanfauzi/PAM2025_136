package com.example.rotibox.ui.pelanggan.profil

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.components.RotiBoxButton
import com.example.rotibox.ui.components.RotiBoxTextField
import com.example.rotibox.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfilViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()
    
    val user by viewModel.user.observeAsState()
    val infoContact by viewModel.infoContact.observeAsState()
    val updateResult by viewModel.updateResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showEditDialog by remember { mutableStateOf(false) }
    var showInfoTokoDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Load user data and info contact
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadUser(it) }
        viewModel.loadInfoContact()
    }
    
    // Handle update result
    LaunchedEffect(updateResult) {
        when (val result = updateResult) {
            is ProfilViewModel.UpdateResult.Success -> {
                snackbarHostState.showSnackbar("Profil berhasil diupdate")
                showEditDialog = false
                userId?.let { viewModel.loadUser(it) } // Reload data
            }
            is ProfilViewModel.UpdateResult.Error -> {
                snackbarHostState.showSnackbar(result.message)
            }
            null -> {}
        }
    }
    
    // Edit Profile Dialog
    if (showEditDialog && user != null) {
        EditProfileDialog(
            user = user!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUser ->
                viewModel.updateProfile(updatedUser)
            }
        )
    }
    
    // Info Toko Dialog
    if (showInfoTokoDialog) {
        com.example.rotibox.ui.pelanggan.checkout.InfoTokoDialog(
            infoContact = infoContact,
            onDismiss = { showInfoTokoDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            com.example.rotibox.ui.components.RotiBoxTopAppBar(
                title = "Profil"
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Icon
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // Name
            Text(
                text = user?.name ?: "Loading...",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Email
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Profile Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Informasi Profil",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Divider()
                    
                    // Email Info
                    ProfileInfoItem(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = user?.email ?: "-"
                    )
                    
                    // Phone Info
                    ProfileInfoItem(
                        icon = Icons.Default.Phone,
                        label = "Nomor Telepon",
                        value = user?.phone ?: "-"
                    )
                    
                    // Address Info
                    ProfileInfoItem(
                        icon = Icons.Default.Home,
                        label = "Alamat",
                        value = user?.address?.ifBlank { "-" } ?: "-"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Edit Profile Button
            Button(
                onClick = { showEditDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = user != null
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profil")
            }
            
            // Info Toko Button
            OutlinedButton(
                onClick = { showInfoTokoDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Info Toko")
            }
            
            // Logout Button
            RotiBoxButton(
                text = "Logout",
                onClick = {
                    showLogoutDialog = true
                },
                icon = Icons.Default.Logout
            )
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
            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
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

@Composable
fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    user: com.example.rotibox.data.local.entity.UserEntity,
    onDismiss: () -> Unit,
    onSave: (com.example.rotibox.data.local.entity.UserEntity) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var phone by remember { mutableStateOf(user.phone) }
    var address by remember { mutableStateOf(user.address) }
    var isLoading by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "Edit Profil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name Field
                RotiBoxTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nama Lengkap",
                    placeholder = "Masukkan nama lengkap",
                    leadingIcon = Icons.Default.Person,
                    imeAction = ImeAction.Next,
                    enabled = !isLoading
                )
                
                // Email Field (Read-only)
                RotiBoxTextField(
                    value = user.email,
                    onValueChange = {},
                    label = "Email (tidak bisa diubah)",
                    leadingIcon = Icons.Default.Email,
                    enabled = false
                )
                
                // Phone Field
                RotiBoxTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Nomor Telepon",
                    placeholder = "Masukkan nomor telepon",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                    enabled = !isLoading
                )
                
                // Address Field
                RotiBoxTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Alamat",
                    placeholder = "Masukkan alamat lengkap",
                    leadingIcon = Icons.Default.Home,
                    imeAction = ImeAction.Done,
                    enabled = !isLoading,
                    singleLine = false,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        isLoading = true
                        val updatedUser = user.copy(
                            name = name.trim(),
                            phone = phone.trim(),
                            address = address.trim(),
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(updatedUser)
                    }
                },
                enabled = !isLoading && name.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Batal")
            }
        }
    )
}
