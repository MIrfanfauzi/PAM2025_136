package com.example.rotibox.ui.pelanggan.detail_menu

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.components.LoadingIndicator
import com.example.rotibox.ui.components.QuantitySelector
import com.example.rotibox.ui.components.RotiBoxButton
import com.example.rotibox.ui.pelanggan.beranda.formatRupiah
import com.example.rotibox.util.SessionManager

/**
 * Menu Detail Screen - Composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDetailScreen(
    menuId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: DetailMenuViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()
    
    var quantity by remember { mutableStateOf(1) }
    val menu by viewModel.menu.observeAsState()
    
    LaunchedEffect(menuId) {
        viewModel.loadMenu(menuId)
    }
    
    Scaffold(
        topBar = {
            com.example.rotibox.ui.components.RotiBoxTopAppBar(
                title = "Detail Menu",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (menu == null) {
            LoadingIndicator(modifier = Modifier.padding(paddingValues))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val imageFile = if (menu!!.imageUrl.isNotBlank() && 
                        !menu!!.imageUrl.startsWith("placeholder_")) {
                        java.io.File(menu!!.imageUrl)
                    } else null
                    
                    if (imageFile != null && imageFile.exists()) {
                        coil.compose.AsyncImage(
                            model = imageFile,
                            contentDescription = menu!!.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = menu!!.name,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Menu Info
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = menu!!.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = formatRupiah(menu!!.price),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Divider()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Deskripsi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = menu!!.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Quantity Selector
                    Text(
                        text = "Jumlah",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    QuantitySelector(
                        quantity = quantity,
                        onQuantityChange = { newQty -> quantity = newQty },
                        maxStock = menu!!.stock,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Stock Info
                    if (menu!!.stock > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Stok tersedia: ${menu!!.stock}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (menu!!.stock < 10) MaterialTheme.colorScheme.error 
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Add to Cart Button
                    RotiBoxButton(
                        text = "Tambah ke Keranjang",
                        onClick = {
                            if (userId != null) {
                                viewModel.addToCart(userId, menuId, quantity)
                                Toast.makeText(context, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                                onNavigateToCart()
                            } else {
                                Toast.makeText(context, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                                // Optional: navigate to login screen here
                            }
                        },
                        icon = Icons.Default.ShoppingCart
                    )
                }
            }
        }
    }
}
