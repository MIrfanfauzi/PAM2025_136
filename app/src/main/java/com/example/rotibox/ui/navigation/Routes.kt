package com.example.rotibox.ui.navigation

/**
 * Navigation routes untuk aplikasi RotiBox
 */
object Routes {
    // Auth
    const val LOGIN = "login"
    const val REGISTER = "register"
    
    // Customer
    const val CUSTOMER_HOME = "customer_home"
    const val MENU_DETAIL = "menu_detail/{menuId}"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDER_HISTORY = "order_history"
    const val ORDER_DETAIL = "order_detail/{orderId}"
    const val PROFILE = "profile"
    
    // Admin
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val MANAGE_MENU = "manage_menu"
    const val ADD_EDIT_MENU = "add_edit_menu/{menuId}"
    const val MANAGE_ORDERS = "manage_orders"
    const val ADMIN_ORDER_DETAIL = "admin_order_detail/{orderId}"
    const val SALES_REPORT = "sales_report"
    const val ADMIN_INFO_KONTAK = "admin_info_kontak"
    
    // Helper functions
    fun menuDetail(menuId: String) = "menu_detail/$menuId"
    fun orderDetail(orderId: String) = "order_detail/$orderId"
    fun addEditMenu(menuId: String = "new") = "add_edit_menu/$menuId"
    fun adminOrderDetail(orderId: String) = "admin_order_detail/$orderId"
}
