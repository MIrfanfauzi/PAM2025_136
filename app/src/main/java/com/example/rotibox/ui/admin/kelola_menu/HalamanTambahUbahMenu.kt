package com.example.rotibox.ui.admin.kelola_menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.rotibox.data.local.entity.MenuEntity
import com.example.rotibox.ui.ViewModelFactory
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMenuScreen(
    menuId: String?,
    onNavigateBack: () -> Unit,
    viewModel: KelolaMenuViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("100") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImagePath by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var currentMenu by remember { mutableStateOf<MenuEntity?>(null) }
    
    val operationResult by viewModel.operationResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    
    // Handle operation result
    LaunchedEffect(operationResult) {
        operationResult?.let { result ->
            when (result) {
                is KelolaMenuViewModel.OperationResult.Success -> {
                    snackbarHostState.showSnackbar(result.message)
                    onNavigateBack()
                }
                is KelolaMenuViewModel.OperationResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                    isLoading = false
                }
            }
        }
    }
    
    // Load menu data if editing
    LaunchedEffect(menuId) {
        if (menuId != null) {
            val menu = viewModel.getMenuById(menuId)
            if (menu != null) {
                currentMenu = menu
                name = menu.name
                description = menu.description
                price = menu.price.toString()
                stock = menu.stock.toString()
                existingImagePath = menu.imageUrl
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (menuId == null) "Tambah Menu" else "Edit Menu") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Preview & Picker
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Gambar Produk",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Image Preview
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            imageUri != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUri),
                                    contentDescription = "Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            existingImagePath != null && existingImagePath!!.isNotBlank() -> {
                                val file = File(existingImagePath!!)
                                if (file.exists()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(file),
                                        contentDescription = "Existing Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Image,
                                        contentDescription = "No Image",
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            else -> {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = "No Image",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                    
                    // Pick Image Button
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (imageUri != null || existingImagePath != null) "Ganti Gambar" else "Pilih Gambar dari Galeri")
                    }
                }
            }
            
            // Nama Menu
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Menu") },
                placeholder = { Text("Contoh: Roti Tawar") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )
            
            // Deskripsi
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                placeholder = { Text("Deskripsi menu...") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                minLines = 3,
                maxLines = 5
            )
            
            // Harga
            OutlinedTextField(
                value = price,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        price = newValue
                    }
                },
                label = { Text("Harga (Rp)") },
                placeholder = { Text("Contoh: 15000") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("Rp ") }
            )
            
            // Stok Maksimal
            OutlinedTextField(
                value = stock,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        stock = newValue
                    }
                },
                label = { Text("Stok Maksimal") },
                placeholder = { Text("Contoh: 100") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = { Text("Batas maksimal pemesanan (tidak berkurang)") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tombol Simpan
            Button(
                onClick = {
                    isLoading = true
                    val priceValue = price.toLongOrNull() ?: 0
                    val stockValue = stock.toIntOrNull() ?: 0
                    
                    // Save image to internal storage if new image selected
                    val finalImagePath = if (imageUri != null) {
                        try {
                            val menuImageId = menuId ?: java.util.UUID.randomUUID().toString()
                            val imagesDir = File(context.filesDir, "menu_images")
                            if (!imagesDir.exists()) {
                                imagesDir.mkdirs()
                            }
                            val imageFile = File(imagesDir, "menu_$menuImageId.jpg")
                            
                            context.contentResolver.openInputStream(imageUri!!)?.use { input ->
                                FileOutputStream(imageFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            imageFile.absolutePath
                        } catch (e: Exception) {
                            existingImagePath ?: ""
                        }
                    } else {
                        existingImagePath ?: ""
                    }
                    
                    if (menuId == null) {
                        // Add new menu
                        viewModel.addMenu(
                            name = name.trim(),
                            description = description.trim(),
                            price = priceValue,
                            stock = stockValue,
                            imageUrl = finalImagePath
                        )
                    } else {
                        // Update existing menu
                        currentMenu?.let { menu ->
                            viewModel.updateMenu(
                                menu.copy(
                                    name = name.trim(),
                                    description = description.trim(),
                                    price = priceValue,
                                    stock = stockValue,
                                    imageUrl = finalImagePath
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && name.isNotBlank() && price.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (menuId == null) "Tambah Menu" else "Simpan Perubahan")
                }
            }
            
            // Tombol Batal
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Batal")
            }
        }
    }
}
