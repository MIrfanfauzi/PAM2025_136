package com.example.rotibox.ui.admin.kelola_menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.rotibox.data.local.entity.MenuEntity
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.components.EmptyState
import com.example.rotibox.util.formatRupiah
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMenuScreen(
    onNavigateBack: () -> Unit,
    onAddMenu: () -> Unit,
    onEditMenu: (String) -> Unit,
    viewModel: KelolaMenuViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val menuList by viewModel.menuList.observeAsState(emptyList())
    val operationResult by viewModel.operationResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle operation result
    LaunchedEffect(operationResult) {
        operationResult?.let { result ->
            when (result) {
                is KelolaMenuViewModel.OperationResult.Success -> {
                    snackbarHostState.showSnackbar(result.message)
                }
                is KelolaMenuViewModel.OperationResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Menu") },
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
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMenu) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Menu")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (menuList.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Fastfood,
                message = "Belum ada menu",
                actionText = "Tambah Menu",
                onActionClick = onAddMenu,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(menuList) { menu ->
                    MenuItemCard(
                        menu = menu,
                        onEdit = { onEditMenu(menu.id) },
                        onToggleStatus = { viewModel.toggleMenuStatus(menu.id, !menu.isActive) },
                        onDelete = { viewModel.deleteMenu(menu) }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(
    menu: MenuEntity,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (menu.imageUrl.isNotBlank()) {
                    val imageFile = File(menu.imageUrl)
                    if (imageFile.exists()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageFile),
                            contentDescription = menu.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Fastfood,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.Fastfood,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Menu Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = menu.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Status Badge
                    Surface(
                        color = if (menu.isActive)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = if (menu.isActive) "Aktif" else "Nonaktif",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (menu.isActive)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                if (menu.description.isNotBlank()) {
                    Text(
                        text = menu.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = formatRupiah(menu.price),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Edit Button
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }

                    // Toggle Status Button
                    OutlinedButton(
                        onClick = onToggleStatus,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            if (menu.isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (menu.isActive) "" else "",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (menu.isActive) "" else "")
                    }

                    // Delete Button
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Menu") },
                text = { Text("Apakah Anda yakin ingin menghapus menu \"${menu.name}\"?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDelete()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}
