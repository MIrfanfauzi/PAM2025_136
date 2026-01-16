package com.example.rotibox.ui.pelanggan.keranjang

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
import com.example.rotibox.data.local.entity.CartItemDenganMenu
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.components.EmptyState
import com.example.rotibox.ui.components.QuantitySelector
import com.example.rotibox.ui.components.RotiBoxButton
import com.example.rotibox.util.SessionManager
import com.example.rotibox.util.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToCheckout: () -> Unit,
    onNavigateBack: () -> Unit = {},
    viewModel: KeranjangViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()
    
    val cartItems by viewModel.cartItems.observeAsState(emptyList())
    val totalAmount by viewModel.totalAmount.observeAsState(0L)
    
    var itemToDelete by remember { mutableStateOf<CartItemDenganMenu?>(null) }
    
    // Load cart items
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadCartItems(it) }
    }
    
    // Delete confirmation dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Hapus Item?") },
            text = { Text("Apakah Anda yakin ingin menghapus ${itemToDelete?.menu?.name} dari keranjang?") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { viewModel.removeItem(it.cartItem) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            com.example.rotibox.ui.components.RotiBoxTopAppBar(
                title = "Keranjang"
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyState(
                icon = Icons.Default.ShoppingCart,
                message = "Keranjang masih kosong",
                actionText = "Mulai Belanja",
                onActionClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Cart Items List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(item.cartItem, newQuantity)
                            },
                            onDelete = { itemToDelete = item }
                        )
                    }
                }
                
                // Bottom Section: Total & Checkout
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatRupiah(totalAmount),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // Checkout Button
                        RotiBoxButton(
                            text = "Checkout",
                            onClick = onNavigateToCheckout,
                            icon = Icons.Default.ShoppingCart
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItemDenganMenu,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image Placeholder
            Surface(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val imageFile = if (item.menu.imageUrl.isNotBlank() && 
                        !item.menu.imageUrl.startsWith("placeholder_")) {
                        java.io.File(item.menu.imageUrl)
                    } else null
                    
                    if (imageFile != null && imageFile.exists()) {
                        coil.compose.AsyncImage(
                            model = imageFile,
                            contentDescription = item.menu.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Item Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.menu.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = formatRupiah(item.menu.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Quantity Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuantitySelector(
                        quantity = item.cartItem.quantity,
                        onQuantityChange = onQuantityChange,
                        maxStock = item.menu.stock,
                        modifier = Modifier.weight(1f),
                        compact = true // Mode compact untuk keranjang
                    )
                    
                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                // Subtotal
                Text(
                    text = "Subtotal: ${formatRupiah(item.menu.price * item.cartItem.quantity)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
