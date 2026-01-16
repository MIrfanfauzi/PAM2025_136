package com.example.rotibox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.admin.beranda_admin.AdminDashboardScreen
import com.example.rotibox.ui.admin.info_kontak.HalamanInfoKontak
import com.example.rotibox.ui.admin.kelola_menu.AddEditMenuScreen
import com.example.rotibox.ui.admin.kelola_menu.ManageMenuScreen
import com.example.rotibox.ui.admin.kelola_pesanan.AdminOrderDetailScreen
import com.example.rotibox.ui.admin.kelola_pesanan.ManageOrdersScreen
import com.example.rotibox.ui.admin.laporan.SalesReportScreen
import com.example.rotibox.ui.auth.daftar.RegisterScreen
import com.example.rotibox.ui.auth.login.LoginScreen
import com.example.rotibox.ui.navigation.Routes
import com.example.rotibox.ui.pelanggan.beranda.CustomerHomeScreen
import com.example.rotibox.ui.pelanggan.checkout.CheckoutScreen
import com.example.rotibox.ui.pelanggan.detail_menu.MenuDetailScreen
import com.example.rotibox.ui.pelanggan.detail_pesanan.OrderDetailScreen
import com.example.rotibox.ui.pelanggan.keranjang.CartScreen
import com.example.rotibox.ui.pelanggan.pesanan_saya.OrderHistoryScreen
import com.example.rotibox.ui.pelanggan.profil.ProfileScreen
import com.example.rotibox.ui.theme.RotiBoxTheme
import com.example.rotibox.util.Konstanta
import com.example.rotibox.util.SessionManager

/**
 * MainActivity - Host untuk Compose Navigation
 * Mengelola navigasi antar screen menggunakan Jetpack Compose
 */
class MainActivity : ComponentActivity() {
    
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        
        setContent {
            RotiBoxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RotiBoxApp(sessionManager)
                }
            }
        }
    }
}

@Composable
fun RotiBoxApp(sessionManager: SessionManager) {
    val navController = rememberNavController()
    
    // Determine start destination based on login status
    val startDestination = remember {
        when {
            !sessionManager.isLoggedIn() -> Routes.LOGIN
            sessionManager.isAdmin() -> Routes.ADMIN_DASHBOARD
            sessionManager.isPelanggan() -> Routes.CUSTOMER_HOME
            else -> Routes.LOGIN
        }
    }
    
    // Check if current route requires bottom navigation
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in listOf(
        Routes.CUSTOMER_HOME,
        Routes.CART,
        Routes.ORDER_HISTORY,
        Routes.PROFILE
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CustomerBottomNavigation(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Auth Routes
            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onLoginSuccess = { role ->
                        when (role) {
                            Konstanta.ROLE_ADMIN -> {
                                navController.navigate(Routes.ADMIN_DASHBOARD) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                            Konstanta.ROLE_PELANGGAN -> {
                                navController.navigate(Routes.CUSTOMER_HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
            
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                    onRegisterSuccess = {
                        navController.navigate(Routes.CUSTOMER_HOME) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                )
            }
            
            // Customer Routes
            composable(Routes.CUSTOMER_HOME) {
                CustomerHomeScreen(
                    onMenuClick = { menuId ->
                        navController.navigate(Routes.menuDetail(menuId))
                    }
                )
            }
            
            composable(
                route = Routes.MENU_DETAIL,
                arguments = listOf(navArgument("menuId") { type = NavType.StringType })
            ) { backStackEntry ->
                val menuId = backStackEntry.arguments?.getString("menuId") ?: ""
                MenuDetailScreen(
                    menuId = menuId,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToCart = { navController.navigate(Routes.CART) }
                )
            }
            
            composable(Routes.CART) {
                CartScreen(
                    onNavigateToCheckout = { navController.navigate(Routes.CHECKOUT) }
                )
            }
            
            composable(Routes.CHECKOUT) {
                CheckoutScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onCheckoutSuccess = {
                        navController.navigate(Routes.ORDER_HISTORY) {
                            popUpTo(Routes.CUSTOMER_HOME)
                        }
                    }
                )
            }
            
            composable(Routes.ORDER_HISTORY) {
                OrderHistoryScreen(
                    onOrderClick = { orderId ->
                        navController.navigate(Routes.orderDetail(orderId))
                    }
                )
            }
            
            composable(
                route = Routes.ORDER_DETAIL,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            
            // Admin Routes
            composable(Routes.ADMIN_DASHBOARD) {
                AdminDashboardScreen(
                    onNavigateToManageMenu = { navController.navigate(Routes.MANAGE_MENU) },
                    onNavigateToManageOrders = { navController.navigate(Routes.MANAGE_ORDERS) },
                    onNavigateToSalesReport = { navController.navigate(Routes.SALES_REPORT) },
                    onNavigateToInfoKontak = { navController.navigate(Routes.ADMIN_INFO_KONTAK) },
                    onOrderClick = { orderId ->
                        navController.navigate(Routes.adminOrderDetail(orderId))
                    },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Routes.MANAGE_MENU) {
                ManageMenuScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onAddMenu = { navController.navigate(Routes.addEditMenu()) },
                    onEditMenu = { menuId ->
                        navController.navigate(Routes.addEditMenu(menuId))
                    }
                )
            }
            
            composable(
                route = Routes.ADD_EDIT_MENU,
                arguments = listOf(navArgument("menuId") { type = NavType.StringType })
            ) { backStackEntry ->
                val menuId = backStackEntry.arguments?.getString("menuId")
                AddEditMenuScreen(
                    menuId = if (menuId == "new") null else menuId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            
            composable(Routes.MANAGE_ORDERS) {
                ManageOrdersScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onOrderClick = { orderId ->
                        navController.navigate(Routes.adminOrderDetail(orderId))
                    }
                )
            }
            
            composable(
                route = Routes.ADMIN_ORDER_DETAIL,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                AdminOrderDetailScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            
            composable(Routes.SALES_REPORT) {
                SalesReportScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            
            composable(Routes.ADMIN_INFO_KONTAK) {
                HalamanInfoKontak(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}

@Composable
fun CustomerBottomNavigation(navController: NavHostController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda") },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == Routes.CUSTOMER_HOME,
            onClick = {
                navController.navigate(Routes.CUSTOMER_HOME) {
                    popUpTo(Routes.CUSTOMER_HOME) { inclusive = true }
                }
            }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang") },
            label = { Text("Keranjang") },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == Routes.CART,
            onClick = {
                navController.navigate(Routes.CART) {
                    launchSingleTop = true
                }
            }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Receipt, contentDescription = "Pesanan") },
            label = { Text("Pesanan") },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == Routes.ORDER_HISTORY,
            onClick = {
                navController.navigate(Routes.ORDER_HISTORY) {
                    launchSingleTop = true
                }
            }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == Routes.PROFILE,
            onClick = {
                navController.navigate(Routes.PROFILE) {
                    launchSingleTop = true
                }
            }
        )
    }
}

